package br.com.caelum.leilao.dominio;

import org.junit.Test;

public class LanceTest {

    @Test(expected = IllegalArgumentException.class)
    public void naoDeveAceitarLancesComValorZeroOuNegativo() {
        new Lance(new Usuario("João"), 0);
        new Lance(new Usuario("Maria"), -10);
    }
}
