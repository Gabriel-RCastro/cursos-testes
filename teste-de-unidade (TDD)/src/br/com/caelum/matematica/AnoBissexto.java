package br.com.caelum.matematica;

public class AnoBissexto {
    public boolean ehBissexto(int ano) {
        return (ano % 400 == 0) || ((ano % 4 == 0) && (ano % 100 != 0));
    }
}
