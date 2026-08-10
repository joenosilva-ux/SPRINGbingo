package br.com.bingo.service;

import br.com.bingo.model.Cartela;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BingoServiceTest {

    private static final int QUANTIDADE_DE_GERACOES = 500;

    private final BingoService service = new BingoService(new Random(20260804L));

    @Test
    void deveGerarCincoPosicoesEmCadaColuna() {
        repetirParaMuitasCartelas(cartela -> assertAll(
                () -> assertEquals(5, cartela.getB().size()),
                () -> assertEquals(5, cartela.getI().size()),
                () -> assertEquals(5, cartela.getN().size()),
                () -> assertEquals(5, cartela.getG().size()),
                () -> assertEquals(5, cartela.getO().size())
        ));
    }

    @Test
    void deveRespeitarAsFaixasTradicionais() {
        repetirParaMuitasCartelas(cartela -> assertAll(
                () -> assertValoresNaFaixa(cartela.getB(), 1, 15),
                () -> assertValoresNaFaixa(cartela.getI(), 16, 30),
                () -> assertValoresNaFaixa(numerosJogaveis(cartela.getN()), 31, 45),
                () -> assertValoresNaFaixa(cartela.getG(), 46, 60),
                () -> assertValoresNaFaixa(cartela.getO(), 61, 75)
        ));
    }

    @Test
    void naoDeveRepetirNumerosDentroDaMesmaColuna() {
        repetirParaMuitasCartelas(cartela -> assertAll(
                () -> assertSemRepeticao(cartela.getB()),
                () -> assertSemRepeticao(cartela.getI()),
                () -> assertSemRepeticao(numerosJogaveis(cartela.getN())),
                () -> assertSemRepeticao(cartela.getG()),
                () -> assertSemRepeticao(cartela.getO())
        ));
    }

    @Test
    void deveTerCentroLivreEExatamenteVinteEQuatroNumerosJogaveis() {
        repetirParaMuitasCartelas(cartela -> {
            long total = todasAsPosicoes(cartela).stream().filter(numero -> numero != null).count();

            assertAll(
                    () -> assertNull(cartela.getN().get(2)),
                    () -> assertEquals(24, total)
            );
        });
    }

    @Test
    void deveManterAsColunasVisualmenteOrdenadas() {
        repetirParaMuitasCartelas(cartela -> assertAll(
                () -> assertOrdenada(cartela.getB()),
                () -> assertOrdenada(cartela.getI()),
                () -> assertOrdenada(numerosJogaveis(cartela.getN())),
                () -> assertOrdenada(cartela.getG()),
                () -> assertOrdenada(cartela.getO())
        ));
    }

    @Test
    void mesmaSementeDevePermitirTesteDeterministico() {
        Cartela primeira = new BingoService(new Random(1234L)).gerarCartela();
        Cartela segunda = new BingoService(new Random(1234L)).gerarCartela();

        assertAll(
                () -> assertEquals(primeira.getB(), segunda.getB()),
                () -> assertEquals(primeira.getI(), segunda.getI()),
                () -> assertEquals(primeira.getN(), segunda.getN()),
                () -> assertEquals(primeira.getG(), segunda.getG()),
                () -> assertEquals(primeira.getO(), segunda.getO())
        );
    }

    @Test
    void deveGerarLoteSemCartelasRepetidas() {
        List<Cartela> cartelas = service.gerarCartelasDiferentes(200);

        assertEquals(200, cartelas.size());
        assertEquals(200, new HashSet<>(cartelas).size());
    }

    @Test
    void deveRejeitarTamanhoDeLoteInvalido() {
        assertThrows(IllegalArgumentException.class, () -> service.gerarCartelasDiferentes(0));
        assertThrows(IllegalArgumentException.class, () -> service.gerarCartelasDiferentes(201));
    }

    private void repetirParaMuitasCartelas(java.util.function.Consumer<Cartela> verificacao) {
        for (int geracao = 0; geracao < QUANTIDADE_DE_GERACOES; geracao++) {
            verificacao.accept(service.gerarCartela());
        }
    }

    private void assertValoresNaFaixa(List<Integer> numeros, int minimo, int maximo) {
        assertTrue(numeros.stream().allMatch(numero -> numero >= minimo && numero <= maximo));
    }

    private void assertSemRepeticao(List<Integer> numeros) {
        assertEquals(numeros.size(), new HashSet<>(numeros).size());
    }

    private void assertOrdenada(List<Integer> numeros) {
        List<Integer> copiaOrdenada = numeros.stream().sorted().toList();
        assertEquals(copiaOrdenada, numeros);
    }

    private List<Integer> numerosJogaveis(List<Integer> coluna) {
        return coluna.stream().filter(numero -> numero != null).toList();
    }

    private List<Integer> todasAsPosicoes(Cartela cartela) {
        List<Integer> posicoes = new ArrayList<>(25);
        posicoes.addAll(cartela.getB());
        posicoes.addAll(cartela.getI());
        posicoes.addAll(cartela.getN());
        posicoes.addAll(cartela.getG());
        posicoes.addAll(cartela.getO());
        return posicoes;
    }
}
