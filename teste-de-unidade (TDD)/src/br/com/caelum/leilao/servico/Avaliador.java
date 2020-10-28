package br.com.caelum.leilao.servico;

import br.com.caelum.leilao.dominio.Lance;
import br.com.caelum.leilao.dominio.Leilao;

import java.util.ArrayList;
import java.util.List;

public class Avaliador {
    private double maiorLance = Double.NEGATIVE_INFINITY;
    private double menorLance = Double.POSITIVE_INFINITY;
    private double mediaLance;
    List<Lance> maiores;

    public void avalia(Leilao leilao) {
        if (leilao.getLances().size() == 0) {
            throw new RuntimeException("Não é possível avaliar um leilão sem lances!");
        }

        for (Lance lance : leilao.getLances()) {
            if (lance.getValor() > maiorLance) maiorLance = lance.getValor();
            if (lance.getValor() < menorLance) menorLance = lance.getValor();
        }

        calulaMediaLances(leilao);
        calculaTresMaioresLances(leilao);
    }

    private void calulaMediaLances(Leilao leilao) {
        double total = 0;

        for (Lance lance : leilao.getLances()) {
            total += lance.getValor();
        }

        if (total != 0) {
            mediaLance = total / leilao.getLances().size();
        }
    }

    private void calculaTresMaioresLances(Leilao leilao) {
        maiores = new ArrayList<>(leilao.getLances());
        maiores.sort((o1, o2) -> Double.compare(o2.getValor(), o1.getValor()));
        maiores = maiores.subList(0, Math.min(maiores.size(), 3));
    }

    public double getMaiorLance() {
        return maiorLance;
    }

    public double getMenorLance() {
        return menorLance;
    }

    public double getMediaLance() {
        return mediaLance;
    }

    public List<Lance> getTresMaiores() {
        return maiores;
    }
}
