package br.insper.chouse.pool.service;

import br.insper.chouse.pool.event.EventoPublisher;
import br.insper.chouse.pool.event.VotoRegistrado;
import br.insper.chouse.pool.model.Enquete;
import br.insper.chouse.pool.model.Opcao;
import br.insper.chouse.pool.model.StatusEnquete;
import br.insper.chouse.pool.model.Voto;
import br.insper.chouse.pool.repository.EnqueteRepository;
import br.insper.chouse.pool.repository.VotoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class VotoService {

    private final EnqueteRepository enqueteRepository;
    private final VotoRepository votoRepository;
    private final EventoPublisher eventoPublisher;

    @Transactional
    public Voto votar(Long usuarioId, Long enqueteId, Long opcaoId) {
        if (votoRepository.existsByUsuarioIdAndEnqueteId(usuarioId, enqueteId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Usuário já votou nesta enquete");
        }

        Enquete enquete = enqueteRepository.findById(enqueteId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Enquete não encontrada"));
        if (enquete.getStatus() != StatusEnquete.ATIVA) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Enquete não está ativa");
        }

        final Opcao opcao;
        try {
            opcao = enquete.encontrarOpcao(opcaoId);
        } catch (IllegalArgumentException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, exception.getMessage());
        }

        opcao.incrementarVotos();
        Voto voto = votoRepository.saveAndFlush(new Voto(usuarioId, enqueteId, opcaoId));
        eventoPublisher.publicar(new VotoRegistrado(
                voto.getId(), voto.getEnqueteId(), voto.getOpcaoId(), voto.getDataHora()));
        return voto;
    }

    @Transactional(readOnly = true)
    public Map<Long, Double> obterPercentuais(Long enqueteId) {
        Enquete enquete = enqueteRepository.findById(enqueteId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Enquete não encontrada"));
        return enquete.calcularPercentuais();
    }
}
