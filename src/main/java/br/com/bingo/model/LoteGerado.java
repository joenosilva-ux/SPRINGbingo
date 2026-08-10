package br.com.bingo.model;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/** Lote temporário que pode ser consultado folha por folha. */
public final class LoteGerado {

    private final UUID id;
    private final LoteImpressao lote;
    private final Instant criadoEm;
    private final Instant expiraEm;

    public LoteGerado(UUID id, LoteImpressao lote, Instant criadoEm, Instant expiraEm) {
        this.id = Objects.requireNonNull(id, "O identificador do lote é obrigatório");
        this.lote = Objects.requireNonNull(lote, "O conteúdo do lote é obrigatório");
        this.criadoEm = Objects.requireNonNull(criadoEm, "A criação do lote é obrigatória");
        this.expiraEm = Objects.requireNonNull(expiraEm, "A expiração do lote é obrigatória");
        if (!expiraEm.isAfter(criadoEm)) {
            throw new IllegalArgumentException("A expiração deve ocorrer depois da criação");
        }
    }

    public UUID getId() { return id; }
    public LoteImpressao getLote() { return lote; }
    public Instant getCriadoEm() { return criadoEm; }
    public Instant getExpiraEm() { return expiraEm; }

    public boolean estaExpirado(Instant agora) {
        return !expiraEm.isAfter(agora);
    }
}
