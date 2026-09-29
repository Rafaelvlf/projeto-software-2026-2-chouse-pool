package br.insper.chouse.pool.service;

import br.insper.chouse.pool.factory.EnqueteFactory;
import br.insper.chouse.pool.model.Enquete;
import br.insper.chouse.pool.model.Opcao;
import br.insper.chouse.pool.repository.EnqueteRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EnqueteServiceTest {

    @Mock
    private EnqueteRepository repository;

    @Mock
    private EnqueteFactory factory;

    @InjectMocks
    private EnqueteService enqueteService;

    @Test
    @DisplayName("Deve criar e salvar uma enquete com sucesso")
    void deveCriarEnquete() {
        Enquete enquete = mock(Enquete.class);
        when(factory.criar("Qual o melhor?", "Tecnologia", "Java", "Python")).thenReturn(enquete);
        when(repository.save(enquete)).thenReturn(enquete);

        Enquete resultado = enqueteService.criar("Qual o melhor?", "Tecnologia", "Java", "Python");

        assertNotNull(resultado);
        verify(factory).criar("Qual o melhor?", "Tecnologia", "Java", "Python");
        verify(repository).save(enquete);
    }

    @Test
    @DisplayName("Deve buscar enquete por ID com sucesso")
    void deveBuscarEnqueteExistente() {
        Enquete enquete = mock(Enquete.class);
        when(repository.findById(1L)).thenReturn(Optional.of(enquete));

        Enquete resultado = enqueteService.buscar(1L);

        assertEquals(enquete, resultado);
        verify(repository).findById(1L);
    }

    @Test
    @DisplayName("Deve lançar ResponseStatusException (NOT_FOUND) ao buscar enquete inexistente")
    void deveLancarExcecaoAoBuscarEnqueteInexistente() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> enqueteService.buscar(99L));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
        assertEquals("Enquete não encontrada", ex.getReason());
    }

    @Test
    @DisplayName("Deve listar todas as enquetes")
    void deveListarEnquetes() {
        List<Enquete> enquetes = List.of(mock(Enquete.class), mock(Enquete.class));
        when(repository.findAll()).thenReturn(enquetes);

        List<Enquete> resultado = enqueteService.listar();

        assertEquals(2, resultado.size());
        verify(repository).findAll();
    }

    @Test
    @DisplayName("Deve encerrar enquete com sucesso")
    void deveEncerrarEnquete() {
        Enquete enquete = mock(Enquete.class);
        when(repository.findById(1L)).thenReturn(Optional.of(enquete));

        Enquete resultado = enqueteService.encerrar(1L);

        assertEquals(enquete, resultado);
        verify(enquete).encerrar();
    }

    @Test
    @DisplayName("Deve calcular resultado quando há uma opção vencedora clara")
    void deveObterResultadoComVencedora() {
        Enquete enquete = mock(Enquete.class);
        Opcao opcaoA = mock(Opcao.class);
        Opcao opcaoB = mock(Opcao.class);

        when(opcaoA.getId()).thenReturn(10L);
        when(opcaoA.getTotalVotos()).thenReturn(5);

        when(opcaoB.getId()).thenReturn(20L);
        when(opcaoB.getTotalVotos()).thenReturn(2);

        when(enquete.getId()).thenReturn(1L);
        when(enquete.getOpcoes()).thenReturn(List.of(opcaoA, opcaoB));
        when(enquete.calcularPercentuais()).thenReturn(Map.of(10L, 71.4, 20L, 28.6));
        when(repository.findById(1L)).thenReturn(Optional.of(enquete));

        EnqueteService.ResultadoEnquete resultado = enqueteService.obterResultado(1L);

        assertEquals(1L, resultado.getEnqueteId());
        assertEquals(10L, resultado.getOpcaoVencedoraId());
        assertEquals(71.4, resultado.getPercentualA());
        assertEquals(28.6, resultado.getPercentualB());
    }

    @Test
    @DisplayName("Deve calcular resultado com opcaoVencedoraId nulo em caso de empate")
    void deveObterResultadoComEmpate() {
        Enquete enquete = mock(Enquete.class);
        Opcao opcaoA = mock(Opcao.class);
        Opcao opcaoB = mock(Opcao.class);

        when(opcaoA.getId()).thenReturn(10L);
        when(opcaoA.getTotalVotos()).thenReturn(3);

        when(opcaoB.getId()).thenReturn(20L);
        when(opcaoB.getTotalVotos()).thenReturn(3);

        when(enquete.getId()).thenReturn(1L);
        when(enquete.getOpcoes()).thenReturn(List.of(opcaoA, opcaoB));
        when(enquete.calcularPercentuais()).thenReturn(Map.of(10L, 50.0, 20L, 50.0));
        when(repository.findById(1L)).thenReturn(Optional.of(enquete));

        EnqueteService.ResultadoEnquete resultado = enqueteService.obterResultado(1L);

        assertEquals(1L, resultado.getEnqueteId());
        assertNull(resultado.getOpcaoVencedoraId());
        assertEquals(50.0, resultado.getPercentualA());
        assertEquals(50.0, resultado.getPercentualB());
    }
}