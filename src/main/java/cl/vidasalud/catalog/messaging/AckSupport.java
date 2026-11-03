package cl.vidasalud.catalog.messaging;

import com.rabbitmq.client.Channel;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/** Utilidades de ACK manual: ack, nack sin requeue (-> DLQ) y reintentos acotados. */
@Component
public class AckSupport {
    private static final Logger log = LoggerFactory.getLogger(AckSupport.class);
    public static final String RETRY_HEADER = "x-app-retries";
    public static final int MAX_RETRIES = 3;

    private final RabbitTemplate template;

    public AckSupport(RabbitTemplate template) {
        this.template = template;
    }

    public void ack(Channel channel, Message m) throws IOException {
        channel.basicAck(m.getMessageProperties().getDeliveryTag(), false);
    }

    /** Error no recuperable: NACK sin requeue; el DLX lo envía a la DLQ. */
    public void rejectToDlq(Channel channel, Message m, String reason) throws IOException {
        log.warn("NACK sin requeue -> DLQ. motivo={} messageId={}", reason, m.getMessageProperties().getMessageId());
        channel.basicNack(m.getMessageProperties().getDeliveryTag(), false, false);
    }

    /** Error recuperable: reintenta (republica en la misma cola) hasta MAX_RETRIES; luego DLQ. */
    public void retryOrDlq(Channel channel, Message m, String queue, Exception cause) throws IOException {
        Integer retries = m.getMessageProperties().getHeader(RETRY_HEADER);
        int n = retries == null ? 0 : retries;
        if (n >= MAX_RETRIES) {
            rejectToDlq(channel, m, "reintentos agotados (" + n + "): " + cause.getMessage());
            return;
        }
        log.warn("Error recuperable, reintento {}/{} en {}: {}", n + 1, MAX_RETRIES, queue, cause.getMessage());
        Message copy = MessageBuilder.fromMessage(m).setHeader(RETRY_HEADER, n + 1).build();
        template.send("", queue, copy); // exchange por defecto: enruta por nombre de cola
        ack(channel, m);
    }
}
