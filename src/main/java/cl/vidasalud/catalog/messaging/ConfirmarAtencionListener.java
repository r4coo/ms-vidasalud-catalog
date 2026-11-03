package cl.vidasalud.catalog.messaging;

import cl.vidasalud.catalog.dto.AtencionEvent;
import cl.vidasalud.catalog.dto.ConfirmarAtencionCommand;
import cl.vidasalud.catalog.service.CupoService;
import com.rabbitmq.client.Channel;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

/**
 * Dominio catálogo: recibe el comando "confirmar atención", descuenta el cupo del box,
 * confirma (ACK) y publica el evento con el resultado.
 */
@Component
public class ConfirmarAtencionListener {
    private static final Logger log = LoggerFactory.getLogger(ConfirmarAtencionListener.class);

    private final CupoService cupos;
    private final EventPublisher events;
    private final JsonCodec json;
    private final AckSupport ack;

    public ConfirmarAtencionListener(CupoService cupos, EventPublisher events, JsonCodec json, AckSupport ack) {
        this.cupos = cupos;
        this.events = events;
        this.json = json;
        this.ack = ack;
    }

    @RabbitListener(queues = RabbitNames.Q_CONFIRMAR)
    public void onComando(Message message, Channel channel) throws IOException {
        try {
            ConfirmarAtencionCommand cmd = json.fromBytes(message.getBody(), ConfirmarAtencionCommand.class);
            if (cmd.atencionId() == null || cmd.prestacionId() == null || cmd.fecha() == null || cmd.hora() == null) {
                throw new IllegalArgumentException("Comando incompleto");
            }
            switch (cupos.reservar(cmd)) {
                case RESERVADO, YA_RESERVADO -> events.publicar(cmd, AtencionEvent.CONFIRMADA, null);
                case SIN_CUPOS -> events.publicar(cmd, AtencionEvent.RECHAZADA, "Sin cupos disponibles en ese horario");
            }
            ack.ack(channel, message);
            log.info("Comando procesado atencion={}", cmd.atencionId());
        } catch (IllegalArgumentException e) {            // no recuperable -> NACK sin requeue -> DLQ
            ack.rejectToDlq(channel, message, e.getMessage());
        } catch (DataAccessException | AmqpException e) { // recuperable -> reintentos acotados
            ack.retryOrDlq(channel, message, RabbitNames.Q_CONFIRMAR, e);
        } catch (RuntimeException e) {
            ack.rejectToDlq(channel, message, "error inesperado: " + e);
        }
    }
}
