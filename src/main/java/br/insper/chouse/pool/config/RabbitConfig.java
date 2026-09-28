package br.insper.chouse.pool.config;

import br.insper.chouse.pool.event.RabbitEventoPublisher;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    @Bean
    Queue votosRegistradosQueue() {
        return new Queue(RabbitEventoPublisher.FILA, true);
    }
}
