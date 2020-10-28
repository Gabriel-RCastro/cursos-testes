package br.com.caelum.leilao.servico;

import br.com.caelum.leilao.util.Relogio;

import java.util.Calendar;

public class RelogioDoSistema implements Relogio {
    @Override
    public Calendar hoje() {
        return Calendar.getInstance();
    }
}
