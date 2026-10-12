package config;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Abstração para obtenção de conexões. Os repositórios JDBC dependem desta
 * interface (Inversão de Dependência); no projeto web ela poderá ser
 * substituída por um DataSource gerenciado pelo Spring.
 */
@FunctionalInterface
public interface ConexaoProvider {

    Connection obter() throws SQLException;
}
