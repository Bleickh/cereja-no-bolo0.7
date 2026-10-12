package repository.jdbc;

import config.ConexaoProvider;
import exception.RepositorioException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import model.Categoria;
import repository.CategoriaRepository;

/** Persistência de categorias em MySQL via JDBC. */
public class CategoriaJdbcRepository implements CategoriaRepository {

    private final ConexaoProvider conexoes;

    public CategoriaJdbcRepository(ConexaoProvider conexoes) {
        this.conexoes = conexoes;
    }

    @Override
    public Categoria salvar(Categoria categoria) {
        String sql = "INSERT INTO categoria(nome) VALUES (?)";
        try (Connection c = conexoes.obter();
                PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, categoria.getNome());
            ps.executeUpdate();
            try (ResultSet chaves = ps.getGeneratedKeys()) {
                if (chaves.next()) {
                    categoria.setId(chaves.getInt(1));
                }
            }
            return categoria;
        } catch (SQLException e) {
            throw new RepositorioException("Erro ao salvar categoria", e);
        }
    }

    @Override
    public Optional<Categoria> buscarPorId(int id) {
        String sql = "SELECT id, nome FROM categoria WHERE id = ?";
        try (Connection c = conexoes.obter(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new RepositorioException("Erro ao buscar categoria " + id, e);
        }
    }

    @Override
    public List<Categoria> listar() {
        String sql = "SELECT id, nome FROM categoria ORDER BY nome";
        try (Connection c = conexoes.obter();
                PreparedStatement ps = c.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            List<Categoria> lista = new ArrayList<>();
            while (rs.next()) {
                lista.add(mapear(rs));
            }
            return lista;
        } catch (SQLException e) {
            throw new RepositorioException("Erro ao listar categorias", e);
        }
    }

    private Categoria mapear(ResultSet rs) throws SQLException {
        return new Categoria(rs.getInt("id"), rs.getString("nome"));
    }
}
