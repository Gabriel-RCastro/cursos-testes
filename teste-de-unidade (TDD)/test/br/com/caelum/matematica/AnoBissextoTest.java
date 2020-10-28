package br.com.caelum.matematica;

import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public class AnoBissextoTest {

    @Test
    public void deveRetornarAnoBissexto() {
        AnoBissexto ano = new AnoBissexto();
        assertTrue(ano.ehBissexto(2016));
        assertTrue(ano.ehBissexto(2020));
    }

    @Test
    public void naoDeveRetornarAnoBissexto() {
        AnoBissexto ano = new AnoBissexto();
        assertFalse(ano.ehBissexto(2015));
        assertFalse(ano.ehBissexto(2022));
    }
 }
