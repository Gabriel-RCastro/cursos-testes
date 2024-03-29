package br.com.caelum.leilao.dominio;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Leilao {

    private final String descricao;
    private final List<Lance> lances;

    public Leilao(String descricao) {
        this.descricao = descricao;
        this.lances = new ArrayList<Lance>();
    }

    public void propoe(Lance lance) {
        if (lances.isEmpty() || podeDarLance(lance.getUsuario())) {
            lances.add(lance);
        }
    }

    public void dobraLance(Usuario usuario) {
        Lance ultimoLance = ultimoLanceDo(usuario);

        if (ultimoLance != null) {
            propoe(new Lance(usuario, ultimoLance.getValor() * 2));
        }
    }

    private boolean podeDarLance(Usuario usuario) {
        return !ultimoLanceDado().getUsuario().equals(usuario) && quantidadeDeLancesDo(usuario) < 5;
    }

    private Lance ultimoLanceDado() {
        return lances.get(lances.size() - 1);
    }

    private Lance ultimoLanceDo(Usuario usuario) {
        Lance ultimo = null;
        for (Lance lance : lances) {
            if (lance.getUsuario().equals(usuario)) ultimo = lance;
        }

        return ultimo;
    }

    private int quantidadeDeLancesDo(Usuario usuario) {
        int total = 0;

        for (Lance l : lances) {
            if (l.getUsuario().equals(usuario)) {
                total++;
            }
        }
        return total;
    }

    public String getDescricao() {
        return descricao;
    }

    public List<Lance> getLances() {
        return Collections.unmodifiableList(lances);
    }
}
