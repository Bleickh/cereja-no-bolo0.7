package model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import exception.RegraNegocioException;
import org.junit.Test;

/** Testes unitários da regra de saldo de cada tipo de movimentação (RF04, RF05). */
public class TipoMovimentacaoTest {

    @Test
    public void entrada_somaAoEstoque() {
        assertEquals(15, TipoMovimentacao.ENTRADA.aplicar(10, 5));
    }

    @Test
    public void saida_subtraiDoEstoque() {
        assertEquals(6, TipoMovimentacao.SAIDA.aplicar(10, 4));
    }

    @Test
    public void saida_podeZerarOEstoque() {
        assertEquals(0, TipoMovimentacao.SAIDA.aplicar(10, 10));
    }

    @Test
    public void saida_naoPermiteEstoqueNegativo() {
        try {
            TipoMovimentacao.SAIDA.aplicar(10, 11);
            fail("Deveria lançar RegraNegocioException");
        } catch (RegraNegocioException esperado) {
            // ok
        }
    }

    @Test
    public void de_aceitaRotuloDaTelaENomeDoEnum() {
        assertEquals(TipoMovimentacao.SAIDA, TipoMovimentacao.de("Saída"));
        assertEquals(TipoMovimentacao.ENTRADA, TipoMovimentacao.de("entrada"));
    }

    @Test(expected = RegraNegocioException.class)
    public void de_rejeitaTextoDesconhecido() {
        TipoMovimentacao.de("Transferência");
    }
}
