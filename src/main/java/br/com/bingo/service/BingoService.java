package br.com.bingo.service;

import br.com.bingo.model.Cartela;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.random.RandomGenerator;

/**
 * Service: concentra a regra de negócio de geração de uma cartela.
 * O Controller apenas solicita o resultado e não conhece a lógica do sorteio.
 */
@Service
public class BingoService {

    private final RandomGenerator random;

    public BingoService() {
        this(new SecureRandom());
    }

    // Visível dentro do pacote para que testes possam fornecer um gerador previsível.
    BingoService(RandomGenerator random) {
        this.random = random;
    }

    public Cartela gerarCartela() {
        List<Integer> b = gerarColuna(1, 15, 5);
        List<Integer> i = gerarColuna(16, 30, 5);
        List<Integer> n = montarColunaN(gerarColuna(31, 45, 4));
        List<Integer> g = gerarColuna(46, 60, 5);
        List<Integer> o = gerarColuna(61, 75, 5);

        return new Cartela(b, i, n, g, o);
    }

    public List<Cartela> gerarCartelasDiferentes(int quantidade) {
        if (quantidade < 1 || quantidade > 200) {
            throw new IllegalArgumentException("A quantidade de cartelas deve estar entre 1 e 200");
        }

        Set<Cartela> cartelas = new LinkedHashSet<>(quantidade);
        while (cartelas.size() < quantidade) {
            cartelas.add(gerarCartela());
        }
        return List.copyOf(cartelas);
    }

    private List<Integer> gerarColuna(int inicio, int fim, int quantidade) {
        int[] faixa = criarFaixaNumerica(inicio, fim);
        embaralharFaixa(faixa);
        return selecionarEOrdenar(faixa, quantidade);
    }

    private int[] criarFaixaNumerica(int inicio, int fim) {
        int quantidadeDeValores = fim - inicio + 1;
        int[] faixa = new int[quantidadeDeValores];

        for (int indice = 0; indice < faixa.length; indice++) {
            faixa[indice] = inicio + indice;
        }
        return faixa;
    }

    /**
     * Embaralha explicitamente com Fisher-Yates.
     *
     * <p>{@code faixa.length} é a quantidade de elementos, não um índice. Como
     * arrays começam no índice zero, cinco elementos ocupam os índices 0, 1, 2,
     * 3 e 4; por isso a última posição acessível é {@code length - 1}.</p>
     *
     * <p>Em cada passagem, {@code indiceAtual} indica a posição que será
     * preenchida. {@code nextInt(indiceAtual + 1)} usa um limite superior
     * exclusivo, logo produz um índice entre zero e {@code indiceAtual},
     * inclusive. A variável {@code auxiliar} preserva o valor que seria perdido
     * ao sobrescrever uma posição durante a troca.</p>
     *
     * <p>Índice é o número usado para acessar o array; posição é o lugar que ele
     * representa; quantidade é quantos itens existem; valor é o número guardado
     * naquele índice. Assim, o índice 0 (primeira posição) pode guardar o valor 15.</p>
     */
    private void embaralharFaixa(int[] faixa) {
        for (int indiceAtual = faixa.length - 1; indiceAtual > 0; indiceAtual--) {
            int indiceSorteado = random.nextInt(indiceAtual + 1);

            int auxiliar = faixa[indiceAtual];
            faixa[indiceAtual] = faixa[indiceSorteado];
            faixa[indiceSorteado] = auxiliar;
        }
    }

    private List<Integer> selecionarEOrdenar(int[] faixaEmbaralhada, int quantidade) {
        int[] selecionados = Arrays.copyOf(faixaEmbaralhada, quantidade);
        Arrays.sort(selecionados);
        return Arrays.stream(selecionados).boxed().toList();
    }

    private List<Integer> montarColunaN(List<Integer> numerosOrdenados) {
        List<Integer> colunaN = new ArrayList<>(5);
        colunaN.add(numerosOrdenados.get(0));
        colunaN.add(numerosOrdenados.get(1));
        colunaN.add(null); // null expressa ausência de número, sem número mágico.
        colunaN.add(numerosOrdenados.get(2));
        colunaN.add(numerosOrdenados.get(3));
        return colunaN;
    }
}
