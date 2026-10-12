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
import model.Produto;
import repository.ProdutoRepository;

/** Persistência de produtos em MySQL via JDBC (PreparedStatement + try-with-resources). */
public class ProdutoJdbcRepository implements ProdutoRepository {

    private static final String COLUNAS = "id, nome, preco, categoria_id, estoque";

    private final ConexaoProvider conexoes;

    public ProdutoJdbcRepository(ConexaoProvider conexoes) {
        this.conexoes = conexoes;
    }

    @Override
    public Produto salvar(Produto produto) {
        String sql = "INSERT INTO produto(nome, preco, categoria_id, estoque) VALUES (?, ?, ?, ?)";
        try (Connection c = conexoes.obter();
                PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, produto.getNome());
            ps.setBigDecimal(2, produto.getPreco());
            ps.setInt(3, produto.getCategoriaId());
            ps.setInt(4, produto.getEstoque());
            ps.executeUpdate();
            try (ResultSet chaves = ps.getGeneratedKeys()) {
                if (chaves.next()) {
                    produto.setId(chaves.getInt(1));
                }
            }
            return produto;
        } catch (SQLException e) {
            throw new RepositorioException("Erro ao salvar produto", e);
        }
    }

    @Override
    public Optional<Produto> buscarPorId(int id) {
        String sql = "SELECT " + COLUNAS + " FROM produto WHERE id = ?";
        try (Connection c = conexoes.obter(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new RepositorioException("Erro ao buscar produto " + id, e);
        }
    }

    @Override
    public List<Produto> listar() {
        String sql = "SELECT " + COLUNAS + " FROM produto ORDER BY nome";
        try (Connection c = conexoes.obter();
                PreparedStatement ps = c.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            List<Produto> lista = new ArrayList<>();
            while (rs.next()) {
                lista.add(mapear(rs));
            }
            return lista;
        } catch (SQLException e) {
            throw new RepositorioException("Erro ao listar produtos", e);
        }
    }

    @Override
    public void atualizar(Produto produto) {
        String sql = "UPDATE produto SET nome = ?, preco = ?, categoria_id = ?, estoque = ? WHERE id = ?";
        try (Connection c = conexoes.obter(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, produto.getNome());
            ps.setBigDecimal(2, produto.getPreco());
            ps.setInt(3, produto.getCategoriaId());
            ps.setInt(4, produto.getEstoque());
            ps.setInt(5, produto.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RepositorioException("Erro ao atualizar produto " + produto.getId(), e);
        }
    }

    @Override
    public void excluir(int id) {
        String sql = "DELETE FROM produto WHERE id = ?";
        try (Connection c = conexoes.obter(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RepositorioException("Erro ao excluir produto " + id, e);
        }
    }

    private Produto mapear(ResultSet rs) throws SQLException {
        return new Produto(rs.getInt("id"), rs.getString("nome"), rs.getBigDecimal("preco"),
                rs.getInt("categoria_id"), rs.getInt("estoque"));
    }
}
