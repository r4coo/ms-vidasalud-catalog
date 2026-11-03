package cl.vidasalud.catalog.messaging;

import cl.vidasalud.catalog.dto.AtencionEvent;
import cl.vidasalud.catalog.dto.ConfirmarAtencionCommand;
import java.util.UUID;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/** Productor de eventos de dominio hacia el exchange topic. */
@Component
public class EventPublisher {
    private final RabbitTemplate template;
    private final JsonCodec json;

    public EventPublisher(RabbitTemplate template, JsonCodec json) {
        this.template = template;
        this.json = json;
    }

    public void publicar(ConfirmarAtencionCommand c, String tipo, String motivo) {
        boolean ok = AtencionEvent.CONFIRMADA.equals(tipo);
        AtencionEvent event = new AtencionEvent(c.atencionId(), tipo, ok ? "CONFIRMADA" : "SOLICITADA", motivo,
                c.paciente(), c.prestacion(), c.fecha(), c.hora());
        Message message = MessageBuilder.withBody(json.toBytes(event))
                .setContentType(MessageProperties.CONTENT_TYPE_JSON)
                .setDeliveryMode(MessageDeliveryMode.PERSISTENT)
                .setMessageId(UUID.randomUUID().toString())
                .build();
        template.send(RabbitNames.EX_EVENTOS,
                ok ? RabbitNames.RK_ATENCION_CONFIRMADA : RabbitNames.RK_ATENCION_RECHAZADA, message);
    }
}
