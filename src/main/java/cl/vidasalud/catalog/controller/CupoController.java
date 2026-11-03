package cl.vidasalud.catalog.controller;

import cl.vidasalud.catalog.exception.NotFoundException;
import cl.vidasalud.catalog.model.Cupo;
import cl.vidasalud.catalog.repository.CupoRepository;
import java.time.LocalDate;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/** CRUD de cupos. Expuesto como /api/catalog/cupos. */
@RestController
@RequestMapping("/api/catalog/cupos")
public class CupoController {
    private final CupoRepository repository;

    public CupoController(CupoRepository repository) {
        this.repository = repository;
    }

    /** Cupos con disponibilidad de una prestación en una fecha (para la pantalla de agendamiento). */
    @GetMapping
    public List<Cupo> disponibles(@RequestParam Long prestacionId, @RequestParam LocalDate fecha) {
        return repository.findByPrestacionIdAndFechaOrderByHora(prestacionId, fecha).stream()
                .filter(c -> c.getDisponibles() > 0).toList();
    }

    @PostMapping
    public ResponseEntity<Cupo> crear(@RequestBody Cupo body) {
        return ResponseEntity.status(HttpStatus.CREATED).body(repository.save(body));
    }

    @PutMapping("/{id}")
    public Cupo actualizar(@PathVariable Long id, @RequestBody Cupo body) {
        Cupo actual = repository.findById(id).orElseThrow(() -> new NotFoundException("Cupo no encontrado"));
        actual.setDisponibles(body.getDisponibles());
        actual.setHora(body.getHora());
        actual.setFecha(body.getFecha());
        actual.setBoxId(body.getBoxId());
        actual.setPrestacionId(body.getPrestacionId());
        return repository.save(actual);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (!repository.existsById(id)) throw new NotFoundException("Cupo no encontrado");
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
