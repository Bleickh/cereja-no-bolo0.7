package repository.memory;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import model.Usuario;
import repository.UsuarioRepository;

/** Implementação em memória de UsuarioRepository. */
public class UsuarioMemoriaRepository implements UsuarioRepository {

    private final Map<String, Usuario> dados = new HashMap<>();
    private int proximoId = 1;

    @Override
    public Usuario salvar(Usuario usuario) {
        usuario.setId(proximoId++);
        dados.put(usuario.getLogin().toLowerCase(), usuario);
        return usuario;
    }

    @Override
    public Optional<Usuario> buscarPorLogin(String login) {
        return Optional.ofNullable(dados.get(login.toLowerCase()));
    }
}
