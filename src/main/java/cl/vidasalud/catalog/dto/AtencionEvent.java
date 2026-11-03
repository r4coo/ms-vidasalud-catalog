package cl.vidasalud.catalog.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

/** Evento (exchange topic). tipo: CONFIRMADA / RECHAZADA (del catálogo) o ESTADO (cambio de estado). */
public record AtencionEvent(UUID atencionId, String tipo, String estado, String motivo, String paciente,
                            String prestacion, LocalDate fecha, LocalTime hora) {
    public static final String CONFIRMADA = "CONFIRMADA";
    public static final String RECHAZADA = "RECHAZADA";
    public static final String ESTADO = "ESTADO";
}
