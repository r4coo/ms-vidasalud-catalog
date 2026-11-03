package cl.vidasalud.catalog.service;

import cl.vidasalud.catalog.dto.ConfirmarAtencionCommand;
import cl.vidasalud.catalog.model.Cupo;
import cl.vidasalud.catalog.model.Reserva;
import cl.vidasalud.catalog.repository.CupoRepository;
import cl.vidasalud.catalog.repository.PrestacionRepository;
import cl.vidasalud.catalog.repository.ReservaRepository;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CupoService {
    public enum Resultado { RESERVADO, YA_RESERVADO, SIN_CUPOS }

    private final CupoRepository cupos;
    private final ReservaRepository reservas;
    private final PrestacionRepository prestaciones;

    public CupoService(CupoRepository cupos, ReservaRepository reservas, PrestacionRepository prestaciones) {
        this.cupos = cupos;
        this.reservas = reservas;
        this.prestaciones = prestaciones;
    }

    /**
     * Regla del catálogo: el cupo del box disminuye al confirmar la atención.
     * Idempotente ante reentregas: una reserva por atención.
     * @throws IllegalArgumentException si la prestación no existe (error no recuperable).
     */
    @Transactional
    public Resultado reservar(ConfirmarAtencionCommand c) {
        if (!prestaciones.existsById(c.prestacionId())) {
            throw new IllegalArgumentException("Prestación inexistente: " + c.prestacionId());
        }
        if (reservas.existsByAtencionId(c.atencionId())) return Resultado.YA_RESERVADO;

        Optional<Cupo> cupo = cupos.findFirstByPrestacionIdAndFechaAndHoraAndDisponiblesGreaterThan(
                c.prestacionId(), c.fecha(), c.hora(), 0);
        if (cupo.isEmpty() || cupos.descontar(cupo.get().getId()) == 0) return Resultado.SIN_CUPOS;

        reservas.save(new Reserva(c.atencionId(), cupo.get().getId()));
        return Resultado.RESERVADO;
    }
}
