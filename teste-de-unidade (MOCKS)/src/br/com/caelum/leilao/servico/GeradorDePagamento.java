package br.com.caelum.leilao.servico;

import br.com.caelum.leilao.dominio.Leilao;
import br.com.caelum.leilao.dominio.Pagamento;
import br.com.caelum.leilao.util.Relogio;
import br.com.caelum.leilao.util.RepositorioDeLeiloes;
import br.com.caelum.leilao.util.RepositorioDePagamentos;

import java.util.Calendar;
import java.util.List;

public class GeradorDePagamento {

    private final RepositorioDeLeiloes leiloes;
    private final Avaliador avaliador;
    private final RepositorioDePagamentos pagamentos;
    private final Relogio relogio;

    public GeradorDePagamento(RepositorioDeLeiloes leiloes, Avaliador avaliador, RepositorioDePagamentos pagamentos, Relogio relogio) {
        this.leiloes = leiloes;
        this.avaliador = avaliador;
        this.pagamentos = pagamentos;
        this.relogio = relogio;
    }

    public GeradorDePagamento(RepositorioDeLeiloes leiloes, Avaliador avaliador, RepositorioDePagamentos pagamentos) {
        this(leiloes, avaliador, pagamentos, new RelogioDoSistema());
    }

    public void gera() {
        List<Leilao> leiloesEncerrados = this.leiloes.encerrados();

        for (Leilao leilao : leiloesEncerrados) {
            this.avaliador.avalia(leilao);

            Pagamento novoPagamento = new Pagamento(avaliador.getMaiorLance(), primeiroDiaUtil());
            this.pagamentos.salva(novoPagamento);
        }
    }

    private Calendar primeiroDiaUtil() {
        Calendar data = relogio.hoje();
        int diaDaSemana = data.get(Calendar.DAY_OF_WEEK);

        if (diaDaSemana == Calendar.SATURDAY) {
            data.add(Calendar.DAY_OF_MONTH, 2);
        } else if (diaDaSemana == Calendar.SUNDAY) {
            data.add(Calendar.DAY_OF_MONTH, 1);
        }
        return data;
    }
}
