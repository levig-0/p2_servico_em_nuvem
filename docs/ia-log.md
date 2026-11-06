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

> **Atualização posterior (31/10/2026):** na implementação, o banco passou a ser **MySQL 8.4** (e não PostgreSQL) e as entidades do CRUD são **categorias e livros** (1:N). O CRUD ficou em EC2 com Docker e o `design.md` gerado aqui foi reescrito. Ver Entradas 03 a 06.


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


## Entrada 03 - [31/10/2026]

### Contexto/Objetivo
Criar pelo console da AWS a rede (VPC e sub-redes), os Security Groups e o RDS privado para a entrega da Aula 3 (RF-01, RNF-01).

### Ferramenta e prompt (resumo)
Claude (assistente de IA da Anthropic). Pedido de um passo a passo para criar VPC com sub-redes públicas e privadas, Security Groups e RDS privado, validando cada etapa com prints do console.

### O que a IA Gerou
* Roteiro para criar a VPC `projeto02-vpc` (`10.0.0.0/16`) com o assistente "VPC e muito mais": 2 zonas, 2 sub-redes públicas, 2 privadas, sem NAT Gateway e com endpoint de VPC Gateway para o S3.
* Dois Security Groups: `app-sg` (HTTP 80, SSH 22, TCP 8080) e `rds-sg` (MySQL 3306 com origem no `app-sg`).
* Roteiro do RDS MySQL privado em grupo de sub-redes só com as privadas.

### O que o Grupo Aceitou / Rejeitou / Corrigiu
* **Corrigido:** a primeira VPC foi criada sem sub-redes (opção "Somente VPC"). Foi excluída e recriada com "VPC e muito mais", e o mapa de recursos confirmou as 4 sub-redes, a Internet Gateway e o endpoint do S3.
* **Corrigido:** a IA sugeriu o nome `sg-app`, que a AWS recusa (prefixo `sg-` reservado), e a descrição tinha acentos. Os grupos ficaram `app-sg` e `rds-sg`, com descrição sem acentos.
* **Corrigido:** o formulário do RDS veio com valores de produção (`db.m7g.large`, 200 GiB) e com a VPC padrão selecionada. Foram trocados para `db.t3.micro`, 20 GiB e `projeto02-vpc`, para economizar o orçamento do Learner Lab e manter o banco dentro da nossa rede.
* **Corrigido:** o grupo de sub-redes do RDS foi criado manualmente com apenas as sub-redes privadas (a criação automática incluiria as públicas).
* **Validado:** o RDS ficou com acesso público desativado e o `rds-sg` só aceita conexões vindas do `app-sg`.


## Entrada 04 - [31/10/2026]

### Contexto/Objetivo
Implementar o CRUD do RF-01 (categorias e livros, relação 1:N) em Java com Spring Boot.

### Ferramenta e prompt (resumo)
Claude. Pedido do schema SQL e do backend em Java para o domínio de biblioteca, com persistência no RDS e credenciais fora do repositório.

### O que a IA Gerou
* `sql/001_schema.sql` (tabelas `categorias` e `livros` com chave estrangeira) e um script de dados de exemplo.
* Projeto Spring Boot (Web, Data JPA, MySQL Driver, Lombok): entidades `Categoria` e `Livro`, repositórios `JpaRepository` e controllers REST com CRUD completo e `@CrossOrigin`.
* `application.properties` lendo `DB_HOST`, `DB_USER`, `DB_NAME` e `DB_PASSWORD` de variáveis de ambiente, com `ddl-auto=none`.

### O que o Grupo Aceitou / Rejeitou / Corrigiu
* **Aceito:** `ddl-auto=none`, para o schema vir somente dos scripts SQL versionados, e as variáveis de ambiente, para não haver segredo no repositório (RNF-02).
* **Corrigido:** o build falhou com "release version 25 not supported". Ajustamos `java.version` no `pom.xml` para 17, a versão usada também na EC2.
* **Corrigido:** o Lombok não gerava getters e setters e o editor acusava erro. O grupo ajustou a configuração do Lombok no `pom.xml` até o build passar.
* **Ajustado:** o build usa `-DskipTests`, porque o teste de contexto gerado pelo Spring tenta conectar ao banco, que é privado e inacessível da máquina local.


## Entrada 05 - [31/10/2026]

### Contexto/Objetivo
Publicar o CRUD na AWS e comprovar a persistência no RDS (RF-01, RNF-03, RNF-04).

