package br.com.caelum.leilao.servico;

import br.com.caelum.leilao.builder.CriadorDeLeilao;
import br.com.caelum.leilao.dominio.Leilao;
import br.com.caelum.leilao.dominio.Pagamento;
import br.com.caelum.leilao.dominio.Usuario;
import br.com.caelum.leilao.util.Relogio;
import br.com.caelum.leilao.util.RepositorioDeLeiloes;
import br.com.caelum.leilao.util.RepositorioDePagamentos;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.Calendar;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class GeradorDePagamentoTest {
    RepositorioDeLeiloes leiloes;
    RepositorioDePagamentos pagamentos;
    GeradorDePagamento gerador;
    Relogio relogio;

    @Before
    public void setUp() {
        this.leiloes = mock(RepositorioDeLeiloes.class);
        this.pagamentos = mock(RepositorioDePagamentos.class);
        this.relogio = mock(Relogio.class);
        this.gerador = new GeradorDePagamento(leiloes, new Avaliador(), pagamentos);
    }

    @Test
    public void deveGerarPagamentoParaUmLeilaoEncerrado() {
        Leilao leilao = new CriadorDeLeilao().para("Playstation")
                .lance(new Usuario("José da Silva"), 2000.0)
                .lance(new Usuario("Maria Pereira"), 2500.0)
                .constroi();

        when(leiloes.encerrados()).thenReturn(Collections.singletonList(leilao));

        gerador.gera();

        ArgumentCaptor<Pagamento> argumento = ArgumentCaptor.forClass(Pagamento.class);
        verify(pagamentos).salva(argumento.capture());

        Pagamento pagamentoGerado = argumento.getValue();

        assertEquals(2500.0, pagamentoGerado.getValor(), 0.00001);
    }

    @Test
    public void deveEmpurrarParaProximoDiaUtil() {
        Leilao leilao = new CriadorDeLeilao().para("Playstation")
                .lance(new Usuario("José da Silva"), 2000.0)
                .lance(new Usuario("Maria Pereira"), 2500.0)
                .constroi();

        when(leiloes.encerrados()).thenReturn(Collections.singletonList(leilao));

        /* Calendar sabado = Calendar.getInstance();
        sabado.set(2012, Calendar.APRIL, 7);

        when(relogio.hoje()).thenReturn(sabado); */

        Calendar domingo = Calendar.getInstance();
        domingo.set(2012, Calendar.APRIL, 8);

        when(relogio.hoje()).thenReturn(domingo);

        this.gerador = new GeradorDePagamento(leiloes, new Avaliador(), pagamentos, relogio);
        gerador.gera();

        ArgumentCaptor<Pagamento> argumento = ArgumentCaptor.forClass(Pagamento.class);
        verify(pagamentos).salva(argumento.capture());

        Pagamento pagamentoGerado = argumento.getValue();

        assertEquals(Calendar.MONDAY, pagamentoGerado.getData().get(Calendar.DAY_OF_WEEK));
        assertEquals(9, pagamentoGerado.getData().get(Calendar.DAY_OF_MONTH));
    }
}
