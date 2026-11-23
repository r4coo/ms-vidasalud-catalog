package cl.vidasalud.catalog.messaging.kafka;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import cl.vidasalud.catalog.dto.ConfirmarAtencionCommand;
import cl.vidasalud.catalog.dto.CupoOcupacionEvent;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.kafka.core.KafkaTemplate;

class CupoKafkaProducerTest {
    private static final String TOPIC = "vidasalud.catalogo.cupos";
    private KafkaTemplate<String, CupoOcupacionEvent> template;
    private CupoKafkaProducer producer;
    private final ConfirmarAtencionCommand cmd = new ConfirmarAtencionCommand(UUID.randomUUID(), "p@vidasalud.cl",
            2L, "Kinesiología", LocalDate.of(2026, 11, 3), LocalTime.of(10, 0));

    @SuppressWarnings("unchecked")
    @BeforeEach
    void setUp() {
        template = mock(KafkaTemplate.class);
        producer = new CupoKafkaProducer(template, TOPIC);
    }

    @Test
    void publica_conClaveCompuestaYCuposRestantes() {
        when(template.send(anyString(), anyString(), any(CupoOcupacionEvent.class)))
                .thenReturn(CompletableFuture.completedFuture(null));

        producer.publicarOcupacion(cmd, 3);

        ArgumentCaptor<CupoOcupacionEvent> captor = ArgumentCaptor.forClass(CupoOcupacionEvent.class);
        verify(template).send(eq(TOPIC), eq("2|2026-11-03|10:00"), captor.capture());
        assertEquals(3, captor.getValue().disponibles());
        assertEquals("Kinesiología", captor.getValue().prestacion());
        assertEquals(CupoOcupacionEvent.VERSION, captor.getValue().version());
    }

    @Test
    void siKafkaFalla_noPropagaLaExcepcion() {
        when(template.send(anyString(), anyString(), any(CupoOcupacionEvent.class)))
                .thenThrow(new IllegalStateException("broker caído"));
        assertDoesNotThrow(() -> producer.publicarOcupacion(cmd, 0));
    }

    @Test
    void siElEnvioFallaDespues_soloSeRegistra() {
        when(template.send(anyString(), anyString(), any(CupoOcupacionEvent.class)))
                .thenReturn(CompletableFuture.failedFuture(new RuntimeException("timeout")));
        assertDoesNotThrow(() -> producer.publicarOcupacion(cmd, 1));
    }
}
