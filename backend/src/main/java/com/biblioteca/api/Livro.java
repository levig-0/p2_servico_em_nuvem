package com.biblioteca.api;

import jakarta.persistence.*;

@Entity
@Table(name = "livros")
public class Livro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String titulo;

    @Column(nullable = false)
    private String autor;

    private String isbn;

    @Column(name = "ano_publicacao")
    private Integer anoPublicacao;

    @ManyToOne(optional = false)
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;

    @Column(name = "capa_url")
    private String capaUrl;

    @Column(name = "capa_processada")
    private Boolean capaProcessada = false;

    @Column(name = "capa_tamanho_bytes")
    private Integer capaTamanhoBytes;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getAutor() { return autor; }
    public void setAutor(String autor) { this.autor = autor; }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public Integer getAnoPublicacao() { return anoPublicacao; }
    public void setAnoPublicacao(Integer anoPublicacao) { this.anoPublicacao = anoPublicacao; }

    public Categoria getCategoria() { return categoria; }
    public void setCategoria(Categoria categoria) { this.categoria = categoria; }

    public String getCapaUrl() { return capaUrl; }
    public void setCapaUrl(String capaUrl) { this.capaUrl = capaUrl; }

    public Boolean getCapaProcessada() { return capaProcessada; }
    public void setCapaProcessada(Boolean capaProcessada) { this.capaProcessada = capaProcessada; }

    public Integer getCapaTamanhoBytes() { return capaTamanhoBytes; }
    public void setCapaTamanhoBytes(Integer capaTamanhoBytes) { this.capaTamanhoBytes = capaTamanhoBytes; }
}