package service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import exception.RegraNegocioException;
import java.math.BigDecimal;
import org.junit.Before;
import org.junit.Test;

/** Testes unitários dos cálculos de precificação (RN: preço deve ser maior que o custo). */
public class CalculadoraPrecoTest {

    private CalculadoraPreco calc;

    @Before
    public void preparar() {
        calc = new CalculadoraPreco();
    }

    private static BigDecimal bd(String valor) {
        return new BigDecimal(valor);
    }

    // ---- calcularPrecoVenda
    @Test
    public void precoVenda_comMargemDe50PorCento() {
        assertEquals(bd("15.00"), calc.calcularPrecoVenda(bd("10.00"), bd("50")));
    }

    @Test
    public void precoVenda_comMargemZeroMantemOCusto() {
        assertEquals(bd("10.00"), calc.calcularPrecoVenda(bd("10.00"), BigDecimal.ZERO));
    }

    @Test
    public void precoVenda_arredondaParaDuasCasasHalfUp() {
        // 3.33 * 1.15 = 3.8295 -> 3.83
        assertEquals(bd("3.83"), calc.calcularPrecoVenda(bd("3.33"), bd("15")));
    }

    @Test
    public void precoVenda_rejeitaCustoZeroOuNegativo() {
        assertRegra(() -> calc.calcularPrecoVenda(BigDecimal.ZERO, bd("10")));
        assertRegra(() -> calc.calcularPrecoVenda(bd("-1"), bd("10")));
    }

    @Test
    public void precoVenda_rejeitaMargemNegativaOuNula() {
        assertRegra(() -> calc.calcularPrecoVenda(bd("10"), bd("-5")));
        assertRegra(() -> calc.calcularPrecoVenda(bd("10"), null));
    }

    // ---- calcularMargemPercentual / lucro
    @Test
    public void margem_de50PorCentoQuandoPrecoE15ECustoE10() {
        assertEquals(bd("50.00"), calc.calcularMargemPercentual(bd("10"), bd("15")));
    }

    @Test
    public void margem_negativaQuandoPrecoMenorQueCusto() {
        assertEquals(bd("-20.00"), calc.calcularMargemPercentual(bd("10"), bd("8")));
    }

    @Test
    public void lucroUnitario_eDiferencaEntrePrecoECusto() {
        assertEquals(bd("5.50"), calc.calcularLucroUnitario(bd("10.00"), bd("15.50")));
    }

    // ---- precoCobreCusto (regra "preço maior que o custo")
    @Test
    public void precoCobreCusto_verdadeiroQuandoPrecoMaior() {
        assertTrue(calc.precoCobreCusto(bd("10.00"), bd("10.01")));
    }

    @Test
    public void precoCobreCusto_falsoQuandoPrecoIgualOuMenor() {
        assertFalse(calc.precoCobreCusto(bd("10.00"), bd("10.00")));
        assertFalse(calc.precoCobreCusto(bd("10.00"), bd("9.99")));
    }

    @Test
    public void precoCobreCusto_ignoraDiferencaDeEscala() {
        assertFalse(calc.precoCobreCusto(bd("10"), bd("10.00")));
    }

    // ---- valorTotalEstoque
    @Test
    public void valorTotalEstoque_multiplicaPrecoPelaQuantidade() {
        assertEquals(bd("48.00"), calc.valorTotalEstoque(bd("16.00"), 3));
    }

    @Test
    public void valorTotalEstoque_zeroUnidadesResultaEmZero() {
        assertEquals(bd("0.00"), calc.valorTotalEstoque(bd("16.00"), 0));
    }

    @Test
    public void valorTotalEstoque_rejeitaQuantidadeNegativa() {
        assertRegra(() -> calc.valorTotalEstoque(bd("16.00"), -1));
    }

    private static void assertRegra(Runnable acao) {
        try {
            acao.run();
            fail("Era esperada uma RegraNegocioException");
        } catch (RegraNegocioException esperado) {
            // ok
        }
    }
}
