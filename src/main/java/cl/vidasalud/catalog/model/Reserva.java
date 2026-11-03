package cl.vidasalud.catalog.model;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "reservas")
public class Reserva {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(name = "atencion_id", nullable = false, unique = true) private UUID atencionId;
    @Column(name = "cupo_id", nullable = false) private Long cupoId;
    @Column(name = "creada_en", nullable = false) private Instant creadaEn = Instant.now();

    protected Reserva() {}

    public Reserva(UUID atencionId, Long cupoId) {
        this.atencionId = atencionId;
        this.cupoId = cupoId;
    }

    public UUID getAtencionId() { return atencionId; }
    public Long getCupoId() { return cupoId; }
}
