package cl.vidasalud.catalog.dto;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Evento del tópico compactado vidasalud.catalogo.cupos. Clave = prestacionId|fecha|hora: con
 * cleanup.policy=compact Kafka conserva solo el último estado de cada horario.
 */
public record CupoOcupacionEvent(String eventId, int version, Long prestacionId, String prestacion,
                                 LocalDate fecha, LocalTime hora, int disponibles, Instant ocurridoEn) {
    public static final int VERSION = 1;

    public static String clave(Long prestacionId, LocalDate fecha, LocalTime hora) {
        return prestacionId + "|" + fecha + "|" + hora;
    }
}
