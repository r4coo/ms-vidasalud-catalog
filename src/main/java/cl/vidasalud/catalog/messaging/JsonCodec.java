package cl.vidasalud.catalog.messaging;

import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

/** (De)serialización JSON de mensajes. Un JSON inválido es un error NO recuperable. */
@Component
public class JsonCodec {
    private final JsonMapper mapper = JsonMapper.builder().build();

    public byte[] toBytes(Object value) {
        return mapper.writeValueAsBytes(value);
    }

    public <T> T fromBytes(byte[] body, Class<T> type) {
        try {
            T value = mapper.readValue(body, type);
            if (value == null) throw new IllegalArgumentException("Mensaje vacío");
            return value;
        } catch (RuntimeException e) {
            throw new IllegalArgumentException("Mensaje inválido: " + e.getMessage(), e);
        }
    }
}
