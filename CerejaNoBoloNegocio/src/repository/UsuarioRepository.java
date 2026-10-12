package repository;

import java.util.Optional;
import model.Usuario;

/** Contrato de persistência de usuários. */
public interface UsuarioRepository {

    Usuario salvar(Usuario usuario);

    Optional<Usuario> buscarPorLogin(String login);
}
