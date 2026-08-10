package br.com.bingo.service;

import br.com.bingo.model.Cartela;
import br.com.bingo.model.ConfiguracaoImpressao;
import br.com.bingo.model.ItemCartelaImpressao;
import br.com.bingo.model.LoteImpressao;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class LoteImpressaoServiceTest {

    private final LoteImpressaoService service =
            new LoteImpressaoService(new BingoService(new Random(20260804L)));

    @Test
    void cinquentaCartelasComSeisPremiosDevemGerarCinquentaFolhas() {
        LoteImpressao lote = service.gerar(ConfiguracaoImpressao.variosPremios(50, 6));

        assertEquals(50, lote.getTotalFolhas());
        assertEquals("Cartela 050", lote.getPaginas().getLast().getItens().getFirst().getIdentificacao());

        List<Cartela> cartelasDasFolhas = lote.getPaginas().stream()
                .map(pagina -> pagina.getItens().getFirst().getCartela())
                .toList();
        assertEquals(50, new HashSet<>(cartelasDasFolhas).size());

        lote.getPaginas().forEach(pagina -> {
            assertEquals(6, pagina.getItens().size());
            Cartela cartelaDaFolha = pagina.getItens().getFirst().getCartela();
            pagina.getItens().stream().map(ItemCartelaImpressao::getCartela)
                    .forEach(cartela -> assertSame(cartelaDaFolha, cartela));
        });
    }

    @Test
    void cinquentaCartelasComSeisPorFolhaDevemGerarNoveFolhas() {
        LoteImpressao lote = service.gerar(ConfiguracaoImpressao.paraJogadores(50, 6));

        assertEquals(9, lote.getTotalFolhas());
        assertEquals(6, lote.getPaginas().getFirst().getItens().size());
        assertEquals(2, lote.getPaginas().getLast().getItens().size());
        assertEquals("Cartela 050", lote.getPaginas().getLast().getItens().getLast().getIdentificacao());

        List<Cartela> todas = lote.getPaginas().stream()
                .flatMap(pagina -> pagina.getItens().stream())
                .map(ItemCartelaImpressao::getCartela)
                .toList();
        assertEquals(50, todas.size());
        assertEquals(50, new HashSet<>(todas).size());
    }
}
