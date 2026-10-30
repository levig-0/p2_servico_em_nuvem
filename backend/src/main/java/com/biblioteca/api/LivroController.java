package com.biblioteca.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/livros")
@CrossOrigin(origins = "*")
public class LivroController {
    private final LivroRepository repo;

    public LivroController(LivroRepository repo) { this.repo = repo; }

    @GetMapping
    public List<Livro> listar() { return repo.findAll(); }

    @GetMapping("/{id}")
    public ResponseEntity<Livro> buscar(@PathVariable Integer id) {
        return repo.findById(id).map(ResponseEntity::ok)
                   .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Livro criar(@RequestBody Livro l) {
        l.setId(null);
        return repo.save(l);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Livro> atualizar(@PathVariable Integer id, @RequestBody Livro l) {
        return repo.findById(id).map(existente -> {
            existente.setTitulo(l.getTitulo());
            existente.setAutor(l.getAutor());
            existente.setIsbn(l.getIsbn());
            existente.setAnoPublicacao(l.getAnoPublicacao());
            existente.setCategoria(l.getCategoria());
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