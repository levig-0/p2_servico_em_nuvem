# Tarefas

| ID | Requisito | Tarefa | Responsável | Status |
|---|---|---|---|---|
| T-01 | RNF-01 | Criar VPC `projeto02-vpc` com 2 sub-redes públicas e 2 privadas, sem NAT Gateway | [Levi] | Concluído |
| T-02 | RNF-01 | Criar Security Groups `app-sg` e `rds-sg` e documentar no design.md | [Levi] | Concluído |
| T-03 | RF-01 | Criar RDS MySQL privado (`projeto02-db`) com grupo de sub-redes privadas | [Levi] | Concluído |
| T-04 | RF-01 | Escrever e versionar os scripts SQL (`sql/001_schema.sql`, `sql/002_schema.sql`) | [Levi] | Concluído |
| T-05 | RF-01 | Implementar CRUD de categorias e livros (Spring Boot + JPA) | [Levi] | Concluído |
| T-06 | RF-01 | Rodar a API em Docker na EC2 e validar o CRUD e a persistência no RDS | [Levi] | Concluído |
| T-07 | RNF-02 | Garantir que nenhuma senha ou chave esteja no repositório (`.env` fora do Git) | [João] | Concluído |
| T-08 | RNF-04 | Registrar o ADR (CRUD em EC2 com Docker) no design.md | [João] | Concluído |
| T-09 | RNF-03 | Escrever o README com o passo a passo para recriar tudo | [João] | Concluído |
| T-10 | Aula 3 | Gravar o vídeo demonstrativo (até 5 min) e enviar no Moodle | [Levi] | Concluído |
| T-11 | RF-02 | Endpoint da API que gera URL pré-assinada + bucket de uploads com CORS | [Levi] | Concluído |
| T-12 | RF-03 | Lambda disparada pelo S3 que grava no RDS, com log no CloudWatch | [João] | Concluído |
| T-13 | RF-04 | Lambda de relatório (JOIN/GROUP BY) exposta no API Gateway com CORS | [Levi] | Concluído |
| T-14 | RF-05 | Front-end consumindo a API, publicado no S3 | [João] | Concluído |
| T-15 | Final | Evidências em `docs/evidencias/`, reflexão final no ia-log.md e ZIP no Moodle | [Levi/João] | Concluído |    