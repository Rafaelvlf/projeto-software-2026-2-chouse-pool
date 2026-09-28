package br.insper.chouse.pool.service;

import br.insper.chouse.pool.event.EventoPublisher;
import br.insper.chouse.pool.event.VotoRegistrado;
import br.insper.chouse.pool.model.Enquete;
import br.insper.chouse.pool.model.Opcao;
import br.insper.chouse.pool.model.StatusEnquete;
import br.insper.chouse.pool.model.Voto;
import br.insper.chouse.pool.repository.EnqueteRepository;
import br.insper.chouse.pool.repository.VotoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VotoServiceTest {

    @Mock
    private EnqueteRepository enqueteRepository;

    @Mock
    private VotoRepository votoRepository;

    @Mock
    private EventoPublisher eventoPublisher;

    @InjectMocks
    private VotoService votoService;

    @Test
    @DisplayName("Deve registrar voto com sucesso e disparar evento")
    void deveVotarComSucesso() {
        Enquete enquete = mock(Enquete.class);
        Opcao opcao = mock(Opcao.class);
        Voto votoSalvo = new Voto(1L, 10L, 100L);

        when(votoRepository.existsByUsuarioIdAndEnqueteId(1L, 10L)).thenReturn(false);
        when(enqueteRepository.findById(10L)).thenReturn(Optional.of(enquete));
        when(enquete.getStatus()).thenReturn(StatusEnquete.ATIVA);
        when(enquete.encontrarOpcao(100L)).thenReturn(opcao);
        when(votoRepository.saveAndFlush(any(Voto.class))).thenReturn(votoSalvo);

        Voto resultado = votoService.votar(1L, 10L, 100L);

        assertNotNull(resultado);
        verify(opcao).incrementarVotos();
        verify(votoRepository).saveAndFlush(any(Voto.class));
        verify(eventoPublisher).publicar(any(VotoRegistrado.class));
    }

    @Test
    @DisplayName("Deve lançar CONFLICT se o usuário já votou na enquete")
    void deveLancarExcecaoQuandoUsuarioJaVotou() {
        when(votoRepository.existsByUsuarioIdAndEnqueteId(1L, 10L)).thenReturn(true);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> votoService.votar(1L, 10L, 100L));

        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
        assertEquals("Usuário já votou nesta enquete", ex.getReason());
    }

    @Test
    @DisplayName("Deve lançar NOT_FOUND ao tentar votar em enquete inexistente")
    void deveLancarExcecaoQuandoEnqueteInexistente() {
        when(votoRepository.existsByUsuarioIdAndEnqueteId(1L, 10L)).thenReturn(false);
        when(enqueteRepository.findById(10L)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> votoService.votar(1L, 10L, 100L));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
        assertEquals("Enquete não encontrada", ex.getReason());
    }

    @Test
    @DisplayName("Deve lançar CONFLICT se a enquete não estiver ativa")
    void deveLancarExcecaoQuandoEnqueteNaoAtiva() {
        Enquete enquete = mock(Enquete.class);
        when(votoRepository.existsByUsuarioIdAndEnqueteId(1L, 10L)).thenReturn(false);
        when(enqueteRepository.findById(10L)).thenReturn(Optional.of(enquete));
        when(enquete.getStatus()).thenReturn(StatusEnquete.ENCERRADA);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> votoService.votar(1L, 10L, 100L));

        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
        assertEquals("Enquete não está ativa", ex.getReason());
    }

    @Test
    @DisplayName("Deve lançar BAD_REQUEST se a opção não existir na enquete")
    void deveLancarExcecaoQuandoOpcaoInvalida() {
        Enquete enquete = mock(Enquete.class);
        when(votoRepository.existsByUsuarioIdAndEnqueteId(1L, 10L)).thenReturn(false);
        when(enqueteRepository.findById(10L)).thenReturn(Optional.of(enquete));
        when(enquete.getStatus()).thenReturn(StatusEnquete.ATIVA);
        when(enquete.encontrarOpcao(999L)).thenThrow(new IllegalArgumentException("Opção não encontrada"));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> votoService.votar(1L, 10L, 999L));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        assertEquals("Opção não encontrada", ex.getReason());
    }

    @Test
    @DisplayName("Deve obter percentuais da enquete")
    void deveObterPercentuaisComSucesso() {
        Enquete enquete = mock(Enquete.class);
        Map<Long, Double> percentuaisEsperados = Map.of(100L, 40.0, 200L, 60.0);
        when(enqueteRepository.findById(10L)).thenReturn(Optional.of(enquete));
        when(enquete.calcularPercentuais()).thenReturn(percentuaisEsperados);

        Map<Long, Double> resultado = votoService.obterPercentuais(10L);

        assertEquals(percentuaisEsperados, resultado);
    }

    @Test
    @DisplayName("Deve lançar NOT_FOUND ao obter percentuais de enquete inexistente")
    void deveLancarExcecaoAoObterPercentuaisEnqueteInexistente() {
        when(enqueteRepository.findById(99L)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> votoService.obterPercentuais(99L));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }
}