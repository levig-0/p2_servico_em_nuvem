package com.biblioteca.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/categorias")
@CrossOrigin(origins = "*")
public class CategoriaController {
    private final CategoriaRepository repo;

    public CategoriaController(CategoriaRepository repo) { this.repo = repo; }

    @GetMapping
    public List<Categoria> listar() { return repo.findAll(); }

    @GetMapping("/{id}")
    public ResponseEntity<Categoria> buscar(@PathVariable Integer id) {
        return repo.findById(id).map(ResponseEntity::ok)
                   .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Categoria criar(@RequestBody Categoria c) {
        c.setId(null);
        return repo.save(c);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Categoria> atualizar(@PathVariable Integer id, @RequestBody Categoria c) {
        return repo.findById(id).map(existente -> {
            existente.setNome(c.getNome());
            return ResponseEntity.ok(repo.save(existente));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Integer id) {
        if (!repo.existsById(id)) return ResponseEntity.notFound().build();
        repo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}