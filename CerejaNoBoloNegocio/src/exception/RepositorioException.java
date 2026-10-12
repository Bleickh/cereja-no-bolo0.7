package exception;

/** Falha de infraestrutura de persistência (SQL, conexão...). Carrega a causa original. */
public class RepositorioException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public RepositorioException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
