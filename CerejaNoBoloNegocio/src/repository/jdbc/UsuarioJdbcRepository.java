package repository.jdbc;

import config.ConexaoProvider;
import exception.RepositorioException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;
import model.Usuario;
import repository.UsuarioRepository;

/** Persistência de usuários em MySQL via JDBC. */
public class UsuarioJdbcRepository implements UsuarioRepository {

    private final ConexaoProvider conexoes;

    public UsuarioJdbcRepository(ConexaoProvider conexoes) {
        this.conexoes = conexoes;
    }

    @Override
    public Usuario salvar(Usuario usuario) {
        String sql = "INSERT INTO usuario(login, senha_hash) VALUES (?, ?)";
        try (Connection c = conexoes.obter();
                PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, usuario.getLogin());
            ps.setString(2, usuario.getSenhaHash());
            ps.executeUpdate();
            try (ResultSet chaves = ps.getGeneratedKeys()) {
                if (chaves.next()) {
                    usuario.setId(chaves.getInt(1));
                }
            }
            return usuario;
        } catch (SQLException e) {
            throw new RepositorioException("Erro ao salvar usuário", e);
        }
    }

    @Override
    public Optional<Usuario> buscarPorLogin(String login) {
        String sql = "SELECT id, login, senha_hash FROM usuario WHERE login = ?";
        try (Connection c = conexoes.obter(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, login);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next()
                        ? Optional.of(new Usuario(rs.getInt("id"), rs.getString("login"), rs.getString("senha_hash")))
                        : Optional.empty();
            }
        } catch (SQLException e) {
            throw new RepositorioException("Erro ao buscar usuário", e);
        }
    }
}
