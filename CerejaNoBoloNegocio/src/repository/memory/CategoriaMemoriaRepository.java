package repository.memory;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import model.Categoria;
import repository.CategoriaRepository;

/** Implementação em memória: usada nos testes do main() sem precisar do MySQL. */
public class CategoriaMemoriaRepository implements CategoriaRepository {

    private final Map<Integer, Categoria> dados = new LinkedHashMap<>();
    private int proximoId = 1;

    @Override
    public Categoria salvar(Categoria categoria) {
        categoria.setId(proximoId++);
        dados.put(categoria.getId(), categoria);
        return categoria;
    }

    @Override
    public Optional<Categoria> buscarPorId(int id) {
        return Optional.ofNullable(dados.get(id));
    }

    @Override
    public List<Categoria> listar() {
        return new ArrayList<>(dados.values());
    }
}
