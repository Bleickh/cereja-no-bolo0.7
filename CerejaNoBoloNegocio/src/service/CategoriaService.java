package service;

import exception.RegraNegocioException;
import java.util.List;
import model.Categoria;
import repository.CategoriaRepository;

/** Regras de negócio de categorias (RF07). */
public class CategoriaService {

    private final CategoriaRepository categorias;

    public CategoriaService(CategoriaRepository categorias) {
        this.categorias = categorias;
    }

    public Categoria cadastrar(String nome) {
        String nomeLimpo = Validador.textoObrigatorio(nome, "Nome da categoria");
        boolean duplicada = categorias.listar().stream()
                .anyMatch(c -> c.getNome().equalsIgnoreCase(nomeLimpo));
        if (duplicada) {
            throw new RegraNegocioException("Já existe uma categoria chamada '" + nomeLimpo + "'.");
        }
        return categorias.salvar(new Categoria(0, nomeLimpo));
    }

    public List<Categoria> listar() {
        return categorias.listar();
    }
}
