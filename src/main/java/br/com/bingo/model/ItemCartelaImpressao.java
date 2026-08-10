package br.com.bingo.model;

import java.util.Objects;

public final class ItemCartelaImpressao {

    private final Cartela cartela;
    private final String identificacao;
    private final String titulo;
    private final int numeroPremio;

    public ItemCartelaImpressao(Cartela cartela, String identificacao,
                                String titulo, int numeroPremio) {
        this.cartela = Objects.requireNonNull(cartela, "A cartela é obrigatória");
        this.identificacao = Objects.requireNonNull(identificacao, "A identificação é obrigatória");
        this.titulo = titulo;
        this.numeroPremio = numeroPremio;
    }

    public Cartela getCartela() { return cartela; }
    public String getIdentificacao() { return identificacao; }
    public String getTitulo() { return titulo; }
    public int getNumeroPremio() { return numeroPremio; }
}
