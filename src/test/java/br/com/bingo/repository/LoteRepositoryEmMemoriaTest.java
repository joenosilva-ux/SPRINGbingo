package br.com.bingo.repository;

import br.com.bingo.model.ConfiguracaoImpressao;
import br.com.bingo.model.LoteGerado;
import br.com.bingo.model.LoteImpressao;
import br.com.bingo.service.BingoService;
import br.com.bingo.service.LoteImpressaoService;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LoteRepositoryEmMemoriaTest {

    private static final Instant AGORA = Instant.parse("2026-08-04T20:00:00Z");

    @Test
    void deveGuardarRemoverEIgnorarLoteExpirado() {
        LoteRepositoryEmMemoria repository = new LoteRepositoryEmMemoria(
                Clock.fixed(AGORA, ZoneOffset.UTC));
        LoteImpressao conteudo = new LoteImpressaoService(new BingoService())
                .gerar(ConfiguracaoImpressao.variosPremios(1, 1));

        LoteGerado valido = new LoteGerado(
                UUID.randomUUID(), conteudo, AGORA.minusSeconds(60), AGORA.plusSeconds(60));
        repository.salvar(valido);
        assertTrue(repository.buscar(valido.getId()).isPresent());

        repository.remover(valido.getId());
        assertFalse(repository.buscar(valido.getId()).isPresent());

        LoteGerado expirado = new LoteGerado(
                UUID.randomUUID(), conteudo, AGORA.minusSeconds(120), AGORA.minusSeconds(60));
        repository.salvar(expirado);
        assertFalse(repository.buscar(expirado.getId()).isPresent());
    }
}
