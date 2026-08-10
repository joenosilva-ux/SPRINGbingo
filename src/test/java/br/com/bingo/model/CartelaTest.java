package br.com.bingo.model;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CartelaTest {

    @Test
    void cartelasComOsMesmosNumerosDevemSerIguais() {
        Cartela primeira = cartelaValida();
        Cartela segunda = cartelaValida();

        assertEquals(primeira, segunda);
        assertEquals(primeira.hashCode(), segunda.hashCode());
    }

    @Test
    void deveDistinguirCartelasComNumerosDiferentes() {
        Cartela primeira = cartelaValida();
        Cartela segunda = new Cartela(
                List.of(2, 5, 8, 11, 14), primeira.getI(), primeira.getN(),
                primeira.getG(), primeira.getO());

        assertNotEquals(primeira, segunda);
    }

    @Test
    void deveProtegerAsColunasContraAlteracao() {
        List<Integer> colunaB = new ArrayList<>(List.of(1, 4, 7, 10, 13));
        Cartela cartela = new Cartela(colunaB, List.of(16, 19, 22, 25, 28),
                Arrays.asList(31, 34, null, 40, 43), List.of(46, 49, 52, 55, 58),
                List.of(61, 64, 67, 70, 73));

        colunaB.set(0, 2);

        assertEquals(1, cartela.getB().getFirst());
        assertThrows(UnsupportedOperationException.class, () -> cartela.getB().set(0, 3));
    }

    @Test
    void deveRejeitarFaixaRepeticaoECentroInvalidos() {
        Cartela base = cartelaValida();

        assertThrows(IllegalArgumentException.class, () -> new Cartela(
                List.of(1, 4, 7, 10, 16), base.getI(), base.getN(), base.getG(), base.getO()));
        assertThrows(IllegalArgumentException.class, () -> new Cartela(
                List.of(1, 4, 4, 10, 13), base.getI(), base.getN(), base.getG(), base.getO()));
        assertThrows(IllegalArgumentException.class, () -> new Cartela(
                base.getB(), base.getI(), List.of(31, 34, 37, 40, 43), base.getG(), base.getO()));
    }

    private Cartela cartelaValida() {
        return new Cartela(
                List.of(1, 4, 7, 10, 13),
                List.of(16, 19, 22, 25, 28),
                Arrays.asList(31, 34, null, 40, 43),
                List.of(46, 49, 52, 55, 58),
                List.of(61, 64, 67, 70, 73));
    }
}
