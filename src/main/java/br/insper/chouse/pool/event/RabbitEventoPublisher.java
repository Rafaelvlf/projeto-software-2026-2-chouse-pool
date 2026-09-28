package br.insper.chouse.pool.event;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RabbitEventoPublisher implements EventoPublisher {

    public static final String FILA = "votos.registrados";

    private final RabbitTemplate rabbitTemplate;

    @Override
    public void publicar(VotoRegistrado evento) {
        rabbitTemplate.convertAndSend(FILA, evento);
    }
}
