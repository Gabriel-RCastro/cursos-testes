package br.com.caelum.pm73.dao;

import br.com.caelum.pm73.dominio.Leilao;
import br.com.caelum.pm73.dominio.Usuario;
import br.com.caelum.pm73.util.LeilaoBuilder;
import org.hibernate.Session;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Calendar;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class LeilaoDaoTest {
    private Session session;
    private UsuarioDao usuarioDao;
    private LeilaoDao leilaoDao;

    @Before
    public void antes() {
        this.session = new CriadorDeSessao().getSession();
        this.usuarioDao = new UsuarioDao(session);
        this.leilaoDao = new LeilaoDao(session);

        session.beginTransaction();
    }

    @After
    public void depois() {
        session.getTransaction().rollback();
        session.close();
    }

    @Test
    public void deveContarLeiloesNaoEncerrados() {
        Usuario mauricio = new Usuario("Mauricio", "mauricio@email.com");

        Leilao leilaoAtivo = new LeilaoBuilder()
                .comDono(mauricio)
                .constroi();

        Leilao leilaoEncerrado = new LeilaoBuilder()
                .comDono(mauricio)
                .encerrado()
                .constroi();

        usuarioDao.salvar(mauricio);
        leilaoDao.salvar(leilaoAtivo);
        leilaoDao.salvar(leilaoEncerrado);

        long total = leilaoDao.total();

        assertEquals(1L, total);
    }

    @Test
    public void deveRetornarZeroCasoNaoEncontreLeiloesEncerrados() {
        Usuario mauricio = new Usuario("Mauricio", "mauricio@email.com");

        Leilao leilaoEncerrado = new LeilaoBuilder()
                .comDono(mauricio)
                .encerrado()
                .constroi();

        Leilao leilaoEncerrado1 = new LeilaoBuilder()
                .comDono(mauricio)
                .encerrado()
                .constroi();

        usuarioDao.salvar(mauricio);
        leilaoDao.salvar(leilaoEncerrado);
        leilaoDao.salvar(leilaoEncerrado1);

        long total = leilaoDao.total();

        assertEquals(0L, total);
    }

    @Test
    public void deveRetornarApenasLeiloesDeProdutosNovos() {
        Usuario mauricio = new Usuario("Mauricio", "mauricio@email.com");

        Leilao leilao = new LeilaoBuilder()
                .comDono(mauricio)
                .usado()
                .constroi();

        Leilao leilao1 = new LeilaoBuilder()
                .comDono(mauricio)
                .constroi();

        usuarioDao.salvar(mauricio);
        leilaoDao.salvar(leilao);
        leilaoDao.salvar(leilao1);

        List<Leilao> leiloesNovos = leilaoDao.novos();

        assertEquals(1, leiloesNovos.size());
        assertEquals("Xbox", leiloesNovos.get(0).getNome());
    }

    @Test
    public void deveRetornarApenasLeiloesAntigos() {
        Usuario mauricio = new Usuario("Mauricio", "mauricio@email.com");

        Leilao antigo = new LeilaoBuilder()
                .comDono(mauricio)
                .comNome("Geladeira")
                .usado()
                .diasAtras(10)
                .constroi();

        Leilao recente = new LeilaoBuilder()
                .comDono(mauricio)
                .constroi();

        usuarioDao.salvar(mauricio);
        leilaoDao.salvar(antigo);
        leilaoDao.salvar(recente);

        List<Leilao> leiloesAntigos = leilaoDao.antigos();

        assertEquals(1, leiloesAntigos.size());
        assertEquals("Geladeira", leiloesAntigos.get(0).getNome());
    }

    @Test
    public void deveRetornarLeiloesCriadosHaMaisDeSeteDias() {
        Usuario mauricio = new Usuario("Mauricio", "mauricio@email.com");

        Leilao antigo = new LeilaoBuilder()
                .comDono(mauricio)
                .comNome("Geladeira")
                .usado()
                .diasAtras(7)
                .constroi();

        usuarioDao.salvar(mauricio);
        leilaoDao.salvar(antigo);

        List<Leilao> leiloesAntigos = leilaoDao.antigos();

        assertEquals(1, leiloesAntigos.size());
        assertEquals("Geladeira", leiloesAntigos.get(0).getNome());
    }

    @Test
    public void deveTrazerLeiloesNaoEncerradosNoPeriodo() {
        Calendar comecoDoIntervalo = Calendar.getInstance();
        comecoDoIntervalo.add(Calendar.DAY_OF_MONTH, -10);
        Calendar fimDoIntervalo = Calendar.getInstance();

        Usuario mauricio = new Usuario("Mauricio", "mauricio@email.com");

        Leilao leilao1 = new LeilaoBuilder()
                .comDono(mauricio)
                .diasAtras(20)
                .constroi();

        Leilao leilao2 = new LeilaoBuilder()
                .comDono(mauricio)
                .diasAtras(2)
                .constroi();

        usuarioDao.salvar(mauricio);
        leilaoDao.salvar(leilao1);
        leilaoDao.salvar(leilao2);

        List<Leilao> leiloes = leilaoDao.porPeriodo(comecoDoIntervalo, fimDoIntervalo);

        assertEquals(1, leiloes.size());
        assertEquals("Xbox", leiloes.get(0).getNome());
    }

    @Test
    public void naoDeveTrazerLeiloesEncerradosNoPeriodo() {
        Calendar comecoDoIntervalo = Calendar.getInstance();
        comecoDoIntervalo.add(Calendar.DAY_OF_MONTH, -10);
        Calendar fimDoIntervalo = Calendar.getInstance();

        Usuario mauricio = new Usuario("Mauricio", "mauricio@email.com");

        Leilao leilao1 = new LeilaoBuilder()
                .comDono(mauricio)
                .diasAtras(2)
                .encerrado()
                .constroi();

        usuarioDao.salvar(mauricio);
        leilaoDao.salvar(leilao1);

        List<Leilao> leiloes = leilaoDao.porPeriodo(comecoDoIntervalo, fimDoIntervalo);

        assertEquals(0, leiloes.size());
    }

    @Test
    public void deveRetornarLeiloesDisputados() {

        Usuario mauricio = new Usuario("Mauricio", "mauricio@email.com");
        Usuario pedro = new Usuario("Pedro", "pedro@email.com");

        Leilao leilao1 = new LeilaoBuilder()
                .comDono(pedro)
                .comValor(3000.0)
                .comLance(Calendar.getInstance(), mauricio, 3000)
                .comLance(Calendar.getInstance(), pedro, 3100)
                .constroi();

        Leilao leilao2 = new LeilaoBuilder()
                .comDono(mauricio)
                .comValor(3200.0)
                .comLance(Calendar.getInstance(), mauricio, 3000)
                .comLance(Calendar.getInstance(), pedro, 3100)
                .comLance(Calendar.getInstance(), mauricio, 3200)
                .comLance(Calendar.getInstance(), pedro, 3300)
                .comLance(Calendar.getInstance(), mauricio, 3400)
                .comLance(Calendar.getInstance(), pedro, 3500)
                .constroi();

        usuarioDao.salvar(mauricio);
        usuarioDao.salvar(pedro);
        leilaoDao.salvar(leilao1);
        leilaoDao.salvar(leilao2);

        List<Leilao> leiloes = leilaoDao.disputadosEntre(2500, 3500);

        assertEquals(1, leiloes.size());
        assertEquals(3200.0, leiloes.get(0).getValorInicial(), 0.00001);
    }

    @Test
    public void listaSomenteOsLeiloesDoUsuario() {
        Usuario dono = new Usuario("Dono", "dono@email.com");
        Usuario comprador1 = new Usuario("Comprador1", "comprador1@email.com");
        Usuario comprador2 = new Usuario("Comrprador2", "comprador2@email.com");

        Leilao leilao1 = new LeilaoBuilder()
                .comDono(dono)
                .comValor(3000.0)
                .constroi();

        Leilao leilao2 = new LeilaoBuilder()
                .comDono(dono)
                .comValor(3200.0)
                .comLance(Calendar.getInstance(), comprador1, 3300)
                .comLance(Calendar.getInstance(), comprador2, 3500)
                .constroi();

        usuarioDao.salvar(dono);
        usuarioDao.salvar(comprador1);
        usuarioDao.salvar(comprador2);
        leilaoDao.salvar(leilao1);
        leilaoDao.salvar(leilao2);

        List<Leilao> leiloes = leilaoDao.listaLeiloesDoUsuario(comprador1);

        assertEquals(1, leiloes.size());
        assertEquals(leilao2, leiloes.get(0));
    }

    @Test
    public void listaDeLeiloesDeUmUsuarioNaoTemRepeticao() {
        Usuario dono = new Usuario("Dono", "dono@email.com");
        Usuario comprador = new Usuario("Comprador", "comprador@email.com");

        Leilao leilao = new LeilaoBuilder()
                .comDono(dono)
                .comLance(Calendar.getInstance(), comprador, 3300)
                .comLance(Calendar.getInstance(), comprador, 3500)
                .constroi();

        usuarioDao.salvar(dono);
        usuarioDao.salvar(comprador);
        leilaoDao.salvar(leilao);

        List<Leilao> leiloes = leilaoDao.listaLeiloesDoUsuario(comprador);

        assertEquals(1, leiloes.size());
        assertEquals(leilao, leiloes.get(0));
    }

    @Test
    public void listaValorInicialMedioDoUsuario() {
        Usuario dono = new Usuario("Dono", "dono@email.com");
        Usuario comprador = new Usuario("Comprador", "comprador@email.com");

        Leilao leilao1 = new LeilaoBuilder()
                .comDono(dono)
                .comValor(3000)
                .comLance(Calendar.getInstance(), comprador, 3300)
                .comLance(Calendar.getInstance(), comprador, 3500)
                .constroi();

        Leilao leilao2 = new LeilaoBuilder()
                .comDono(dono)
                .comValor(1000)
                .comLance(Calendar.getInstance(), comprador, 2000)
                .constroi();

        usuarioDao.salvar(dono);
        usuarioDao.salvar(comprador);
        leilaoDao.salvar(leilao1);
        leilaoDao.salvar(leilao2);

        double valorMedio = leilaoDao.getValorInicialMedioDoUsuario(comprador);

        assertEquals(2000.0, valorMedio, 0.00001);
    }

    @Test
    public void deveDeletarUmLeilao() {
        Usuario usuario = new Usuario("Dono", "dono@email.com");

        Leilao leilao = new LeilaoBuilder()
                .comDono(usuario)
                .constroi();

        usuarioDao.salvar(usuario);
        leilaoDao.salvar(leilao);

        session.flush();
        session.clear();

        leilaoDao.deleta(leilao);

        assertNull(leilaoDao.porId(leilao.getId()));
    }
}
