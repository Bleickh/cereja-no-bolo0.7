package repository;

import java.util.List;
import model.Movimentacao;

/** Contrato de persistência do histórico de movimentações de estoque. */
public interface MovimentacaoRepository {

    Movimentacao salvar(Movimentacao movimentacao);

    List<Movimentacao> listarPorProduto(int produtoId);
}
