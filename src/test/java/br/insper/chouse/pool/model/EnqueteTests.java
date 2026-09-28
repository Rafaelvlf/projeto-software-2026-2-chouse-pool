package br.insper.chouse.pool.model;

import br.insper.chouse.pool.factory.EnqueteFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EnqueteTests {

    private final EnqueteFactory factory = new EnqueteFactory();

    @Test
    void deveCriarEnqueteComExatamenteDuasOpcoes() {
        Enquete enquete = factory.criar("Café ou chá?", "Bebidas", "Café", "Chá");

        assertEquals(StatusEnquete.ATIVA, enquete.getStatus());
        assertEquals(2, enquete.getOpcoes().size());
        assertEquals(0, enquete.getTotalVotos());
    }

    @Test
    void deveCalcularPercentual() {
        Opcao opcao = new Opcao("Café");
        opcao.incrementarVotos();

        assertEquals(50.0, opcao.percentual(2));
    }

    @Test
    void naoDeveAceitarOpcoesIguais() {
        assertThrows(IllegalArgumentException.class,
                () -> factory.criar("Pergunta", "Opção", " opção "));
    }
}
