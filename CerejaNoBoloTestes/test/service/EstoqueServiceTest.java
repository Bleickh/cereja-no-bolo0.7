package service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import exception.RecursoNaoEncontradoException;
import exception.RegraNegocioException;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import model.Categoria;
import model.Movimentacao;
import model.Produto;
import model.TipoMovimentacao;
import org.junit.Before;
import org.junit.Test;
import repository.memory.CategoriaMemoriaRepository;
import repository.memory.MovimentacaoMemoriaRepository;
import repository.memory.ProdutoMemoriaRepository;

/**
 * Testa EstoqueService com repositórios em memória (sem banco de dados) e
 * relógio fixo, garantindo testes rápidos e determinísticos.
 */
public class EstoqueServiceTest {

    private ProdutoMemoriaRepository produtos;
    private EstoqueService estoque;
    private int produtoId;

    @Before
    public void preparar() {
        Clock relogio = Clock.fixed(Instant.parse("2026-03-19T14:30:00Z"), ZoneId.of("UTC"));
        CategoriaMemoriaRepository categorias = new CategoriaMemoriaRepository();
        produtos = new ProdutoMemoriaRepository();
        estoque = new EstoqueService(produtos, new MovimentacaoMemoriaRepository(), relogio);

        Categoria bolos = categorias.salvar(new Categoria(0, "Bolos"));
        produtoId = new ProdutoService(produtos, categorias)
                .cadastrar("Bolo de cenoura", new BigDecimal("16.00"), bolos.getId()).getId();
    }

    private int saldo() {
        return produtos.buscarPorId(produtoId).get().getEstoque();
    }

    @Test
    public void entrada_aumentaOSaldoERegistraMovimentacao() {
        Movimentacao m = estoque.registrar(produtoId, TipoMovimentacao.ENTRADA, 10);
        assertEquals(10, saldo());
        assertEquals(TipoMovimentacao.ENTRADA, m.getTipo());
        assertEquals(LocalDateTime.of(2026, 3, 19, 14, 30), m.getData());
    }

    @Test
    public void saida_diminuiOSaldo() {
        estoque.registrar(produtoId, TipoMovimentacao.ENTRADA, 10);
        estoque.registrar(produtoId, TipoMovimentacao.SAIDA, 4);
        assertEquals(6, saldo());
    }

    @Test
    public void saidaMaiorQueOSaldo_eRejeitadaSemAlterarEstoqueNemHistorico() {
        estoque.registrar(produtoId, TipoMovimentacao.ENTRADA, 5);
        try {
            estoque.registrar(produtoId, TipoMovimentacao.SAIDA, 6);
            org.junit.Assert.fail("Deveria rejeitar saída acima do estoque");
        } catch (RegraNegocioException esperado) {
            assertTrue(esperado.getMessage().contains("insuficiente"));
        }
        assertEquals(5, saldo());
        assertEquals(1, estoque.historico(produtoId).size());
    }

    @Test(expected = RegraNegocioException.class)
    public void quantidadeZero_eRejeitada() {
        estoque.registrar(produtoId, TipoMovimentacao.ENTRADA, 0);
    }

    @Test(expected = RecursoNaoEncontradoException.class)
    public void produtoInexistente_eRejeitado() {
        estoque.registrar(999, TipoMovimentacao.ENTRADA, 1);
    }

    @Test
    public void historico_guardaApenasMovimentacoesValidas() {
        estoque.registrar(produtoId, TipoMovimentacao.ENTRADA, 3);
        try {
            estoque.registrar(produtoId, TipoMovimentacao.SAIDA, 99);
        } catch (RegraNegocioException ignorada) {
            // esperado
        }
        estoque.registrar(produtoId, TipoMovimentacao.SAIDA, 1);
        assertEquals(2, estoque.historico(produtoId).size());
    }
}
