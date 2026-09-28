package br.insper.chouse.pool.controller;

import br.insper.chouse.pool.model.Enquete;
import br.insper.chouse.pool.model.Voto;
import br.insper.chouse.pool.service.EnqueteService;
import br.insper.chouse.pool.service.EnqueteService.ResultadoEnquete;
import br.insper.chouse.pool.service.VotoService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/enquetes")
@RequiredArgsConstructor
public class EnqueteController {

    private final EnqueteService enqueteService;
    private final VotoService votoService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Enquete criar(@Valid @RequestBody CriarEnqueteRequest request) {
        return enqueteService.criar(
                request.getPergunta(), request.getCategoria(), request.getTextoA(), request.getTextoB());
    }

    @GetMapping
    public List<Enquete> listar() {
        return enqueteService.listar();
    }

    @GetMapping("/{id}")
    public Enquete buscar(@PathVariable Long id) {
        return enqueteService.buscar(id);
    }

    @PostMapping("/{id}/encerrar")
    public Enquete encerrar(@PathVariable Long id) {
        return enqueteService.encerrar(id);
    }

    @PostMapping("/{id}/votos")
    @ResponseStatus(HttpStatus.CREATED)
    public Voto votar(@PathVariable Long id, @Valid @RequestBody VotarRequest request) {
        return votoService.votar(request.getUsuarioId(), id, request.getOpcaoId());
    }

    @GetMapping("/{id}/percentuais")
    public Map<Long, Double> percentuais(@PathVariable Long id) {
        return votoService.obterPercentuais(id);
    }

    @GetMapping("/{id}/resultado")
    public ResultadoEnquete resultado(@PathVariable Long id) {
        return enqueteService.obterResultado(id);
    }

    @Data
    @NoArgsConstructor
    public static class CriarEnqueteRequest {
        @NotBlank private String pergunta;
        private String categoria;
        @NotBlank private String textoA;
        @NotBlank private String textoB;
    }

    @Data
    @NoArgsConstructor
    public static class VotarRequest {
        @NotNull @Positive private Long usuarioId;
        @NotNull @Positive private Long opcaoId;
    }
}
