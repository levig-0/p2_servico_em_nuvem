-- Adiciona colunas para controle da capa do livro (RF-02 e RF-03)
ALTER TABLE livros
ADD COLUMN capa_url VARCHAR(500) NULL,
ADD COLUMN capa_processada BOOLEAN DEFAULT FALSE,
ADD COLUMN capa_tamanho_bytes INT NULL;
