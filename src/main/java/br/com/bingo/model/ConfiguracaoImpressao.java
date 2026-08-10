package br.com.bingo.model;

import java.util.Objects;
import java.util.Set;

public record ConfiguracaoImpressao(
        ModoImpressao modo,
        int quantidadeCartelas,
        int quantidadePremios,
        int cartelasPorPagina) {

    public static final int LIMITE_CARTELAS = 200;
    private static final Set<Integer> QUANTIDADES_POR_PAGINA = Set.of(2, 4, 6);

    public ConfiguracaoImpressao {
        Objects.requireNonNull(modo, "O modo de impressão é obrigatório");
        if (quantidadeCartelas < 1 || quantidadeCartelas > LIMITE_CARTELAS) {
            throw new IllegalArgumentException(
                    "A quantidade de cartelas deve estar entre 1 e " + LIMITE_CARTELAS);
        }

        if (modo == ModoImpressao.VARIOS_PREMIOS) {
            if (quantidadePremios < 1 || quantidadePremios > 6) {
                throw new IllegalArgumentException("A quantidade de prêmios deve estar entre 1 e 6");
            }
            cartelasPorPagina = 1;
        } else {
            if (!QUANTIDADES_POR_PAGINA.contains(cartelasPorPagina)) {
                throw new IllegalArgumentException("Escolha 2, 4 ou 6 cartelas por página");
            }
            quantidadePremios = 0;
        }
    }

    public static ConfiguracaoImpressao variosPremios(int quantidadeCartelas, int quantidadePremios) {
        return new ConfiguracaoImpressao(
                ModoImpressao.VARIOS_PREMIOS, quantidadeCartelas, quantidadePremios, 1);
    }

    public static ConfiguracaoImpressao paraJogadores(int quantidadeCartelas, int cartelasPorPagina) {
        return new ConfiguracaoImpressao(
                ModoImpressao.CARTELAS_PARA_JOGADORES, quantidadeCartelas, 0, cartelasPorPagina);
    }

    public int calcularTotalFolhas() {
        if (modo == ModoImpressao.VARIOS_PREMIOS) {
            return quantidadeCartelas;
        }
        return (quantidadeCartelas + cartelasPorPagina - 1) / cartelasPorPagina;
    }
}
