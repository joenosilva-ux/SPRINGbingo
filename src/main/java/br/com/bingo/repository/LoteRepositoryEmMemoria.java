package br.com.bingo.repository;

import br.com.bingo.model.LoteGerado;
import org.springframework.stereotype.Repository;

import java.time.Clock;
import java.time.Instant;
import java.util.Comparator;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Repository
public class LoteRepositoryEmMemoria implements LoteRepository {

    private static final int LIMITE_LOTES_ATIVOS = 500;

    private final ConcurrentMap<UUID, LoteGerado> lotes = new ConcurrentHashMap<>();
    private final Clock clock;

    public LoteRepositoryEmMemoria() {
        this(Clock.systemUTC());
    }

    LoteRepositoryEmMemoria(Clock clock) {
        this.clock = clock;
    }

    @Override
    public LoteGerado salvar(LoteGerado lote) {
        limparExpirados();
        liberarEspacoSeNecessario();
        lotes.put(lote.getId(), lote);
        return lote;
    }

    @Override
    public Optional<LoteGerado> buscar(UUID id) {
        LoteGerado lote = lotes.get(id);
        if (lote == null) {
            return Optional.empty();
        }
        if (lote.estaExpirado(clock.instant())) {
            lotes.remove(id, lote);
            return Optional.empty();
        }
        return Optional.of(lote);
    }

    @Override
    public void remover(UUID id) {
        if (id != null) {
            lotes.remove(id);
        }
    }

    private void limparExpirados() {
        Instant agora = clock.instant();
        lotes.entrySet().removeIf(entrada -> entrada.getValue().estaExpirado(agora));
    }

    private void liberarEspacoSeNecessario() {
        if (lotes.size() < LIMITE_LOTES_ATIVOS) {
            return;
        }
        lotes.values().stream()
                .min(Comparator.comparing(LoteGerado::getCriadoEm))
                .ifPresent(lote -> lotes.remove(lote.getId(), lote));
    }
}
