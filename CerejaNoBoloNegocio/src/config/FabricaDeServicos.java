package config;

import java.time.Clock;
import repository.jdbc.CategoriaJdbcRepository;
import repository.jdbc.MovimentacaoJdbcRepository;
import repository.jdbc.ProdutoJdbcRepository;
import repository.jdbc.UsuarioJdbcRepository;
import repository.memory.CategoriaMemoriaRepository;
import repository.memory.MovimentacaoMemoriaRepository;
import repository.memory.ProdutoMemoriaRepository;
import repository.memory.UsuarioMemoriaRepository;
import service.AutenticacaoService;
import service.CategoriaService;
import service.EstoqueService;
import service.Pbkdf2SenhaHasher;
import service.ProdutoService;

/**
 * Raiz de composição (Simple Factory): único lugar que conhece as classes
 * concretas e liga as dependências por construtor. No projeto web, este papel
 * passa a ser do contêiner de injeção de dependências do Spring.
 */
public final class FabricaDeServicos {

    private FabricaDeServicos() {
    }

    public static Servicos emMemoria(Clock relogio) {
        CategoriaMemoriaRepository categorias = new CategoriaMemoriaRepository();
        ProdutoMemoriaRepository produtos = new ProdutoMemoriaRepository();
        return new Servicos(
                new CategoriaService(categorias),
                new ProdutoService(produtos, categorias),
                new EstoqueService(produtos, new MovimentacaoMemoriaRepository(), relogio),
                new AutenticacaoService(new UsuarioMemoriaRepository(), new Pbkdf2SenhaHasher()));
    }

    public static Servicos comMySql(ConexaoProvider conexoes, Clock relogio) {
        CategoriaJdbcRepository categorias = new CategoriaJdbcRepository(conexoes);
        ProdutoJdbcRepository produtos = new ProdutoJdbcRepository(conexoes);
        return new Servicos(
                new CategoriaService(categorias),
                new ProdutoService(produtos, categorias),
                new EstoqueService(produtos, new MovimentacaoJdbcRepository(conexoes), relogio),
                new AutenticacaoService(new UsuarioJdbcRepository(conexoes), new Pbkdf2SenhaHasher()));
    }
}
