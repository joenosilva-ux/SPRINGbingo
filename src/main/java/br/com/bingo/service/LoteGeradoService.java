package br.com.bingo.service;

import br.com.bingo.model.ConfiguracaoImpressao;
import br.com.bingo.model.LoteGerado;
import br.com.bingo.model.LoteImpressao;
import br.com.bingo.model.PaginaBingo;
import br.com.bingo.repository.LoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Service
public class LoteGeradoService {

    static final Duration TEMPO_DE_VIDA = Duration.ofMinutes(30);

    private final LoteImpressaoService loteImpressaoService;
    private final LoteRepository loteRepository;
    private final Clock clock;

    @Autowired
    public LoteGeradoService(LoteImpressaoService loteImpressaoService,
                             LoteRepository loteRepository) {
        this(loteImpressaoService, loteRepository, Clock.systemUTC());
    }

    LoteGeradoService(LoteImpressaoService loteImpressaoService,
                      LoteRepository loteRepository, Clock clock) {
        this.loteImpressaoService = loteImpressaoService;
        this.loteRepository = loteRepository;
        this.clock = clock;
    }

    public LoteGerado criar(ConfiguracaoImpressao configuracao, UUID loteAnterior) {
        LoteImpressao conteudo = loteImpressaoService.gerar(configuracao);
        Instant criadoEm = clock.instant();
        LoteGerado lote = new LoteGerado(
                UUID.randomUUID(), conteudo, criadoEm, criadoEm.plus(TEMPO_DE_VIDA));
        loteRepository.salvar(lote);
        loteRepository.remover(loteAnterior);
        return lote;
    }

    public LoteGerado buscar(UUID id) {
        return loteRepository.buscar(id).orElseThrow(LoteNaoEncontradoException::new);
    }

    public PaginaBingo buscarPagina(UUID id, int numeroPagina) {
        LoteGerado lote = buscar(id);
        if (numeroPagina < 1 || numeroPagina > lote.getLote().getTotalFolhas()) {
            throw new IllegalArgumentException("Escolha uma página válida deste lote");
        }
        return lote.getLote().getPaginas().get(numeroPagina - 1);
    }
}
