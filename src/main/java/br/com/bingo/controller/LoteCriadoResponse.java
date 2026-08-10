package br.com.bingo.controller;

import br.com.bingo.model.LoteGerado;

public record LoteCriadoResponse(
        String id,
        int totalFolhas,
        int totalCartelas,
        String modo) {

    public static LoteCriadoResponse de(LoteGerado lote) {
        return new LoteCriadoResponse(
                lote.getId().toString(),
                lote.getLote().getTotalFolhas(),
                lote.getLote().getTotalCartelas(),
                lote.getLote().getConfiguracao().modo().name());
    }
}
