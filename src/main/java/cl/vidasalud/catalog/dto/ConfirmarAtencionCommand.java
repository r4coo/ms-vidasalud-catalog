package cl.vidasalud.catalog.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

/** Comando (exchange direct): "confirmar esta atención" → el catálogo descuenta el cupo del box. */
public record ConfirmarAtencionCommand(UUID atencionId, String paciente, Long prestacionId, String prestacion,
                                       LocalDate fecha, LocalTime hora) {}
