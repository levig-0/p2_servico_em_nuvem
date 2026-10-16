# Especificação do Sistema - Gestão de Biblioteca em Nuvem

## 1. Descrição do Problema
Bibliotecas acadêmicas enfrentam problemas para gerenciar seu acervo físico, registrar fotos/capas atualizadas de livros e emitir relatórios gerenciais sobre as categorias de livros mais emprestadas. 

O objetivo deste projeto é disponibilizar um sistema web publicado na AWS para gerenciar livros, leitores e empréstimos, permitindo upload seguro de imagens de capa, processamento automático em nuvem e geração de relatórios consolidados em tempo real no banco de dados relacional.

---

## 2. Personas

1. **Ana Maria (Bibliotecária / Admin)**
   * **Perfil:** Responsável por cadastrar livros no acervo, enviar a foto da capa do livro, registrar empréstimos e emitir relatórios de movimentação.
   * **Necessidade:** Processo simples para adicionar capas de livros em alta qualidade sem travar o sistema e relatórios resumidos por categoria.

2. **Lucas Silva (Estudante / Leitor)**
   * **Perfil:** Aluno universitário que consulta a biblioteca para empréstimo de materiais.
   * **Necessidade:** Consultar livros disponíveis com a visualização da capa do exemplar e histórico de empréstimos.

---

## 3. Aplicação dos Requisitos Funcionais (RF-01 a RF-05) ao Domínio de Biblioteca

* **RF-01 (CRUD no Amazon RDS):** O sistema permitirá o CRUD completo de duas entidades relacionadas (1:N): **Livro** e **Empréstimo**, persistidas em um banco de dados relacional **Amazon RDS (PostgreSQL/MySQL)**.
* **RF-02 (Upload via Pre-Signed URL no S3):** Para cadastrar a capa de um livro, o backend gera uma **URL pré-assinada do Amazon S3** para que o front-end faça o upload direto do arquivo (imagem/JPG/PNG) sem sobrecarregar o backend.
* **RF-03 (Processamento Assíncrono via S3 Event Trigger + Lambda):** Assim que a foto da capa é enviada ao S3, um evento dispara uma função **AWS Lambda**, que valida/extrai os metadados do arquivo (ex: tamanho, extensão, dimensões) e atualiza o registro do **Livro no RDS** marcando a capa como "Processada".
* **RF-04 (Relatório com JOIN/GROUP BY via API Gateway + Lambda):** O sistema exporá a rota `GET /relatorios/emprestimos-por-categoria` integrada ao **API Gateway** e executada por uma **AWS Lambda**, que consulta o RDS fazendo **JOIN** entre as tabelas `Livro` e `Empréstimo` e **GROUP BY** por categoria para retornar estatísticas de uso.
* **RF-05 (Front-end Web Publicado):** Interface web moderna hospedada em um bucket **S3 Static Website Hosting** consumindo as APIs REST do sistema.

---

## 4. Histórias de Usuário e Critérios de Aceite

### US-01: Cadastro de Livro e Geração de Empréstimo (RF-01)
* **Como** Bibliotecária (Ana)
* **Quero** cadastrar um novo livro e registrar empréstimos vinculados a ele no banco relacional
* **Para que** os dados fiquem salvos permanentemente no Amazon RDS.

#### Critérios de Aceite:
* **Cenário 1: Cadastro de livro no RDS com sucesso**
  * **Dado** que estou autenticada como Bibliotecária
  * **Quando** eu enviar os dados do livro (Título, Autor, Categoria, ISBN)
  * **Então** o registro deve ser persistido na tabela `livros` no Amazon RDS e retornar o ID gerado.
* **Cenário 2: Registro de empréstimo (Relacionamento 1:N)**
  * **Dado** que um livro já existe no acervo do RDS
  * **Quando** eu vincular um novo empréstimo (Aluno, Data Inicio, Data Prevista) a este livro
  * **Então** a tabela `emprestimos` deve salvar a chave estrangeira `livro_id` referente ao livro selecionado.

---

### US-02: Upload da Capa do Livro via Pre-Signed URL (RF-02)
* **Como** Bibliotecária (Ana)
* **Quero** obter uma URL segura para enviar a imagem da capa do livro direto para o S3
* **Para que** o backend não precise processar o tráfego do arquivo pesado.

#### Critérios de Aceite:
* **Cenário 1: Solicitação de URL pré-assinada**
  * **Dado** que estou cadastrando a capa de um livro com ID válido
  * **Quando** o front-end chamar a rota `POST /livros/{id}/capa-url` informando o tipo do arquivo (`image/jpeg`)
  * **Então** o backend deve retornar uma Pre-Signed URL com expiração temporária (ex: 5 minutos) liberando a gravação no S3.

---

### US-03: Processamento Automático da Imagem de Capa (RF-03)
* **Como** Sistema de Biblioteca
* **Quero** processar automaticamente qualquer nova capa enviada ao bucket S3
* **Para que** os metadados do arquivo sejam salvos no banco de dados RDS.

#### Critérios de Aceite:
* **Cenário 1: Disparo da Lambda pós-upload**
  * **Dado** que uma foto de capa foi salva no bucket S3 via Pre-Signed URL
  * **Quando** o evento `s3:ObjectCreated:*` for disparado
  * **Então** a função AWS Lambda deve ser executada, ler a imagem do S3, extrair o tamanho/extensão e atualizar o registro correspondente no Amazon RDS com o link final e status `capa_processada = true`.

---

### US-04: Emissão de Relatório de Empréstimos por Categoria (RF-04)
* **Como** Bibliotecária (Ana)
* **Quero** visualizar um relatório agregando total de empréstimos por categoria de livro
* **Para que** eu identifique as áreas do acervo com maior demanda.

#### Critérios de Aceite:
* **Cenário 1: Consulta de relatório via API Gateway**
  * **Dado** que existem empréstimos gravados na base de dados do RDS
  * **Quando** eu acessar a rota `GET /relatorios/emprestimos-por-categoria`
  * **Então** o API Gateway deve invocar a Lambda do relatório, que executará uma query SQL com `JOIN` entre `livros` e `emprestimos` agrupando com `GROUP BY categoria` e retornando o JSON com a contagem.

---

### US-05: Interface do Usuário Integrada (RF-05)
* **Como** Leitor ou Bibliotecária
* **Quero** acessar a aplicação via navegador web no S3 Static Hosting
* **Para que** eu possa interagir com o catálogo, capas dos livros e relatórios sem instalar nada localmente.

#### Critérios de Aceite:
* **Cenário 1: Acesso ao Front-end estático**
  * **Dado** que navego para a URL do S3 Static Website Hosting
  * **Quando** a página carregar
  * **Então** o front-end deve efetuar as requisições HTTP para os endpoints das Lambdas/API Gateway e renderizar os dados do acervo.