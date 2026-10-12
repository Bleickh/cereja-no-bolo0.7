package config;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.DriverManager;
import java.util.Properties;

/**
 * Lê URL, usuário e senha do banco SEM deixá-los no código-fonte.
 * Ordem de busca: variáveis de ambiente (CEREJA_DB_URL, CEREJA_DB_USER,
 * CEREJA_DB_PASSWORD) e, se ausentes, o arquivo config/database.properties
 * (que não deve ser versionado - veja .gitignore).
 */
public final class ConfiguracaoBanco {

    private static final Path ARQUIVO = Path.of("config", "database.properties");

    private final String url;
    private final String usuario;
    private final String senha;

    private ConfiguracaoBanco(String url, String usuario, String senha) {
        this.url = url;
        this.usuario = usuario;
        this.senha = senha;
    }

    public static ConfiguracaoBanco carregar() {
        Properties arquivo = lerArquivo();
        String url = valor("CEREJA_DB_URL", "db.url", arquivo);
        String usuario = valor("CEREJA_DB_USER", "db.user", arquivo);
        String senha = valor("CEREJA_DB_PASSWORD", "db.password", arquivo);
        if (url == null || usuario == null || senha == null) {
            throw new IllegalStateException("Configuração do banco ausente. Defina as variáveis "
                    + "CEREJA_DB_URL/USER/PASSWORD ou crie config/database.properties "
                    + "a partir de database.properties.example");
        }
        return new ConfiguracaoBanco(url, usuario, senha);
    }

    /** Fornece conexões novas a cada chamada (o chamador fecha com try-with-resources). */
    public ConexaoProvider comoProvider() {
        return () -> DriverManager.getConnection(url, usuario, senha);
    }

    private static String valor(String variavel, String chave, Properties arquivo) {
        String doAmbiente = System.getenv(variavel);
        return doAmbiente != null ? doAmbiente : arquivo.getProperty(chave);
    }

    private static Properties lerArquivo() {
        Properties props = new Properties();
        if (Files.exists(ARQUIVO)) {
            try (InputStream in = Files.newInputStream(ARQUIVO)) {
                props.load(in);
            } catch (IOException e) {
                throw new IllegalStateException("Não foi possível ler " + ARQUIVO, e);
            }
        }
        return props;
    }
}
