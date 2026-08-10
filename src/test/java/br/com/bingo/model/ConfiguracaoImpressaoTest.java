package br.com.bingo.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ConfiguracaoImpressaoTest {

    @Test
    void variosPremiosDeveUsarUmaFolhaPorCartela() {
        ConfiguracaoImpressao configuracao = ConfiguracaoImpressao.variosPremios(50, 6);

        assertEquals(50, configuracao.calcularTotalFolhas());
        assertEquals(1, configuracao.cartelasPorPagina());
    }

    @Test
    void jogadoresDeveCalcularAUltimaFolhaIncompleta() {
        ConfiguracaoImpressao configuracao = ConfiguracaoImpressao.paraJogadores(50, 6);

        assertEquals(9, configuracao.calcularTotalFolhas());
        assertEquals(0, configuracao.quantidadePremios());
    }

    @Test
    void deveRejeitarQuantidadesInvalidas() {
        assertThrows(IllegalArgumentException.class,
                () -> ConfiguracaoImpressao.variosPremios(50, 7));
        assertThrows(IllegalArgumentException.class,
                () -> ConfiguracaoImpressao.paraJogadores(50, 3));
        assertThrows(IllegalArgumentException.class,
                () -> ConfiguracaoImpressao.variosPremios(201, 2));
    }
}
