package service;

import exception.RegraNegocioException;
import java.math.BigDecimal;

/** Validações reutilizadas pelos serviços (evita código duplicado nas regras). */
final class Validador {

    private Validador() {
    }

    static String textoObrigatorio(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new RegraNegocioException(campo + " é obrigatório(a).");
        }
        return valor.trim();
    }

    static void maiorQueZero(BigDecimal valor, String campo) {
        if (valor == null || valor.signum() <= 0) {
            throw new RegraNegocioException(campo + " deve ser maior que zero.");
        }
    }

    static void maiorQueZero(int valor, String campo) {
        if (valor <= 0) {
            throw new RegraNegocioException(campo + " deve ser maior que zero.");
        }
    }
}
