package cl.vidasalud.catalog.repository;

import cl.vidasalud.catalog.model.Reserva;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    boolean existsByAtencionId(UUID atencionId);
}
