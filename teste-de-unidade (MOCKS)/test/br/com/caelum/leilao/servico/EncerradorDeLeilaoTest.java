package br.com.caelum.leilao.servico;

import br.com.caelum.leilao.builder.CriadorDeLeilao;
import br.com.caelum.leilao.dominio.Leilao;
import br.com.caelum.leilao.util.EnviadorDeEmail;
import br.com.caelum.leilao.util.RepositorioDeLeiloes;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class EncerradorDeLeilaoTest {
    Calendar data;
    Leilao leilao1;
    Leilao leilao2;
    RepositorioDeLeiloes daoFalso;
    EnviadorDeEmail carteiroFalso;
    EncerradorDeLeilao encerrador;

    @Before
    public void setUp() {
        this.data = Calendar.getInstance();

        this.leilao1 = new CriadorDeLeilao().para("TV de plasma").naData(data).constroi();
        this.leilao2 = new CriadorDeLeilao().para("Geladeira").naData(data).constroi();

        this.daoFalso = mock(RepositorioDeLeiloes.class);
        this.carteiroFalso = mock(EnviadorDeEmail.class);

        this.encerrador = new EncerradorDeLeilao(daoFalso, carteiroFalso);
    }

    @Test
    public void deveEncerrarLeiloesQueComecaramUmaSemanaAntes() {
        data.set(1999, Calendar.FEBRUARY, 20);

        when(daoFalso.correntes()).thenReturn(Arrays.asList(leilao1, leilao2));

        encerrador.encerra();

        assertThat(encerrador.getTotalEncerrados(), equalTo(2));
        assertTrue(leilao1.isEncerrado());
        assertTrue(leilao2.isEncerrado());
    }

    @Test
    public void naoDeveEncerrarLeiloesQueComecaramOntem() {
        data.set(2020, Calendar.OCTOBER, 25);

        when(daoFalso.correntes()).thenReturn(Arrays.asList(leilao1, leilao2));

        encerrador.encerra();

        assertThat(encerrador.getTotalEncerrados(), equalTo(0));
        assertFalse(leilao1.isEncerrado());
        assertFalse(leilao2.isEncerrado());
    }

    @Test
    public void naoDeveFazerNadaCasoNaoHajaNenhumLeilao() {
        when(daoFalso.correntes()).thenReturn(new ArrayList<>());

        encerrador.encerra();

        assertThat(encerrador.getTotalEncerrados(), equalTo(0));
    }

    @Test
    public void deveAtualizarLeiloesEncerrados() {
        data.set(1999, Calendar.FEBRUARY, 20);

        when(daoFalso.correntes()).thenReturn(Collections.singletonList(leilao1));

        encerrador.encerra();

        verify(daoFalso, times(1)).atualiza(leilao1);
    }

    @Test
    public void naoDeveEncerrarLeiloesQueComecaramMenosDeUmaSemanaAtras() {
        data.add(Calendar.DAY_OF_MONTH, -1);

        when(daoFalso.correntes()).thenReturn(Arrays.asList(leilao1, leilao2));

        encerrador.encerra();

        assertEquals(0, encerrador.getTotalEncerrados());
        assertFalse(leilao1.isEncerrado());
        assertFalse(leilao2.isEncerrado());

        verify(daoFalso, never()).atualiza(leilao1);
        verify(daoFalso, never()).atualiza(leilao2);
    }

    @Test
    public void deveEnviarEmailAposLeilaoSerEncerrado() {
        data.set(1999, Calendar.FEBRUARY, 20);

        when(daoFalso.correntes()).thenReturn(Collections.singletonList(leilao1));

        encerrador.encerra();

        // É passado os mocks que serão verificados
        InOrder inOrder = inOrder(daoFalso, carteiroFalso);

        // Primeira chamada
        inOrder.verify(daoFalso, times(1)).atualiza(leilao1);

        // Segunda chamada
        inOrder.verify(carteiroFalso, times(1)).envia(leilao1);
    }

    @Test
    public void deveContinuarExecucaoMesmoQuandoDaoFalha() {
        data.set(1999, Calendar.FEBRUARY, 20);

        when(daoFalso.correntes()).thenReturn(Arrays.asList(leilao1, leilao2));
        doThrow(new RuntimeException()).when(daoFalso).atualiza(leilao1);

        encerrador.encerra();

        verify(daoFalso).atualiza(leilao2);
        verify(carteiroFalso).envia(leilao2);

        verify(carteiroFalso, times(0)).envia(leilao1);
    }

    @Test
    public void deveDesistirSeDaoFalhaPraSempre() {
        data.set(1999, Calendar.FEBRUARY, 20);

        when(daoFalso.correntes()).thenReturn(Arrays.asList(leilao1, leilao2));
        doThrow(new RuntimeException()).when(daoFalso).atualiza(any(Leilao.class));

        encerrador.encerra();

        verify(carteiroFalso, never()).envia(any(Leilao.class));
    }
}
