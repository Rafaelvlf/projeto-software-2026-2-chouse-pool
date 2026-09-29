package br.insper.chouse.pool.controller;

import br.insper.chouse.pool.model.Enquete;
import br.insper.chouse.pool.model.Voto;
import br.insper.chouse.pool.service.EnqueteService;
import br.insper.chouse.pool.service.EnqueteService.ResultadoEnquete;
import br.insper.chouse.pool.service.VotoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EnqueteController.class)
class EnqueteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private EnqueteService enqueteService;

    @MockitoBean
    private VotoService votoService;

    @Test
    @DisplayName("POST /enquetes - Deve criar enquete com status 201")
    void deveCriarEnquete() throws Exception {
        EnqueteController.CriarEnqueteRequest request = new EnqueteController.CriarEnqueteRequest();
        request.setPergunta("Café ou Chá?");
        request.setCategoria("Bebidas");
        request.setTextoA("Café");
        request.setTextoB("Chá");

        when(enqueteService.criar("Café ou Chá?", "Bebidas", "Café", "Chá")).thenReturn(null);

        mockMvc.perform(post("/enquetes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("POST /enquetes - Deve falhar com 400 se faltar campo obrigatório")
    void deveFalharAoCriarEnqueteInvalida() throws Exception {
        EnqueteController.CriarEnqueteRequest request = new EnqueteController.CriarEnqueteRequest();
        // Não informando campos @NotBlank

        mockMvc.perform(post("/enquetes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /enquetes - Deve listar enquetes com status 200")
    void deveListarEnquetes() throws Exception {
        when(enqueteService.listar()).thenReturn(List.of());

        mockMvc.perform(get("/enquetes"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /enquetes/{id} - Deve buscar enquete por id")
    void deveBuscarEnquete() throws Exception {
        when(enqueteService.buscar(1L)).thenReturn(null);

        mockMvc.perform(get("/enquetes/{id}", 1L))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /enquetes/{id}/encerrar - Deve encerrar enquete")
    void deveEncerrarEnquete() throws Exception {
        when(enqueteService.encerrar(1L)).thenReturn(null);

        mockMvc.perform(post("/enquetes/{id}/encerrar", 1L))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /enquetes/{id}/votos - Deve registrar voto com status 201")
    void deveVotar() throws Exception {
        EnqueteController.VotarRequest request = new EnqueteController.VotarRequest();
        request.setUsuarioId(1L);
        request.setOpcaoId(2L);

        when(votoService.votar(1L, 10L, 2L)).thenReturn(null);

        mockMvc.perform(post("/enquetes/{id}/votos", 10L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("POST /enquetes/{id}/votos - Deve retornar 400 em caso de payload nulo")
    void deveRetornarBadRequestVotoInvalido() throws Exception {
        EnqueteController.VotarRequest request = new EnqueteController.VotarRequest();

        mockMvc.perform(post("/enquetes/{id}/votos", 10L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /enquetes/{id}/percentuais - Deve obter percentuais")
    void deveObterPercentuais() throws Exception {
        when(votoService.obterPercentuais(1L)).thenReturn(Map.of(10L, 100.0));

        mockMvc.perform(get("/enquetes/{id}/percentuais", 1L))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /enquetes/{id}/resultado - Deve obter resultado da enquete")
    void deveObterResultado() throws Exception {
        ResultadoEnquete resultado = new ResultadoEnquete(1L, 10L, 80.0, 20.0);
        when(enqueteService.obterResultado(1L)).thenReturn(resultado);

        mockMvc.perform(get("/enquetes/{id}/resultado", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.enqueteId").value(1L))
                .andExpect(jsonPath("$.opcaoVencedoraId").value(10L));
    }
}