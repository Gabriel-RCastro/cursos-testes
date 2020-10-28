package br.com.caelum.pm73.dao;

import br.com.caelum.pm73.dominio.Usuario;
import org.hibernate.Session;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class UsuarioDaoTest {
    Session session;
    UsuarioDao usuarioDao;

    @Before
    public void antes() {
        this.session = new CriadorDeSessao().getSession();
        this.usuarioDao = new UsuarioDao(session);

        session.beginTransaction();
    }

    @After
    public void depois() {
        session.getTransaction().rollback();
        session.close();
    }

    @Test
    public void deveEncontrarPeloNomeEEmailMockado() {
        Usuario novoUsuario = new Usuario("João da Silva", "joao@email.com");
        usuarioDao.salvar(novoUsuario);

        Usuario usuario = usuarioDao.porNomeEEmail("João da Silva", "joao@email.com");

        assertEquals("João da Silva", usuario.getNome());
        assertEquals("joao@email.com", usuario.getEmail());
    }

    @Test
    public void deveRetornarNuloSeNaoEncontrarUsuario() {
        Usuario usuario = usuarioDao.porNomeEEmail("João da Silva", "joao@email.com");

        assertNull(usuario);
    }

    @Test
    public void deveDeletarUmUsuario() {
        Usuario usuario = new Usuario("João da Silva", "joao@email.com");

        usuarioDao.salvar(usuario);

        session.flush();
        session.clear();

        usuarioDao.deletar(usuario);

        Usuario deletado = usuarioDao.porNomeEEmail("João da Silva", "joao@email.com");

        assertNull(deletado);
    }

    @Test
    public void deveAlterarUmUsuario() {
        Usuario usuario = new Usuario("João da Silva", "joao@email.com");

        usuarioDao.salvar(usuario);

        usuario.setNome("Pedro");
        usuario.setEmail("pedro@email.com");

        usuarioDao.atualizar(usuario);

        session.flush();
        session.clear();

        Usuario usuarioInexistente = usuarioDao.porNomeEEmail("João da Silva", "joao@email.com");
        Usuario usuarioAtualizado = usuarioDao.porNomeEEmail("Pedro", "pedro@email.com");

        assertNull(usuarioInexistente);
        assertNotNull(usuarioAtualizado);
    }
}
