package br.com.bingo.controller;

import br.com.bingo.model.ConfiguracaoImpressao;
import br.com.bingo.model.LoteGerado;
import br.com.bingo.service.LoteGeradoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class BingoController {

    private final LoteGeradoService loteGeradoService;

    public BingoController(LoteGeradoService loteGeradoService) {
        this.loteGeradoService = loteGeradoService;
    }

    @GetMapping("/")
    public String inicio() {
        return "redirect:/bingo";
    }

    @GetMapping("/bingo")
    public String exibirGerador(Model model) {
        LoteGerado lote = loteGeradoService.criar(
                ConfiguracaoImpressao.variosPremios(1, 1), null);
        model.addAttribute("loteId", lote.getId());
        model.addAttribute("pagina", lote.getLote().getPaginas().getFirst());
        model.addAttribute("totalFolhas", lote.getLote().getTotalFolhas());
        model.addAttribute("configuracao", lote.getLote().getConfiguracao());
        return "bingo";
    }
}
