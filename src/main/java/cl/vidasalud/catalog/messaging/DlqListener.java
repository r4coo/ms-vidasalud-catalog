package cl.vidasalud.catalog.messaging;

import com.rabbitmq.client.Channel;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/** Consume la DLQ de comandos de confirmación y deja registro en logs de cada mensaje no entregado. */
@Component
public class DlqListener {
    private static final Logger log = LoggerFactory.getLogger(DlqListener.class);
    private final AckSupport ack;

    public DlqListener(AckSupport ack) {
        this.ack = ack;
    }

    @RabbitListener(queues = RabbitNames.DLQ_CONFIRMAR)
    public void onDeadLetter(Message m, Channel channel) throws IOException {
        log.error("[DLQ] cola={} rk={} x-death={} body={}",
                RabbitNames.DLQ_CONFIRMAR,
                m.getMessageProperties().getReceivedRoutingKey(),
                m.getMessageProperties().getHeaders().get("x-death"),
                new String(m.getBody(), StandardCharsets.UTF_8));
        ack.ack(channel, m);
    }
}
