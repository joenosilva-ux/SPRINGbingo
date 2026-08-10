package br.com.bingo.service;

import br.com.bingo.model.Cartela;
import br.com.bingo.model.ConfiguracaoImpressao;
import br.com.bingo.model.ItemCartelaImpressao;
import br.com.bingo.model.LoteImpressao;
import br.com.bingo.model.ModoImpressao;
import br.com.bingo.model.PaginaBingo;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class LoteImpressaoService {

    private final BingoService bingoService;

    public LoteImpressaoService(BingoService bingoService) {
        this.bingoService = bingoService;
    }

    public LoteImpressao gerar(ConfiguracaoImpressao configuracao) {
        List<Cartela> cartelas = bingoService.gerarCartelasDiferentes(configuracao.quantidadeCartelas());
        List<PaginaBingo> paginas = configuracao.modo() == ModoImpressao.VARIOS_PREMIOS
                ? montarPaginasDePremios(cartelas, configuracao.quantidadePremios())
                : montarPaginasDeJogadores(cartelas, configuracao.cartelasPorPagina());
        return new LoteImpressao(configuracao, paginas);
    }

    private List<PaginaBingo> montarPaginasDePremios(List<Cartela> cartelas, int quantidadePremios) {
        List<PaginaBingo> paginas = new ArrayList<>(cartelas.size());
        for (int indice = 0; indice < cartelas.size(); indice++) {
            Cartela cartela = cartelas.get(indice);
            String identificacao = formatarIdentificacao(indice + 1);
            List<ItemCartelaImpressao> itens = new ArrayList<>(quantidadePremios);
            for (int premio = 1; premio <= quantidadePremios; premio++) {
                itens.add(new ItemCartelaImpressao(
                        cartela, identificacao, premio + "º prêmio", premio));
            }
            paginas.add(new PaginaBingo(indice + 1, itens));
        }
        return paginas;
    }

    private List<PaginaBingo> montarPaginasDeJogadores(List<Cartela> cartelas, int porPagina) {
        List<PaginaBingo> paginas = new ArrayList<>();
        for (int inicio = 0; inicio < cartelas.size(); inicio += porPagina) {
            int fim = Math.min(inicio + porPagina, cartelas.size());
            List<ItemCartelaImpressao> itens = new ArrayList<>(fim - inicio);
            for (int indice = inicio; indice < fim; indice++) {
                itens.add(new ItemCartelaImpressao(
                        cartelas.get(indice), formatarIdentificacao(indice + 1), null, 0));
            }
            paginas.add(new PaginaBingo(paginas.size() + 1, itens));
        }
        return paginas;
    }

    private String formatarIdentificacao(int numero) {
        return "Cartela %03d".formatted(numero);
    }
}
