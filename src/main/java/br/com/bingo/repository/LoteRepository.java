package br.com.bingo.repository;

import br.com.bingo.model.LoteGerado;

import java.util.Optional;
import java.util.UUID;

public interface LoteRepository {

    LoteGerado salvar(LoteGerado lote);

    Optional<LoteGerado> buscar(UUID id);

    void remover(UUID id);
}
