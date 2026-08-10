package br.com.bingo.service;

public class LoteNaoEncontradoException extends RuntimeException {

    public LoteNaoEncontradoException() {
        super("Este lote expirou ou não está mais disponível. Gere uma nova prévia.");
    }
}
