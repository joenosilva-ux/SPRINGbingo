package br.com.bingo.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

/** Representa uma cartela tradicional de bingo, válida e imutável. */
public final class Cartela {

    private static final int TAMANHO_COLUNA = 5;

    private final List<Integer> b;
    private final List<Integer> i;
    private final List<Integer> n;
    private final List<Integer> g;
    private final List<Integer> o;

    public Cartela(List<Integer> b, List<Integer> i, List<Integer> n,
                   List<Integer> g, List<Integer> o) {
        this.b = copiarEValidarColuna(b, "B", 1, 15, false);
        this.i = copiarEValidarColuna(i, "I", 16, 30, false);
        this.n = copiarEValidarColuna(n, "N", 31, 45, true);
        this.g = copiarEValidarColuna(g, "G", 46, 60, false);
        this.o = copiarEValidarColuna(o, "O", 61, 75, false);

        if (this.n.get(2) != null) {
            throw new IllegalArgumentException("O centro da coluna N deve ser livre");
        }
    }

    private static List<Integer> copiarEValidarColuna(List<Integer> coluna, String nome,
                                                       int minimo, int maximo,
                                                       boolean permiteCentroLivre) {
        Objects.requireNonNull(coluna, "A coluna " + nome + " não pode ser nula");
        if (coluna.size() != TAMANHO_COLUNA) {
            throw new IllegalArgumentException("A coluna " + nome + " deve ter cinco posições");
        }

        List<Integer> copia = new ArrayList<>(coluna);
        for (int indice = 0; indice < copia.size(); indice++) {
            Integer numero = copia.get(indice);
            if (numero == null) {
                if (!permiteCentroLivre || indice != 2) {
                    throw new IllegalArgumentException("A coluna " + nome + " possui posição vazia inválida");
                }
                continue;
            }
            if (numero < minimo || numero > maximo) {
                throw new IllegalArgumentException(
                        "O número " + numero + " não pertence à faixa da coluna " + nome);
            }
        }

        List<Integer> jogaveis = copia.stream().filter(Objects::nonNull).toList();
        if (new HashSet<>(jogaveis).size() != jogaveis.size()) {
            throw new IllegalArgumentException("A coluna " + nome + " não pode repetir números");
        }

        return Collections.unmodifiableList(copia);
    }

    public List<Integer> getB() { return b; }
    public List<Integer> getI() { return i; }
    public List<Integer> getN() { return n; }
    public List<Integer> getG() { return g; }
    public List<Integer> getO() { return o; }

    @Override
    public boolean equals(Object outro) {
        if (this == outro) {
            return true;
        }
        if (!(outro instanceof Cartela cartela)) {
            return false;
        }
        return b.equals(cartela.b) && i.equals(cartela.i) && n.equals(cartela.n)
                && g.equals(cartela.g) && o.equals(cartela.o);
    }

    @Override
    public int hashCode() {
        return Objects.hash(b, i, n, g, o);
    }
}
