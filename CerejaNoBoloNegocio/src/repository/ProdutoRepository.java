package repository;

import java.util.List;
import java.util.Optional;
import model.Produto;

/** Contrato de persistência de produtos. */
public interface ProdutoRepository {

    /** Insere o produto e devolve-o com o id gerado. */
    Produto salvar(Produto produto);

    Optional<Produto> buscarPorId(int id);

    List<Produto> listar();

    void atualizar(Produto produto);

    void excluir(int id);
}
