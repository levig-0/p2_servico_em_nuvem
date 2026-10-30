CREATE TABLE IF NOT EXISTS categorias (
  id   INT AUTO_INCREMENT PRIMARY KEY,
  nome VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS livros (
  id               INT AUTO_INCREMENT PRIMARY KEY,
  titulo           VARCHAR(200) NOT NULL,
  autor            VARCHAR(150) NOT NULL,
  isbn             VARCHAR(20),
  ano_publicacao   INT,
  categoria_id     INT NOT NULL,
  criado_em        TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_livros_categoria
    FOREIGN KEY (categoria_id) REFERENCES categorias(id)
);