### Ferramenta e prompt (resumo)
Claude. Pedido de passo a passo para subir a API numa EC2 e rodá-la em contêiner Docker.

### O que a IA Gerou
* Fluxo de deploy: jar gerado localmente, enviado a um bucket S3 privado e baixado pela EC2 (perfil `LabInstanceProfile`); tabelas criadas no RDS pela própria EC2; API executada com `docker build` e `docker run`, credenciais passadas só por variáveis de ambiente.
* `Dockerfile` simples (imagem `amazoncorretto:17` copiando o jar) e, como alternativa, um Dockerfile multi-stage.

### O que o Grupo Aceitou / Rejeitou / Corrigiu
* **Decisão (ADR-01):** CRUD em EC2 com Docker, e não em Lambda (cold start da JVM, pool de conexões e deploy reprodutível).
* **Rejeitado:** o Dockerfile multi-stage, para manter o que já estava testado na AWS e idêntico ao do repositório.
* **Corrigido:** a instalação de pacotes travou porque um `Ctrl+Z` deixou o `dnf` suspenso segurando o lock. Foi retomado com `fg`.
* **Corrigido:** o arquivo de dados de exemplo no bucket se chama `002_schema.sql`, e não `002_seed.sql` como a IA havia escrito. O nome real passou a constar no `design.md` e no README.
* **Corrigido:** o `docker run` falhou porque o `java -jar` ainda ocupava a porta 8080. O processo foi encerrado e o contêiner recriado.
* **Corrigido:** o README foi limpo (removido o trecho de Docker Compose, que não corresponde ao que roda) e o arquivo `.env` ficou fora do Git (RNF-02).
* **Validado:** CRUD das duas entidades funcionando pelo navegador e por `curl` com o IP público da EC2, e consulta direta no RDS confirmando que os dados gravados pela API estavam no banco.


## Entrada 06 - [31/10/2026]

### Contexto/Objetivo
Revisar se o `design.md` descrevia o que realmente roda na AWS.

### Ferramenta e prompt (resumo)
Claude. Revisão do `design.md` gerado na Entrada 01 e do trecho do ADR contra a implementação.

### O que a IA Gerou
* Lista de divergências entre o documento e o ambiente, e uma nova versão do `design.md`.

### O que o Grupo Aceitou / Rejeitou / Corrigiu
* **Problema encontrado:** o `design.md` inicial descrevia CRUD em Lambda atrás do API Gateway, `SG-Lambda`, Lambdas em sub-rede pública, a role `AWSLambdaVPCAccessExecutionRole` (que não pode ser criada no Learner Lab), banco "PostgreSQL/MySQL", rota `/emprestimos` e dizia atender totalmente todos os requisitos. Nada disso correspondia ao que estava implantado.
* **Corrigido:** o documento foi reescrito para EC2 + Docker, MySQL, entidades `categorias` e `livros`, `app-sg` e `rds-sg`. Os itens ainda não feitos (RF-02 a RF-05) aparecem como "previsto", e o ADR-01 passou a registrar que substitui a proposta inicial.
* **Problema encontrado no `spec.md`:** o documento gerado na Entrada 01 descrevia entidades **Livro e Empréstimo**, banco "PostgreSQL/MySQL", categoria como campo de texto, autenticação da bibliotecária, extração de dimensões da imagem e um relatório de empréstimos por categoria. Nada disso correspondia ao que estava implantado (categorias e livros em MySQL, sem login e sem tabela de empréstimos).
* **Corrigido no `spec.md`:** as entidades passaram a ser **Categoria e Livro** (1:N) em MySQL, o relatório virou `GET /relatorios/livros-por-categoria` (JOIN e GROUP BY entre `categorias` e `livros`), a Lambda do RF-03 passou a registrar tamanho, tipo e extensão do arquivo (sem dimensões), e o CRUD ficou na EC2 com Docker, com a Lambda só nos RF-03 e RF-04. Autenticação e controle de empréstimos foram registrados como fora do escopo desta versão.
* **Decisão do grupo:** alinhar a especificação ao que já funciona na AWS, em vez de criar a tabela de empréstimos agora, para manter coerentes `spec.md`, `design.md`, README e o vídeo da Aula 3.
* **Pendente:** `index.html` ainda cita empréstimos e será ajustado no RF-05; as colunas da capa em `livros` (RF-02 e RF-03) serão criadas num novo script SQL, com atualização do modelo ER no `design.md`.
* **Removido:** uma afirmação sobre o saldo de créditos do Learner Lab que o grupo não conseguia comprovar.
* **Aprendizado:** a saída da IA precisa ser conferida contra o que realmente roda, porque o professor avalia a coerência entre documentação, implementação e demonstração.


