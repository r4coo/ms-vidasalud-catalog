package cl.vidasalud.catalog.messaging;

import static cl.vidasalud.catalog.messaging.RabbitNames.*;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Topología: exchanges, colas, DLQ y bindings. Colas "quorum" para replicar entre los dos nodos.
 * Está separada de la lógica de negocio; se declara de forma idempotente desde cada microservicio.
 */
@Configuration
public class RabbitTopologyConfig {

    // ---- Exchanges
    @Bean DirectExchange atencionesExchange() { return new DirectExchange(EX_ATENCIONES, true, false); }
    @Bean TopicExchange eventosExchange() { return new TopicExchange(EX_EVENTOS, true, false); }
    @Bean DirectExchange dlxExchange() { return new DirectExchange(EX_DLX, true, false); }

    // ---- Colas principales (con DLX, TTL y límite de largo)
    @Bean Queue confirmarQueue() { return main(Q_CONFIRMAR, DLQ_CONFIRMAR); }
    @Bean Queue notificacionesQueue() { return main(Q_NOTIFICACIONES, DLQ_NOTIFICACIONES); }
    @Bean Queue auditoriaQueue() { return main(Q_AUDITORIA, DLQ_AUDITORIA); }

    // ---- DLQ
    @Bean Queue confirmarDlq() { return dlq(DLQ_CONFIRMAR); }
    @Bean Queue notificacionesDlq() { return dlq(DLQ_NOTIFICACIONES); }
    @Bean Queue auditoriaDlq() { return dlq(DLQ_AUDITORIA); }

    // ---- Bindings
    @Bean Binding confirmarBinding() {
        return BindingBuilder.bind(confirmarQueue()).to(atencionesExchange()).with(RK_ATENCION_CONFIRMAR);
    }
    @Bean Binding notificacionesBinding() {
        return BindingBuilder.bind(notificacionesQueue()).to(eventosExchange()).with(BIND_NOTIFICACIONES);
    }
    @Bean Binding auditoriaBinding() {
        return BindingBuilder.bind(auditoriaQueue()).to(eventosExchange()).with(BIND_AUDITORIA);
    }
    @Bean Binding confirmarDlqBinding() {
        return BindingBuilder.bind(confirmarDlq()).to(dlxExchange()).with(DLQ_CONFIRMAR);
    }
    @Bean Binding notificacionesDlqBinding() {
        return BindingBuilder.bind(notificacionesDlq()).to(dlxExchange()).with(DLQ_NOTIFICACIONES);
    }
    @Bean Binding auditoriaDlqBinding() {
        return BindingBuilder.bind(auditoriaDlq()).to(dlxExchange()).with(DLQ_AUDITORIA);
    }

    private static Queue main(String name, String dlqKey) {
        return QueueBuilder.durable(name).quorum()
                .deadLetterExchange(EX_DLX)
                .deadLetterRoutingKey(dlqKey)
                .ttl(QUEUE_TTL_MS)
                .maxLength(QUEUE_MAX_LENGTH)
                .overflow(QueueBuilder.Overflow.dropHead) // el más antiguo pasa a la DLQ
                .build();
    }

    private static Queue dlq(String name) {
        return QueueBuilder.durable(name).quorum()
                .ttl(DLQ_TTL_MS)
                .maxLength(DLQ_MAX_LENGTH)
                .overflow(QueueBuilder.Overflow.dropHead)
                .build();
    }
}
