package cl.vidasalud.catalog.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import cl.vidasalud.catalog.dto.ConfirmarAtencionCommand;
import cl.vidasalud.catalog.model.Cupo;
import cl.vidasalud.catalog.repository.CupoRepository;
import cl.vidasalud.catalog.repository.PrestacionRepository;
import cl.vidasalud.catalog.repository.ReservaRepository;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class CupoRestantesTest {
    private final CupoRepository cupos = mock(CupoRepository.class);
    private final CupoService service = new CupoService(cupos, mock(ReservaRepository.class),
            mock(PrestacionRepository.class));
    private final ConfirmarAtencionCommand cmd = new ConfirmarAtencionCommand(UUID.randomUUID(), "a@b.cl", 1L,
            "Medicina general", LocalDate.of(2026, 11, 3), LocalTime.of(10, 0));

    @Test
    void devuelveLosCuposQueQuedan() {
        Cupo c = new Cupo();
        c.setDisponibles(4);
        when(cupos.findFirstByPrestacionIdAndFechaAndHoraAndDisponiblesGreaterThan(anyLong(), any(), any(), anyInt()))
                .thenReturn(Optional.of(c));
        assertEquals(4, service.restantes(cmd));
    }

    @Test
    void sinCupos_devuelveCero() {
        when(cupos.findFirstByPrestacionIdAndFechaAndHoraAndDisponiblesGreaterThan(anyLong(), any(), any(), anyInt()))
                .thenReturn(Optional.empty());
        assertEquals(0, service.restantes(cmd));
    }
}
