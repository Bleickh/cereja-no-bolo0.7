import config.ConfiguracaoBanco;
import config.FabricaDeServicos;
import config.Servicos;
import exception.RegraNegocioException;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import model.Categoria;
import model.Movimentacao;
import model.Produto;
import model.TipoMovimentacao;

/**
 * Testes de verificação executados no main(), conforme pedido na atividade.
 *
 * Sem argumentos: roda a suíte completa com repositórios EM MEMÓRIA (não precisa de MySQL).
 * Com o argumento --mysql: roda um teste de fumaça contra o banco real configurado em
 * config/database.properties (ou variáveis de ambiente CEREJA_DB_*).
 */
public class Main {

    private static int aprovados = 0;
    private static int reprovados = 0;

    public static void main(String[] args) {
        Clock relogioFixo = Clock.fixed(Instant.parse("2026-03-19T14:30:00Z"), ZoneId.of("UTC"));

        System.out.println("=== Suíte em memória ===");
        Servicos s = FabricaDeServicos.emMemoria(relogioFixo);
        testarCategorias(s);
        Produto bolo = testarProdutos(s);
        testarEstoque(s, bolo.getId());
        testarEdicaoEExclusao(s);
        testarAutenticacao(s);

        if (args.length > 0 && args[0].equals("--mysql")) {
            System.out.println("\n=== Teste de fumaça com MySQL ===");
            testarMySql(relogioFixo);
        }

        System.out.printf("%nResultado: %d aprovados, %d reprovados%n", aprovados, reprovados);
        if (reprovados > 0) {
            System.exit(1);
        }
    }

    // ---------------------------------------------------------------- categorias
    private static void testarCategorias(Servicos s) {
        Categoria bolos = s.categorias().cadastrar("  Bolos ");
        verificar("categoria cadastrada recebe id e nome sem espaços extras",
                bolos.getId() > 0 && bolos.getNome().equals("Bolos"));
        s.categorias().cadastrar("Doces");
        verificar("listagem retorna as 2 categorias", s.categorias().listar().size() == 2);
        esperaRegraNegocio("categoria duplicada (ignorando maiúsculas) é rejeitada",
                () -> s.categorias().cadastrar("bolos"));
        esperaRegraNegocio("categoria sem nome é rejeitada", () -> s.categorias().cadastrar("   "));
    }

    // ----------------------------------------------------------------- produtos
    private static Produto testarProdutos(Servicos s) {
        int categoriaId = s.categorias().listar().get(0).getId();
        Produto bolo = s.produtos().cadastrar("Bolo de cenoura", new BigDecimal("16.00"), categoriaId);
        verificar("produto cadastrado nasce com estoque 0 e id gerado",
                bolo.getId() > 0 && bolo.getEstoque() == 0);

        esperaRegraNegocio("produto sem nome é rejeitado",
                () -> s.produtos().cadastrar("", new BigDecimal("10"), categoriaId));
        esperaRegraNegocio("preço zero é rejeitado",
                () -> s.produtos().cadastrar("Brigadeiro", BigDecimal.ZERO, categoriaId));
        esperaRegraNegocio("preço negativo é rejeitado",
                () -> s.produtos().cadastrar("Brigadeiro", new BigDecimal("-1"), categoriaId));
        esperaRegraNegocio("categoria inexistente é rejeitada",
                () -> s.produtos().cadastrar("Brigadeiro", new BigDecimal("3"), 999));
        verificar("nenhum produto inválido foi gravado", s.produtos().listar().size() == 1);
        return bolo;
    }

