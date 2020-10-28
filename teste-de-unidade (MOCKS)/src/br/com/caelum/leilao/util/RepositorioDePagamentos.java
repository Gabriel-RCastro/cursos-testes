package br.com.caelum.leilao.util;

import br.com.caelum.leilao.dominio.Pagamento;

public interface RepositorioDePagamentos {

    void salva(Pagamento pagamento);
}
