package cl.vidasalud.catalog.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "cupos")
public class Cupo {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(name = "prestacion_id", nullable = false) private Long prestacionId;
    @Column(name = "box_id", nullable = false) private Long boxId;
    @Column(nullable = false) private LocalDate fecha;
    @Column(nullable = false) private LocalTime hora;
    @Column(nullable = false) private int disponibles;

    public Long getId() { return id; }
    public Long getPrestacionId() { return prestacionId; }
    public void setPrestacionId(Long prestacionId) { this.prestacionId = prestacionId; }
    public Long getBoxId() { return boxId; }
    public void setBoxId(Long boxId) { this.boxId = boxId; }
    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public LocalTime getHora() { return hora; }
    public void setHora(LocalTime hora) { this.hora = hora; }
    public int getDisponibles() { return disponibles; }
    public void setDisponibles(int disponibles) { this.disponibles = disponibles; }
}
