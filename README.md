## Front-end / Landing Page Estática (S3)

O protótipo/landing page da aplicação está publicado e acessível no Amazon S3 via Static Website Hosting.

* **URL Pública da Landing Page:** `http://biblioteca-frontend-grupo-lj-2026.s3-website-us-east-1.amazonaws.com`
* **Localização do código no repositório:** `/index.html`

## Deploy da API (EC2 + Docker)

1. Gerar o jar na máquina local, dentro de `backend/`:
   `mvnw.cmd clean package -DskipTests`
2. Enviar `backend/target/api-0.0.1-SNAPSHOT.jar` e os scripts de `sql/` para o bucket S3 de deploy (privado).
3. Na EC2 (Amazon Linux 2023, `LabInstanceProfile`, sub-rede pública, `app-sg`):
   - `sudo dnf install -y docker mariadb105 && sudo systemctl enable --now docker`
   - `aws s3 cp s3://<bucket>/api-0.0.1-SNAPSHOT.jar .` (e os `.sql`)
   - Criar o `Dockerfile` (ou copiar o de `backend/Dockerfile`) na mesma pasta do jar
4. Criar as tabelas no RDS:
   `mysql -h <endpoint-rds> -u admin -p projeto < 001_schema.sql`
5. Construir e iniciar o container (credenciais só como variáveis de ambiente, nunca no repositório):
   `sudo docker build -t biblioteca-api .`
   `sudo docker run -d --name biblioteca-api -p 8080:8080 --restart unless-stopped -e DB_HOST -e DB_USER -e DB_NAME -e DB_PASSWORD biblioteca-api`
6. Testar: `http://<ip-publico-da-ec2>:8080/livros`

O IP público da EC2 muda a cada sessão do Learner Lab. Nenhum IP está fixo no código.