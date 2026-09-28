package br.insper.chouse.pool.service;

import br.insper.chouse.pool.factory.EnqueteFactory;
import br.insper.chouse.pool.model.Enquete;
import br.insper.chouse.pool.model.Opcao;
import br.insper.chouse.pool.repository.EnqueteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EnqueteService {

    private final EnqueteRepository repository;
    private final EnqueteFactory factory;

    @Transactional
    public Enquete criar(String pergunta, String categoria, String textoA, String textoB) {
        return repository.save(factory.criar(pergunta, categoria, textoA, textoB));
    }

    @Transactional(readOnly = true)
    public Enquete buscar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Enquete não encontrada"));
    }

    @Transactional(readOnly = true)
    public List<Enquete> listar() {
        return repository.findAll();
    }

    @Transactional
    public Enquete encerrar(Long id) {
        Enquete enquete = buscar(id);
        enquete.encerrar();
        return enquete;
    }

    @Transactional(readOnly = true)
    public ResultadoEnquete obterResultado(Long id) {
        Enquete enquete = buscar(id);
        Map<Long, Double> percentuais = enquete.calcularPercentuais();
        List<Opcao> opcoes = enquete.getOpcoes();

        Long vencedoraId = opcoes.stream()
                .max(Comparator.comparingInt(Opcao::getTotalVotos))
                .filter(maior -> opcoes.stream()
                        .filter(opcao -> opcao.getTotalVotos() == maior.getTotalVotos()).count() == 1)
                .map(Opcao::getId)
                .orElse(null);

        return new ResultadoEnquete(
                enquete.getId(),
                vencedoraId,
                percentuais.getOrDefault(opcoes.get(0).getId(), 0.0),
                percentuais.getOrDefault(opcoes.get(1).getId(), 0.0));
    }

    @lombok.Value
    public static class ResultadoEnquete {
        Long enqueteId;
        Long opcaoVencedoraId;
        double percentualA;
        double percentualB;
    }
}
