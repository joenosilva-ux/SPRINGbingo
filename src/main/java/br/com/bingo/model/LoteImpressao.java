package br.com.bingo.model;

import java.util.List;
import java.util.Objects;

public final class LoteImpressao {

    private final ConfiguracaoImpressao configuracao;
    private final List<PaginaBingo> paginas;

    public LoteImpressao(ConfiguracaoImpressao configuracao, List<PaginaBingo> paginas) {
        this.configuracao = Objects.requireNonNull(configuracao, "A configuração é obrigatória");
        this.paginas = List.copyOf(Objects.requireNonNull(paginas, "As páginas são obrigatórias"));
        if (this.paginas.size() != configuracao.calcularTotalFolhas()) {
            throw new IllegalArgumentException("A quantidade de páginas não corresponde à configuração");
        }
    }

    public ConfiguracaoImpressao getConfiguracao() { return configuracao; }
    public List<PaginaBingo> getPaginas() { return paginas; }
    public int getTotalFolhas() { return paginas.size(); }
    public int getTotalCartelas() { return configuracao.quantidadeCartelas(); }
}
