package model;

import exception.RegraNegocioException;

/**
 * Tipo de movimentação de estoque. Cada constante sabe aplicar a si mesma ao
 * saldo (polimorfismo), o que elimina if/else sobre String e permite novos
 * tipos (ex.: AJUSTE, PERDA) sem alterar o serviço (Aberto/Fechado).
 */
public enum TipoMovimentacao {

    ENTRADA("Entrada") {
        @Override
        public int aplicar(int estoqueAtual, int quantidade) {
            return estoqueAtual + quantidade;
        }
    },
    SAIDA("Saída") {
        @Override
        public int aplicar(int estoqueAtual, int quantidade) {
            int novoEstoque = estoqueAtual - quantidade;
            if (novoEstoque < 0) {
                throw new RegraNegocioException("Estoque insuficiente: disponível "
                        + estoqueAtual + ", solicitado " + quantidade);
            }
            return novoEstoque;
        }
    };

    private final String rotulo;

    TipoMovimentacao(String rotulo) {
        this.rotulo = rotulo;
    }

    /** Retorna o novo saldo após a movimentação ou lança RegraNegocioException. */
    public abstract int aplicar(int estoqueAtual, int quantidade);

    public String getRotulo() {
        return rotulo;
    }

    /** Converte o texto exibido nas telas ("Entrada"/"Saída") ou o nome da constante. */
    public static TipoMovimentacao de(String texto) {
        for (TipoMovimentacao tipo : values()) {
            if (tipo.rotulo.equalsIgnoreCase(texto) || tipo.name().equalsIgnoreCase(texto)) {
                return tipo;
            }
        }
        throw new RegraNegocioException("Tipo de movimentação inválido: " + texto);
    }

    @Override
    public String toString() {
        return rotulo;
    }
}
