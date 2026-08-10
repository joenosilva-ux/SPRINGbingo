package br.com.bingo.model;

import java.util.List;

public final class PaginaBingo {

    private final int numero;
    private final List<ItemCartelaImpressao> itens;

    public PaginaBingo(int numero, List<ItemCartelaImpressao> itens) {
        if (numero < 1) {
            throw new IllegalArgumentException("O número da página deve ser positivo");
        }
        if (itens == null || itens.isEmpty() || itens.size() > 6) {
            throw new IllegalArgumentException("Uma página deve conter entre 1 e 6 cartelas");
        }
        this.numero = numero;
        this.itens = List.copyOf(itens);
    }

    public int getNumero() { return numero; }
    public List<ItemCartelaImpressao> getItens() { return itens; }
}
