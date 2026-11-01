## Front-end / Landing Page Estática (S3)

O protótipo/landing page da aplicação está publicado e acessível no Amazon S3 via Static Website Hosting.

* **URL Pública da Landing Page:** `http://biblioteca-frontend-grupo-lj-2026.s3-website-us-east-1.amazonaws.com`
* **Localização do código no repositório:** `/index.html`

## Backend na EC2 com Docker Compose

A API é executada em um contêiner Docker e se conecta a um banco MySQL externo (por exemplo, Amazon RDS).

1. Instale Docker Engine e o plugin Docker Compose na EC2.
2. Clone o repositório na instância e entre na pasta do projeto.
3. Crie o arquivo de ambiente e configure as credenciais do banco:

   ```bash
   cp .env.example .env
   nano .env
   ```

   Preencha `DB_HOST` com o endpoint do RDS, sem protocolo nem porta. Configure também `DB_NAME`, `DB_USER` e `DB_PASSWORD`. Não compartilhe nem versione o arquivo `.env`.

4. Inicie a aplicação:

   ```bash
   docker compose up -d --build
   ```

5. Consulte os logs ou pare os contêineres quando necessário:

   ```bash
   docker compose logs -f api
   docker compose down
   ```

A API ficará disponível em `http://<IP-PUBLICO-DA-EC2>:8080`. Libere a porta 8080 no Security Group da EC2 somente para as origens necessárias. No Security Group do RDS, permita a porta 3306 a partir do Security Group da EC2.

Se o banco ainda não tiver as tabelas, execute `sql/001_schema.sql` uma vez no banco antes de iniciar a API. `sql/002_schema.sql` contém dados de exemplo e é opcional.