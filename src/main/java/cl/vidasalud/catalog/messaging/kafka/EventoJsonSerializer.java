package cl.vidasalud.catalog.messaging.kafka;

import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;
import tools.jackson.databind.json.JsonMapper;

/** Serializer Kafka: convierte el DTO del evento en JSON (UTF-8). */
public class EventoJsonSerializer<T> implements Serializer<T> {
    private final JsonMapper mapper = JsonMapper.builder().build();

    @Override
    public byte[] serialize(String topic, T data) {
        if (data == null) return null;
        try {
            return mapper.writeValueAsBytes(data);
        } catch (RuntimeException e) {
            throw new SerializationException("No se pudo serializar el evento para el tópico " + topic, e);
        }
    }
}
