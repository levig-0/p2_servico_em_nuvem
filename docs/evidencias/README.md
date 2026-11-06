# Guia de Evidências Obrigatórias (docs/evidencias/)

Conforme exigido pelo enunciado do Projeto 2 para a **Entrega Final (06/11)**, este diretório deve conter os prints comprovando o funcionamento de todos os componentes na AWS.

Capture as imagens pelo Console AWS / Navegador e salve-as nesta pasta (`docs/evidencias/`) com os nomes sugeridos abaixo:

---

### 1. Infraestrutura de Rede e Segurança (RNF-01)
- [ ] `01_vpc_subnets.png`: Visualização do Mapa de Recursos da VPC `projeto02-vpc`, mostrando as 2 sub-redes públicas, as 2 sub-redes privadas e a Internet Gateway.
- [ ] `02_vpc_endpoint_s3.png`: Tabela de rotas associada às subnets privadas contendo o VPC Gateway Endpoint para o S3 (`com.amazonaws.us-east-1.s3`).
- [ ] `03_security_groups.png`: Configuração das regras de entrada do `app-sg` (portas 80, 8080, 22) e do `rds-sg` (porta 3306 com origem estrita no `app-sg` e `lambda-sg`).

---

### 2. Banco de Dados Amazon RDS (RF-01, RNF-01)
- [ ] `04_rds_config.png`: Tela do RDS MySQL (`projeto02-db`) mostrando `Acesso Público: Não` e a Subnet Group privada vinculada.
- [ ] `05_rds_tabelas_dados.png`: Print do terminal da EC2 (`mysql -h ...`) executando `SELECT * FROM categorias;` e `SELECT * FROM livros;` comprovando a persistência no RDS.

---

### 3. Computação e Backend (RF-01, RNF-04)
- [ ] `06_ec2_instancia.png`: Painel da instância EC2 `t3.micro` mostrando o IP público dinâmico e o IAM Role (`LabInstanceProfile`).
- [ ] `07_docker_executando.png`: Terminal da EC2 com o comando `docker ps`, exibindo o container `biblioteca-api` rodando na porta `8080:8080`.

---

### 4. Armazenamento e Upload S3 (RF-02, RF-05)
- [ ] `08_s3_buckets.png`: Listagem dos 2 buckets criados no Amazon S3 (o bucket estático do site e o bucket privado de uploads).
- [ ] `09_s3_cors_uploads.png`: Configuração de CORS ativa no bucket de uploads permitindo método `PUT` e cabeçalhos.
- [ ] `10_s3_objetos_capas.png`: Pasta `capas/` do bucket de uploads com as imagens reais enviadas via Pre-Signed URL.

---

### 5. Lambdas e CloudWatch (RF-03, RF-04)
- [ ] `11_lambda_s3_trigger.png`: Configuração da função Lambda de processamento com o gatilho S3 Event (`s3:ObjectCreated:*`).
- [ ] `12_cloudwatch_logs_s3.png`: Visualização no CloudWatch Logs exibindo os prints da Lambda de processamento identificando a capa e atualizando o RDS.
- [ ] `13_api_gateway_rotas.png`: Painel do API Gateway mostrando a rota `GET /relatorios/livros-por-categoria` integrada à Lambda de relatório com CORS ativado.

---

### 6. Aplicação Web Front-End (RF-05, RNF-03)
- [ ] `14_frontend_catalogo.png`: Página da aplicação aberta no navegador via URL pública do S3 Static Website, exibindo o acervo de livros com as capas renderizadas.
- [ ] `15_frontend_relatorio.png`: Relatório analítico renderizado na tela após o clique no botão "Consultar Relatório", comprovando o consumo do API Gateway.
