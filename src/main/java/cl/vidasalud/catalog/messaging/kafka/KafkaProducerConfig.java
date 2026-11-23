package cl.vidasalud.catalog.messaging.kafka;

import cl.vidasalud.catalog.dto.CupoOcupacionEvent;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

/** Cliente productor Kafka del catálogo. */
@Configuration
public class KafkaProducerConfig {

    @Bean
    public ProducerFactory<String, CupoOcupacionEvent> cupoProducerFactory(
            @Value("${app.kafka.bootstrap-servers}") String bootstrapServers) {
        Map<String, Object> cfg = new HashMap<>();
        cfg.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        cfg.put(ProducerConfig.ACKS_CONFIG, "all");
        cfg.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
        cfg.put(ProducerConfig.RETRIES_CONFIG, 3);
        cfg.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, 5000);
        cfg.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, 10000);
        cfg.put(ProducerConfig.LINGER_MS_CONFIG, 5);
        cfg.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "gzip");
        cfg.put(ProducerConfig.MAX_BLOCK_MS_CONFIG, 3000);
        return new DefaultKafkaProducerFactory<>(cfg, new StringSerializer(), new EventoJsonSerializer<>());
    }

    @Bean
    public KafkaTemplate<String, CupoOcupacionEvent> cupoKafkaTemplate(
            ProducerFactory<String, CupoOcupacionEvent> cupoProducerFactory) {
        return new KafkaTemplate<>(cupoProducerFactory);
    }
}
