package cl.vidasalud.catalog.controller;

import cl.vidasalud.catalog.exception.NotFoundException;
import cl.vidasalud.catalog.model.Prestacion;
import cl.vidasalud.catalog.repository.PrestacionRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/** CRUD de prestaciones. Expuesto como /api/catalog/prestaciones (escritura: Admin / Recepcionista en el BFF). */
@RestController
@RequestMapping("/api/catalog/prestaciones")
public class PrestacionController {
    private final PrestacionRepository repository;

    public PrestacionController(PrestacionRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Prestacion> listar() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public Prestacion obtener(@PathVariable Long id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("Prestacion no encontrado"));
    }

    @PostMapping
    public ResponseEntity<Prestacion> crear(@RequestBody Prestacion body) {
        return ResponseEntity.status(HttpStatus.CREATED).body(repository.save(body));
    }

    @PutMapping("/{id}")
    public Prestacion actualizar(@PathVariable Long id, @RequestBody Prestacion body) {
        Prestacion actual = obtener(id);
        actual.setNombre(body.getNombre());
        actual.setDescripcion(body.getDescripcion());
        actual.setDuracionMin(body.getDuracionMin());
        actual.setActiva(body.isActiva());
        return repository.save(actual);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        obtener(id);
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
