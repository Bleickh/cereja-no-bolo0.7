package repository;

import java.util.List;
import java.util.Optional;
import model.Categoria;

/** Contrato de persistência de categorias (abstração da qual os serviços dependem). */
public interface CategoriaRepository {

    Categoria salvar(Categoria categoria);

    Optional<Categoria> buscarPorId(int id);

    List<Categoria> listar();
}
