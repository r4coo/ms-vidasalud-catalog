package cl.vidasalud.catalog.messaging.kafka;

import cl.vidasalud.catalog.dto.CupoOcupacionEvent;
import cl.vidasalud.catalog.dto.ConfirmarAtencionCommand;
import java.time.Instant;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * Productor Kafka del catálogo: publica los cupos que quedan en un horario tras cada reserva.
 * Un fallo de Kafka se registra, pero nunca afecta al flujo RabbitMQ ni a la base de datos.
 */
@Component
public class CupoKafkaProducer {
    private static final Logger log = LoggerFactory.getLogger(CupoKafkaProducer.class);

    private final KafkaTemplate<String, CupoOcupacionEvent> template;
    private final String topic;

    public CupoKafkaProducer(KafkaTemplate<String, CupoOcupacionEvent> template,
                             @Value("${app.kafka.topic.catalogo-cupos}") String topic) {
        this.template = template;
        this.topic = topic;
    }

    public void publicarOcupacion(ConfirmarAtencionCommand cmd, int disponibles) {
        CupoOcupacionEvent evento = new CupoOcupacionEvent(UUID.randomUUID().toString(), CupoOcupacionEvent.VERSION,
                cmd.prestacionId(), cmd.prestacion(), cmd.fecha(), cmd.hora(), disponibles, Instant.now());
        String clave = CupoOcupacionEvent.clave(cmd.prestacionId(), cmd.fecha(), cmd.hora());
        try {
            template.send(topic, clave, evento).whenComplete((resultado, error) -> {
                if (error != null) {
                    log.warn("Kafka: no se pudo publicar la ocupación {}: {}", clave, error.toString());
                } else if (resultado != null && resultado.getRecordMetadata() != null) {
                    log.info("Kafka ← cupo {} disponibles={} partición={} offset={}", clave, disponibles,
                            resultado.getRecordMetadata().partition(), resultado.getRecordMetadata().offset());
                }
            });
        } catch (RuntimeException e) {
            log.warn("Kafka no disponible; la ocupación {} no se publicó: {}", clave, e.toString());
        }
    }
}
