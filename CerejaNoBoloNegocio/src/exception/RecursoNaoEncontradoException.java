package exception;

/** Lançada quando um registro referenciado (produto, categoria...) não existe. */
public class RecursoNaoEncontradoException extends RegraNegocioException {

    private static final long serialVersionUID = 1L;

    public RecursoNaoEncontradoException(String recurso, int id) {
        super(recurso + " não encontrado(a) (id=" + id + ")");
    }
}
