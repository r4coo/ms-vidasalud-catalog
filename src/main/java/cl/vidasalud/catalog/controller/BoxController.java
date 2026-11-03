package cl.vidasalud.catalog.controller;

import cl.vidasalud.catalog.exception.NotFoundException;
import cl.vidasalud.catalog.model.Box;
import cl.vidasalud.catalog.repository.BoxRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/** CRUD de boxes. Expuesto como /api/catalog/boxes (escritura: Admin / Recepcionista en el BFF). */
@RestController
@RequestMapping("/api/catalog/boxes")
public class BoxController {
    private final BoxRepository repository;

    public BoxController(BoxRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Box> listar() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public Box obtener(@PathVariable Long id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("Box no encontrado"));
    }

    @PostMapping
    public ResponseEntity<Box> crear(@RequestBody Box body) {
        return ResponseEntity.status(HttpStatus.CREATED).body(repository.save(body));
    }

    @PutMapping("/{id}")
    public Box actualizar(@PathVariable Long id, @RequestBody Box body) {
        Box actual = obtener(id);
        actual.setNombre(body.getNombre());
        actual.setActivo(body.isActivo());
        return repository.save(actual);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        obtener(id);
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