    // ------------------------------------------------------------------ estoque
    private static void testarEstoque(Servicos s, int produtoId) {
        Movimentacao entrada = s.estoque().registrar(produtoId, TipoMovimentacao.ENTRADA, 10);
        verificar("entrada de 10 -> estoque 10", s.produtos().buscar(produtoId).getEstoque() == 10);
        verificar("movimentação registra a data do relógio injetado",
                entrada.getData().equals(LocalDateTime.of(2026, 3, 19, 14, 30)));

        s.estoque().registrar(produtoId, TipoMovimentacao.SAIDA, 4);
        verificar("saída de 4 -> estoque 6", s.produtos().buscar(produtoId).getEstoque() == 6);

        esperaRegraNegocio("saída maior que o estoque é rejeitada (sem estoque negativo)",
                () -> s.estoque().registrar(produtoId, TipoMovimentacao.SAIDA, 10));
        verificar("estoque continua 6 após saída rejeitada", s.produtos().buscar(produtoId).getEstoque() == 6);

        esperaRegraNegocio("quantidade zero é rejeitada",
                () -> s.estoque().registrar(produtoId, TipoMovimentacao.ENTRADA, 0));
        esperaRegraNegocio("produto inexistente é rejeitado",
                () -> s.estoque().registrar(999, TipoMovimentacao.ENTRADA, 1));

        List<Movimentacao> historico = s.estoque().historico(produtoId);
        verificar("só as 2 movimentações válidas geraram registro", historico.size() == 2);
        verificar("histórico guarda os tipos na ordem", historico.get(0).getTipo() == TipoMovimentacao.ENTRADA
                && historico.get(1).getTipo() == TipoMovimentacao.SAIDA);
        verificar("conversão de texto da tela para o enum",
                TipoMovimentacao.de("Saída") == TipoMovimentacao.SAIDA
                && TipoMovimentacao.de("entrada") == TipoMovimentacao.ENTRADA);
    }

    // ----------------------------------------------------------- edição/exclusão
    private static void testarEdicaoEExclusao(Servicos s) {
        int categoriaDoces = s.categorias().listar().get(1).getId();
        Produto p = s.produtos().cadastrar("Brigadeiro", new BigDecimal("3.50"), categoriaDoces);
        Produto editado = s.produtos().editar(p.getId(), "Brigadeiro gourmet", new BigDecimal("4.00"), categoriaDoces);
        verificar("edição altera nome e preço",
                editado.getNome().equals("Brigadeiro gourmet") && editado.getPreco().compareTo(new BigDecimal("4.00")) == 0);
        esperaRegraNegocio("edição com preço inválido é rejeitada",
                () -> s.produtos().editar(p.getId(), "Brigadeiro", BigDecimal.ZERO, categoriaDoces));
        s.produtos().excluir(p.getId());
        esperaRegraNegocio("produto excluído não é mais encontrado", () -> s.produtos().buscar(p.getId()));
    }

    // ------------------------------------------------------------ autenticação
    private static void testarAutenticacao(Servicos s) {
        var usuario = s.autenticacao().cadastrar("admin", "senha-forte-123");
        verificar("senha não é guardada em texto puro", !usuario.getSenhaHash().contains("senha-forte-123"));
        verificar("login correto autentica", s.autenticacao().autenticar("admin", "senha-forte-123").isPresent());
        verificar("senha errada não autentica", s.autenticacao().autenticar("admin", "outra").isEmpty());
        verificar("usuário inexistente não autentica", s.autenticacao().autenticar("ninguem", "x").isEmpty());
        esperaRegraNegocio("login duplicado é rejeitado", () -> s.autenticacao().cadastrar("admin", "abc"));
    }

    // -------------------------------------------------------------------- MySQL
    private static void testarMySql(Clock relogio) {
        try {
            Servicos s = FabricaDeServicos.comMySql(ConfiguracaoBanco.carregar().comoProvider(), relogio);
            String sufixo = String.valueOf(System.currentTimeMillis());
            Categoria c = s.categorias().cadastrar("Teste-" + sufixo);
            Produto p = s.produtos().cadastrar("Produto teste " + sufixo, new BigDecimal("9.90"), c.getId());
            s.estoque().registrar(p.getId(), TipoMovimentacao.ENTRADA, 5);
            verificar("MySQL: estoque persistido = 5", s.produtos().buscar(p.getId()).getEstoque() == 5);
            verificar("MySQL: movimentação persistida", s.estoque().historico(p.getId()).size() == 1);
            esperaRegraNegocio("MySQL: saída acima do estoque rejeitada",
                    () -> s.estoque().registrar(p.getId(), TipoMovimentacao.SAIDA, 6));
        } catch (RuntimeException e) {
            verificar("MySQL: execução sem erro (" + e.getMessage() + ")", false);
        }
    }

    // ------------------------------------------------------------------ helpers
    private static void verificar(String descricao, boolean condicao) {
        if (condicao) {
            aprovados++;
            System.out.println("  [OK]    " + descricao);
        } else {
            reprovados++;
            System.out.println("  [FALHA] " + descricao);
        }
    }

    private static void esperaRegraNegocio(String descricao, Runnable acao) {
        try {
            acao.run();
            verificar(descricao + " (nenhuma exceção lançada)", false);
        } catch (RegraNegocioException e) {
            verificar(descricao + " -> \"" + e.getMessage() + "\"", true);
        }
    }
}
