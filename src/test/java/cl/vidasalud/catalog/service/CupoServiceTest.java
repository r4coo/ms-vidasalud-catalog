package cl.vidasalud.catalog.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
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

class CupoServiceTest {
    private final CupoRepository cupos = mock(CupoRepository.class);
    private final ReservaRepository reservas = mock(ReservaRepository.class);
    private final PrestacionRepository prestaciones = mock(PrestacionRepository.class);
    private final CupoService service = new CupoService(cupos, reservas, prestaciones);
    private final ConfirmarAtencionCommand cmd = new ConfirmarAtencionCommand(UUID.randomUUID(), "a@b.cl", 1L,
            "Medicina general", LocalDate.of(2026, 11, 3), LocalTime.of(10, 0));

    @Test
    void descuentaElCupoAlConfirmar() {
        when(prestaciones.existsById(1L)).thenReturn(true);
        Cupo c = new Cupo();
        when(cupos.findFirstByPrestacionIdAndFechaAndHoraAndDisponiblesGreaterThan(anyLong(), any(), any(), anyInt()))
                .thenReturn(Optional.of(c));
        when(cupos.descontar(any())).thenReturn(1);
        assertEquals(CupoService.Resultado.RESERVADO, service.reservar(cmd));
        verify(reservas).save(any());
    }

    @Test
    void sinCupos_noReserva() {
        when(prestaciones.existsById(1L)).thenReturn(true);
        when(cupos.findFirstByPrestacionIdAndFechaAndHoraAndDisponiblesGreaterThan(anyLong(), any(), any(), anyInt()))
                .thenReturn(Optional.empty());
        assertEquals(CupoService.Resultado.SIN_CUPOS, service.reservar(cmd));
        verify(reservas, never()).save(any());
    }

    @Test
    void prestacionInexistente_esErrorNoRecuperable() {
        when(prestaciones.existsById(1L)).thenReturn(false);
        assertThrows(IllegalArgumentException.class, () -> service.reservar(cmd));
    }

    @Test
    void esIdempotenteAnteReentrega() {
        when(prestaciones.existsById(1L)).thenReturn(true);
        when(reservas.existsByAtencionId(cmd.atencionId())).thenReturn(true);
        assertEquals(CupoService.Resultado.YA_RESERVADO, service.reservar(cmd));
        verify(cupos, never()).descontar(any());
    }
}
