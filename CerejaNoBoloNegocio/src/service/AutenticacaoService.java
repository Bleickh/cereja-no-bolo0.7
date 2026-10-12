package service;

import exception.RegraNegocioException;
import java.util.Optional;
import model.Usuario;
import repository.UsuarioRepository;

/**
 * Autenticação real (no sistema desktop, o botão ENTRAR abria o menu sem
 * verificar nada). Compara a senha informada com o hash armazenado.
 */
public class AutenticacaoService {

    private final UsuarioRepository usuarios;
    private final SenhaHasher hasher;

    public AutenticacaoService(UsuarioRepository usuarios, SenhaHasher hasher) {
        this.usuarios = usuarios;
        this.hasher = hasher;
    }

    public Usuario cadastrar(String login, String senha) {
        String loginLimpo = Validador.textoObrigatorio(login, "Login");
        Validador.textoObrigatorio(senha, "Senha");
        if (usuarios.buscarPorLogin(loginLimpo).isPresent()) {
            throw new RegraNegocioException("Login já utilizado.");
        }
        return usuarios.salvar(new Usuario(0, loginLimpo, hasher.gerarHash(senha)));
    }

    public Optional<Usuario> autenticar(String login, String senha) {
        if (login == null || senha == null) {
            return Optional.empty();
        }
        return usuarios.buscarPorLogin(login.trim())
                .filter(u -> hasher.confere(senha, u.getSenhaHash()));
    }
}
