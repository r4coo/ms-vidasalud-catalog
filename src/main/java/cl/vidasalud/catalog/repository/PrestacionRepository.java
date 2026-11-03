package cl.vidasalud.catalog.repository;

import cl.vidasalud.catalog.model.Prestacion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PrestacionRepository extends JpaRepository<Prestacion, Long> {}
