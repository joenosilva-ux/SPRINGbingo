package br.com.bingo.controller;

import br.com.bingo.model.ConfiguracaoImpressao;
import br.com.bingo.model.LoteGerado;
import br.com.bingo.repository.LoteRepositoryEmMemoria;
import br.com.bingo.service.BingoService;
import br.com.bingo.service.LoteGeradoService;
import br.com.bingo.service.LoteImpressaoService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

class BingoControllerTest {

    private MockMvc mockMvc;
    private LoteGeradoService loteGeradoService;

    @BeforeEach
    void prepararControllers() {
        loteGeradoService = new LoteGeradoService(
                new LoteImpressaoService(new BingoService()),
                new LoteRepositoryEmMemoria());
        BingoController bingoController = new BingoController(loteGeradoService);
        LoteController loteController = new LoteController(loteGeradoService);
        InternalResourceViewResolver viewResolver =
                new InternalResourceViewResolver("/WEB-INF/views/", ".html");
        mockMvc = MockMvcBuilders.standaloneSetup(bingoController, loteController)
                .setViewResolvers(viewResolver)
                .build();
    }

    @Test
    void raizDeveRedirecionarParaGerador() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/bingo"));
    }

    @Test
    void deveAbrirPaginaComSomenteAPrimeiraFolha() throws Exception {
        mockMvc.perform(get("/bingo"))
                .andExpect(status().isOk())
                .andExpect(view().name("bingo"))
                .andExpect(model().attribute("pagina",
                        Matchers.hasProperty("numero", Matchers.is(1))))
                .andExpect(model().attribute("totalFolhas", 1))
                .andExpect(model().attributeExists("loteId"));
    }

    @Test
    void deveCriarLoteDeCinquentaFolhasSemRenderizarTodasNaResposta() throws Exception {
        mockMvc.perform(post("/bingo/lotes")
                        .param("modo", "VARIOS_PREMIOS")
                        .param("quantidadeCartelas", "50")
                        .param("quantidadePremios", "6"))
                .andExpect(status().isOk())
                .andExpect(header().string("Cache-Control", "no-store"))
                .andExpect(jsonPath("$.id").isString())
                .andExpect(jsonPath("$.totalFolhas").value(50))
                .andExpect(jsonPath("$.totalCartelas").value(50));
    }

    @Test
    void deveBuscarUmaFolhaEspecificaEODocumentoCompletoSeparadamente() throws Exception {
        LoteGerado lote = loteGeradoService.criar(
                ConfiguracaoImpressao.variosPremios(50, 5), null);

        mockMvc.perform(get("/bingo/lotes/{id}/paginas/50", lote.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("fragments/folha :: folha"))
                .andExpect(model().attribute("pagina",
                        Matchers.hasProperty("numero", Matchers.is(50))))
                .andExpect(model().attribute("totalFolhas", 50));

        mockMvc.perform(get("/bingo/lotes/{id}/impressao", lote.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("fragments/impressao :: impressao"))
                .andExpect(model().attribute("lote",
                        Matchers.hasProperty("totalFolhas", Matchers.is(50))));
    }

    @Test
    void deveExplicarEntradaInvalidaELoteInexistente() throws Exception {
        mockMvc.perform(post("/bingo/lotes").param("quantidadeCartelas", "201"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(Matchers.containsString("entre 1 e 200")));

        mockMvc.perform(get("/bingo/lotes/{id}/paginas/1", UUID.randomUUID()))
                .andExpect(status().isNotFound())
                .andExpect(content().string(Matchers.containsString("expirou")));
    }
}
