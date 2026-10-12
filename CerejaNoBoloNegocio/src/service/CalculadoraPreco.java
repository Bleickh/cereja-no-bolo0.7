package service;

import exception.RegraNegocioException;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Cálculos de precificação separados das telas e do banco (funções puras, sem
 * dependências), o que os torna ideais para testes unitários. Atende à regra
 * do documento da Etapa 1: o preço de venda deve ser maior que o custo.
 */
public class CalculadoraPreco {

    private static final BigDecimal CEM = new BigDecimal("100");

    /** Preço de venda = custo * (1 + margem/100), arredondado a 2 casas (HALF_UP). */
    public BigDecimal calcularPrecoVenda(BigDecimal custo, BigDecimal margemPercentual) {
        Validador.maiorQueZero(custo, "Custo");
        if (margemPercentual == null || margemPercentual.signum() < 0) {
            throw new RegraNegocioException("Margem de lucro não pode ser negativa.");
        }
        BigDecimal fator = BigDecimal.ONE.add(margemPercentual.divide(CEM));
        return custo.multiply(fator).setScale(2, RoundingMode.HALF_UP);
    }

    /** Margem sobre o custo, em %: (preço - custo) / custo * 100, com 2 casas. Pode ser negativa. */
    public BigDecimal calcularMargemPercentual(BigDecimal custo, BigDecimal preco) {
        Validador.maiorQueZero(custo, "Custo");
        Validador.maiorQueZero(preco, "Preço");
        return preco.subtract(custo).multiply(CEM).divide(custo, 2, RoundingMode.HALF_UP);
    }

    /** Lucro por unidade vendida (preço - custo). */
    public BigDecimal calcularLucroUnitario(BigDecimal custo, BigDecimal preco) {
        Validador.maiorQueZero(custo, "Custo");
        Validador.maiorQueZero(preco, "Preço");
        return preco.subtract(custo).setScale(2, RoundingMode.HALF_UP);
    }

    /** Regra "preço maior que o custo": igualdade NÃO é aceita. */
    public boolean precoCobreCusto(BigDecimal custo, BigDecimal preco) {
        Validador.maiorQueZero(custo, "Custo");
        Validador.maiorQueZero(preco, "Preço");
        return preco.compareTo(custo) > 0;
    }

    /** Valor em estoque = preço * quantidade. */
    public BigDecimal valorTotalEstoque(BigDecimal preco, int quantidade) {
        Validador.maiorQueZero(preco, "Preço");
        if (quantidade < 0) {
            throw new RegraNegocioException("Quantidade não pode ser negativa.");
        }
        return preco.multiply(BigDecimal.valueOf(quantidade)).setScale(2, RoundingMode.HALF_UP);
    }
}
