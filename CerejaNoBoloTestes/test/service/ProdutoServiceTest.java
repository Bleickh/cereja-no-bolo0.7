package service;

import static org.junit.Assert.assertEquals;

import exception.RecursoNaoEncontradoException;
import exception.RegraNegocioException;
import java.math.BigDecimal;
import model.Categoria;
import model.Produto;
import org.junit.Before;
import org.junit.Test;
import repository.memory.CategoriaMemoriaRepository;
import repository.memory.ProdutoMemoriaRepository;

/** Testes das regras de cadastro de produtos (RF01, RF02, RF03, RF06). */
public class ProdutoServiceTest {

    private ProdutoService servico;
    private int categoriaId;

    @Before
    public void preparar() {
        CategoriaMemoriaRepository categorias = new CategoriaMemoriaRepository();
        categoriaId = categorias.salvar(new Categoria(0, "Bolos")).getId();
        servico = new ProdutoService(new ProdutoMemoriaRepository(), categorias);
    }

    @Test
    public void cadastrar_comDadosValidos_nasceComEstoqueZero() {
        Produto p = servico.cadastrar("  Bolo de cenoura ", new BigDecimal("16.00"), categoriaId);
        assertEquals("Bolo de cenoura", p.getNome());
        assertEquals(0, p.getEstoque());
        assertEquals(1, servico.listar().size());
    }

    @Test(expected = RegraNegocioException.class)
    public void cadastrar_semNome_eRejeitado() {
        servico.cadastrar("   ", new BigDecimal("10"), categoriaId);
    }

    @Test(expected = RegraNegocioException.class)
    public void cadastrar_comPrecoZero_eRejeitado() {
        servico.cadastrar("Brigadeiro", BigDecimal.ZERO, categoriaId);
    }

    @Test(expected = RecursoNaoEncontradoException.class)
    public void cadastrar_comCategoriaInexistente_eRejeitado() {
        servico.cadastrar("Brigadeiro", new BigDecimal("3"), 999);
    }

    @Test
    public void editar_alteraNomeEPreco() {
        Produto p = servico.cadastrar("Brigadeiro", new BigDecimal("3.50"), categoriaId);
        Produto editado = servico.editar(p.getId(), "Brigadeiro gourmet", new BigDecimal("4.00"), categoriaId);
        assertEquals("Brigadeiro gourmet", editado.getNome());
        assertEquals(new BigDecimal("4.00"), editado.getPreco());
    }

    @Test(expected = RecursoNaoEncontradoException.class)
    public void excluir_removeOProduto() {
        Produto p = servico.cadastrar("Brigadeiro", new BigDecimal("3.50"), categoriaId);
        servico.excluir(p.getId());
        servico.buscar(p.getId());
    }
}
