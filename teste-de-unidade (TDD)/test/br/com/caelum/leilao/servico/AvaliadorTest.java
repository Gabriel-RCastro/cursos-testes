package br.com.caelum.leilao.servico;

import br.com.caelum.leilao.builder.CriadorDeLeilao;
import br.com.caelum.leilao.dominio.Lance;
import br.com.caelum.leilao.dominio.Leilao;
import br.com.caelum.leilao.dominio.Usuario;
import org.junit.*;

import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

public class AvaliadorTest {

    private Avaliador leiloeiro;
    private Usuario joao, maria, jose;

    @Before
    public void setUp() {
        this.leiloeiro = new Avaliador();
        System.out.println("inicializando teste!");
        this.joao = new Usuario("João");
        this.jose = new Usuario("José");
        this.maria = new Usuario("Maria");
    }

    @After
    public void finaliza() {
        System.out.println("fim");
    }

    @BeforeClass
    public static void testandoBeforeClass() {
        System.out.println("before class");
    }

    @AfterClass
    public static void testandoAfterClass() {
        System.out.println("after class");
    }

    @Test(expected = RuntimeException.class)
    public void naoDeveAvaliarLeiloesSemNenhumLanceDado() {
        Leilao leilao = new CriadorDeLeilao().para("Playstation 3 Novo").constroi();

        leiloeiro.avalia(leilao);
    }

    @Test
    public void deveEntenderLancesEmOrdemCrescente() {
        // Parte 1: cenario
        Leilao leilao = new CriadorDeLeilao().para("Playstation 3 Novo")
                .lance(joao, 250)
                .lance(jose, 300)
                .lance(maria, 400)
                .constroi();

        // Parte 2: acao
        leiloeiro.avalia(leilao);

        // Parte 3: validacao
        assertThat(leiloeiro.getMaiorLance(), equalTo(400.0));
        assertThat(leiloeiro.getMenorLance(), equalTo(250.0));
    }

    @Test
    public void deveEntenderLeilaoComApenasUmLance() {
        Leilao leilao = new CriadorDeLeilao().para("Playstation 3 Novo")
                .lance(joao, 1000.0)
                .constroi();

        leiloeiro.avalia(leilao);

        assertThat(leiloeiro.getMaiorLance(), equalTo(1000.0));
        assertThat(leiloeiro.getMenorLance(), equalTo(1000.0));
    }

    @Test
    public void deveCalcularAMedia() {
        // cenario: 3 lances em ordem crescente
        Leilao leilao = new CriadorDeLeilao().para("Playstation 3 Novo")
                .lance(maria, 300.0)
                .lance(joao, 400.0)
                .lance(jose, 500.0)
                .constroi();

        // executando a acao
        leiloeiro.avalia(leilao);

        // comparando a saida com o esperado
        double mediaEsperado = (300.0 + 400.0 + 500.0) / 3;

        assertThat(leiloeiro.getMediaLance(), equalTo(mediaEsperado));
    }

    /* @Test
    public void testaMediaDeZeroLance() {
        // cenario
        Leilao leilao = new CriadorDeLeilao().para("Iphone 7").constroi();

        // acao
        Avaliador avaliador = new Avaliador();
        avaliador.avalia(leilao);

        //validacao
        assertEquals(0, avaliador.getMediaLance(), 0.0001);
    } */

    @Test
    public void deveEntenderLancesEmOrdemAleatoria() {
        // Parte 1: cenario
        Leilao leilao = new CriadorDeLeilao().para("Playstation 3 Novo")
                .lance(joao, 200.0)
                .lance(jose, 450.0)
                .lance(maria, 120.0)
                .lance(joao, 700.0)
                .lance(jose, 630.0)
                .lance(maria, 230.0)
                .constroi();

        // Parte 2: acao
        leiloeiro.avalia(leilao);

        // Parte 3: validacao
        assertThat(leiloeiro.getMaiorLance(), equalTo(700.0));
        assertThat(leiloeiro.getMenorLance(), equalTo(120.0));
    }

    @Test
    public void deveEntenderLancesEmOrdemDecrescente() {
        // Parte 1: cenario
        Leilao leilao = new CriadorDeLeilao().para("Playstation 3 Novo")
                .lance(joao, 400.0)
                .lance(jose, 300.0)
                .lance(maria, 200.0)
                .lance(joao, 100.0)
                .constroi();

        // Parte 2: acao
        leiloeiro.avalia(leilao);

        // Parte 3: validacao
        assertThat(leiloeiro.getMaiorLance(), equalTo(400.0));
        assertThat(leiloeiro.getMenorLance(), equalTo(100.0));
    }

    @Test
    public void deveEncontrarosTresMaioresLances() {
        // cenario
        Leilao leilao = new CriadorDeLeilao().para("Playstation 3 Novo")
                .lance(joao, 100.0)
                .lance(maria, 200.0)
                .lance(joao, 300.0)
                .lance(maria, 400.0)
                .lance(joao, 150.0)
                .constroi();

        // executando a acao
        leiloeiro.avalia(leilao);

        List<Lance> maiores = leiloeiro.getTresMaiores();
        assertEquals(3, maiores.size());

        assertThat(maiores, hasItems(
                new Lance(maria, 400),
                new Lance(joao, 300),
                new Lance(maria, 200)
        ));
    }

    @Test
    public void deveDevolverTodosLancesCasoNaoHajaNoMinimo3() {
        // cenario
        Leilao leilao = new CriadorDeLeilao().para("Playstation 3 Novo")
                .lance(joao, 100.0)
                .lance(maria, 200.0)
                .constroi();

        // executando a acao
        leiloeiro.avalia(leilao);

        List<Lance> maiores = leiloeiro.getTresMaiores();
        assertEquals(2, maiores.size());

        assertEquals(200.0, maiores.get(0).getValor(), 0.00001);
        assertEquals(100.0, maiores.get(1).getValor(), 0.00001);

        assertThat(maiores, hasItems(
                new Lance(maria, 200.0),
                new Lance(joao, 100.0)
        ));
    }

    /* @Test
    public void deveDevolverListaVaziaCasoNaoHajaLances() {
        // cenario
        Leilao leilao = new CriadorDeLeilao().para("Playstation 3 Novo").constroi();

        // executando a acao
        leiloeiro.avalia(leilao);

        List<Lance> maiores = leiloeiro.getTresMaiores();
        assertEquals(0, maiores.size());
    } */
}
