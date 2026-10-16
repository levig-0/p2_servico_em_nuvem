# Registro de Uso de IA (ia-log.md)

## Entrada 01 - [16/10/2026]

### Contexto/Objetivo
Definição inicial da especificação funcional (`specs/spec.md`) e do design técnico/arquitetura (`specs/design.md`) do projeto de gestão de Biblioteca na AWS Learner Lab, alinhado estritamente aos requisitos mínimos (RF-01 a RF-05 e RNF-01 a RNF-05).

### Prompt Enviado ao GitHub Copilot
> "Atue como arquiteto de software cloud na AWS. Escreva a especificação funcional (`specs/spec.md`) e o design de arquitetura (`specs/design.md`) para um sistema de gestão de Biblioteca. 
> O sistema precisa atender rigorosamente aos requisitos mínimos:
> 1. RF-01: CRUD de Livros e Empréstimos (relacionamento 1:N) no Amazon RDS (PostgreSQL/MySQL).
> 2. RF-02: Upload de capa de livro (imagem) para S3 via Pre-signed URL do backend.
> 3. RF-03: Trigger de evento S3 -> Lambda ao subir arquivo para extração de metadados/redimensionamento e atualização do status no RDS.
> 4. RF-04: Endpoint de relatório (`GET /relatorios/emprestimos-por-categoria`) via API Gateway + Lambda executando JOIN e GROUP BY no RDS.
> 5. RF-05: Front-end SPA hospedado em S3 Static Website.
> 6. RNF-01: RDS em Subnet Privada com Security Groups configurados.
> 7. RNF-04: ADR comparativo e decisório entre Lambda vs EC2/ECS no Learner Lab."

### O que a IA Gerou
* Estrutura do `spec.md` com o problema do domínio de biblioteca, personas, mapeamento explícito dos RF-01 a RF-05 e 5 Histórias de Usuário completas com cenários de aceite em Gherkin (Dado-Quando-Então).
* Estrutura do `design.md` contendo diagrama de arquitetura AWS em Mermaid (destacando VPC e Subnets Privadas), Modelo ER condizente com RDS, especificação técnica de Security Groups, tabela de rotas REST e ADR do RNF-04.

### O que o Grupo Aceitou / Rejeitou / Corrigiu
* **Aceito:** A escolha do RDS PostgreSQL em subnet privada e a integração S3 Event -> Lambda para o processamento de imagens de capa de livros.
* **Corrigido/Ajustado:**
  * **Ajuste de Negócio (RF-03):** Garantimos que a Lambda disparada pelo S3 grave o link definitivo e as dimensões/metadados da imagem no banco RDS no registro do livro correspondente.
  * **Decisão do RNF-04 (ADR):** Definimos que o CRUD principal rodará em instâncias **EC2 (ou Docker/Lambda)** integradas com a VPC privada, garantindo segurança ao se comunicar com o RDS em subnet privada dentro do orçamento reduzido do Learner Lab.


## Entrada 02 - [16/10/2026]

### Contexto/Objetivo
Criação do protótipo da interface web (Landing Page / SPA) para publicação em bucket Amazon S3 com hospedagem estática, permitindo a interação com o acervo e simulação dos uploads de capa.

### Prompt Enviado ao Copilot
> "Crie um arquivo index.html em Bootstrap 5 para a landing page/protótipo da biblioteca. Inclua catálogo de livros, formulário de cadastro de capa integrado com Pre-Signed URL e um painel para exibição do relatório do RF-04."

### O que a IA Gerou
* Arquivo HTML responsivo contendo componentes para listagem de acervo, formulário para envio de imagem de capa e área de visualização do relatório de empréstimos por categoria.

### O que o Grupo Aceitou / Rejeitou / Corrigiu
* **Aceito:** O layout responsivo em Bootstrap.
* **Ajustado:** Adicionada a instrução de configuração da variável `API_URL` para apontar ao API Gateway quando as funções Lambdas e o banco RDS forem provisionados nas próximas fases.