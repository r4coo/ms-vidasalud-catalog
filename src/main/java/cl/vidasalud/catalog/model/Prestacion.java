package cl.vidasalud.catalog.model;

import jakarta.persistence.*;

@Entity
@Table(name = "prestaciones")
public class Prestacion {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false, unique = true) private String nombre;
    private String descripcion;
    @Column(name = "duracion_min", nullable = false) private int duracionMin = 30;
    @Column(nullable = false) private boolean activa = true;

    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public int getDuracionMin() { return duracionMin; }
    public void setDuracionMin(int duracionMin) { this.duracionMin = duracionMin; }
    public boolean isActiva() { return activa; }
    public void setActiva(boolean activa) { this.activa = activa; }
}
