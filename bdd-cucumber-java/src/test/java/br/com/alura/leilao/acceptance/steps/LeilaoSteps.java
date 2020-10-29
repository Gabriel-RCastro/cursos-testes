package br.com.alura.leilao.acceptance.steps;

import br.com.alura.leilao.e2e.pages.Browser;
import br.com.alura.leilao.e2e.pages.LeiloesPage;
import br.com.alura.leilao.e2e.pages.LoginPage;
import br.com.alura.leilao.e2e.pages.NovoLeilaoPage;
import org.junit.Assert;

public class LeilaoSteps implements io.cucumber.java8.Pt {

    private LoginPage loginPage;
    private LeiloesPage leiloesPage;
    private NovoLeilaoPage novoLeilaoPage;
    private Browser browser;

    public LeilaoSteps() {
        Dado("um usuario logado", () -> {
            this.browser = new Browser();
            browser.seed();
            loginPage = browser.getLoginPage();
            leiloesPage = loginPage.realizaLoginComoFulano();
        });

        Quando("acessa a pagina de novo leilao", () -> {
            novoLeilaoPage = this.leiloesPage.visitaPaginaParaCriarUmNovoLeilao();
        });

        Quando("prenche o formulario com dados validos", () -> {
            this.leiloesPage = this.novoLeilaoPage.preencheForm("PC Novo", "1500", "01/11/2020");
        });

        Entao("volta para a pagina de leiloes", () -> {
            Assert.assertTrue(this.leiloesPage.estaNaPaginaDeLeiloes());
        });

        Entao("o novo leilao aparece na tabela", () -> {
            this.leiloesPage.existe("PC Novo", "1500", "01/11/2020", "fulano");
            this.browser.clean();
        });
    }
}
