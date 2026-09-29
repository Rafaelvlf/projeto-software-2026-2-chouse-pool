package br.insper.chouse.pool.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class VotoTests {

    @Test
    @DisplayName("Deve instanciar Voto e retornar valores dos atributos")
    void deveInstanciarVoto() {
        Voto voto = new Voto(1L, 10L, 100L);

        assertNull(voto.getId());
        assertEquals(1L, voto.getUsuarioId());
        assertEquals(10L, voto.getEnqueteId());
        assertEquals(100L, voto.getOpcaoId());
        assertNull(voto.getDataHora());
    }

    @Test
    @DisplayName("Deve preencher dataHora no PrePersist caso seja nula")
    void devePreencherDataHoraNoPrePersist() {
        Voto voto = new Voto(1L, 10L, 100L);

        voto.definirDataHora();

        assertNotNull(voto.getDataHora());
    }

    @Test
    @DisplayName("Não deve sobrescrever dataHora no PrePersist se já estiver definida")
    void naoDeveSobrescreverDataHora() {
        Voto voto = new Voto(1L, 10L, 100L);
        voto.definirDataHora();
        LocalDateTime primeiraData = voto.getDataHora();

        // Segunda chamada para testar branch false do if (dataHora == null)
        voto.definirDataHora();

        assertEquals(primeiraData, voto.getDataHora());
    }
}