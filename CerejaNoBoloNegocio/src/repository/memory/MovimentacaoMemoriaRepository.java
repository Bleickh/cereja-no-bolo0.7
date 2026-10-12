package repository.memory;

import java.util.ArrayList;
import java.util.List;
import model.Movimentacao;
import repository.MovimentacaoRepository;

/** Implementação em memória de MovimentacaoRepository. */
public class MovimentacaoMemoriaRepository implements MovimentacaoRepository {

    private final List<Movimentacao> dados = new ArrayList<>();
    private int proximoId = 1;

    @Override
    public Movimentacao salvar(Movimentacao movimentacao) {
        movimentacao.setId(proximoId++);
        dados.add(movimentacao);
        return movimentacao;
    }

    @Override
    public List<Movimentacao> listarPorProduto(int produtoId) {
        List<Movimentacao> resultado = new ArrayList<>();
        for (Movimentacao m : dados) {
            if (m.getProdutoId() == produtoId) {
                resultado.add(m);
            }
        }
        return resultado;
    }
}
