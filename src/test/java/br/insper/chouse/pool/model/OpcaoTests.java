package br.insper.chouse.pool.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OpcaoTests {

    @Test
    @DisplayName("Deve criar opção com texto formatado e votos zerados")
    void deveCriarOpcaoComSucesso() {
        Opcao opcao = new Opcao("  Opção A  ");

        assertEquals("Opção A", opcao.getTexto());
        assertEquals(0, opcao.getTotalVotos());
        assertNull(opcao.getId());
    }

    @Test
    @DisplayName("Deve lançar exceção se o texto for nulo ou em branco")
    void deveLancarExcecaoTextoInvalido() {
        assertThrows(IllegalArgumentException.class, () -> new Opcao(null));
        assertThrows(IllegalArgumentException.class, () -> new Opcao("   "));
    }

    @Test
    @DisplayName("Deve incrementar votos")
    void deveIncrementarVotos() {
        Opcao opcao = new Opcao("Opção A");

        opcao.incrementarVotos();
        opcao.incrementarVotos();

        assertEquals(2, opcao.getTotalVotos());
    }

    @Test
    @DisplayName("Deve calcular percentual corretamente ou retornar 0 se total for zero")
    void deveCalcularPercentual() {
        Opcao opcao = new Opcao("Opção A");

        // Total 0 evita divisão por zero
        assertEquals(0.0, opcao.percentual(0));

        opcao.incrementarVotos();
        assertEquals(50.0, opcao.percentual(2));
        assertEquals(25.0, opcao.percentual(4));
    }
}