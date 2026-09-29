package br.insper.chouse.pool.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StatusEnqueteTests {

    @Test
    @DisplayName("Deve conter todos os status esperados")
    void deveConterTodosStatus() {
        StatusEnquete[] statuses = StatusEnquete.values();
        assertEquals(3, statuses.length);

        assertEquals(StatusEnquete.RASCUNHO, StatusEnquete.valueOf("RASCUNHO"));
        assertEquals(StatusEnquete.ATIVA, StatusEnquete.valueOf("ATIVA"));
        assertEquals(StatusEnquete.ENCERRADA, StatusEnquete.valueOf("ENCERRADA"));
    }
}