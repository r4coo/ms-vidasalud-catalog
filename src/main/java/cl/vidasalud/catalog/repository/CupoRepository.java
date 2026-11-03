package cl.vidasalud.catalog.repository;

import cl.vidasalud.catalog.model.Cupo;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CupoRepository extends JpaRepository<Cupo, Long> {
    List<Cupo> findByPrestacionIdAndFechaOrderByHora(Long prestacionId, LocalDate fecha);

    Optional<Cupo> findFirstByPrestacionIdAndFechaAndHoraAndDisponiblesGreaterThan(
            Long prestacionId, LocalDate fecha, LocalTime hora, int minimo);

    /** Descuento atómico: nunca deja el cupo en negativo aunque haya consumidores concurrentes. */
    @Modifying
    @Query("update Cupo c set c.disponibles = c.disponibles - 1 where c.id = :id and c.disponibles > 0")
    int descontar(@Param("id") Long id);
}
