package br.com.caelum.leilao.matchers;

import br.com.caelum.leilao.dominio.Lance;
import br.com.caelum.leilao.dominio.Leilao;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

public class LeilaoMatcher extends TypeSafeMatcher<Leilao> {

    private final Lance lance;

    public LeilaoMatcher(Lance lance) {
        this.lance = lance;
    }

    public static Matcher<Leilao> temUmLance(Lance lance) {
        return new LeilaoMatcher(lance);
    }

    @Override
    protected boolean matchesSafely(Leilao leilao) {
        return leilao.getLances().contains(this.lance);
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("leilao com lance de " + this.lance.getValor());
    }
}
