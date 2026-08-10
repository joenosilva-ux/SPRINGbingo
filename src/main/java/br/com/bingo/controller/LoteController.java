package br.com.bingo.controller;

import br.com.bingo.model.ConfiguracaoImpressao;
import br.com.bingo.model.LoteGerado;
import br.com.bingo.model.ModoImpressao;
import br.com.bingo.model.PaginaBingo;
import br.com.bingo.service.LoteGeradoService;
import br.com.bingo.service.LoteNaoEncontradoException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.HtmlUtils;

import java.util.UUID;

@Controller
public class LoteController {

    private final LoteGeradoService loteGeradoService;

    public LoteController(LoteGeradoService loteGeradoService) {
        this.loteGeradoService = loteGeradoService;
    }

    @PostMapping("/bingo/lotes")
    @ResponseBody
    public LoteCriadoResponse criarLote(
            @RequestParam(defaultValue = "VARIOS_PREMIOS") String modo,
            @RequestParam(defaultValue = "1") int quantidadeCartelas,
            @RequestParam(defaultValue = "1") int quantidadePremios,
            @RequestParam(defaultValue = "4") int cartelasPorPagina,
            @RequestParam(required = false) UUID loteAnterior,
            HttpServletResponse response) {
        response.setHeader(HttpHeaders.CACHE_CONTROL, "no-store");
        ConfiguracaoImpressao configuracao = new ConfiguracaoImpressao(
                converterModo(modo), quantidadeCartelas, quantidadePremios, cartelasPorPagina);
        return LoteCriadoResponse.de(loteGeradoService.criar(configuracao, loteAnterior));
    }

    @GetMapping("/bingo/lotes/{id}/paginas/{numero}")
    public String buscarPagina(@PathVariable UUID id, @PathVariable int numero,
                               HttpServletResponse response, Model model) {
        response.setHeader(HttpHeaders.CACHE_CONTROL, "no-store");
        LoteGerado lote = loteGeradoService.buscar(id);
        PaginaBingo pagina = loteGeradoService.buscarPagina(id, numero);
        adicionarPaginaAoModelo(model, lote, pagina);
        return "fragments/folha :: folha";
    }

    @GetMapping("/bingo/lotes/{id}/impressao")
    public String buscarImpressao(@PathVariable UUID id,
                                  HttpServletResponse response, Model model) {
        response.setHeader(HttpHeaders.CACHE_CONTROL, "no-store");
        model.addAttribute("lote", loteGeradoService.buscar(id).getLote());
        return "fragments/impressao :: impressao";
    }

    private void adicionarPaginaAoModelo(Model model, LoteGerado lote, PaginaBingo pagina) {
        model.addAttribute("pagina", pagina);
        model.addAttribute("totalFolhas", lote.getLote().getTotalFolhas());
        model.addAttribute("configuracao", lote.getLote().getConfiguracao());
    }

    private ModoImpressao converterModo(String modo) {
        try {
            return ModoImpressao.valueOf(modo);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Escolha um modo de impressão válido");
        }
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> tratarEntradaInvalida(IllegalArgumentException ex) {
        return respostaDeErro(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(LoteNaoEncontradoException.class)
    public ResponseEntity<String> tratarLoteNaoEncontrado(LoteNaoEncontradoException ex) {
        return respostaDeErro(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    private ResponseEntity<String> respostaDeErro(HttpStatus status, String texto) {
        String mensagem = "<p class=\"erro-lote\">" + HtmlUtils.htmlEscape(texto) + "</p>";
        return ResponseEntity.status(status)
                .cacheControl(CacheControl.noStore())
                .header(HttpHeaders.CONTENT_TYPE, "text/html;charset=UTF-8")
                .body(mensagem);
    }
}
