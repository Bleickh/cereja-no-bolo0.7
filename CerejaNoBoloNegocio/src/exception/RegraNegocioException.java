package exception;

/**
 * Violação de uma regra de negócio (ex.: estoque negativo, preço inválido).
 * É não verificada para não poluir as assinaturas; a camada de apresentação
 * (Swing, hoje, ou controller web, amanhã) decide como exibir a mensagem.
 */
public class RegraNegocioException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public RegraNegocioException(String mensagem) {
        super(mensagem);
    }
}
