package repository.jdbc;

import config.ConexaoProvider;
import exception.RepositorioException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import model.Movimentacao;
import model.TipoMovimentacao;
import repository.MovimentacaoRepository;

/** Persistência do histórico de movimentações em MySQL via JDBC. */
public class MovimentacaoJdbcRepository implements MovimentacaoRepository {

    private final ConexaoProvider conexoes;

    public MovimentacaoJdbcRepository(ConexaoProvider conexoes) {
        this.conexoes = conexoes;
    }

    @Override
    public Movimentacao salvar(Movimentacao m) {
        String sql = "INSERT INTO movimentacao(produto_id, quantidade, tipo, data_movimento) VALUES (?, ?, ?, ?)";
        try (Connection c = conexoes.obter();
                PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, m.getProdutoId());
            ps.setInt(2, m.getQuantidade());
            ps.setString(3, m.getTipo().name());
            ps.setTimestamp(4, Timestamp.valueOf(m.getData()));
            ps.executeUpdate();
            try (ResultSet chaves = ps.getGeneratedKeys()) {
                if (chaves.next()) {
                    m.setId(chaves.getInt(1));
                }
            }
            return m;
        } catch (SQLException e) {
            throw new RepositorioException("Erro ao salvar movimentação", e);
        }
    }

    @Override
    public List<Movimentacao> listarPorProduto(int produtoId) {
        String sql = "SELECT id, produto_id, quantidade, tipo, data_movimento FROM movimentacao "
                + "WHERE produto_id = ? ORDER BY data_movimento";
        try (Connection c = conexoes.obter(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, produtoId);
            try (ResultSet rs = ps.executeQuery()) {
                List<Movimentacao> lista = new ArrayList<>();
                while (rs.next()) {
                    lista.add(new Movimentacao(rs.getInt("id"), rs.getInt("produto_id"),
                            TipoMovimentacao.de(rs.getString("tipo")), rs.getInt("quantidade"),
                            rs.getTimestamp("data_movimento").toLocalDateTime()));
                }
                return lista;
            }
        } catch (SQLException e) {
            throw new RepositorioException("Erro ao listar movimentações do produto " + produtoId, e);
        }
    }
}
