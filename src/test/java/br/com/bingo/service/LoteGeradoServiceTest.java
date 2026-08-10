package br.com.bingo.service;

import br.com.bingo.model.ConfiguracaoImpressao;
import br.com.bingo.model.LoteGerado;
import br.com.bingo.repository.LoteRepositoryEmMemoria;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LoteGeradoServiceTest {

    @Test
    void deveManterLoteParaConsultaPaginadaESubstituirOAnterior() {
        LoteGeradoService service = new LoteGeradoService(
                new LoteImpressaoService(new BingoService(new Random(20260804L))),
                new LoteRepositoryEmMemoria());

        LoteGerado primeiro = service.criar(
                ConfiguracaoImpressao.variosPremios(50, 5), null);
        assertEquals(50, service.buscarPagina(primeiro.getId(), 50).getNumero());
        assertEquals(5, service.buscarPagina(primeiro.getId(), 1).getItens().size());

        LoteGerado segundo = service.criar(
                ConfiguracaoImpressao.paraJogadores(50, 6), primeiro.getId());
        assertEquals(9, segundo.getLote().getTotalFolhas());
        assertThrows(LoteNaoEncontradoException.class, () -> service.buscar(primeiro.getId()));
    }
}