## Entrada 07 - [06/11/2026]

### Contexto/Objetivo
Finalização da implementação de todos os requisitos pendentes (RF-02, RF-03, RF-04, RF-05, RNF-03 e RNF-05) para a entrega final do Projeto 2.

### Ferramenta e prompt (resumo)
Assistente de IA Antigravity. Pedido de geração do serviço S3 para Pre-signed URLs no Spring Boot, funções Lambda em Python para o evento S3 e o relatório analítico com JOIN/GROUP BY, refatoração dinâmica do frontend com `config.js` e elaboração do guia passo a passo de infraestrutura sem IaC no `README.md`.

### O que a IA Gerou
* Código Java com AWS SDK v2 (`S3Service` e endpoint em `LivroController`) para gerar URLs pré-assinadas com segurança na EC2 (`LabInstanceProfile`).
* Migração SQL (`sql/003_add_capa_columns.sql`) e campos correspondentes na entidade JPA `Livro`.
* Funções Lambda Python: `lambdas/processa_capa/lambda_function.py` (processamento de evento S3 com logs CloudWatch) e `lambdas/relatorio/lambda_function.py` (agregação SQL com suporte a CORS para API Gateway).
* Refatoração do `index.html` e criação do `config.js` desacoplado para isolar os endpoints dinâmicos da AWS.
* Guia detalhado passo a passo de provisionamento no `README.md` e checklist de evidências em `docs/evidencias/`.

### O que o Grupo Aceitou / Rejeitou / Corrigiu
* **Aceito:** Adoção de Python para as funções Serverless (Lambdas), garantindo execução rápida, baixo consumo de memória e facilidade de deploy em relação a Java.
* **Aceito:** Separação do arquivo `config.js` para atender ao RNF-03, permitindo rodar a aplicação em qualquer nova sessão do Learner Lab apenas trocando o IP da EC2 em uma única linha.
* **Corrigido:** A primeira sugestão de credenciais utilizava chaves estáticas da AWS. Corrigido para utilizar o provedor padrão de credenciais da AWS SDK (`DefaultCredentialsProvider`), aproveitando a `LabInstanceProfile` vinculada à EC2 sem necessidade de expor chaves no repositório (RNF-02).
* **Validado:** Compilação do backend Java via Maven finalizada com sucesso gerando o pacote `api-0.0.1-SNAPSHOT.jar`.


## Reflexão Final sobre o Uso de IA no Projeto

O desenvolvimento deste projeto utilizando assistentes de Inteligência Artificial evidenciou aspectos cruciais sobre engenharia de software e arquitetura em nuvem:

1. **A IA como Aceleradora, mas com Necessidade de Validação Crítica:**
   Nas primeiras iterações (Entrada 01), a IA sugeriu arquiteturas genéricas (CRUD inteiro em Lambda com Spring Boot, banco PostgreSQL, tabelas de empréstimos e roles IAM customizadas). Se o grupo tivesse aceitado cegamente, o projeto teria falhado nas restrições reais do **AWS Learner Lab** (onde não é permitido criar roles IAM customizadas, o cold start do Spring Boot em Lambda torna a experiência inaceitável e o orçamento é limitado). A intervenção humana e a tomada de decisão consciente (documentada no ADR-01) foram determinantes para o sucesso.

2. **A Importância da Especificação Rigorosa:**
   A prática de construir `specs/spec.md`, `specs/design.md` e `specs/tasks.md` antes de gerar código serviu como a melhor âncora de contexto para a IA. Quando as histórias de usuário e os critérios de aceite (cenários BDD) estavam claros, o código gerado pela IA (seja o Java com JPA ou as Lambdas em Python) foi muito mais assertivo, limpo e diretamente integrado ao ecossistema proposto.

3. **Arquitetura Híbrida e Flexibilidade:**
   A combinação de container Docker em EC2 para o CRUD relacional contínuo e Serverless (S3 + Lambda + API Gateway) para eventos e relatórios analíticos demonstrou o verdadeiro potencial de uma solução em nuvem balanceada: resiliente, econômica para o Learner Lab e fácil de manter.

Conclusão: A IA é um excelente copiloto de desenvolvimento e documentação, mas a responsabilidade técnica, a conferência com a infraestrutura real e o domínio das decisões de arquitetura cabem integralmente aos desenvolvedores.