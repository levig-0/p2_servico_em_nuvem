# Especificação do Sistema - Gestão de Biblioteca em Nuvem

## 1. Descrição do Problema
Bibliotecas acadêmicas têm dificuldade para organizar o acervo por categoria, manter as capas dos livros atualizadas e saber como o acervo está distribuído.

Este projeto disponibiliza um sistema web publicado na AWS para cadastrar categorias e livros, enviar a capa de cada livro de forma segura, processar a capa automaticamente em nuvem e emitir um relatório consolidado a partir do banco relacional.

## 2. Personas

1. **Ana Maria (Bibliotecária / Admin)**
   * **Perfil:** cadastra categorias e livros, envia a capa e consulta o relatório do acervo.
   * **Necessidade:** processo simples para manter o acervo organizado e adicionar capas sem travar o sistema, com um relatório resumido por categoria.

2. **Lucas Silva (Estudante / Leitor)**
   * **Perfil:** aluno universitário que consulta o acervo.
   * **Necessidade:** encontrar livros por categoria e ver a capa de cada exemplar.

## 3. Aplicação dos Requisitos ao Domínio de Biblioteca

* **RF-01 (CRUD no Amazon RDS):** CRUD completo de **Categoria** e **Livro** (relação 1:N: uma categoria tem vários livros), persistidos em **Amazon RDS MySQL**.
* **RF-02 (Upload via URL pré-assinada):** o backend gera uma URL pré-assinada do S3 para que o front-end envie a capa do livro (JPG/PNG) direto ao bucket.
* **RF-03 (S3 → Lambda → RDS):** quando a capa chega ao bucket, um evento aciona uma Lambda que lê o tamanho, o tipo e a extensão do arquivo e atualiza o registro do livro no RDS (`capa_processada = true`). O resultado fica visível no CloudWatch.
* **RF-04 (Relatório):** `GET /relatorios/livros-por-categoria`, exposto pelo API Gateway e implementado em Lambda, com `JOIN` entre `categorias` e `livros` e `GROUP BY` por categoria. Retorna o total de livros e de capas processadas por categoria.
* **RF-05 (Front-end):** interface web em S3 Static Website que consome a API do CRUD (EC2) e o relatório (API Gateway).

Decisões de escopo:
* Não há autenticação nem controle de empréstimos nesta versão.
* O CRUD roda em EC2 com Docker (ADR-01 no `design.md`); Lambda é usada em RF-03 e RF-04.

## 4. Histórias de Usuário e Critérios de Aceite

### US-01: Gerenciar categorias e livros (RF-01)
* **Como** Bibliotecária (Ana)
* **Quero** cadastrar, consultar, alterar e excluir categorias e livros
* **Para que** o acervo fique salvo de forma permanente no Amazon RDS.

* **Cenário 1: Cadastrar categoria**
  * **Dado** que informo o nome de uma categoria nova
  * **Quando** eu enviar `POST /categorias`
  * **Então** a categoria é gravada na tabela `categorias` e a API devolve o `id` gerado.
* **Cenário 2: Cadastrar livro em uma categoria**
  * **Dado** que a categoria existe
  * **Quando** eu enviar `POST /livros` com título, autor, ISBN, ano e a categoria
  * **Então** o livro é gravado em `livros` com a chave estrangeira `categoria_id`.
* **Cenário 3: Consultar, alterar e excluir livro**
  * **Dado** que o livro existe
  * **Quando** eu usar `GET`, `PUT` ou `DELETE` em `/livros/{id}`
  * **Então** a API devolve, atualiza ou remove o registro, e a consulta direta ao RDS mostra o mesmo resultado.
* **Cenário 4: Excluir categoria com livros**
  * **Dado** que a categoria ainda tem livros
  * **Quando** eu tentar excluí-la
  * **Então** o banco recusa a exclusão pela chave estrangeira, e a categoria só pode ser removida depois dos seus livros.

### US-02: Enviar a capa do livro por URL pré-assinada (RF-02)
* **Como** Bibliotecária (Ana)
* **Quero** enviar a capa direto para o S3 por uma URL temporária
* **Para que** o backend não precise trafegar o arquivo.

* **Cenário 1: Solicitar a URL**
  * **Dado** que o livro existe
  * **Quando** o front-end chamar `POST /livros/{id}/capa-url` informando o tipo do arquivo (`image/jpeg` ou `image/png`)
  * **Então** o backend devolve uma URL pré-assinada que expira em poucos minutos.
* **Cenário 2: Enviar o arquivo**
  * **Dado** que recebi a URL
  * **Quando** o front-end enviar o arquivo com `PUT` para ela
  * **Então** o arquivo é gravado no bucket de capas sem passar pela API.

### US-03: Processar automaticamente a capa enviada (RF-03)
* **Como** sistema
* **Quero** processar toda capa nova do bucket
* **Para que** os dados do arquivo sejam registrados no banco.

* **Cenário 1: Evento dispara a Lambda**
  * **Dado** que uma capa foi gravada no bucket
  * **Quando** o evento `s3:ObjectCreated:*` ocorrer
  * **Então** a Lambda é executada, lê tamanho, tipo e extensão do arquivo e atualiza o livro correspondente no RDS com `capa_processada = true`.
* **Cenário 2: Evidência**
  * **Dado** que a Lambda executou
  * **Quando** eu abrir o grupo de logs dela no CloudWatch
  * **Então** vejo o registro da execução com o arquivo processado.

### US-04: Relatório de livros por categoria (RF-04)
* **Como** Bibliotecária (Ana)
* **Quero** ver quantos livros e quantas capas processadas há em cada categoria
* **Para que** eu entenda como o acervo está distribuído.

* **Cenário 1: Consultar o relatório**
  * **Dado** que existem categorias e livros no RDS
  * **Quando** eu acessar `GET /relatorios/livros-por-categoria`
  * **Então** o API Gateway aciona a Lambda, que consulta o RDS com `JOIN` e `GROUP BY categoria` e devolve um JSON com o total de livros e de capas processadas por categoria.

### US-05: Interface web (RF-05)
* **Como** Bibliotecária ou Leitor
* **Quero** usar o sistema pelo navegador
* **Para que** eu consulte o acervo, as capas e o relatório sem instalar nada.

* **Cenário 1: Acessar o front-end**
  * **Dado** que abro a URL do S3 Static Website
  * **Quando** a página carregar
  * **Então** ela lista categorias e livros consumindo a API do CRUD e exibe o relatório consumindo o API Gateway.
* **Cenário 2: Gerenciar pela interface**
  * **Dado** que estou na página
  * **Quando** eu criar, editar ou excluir um livro ou enviar uma capa
  * **Então** a mudança aparece na lista e fica gravada no RDS.