package br.com.caelum.leilao.util;

import br.com.caelum.leilao.dominio.Leilao;

import java.util.List;

public interface RepositorioDeLeiloes {
    void salva(Leilao leilao);

    List<Leilao> encerrados();

    List<Leilao> correntes();

    void atualiza(Leilao leilao);
}
