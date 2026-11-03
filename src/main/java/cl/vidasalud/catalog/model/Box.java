package cl.vidasalud.catalog.model;

import jakarta.persistence.*;

@Entity
@Table(name = "boxes")
public class Box {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false, unique = true) private String nombre;
    @Column(nullable = false) private boolean activo = true;

    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
}
