package repository.memory;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import model.Produto;
import repository.ProdutoRepository;

/** Implementação em memória de ProdutoRepository. */
public class ProdutoMemoriaRepository implements ProdutoRepository {

    private final Map<Integer, Produto> dados = new LinkedHashMap<>();
    private int proximoId = 1;

    @Override
    public Produto salvar(Produto produto) {
        produto.setId(proximoId++);
        dados.put(produto.getId(), produto);
        return produto;
    }

    @Override
    public Optional<Produto> buscarPorId(int id) {
        return Optional.ofNullable(dados.get(id));
    }

    @Override
    public List<Produto> listar() {
        return new ArrayList<>(dados.values());
    }

    @Override
    public void atualizar(Produto produto) {
        dados.put(produto.getId(), produto);
    }

    @Override
    public void excluir(int id) {
        dados.remove(id);
    }
}
