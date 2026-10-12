package service;

import exception.RecursoNaoEncontradoException;
import java.math.BigDecimal;
import java.util.List;
import model.Produto;
import repository.CategoriaRepository;
import repository.ProdutoRepository;

/**
 * Regras de negócio de produtos (RF01, RF02, RF03, RF06):
 * nome obrigatório, preço > 0 e todo produto pertence a uma categoria existente.
 * Não conhece Swing, JDBC nem HTTP.
 */
public class ProdutoService {

    private final ProdutoRepository produtos;
    private final CategoriaRepository categorias;

    public ProdutoService(ProdutoRepository produtos, CategoriaRepository categorias) {
        this.produtos = produtos;
        this.categorias = categorias;
    }

    public Produto cadastrar(String nome, BigDecimal preco, int categoriaId) {
        Produto produto = new Produto(0, nome, preco, categoriaId, 0);
        validar(produto);
        return produtos.salvar(produto);
    }

    public Produto editar(int id, String nome, BigDecimal preco, int categoriaId) {
        Produto produto = buscar(id);
        produto.setNome(nome);
        produto.setPreco(preco);
        produto.setCategoriaId(categoriaId);
        validar(produto);
        produtos.atualizar(produto);
        return produto;
    }

    public void excluir(int id) {
        buscar(id);
        produtos.excluir(id);
    }

    public Produto buscar(int id) {
        return produtos.buscarPorId(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Produto", id));
    }

    public List<Produto> listar() {
        return produtos.listar();
    }

    private void validar(Produto produto) {
        produto.setNome(Validador.textoObrigatorio(produto.getNome(), "Nome do produto"));
        Validador.maiorQueZero(produto.getPreco(), "Preço");
        categorias.buscarPorId(produto.getCategoriaId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Categoria", produto.getCategoriaId()));
    }
}
