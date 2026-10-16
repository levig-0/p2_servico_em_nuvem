# Design de Arquitetura e Especificação Técnica

## 1. Diagrama de Arquitetura AWS

A arquitetura atende totalmente aos requisitos de rede privada para o banco de dados (RNF-01), componentes serverless, processamento por evento no S3 e publicação web.

```mermaid
graph TD
    Client[Navegador / Web SPA] -->|1. Acessa Front-end| S3Front[S3 Static Website Hosting]
    Client -->|2. Chamadas REST| APIGw[Amazon API Gateway]
    
    subgraph VPC da Aplicação
        subgraph Subnet Publica
            LambdaCRUD[AWS Lambda - CRUD API]
            LambdaRelatorio[AWS Lambda - Relatório]
        end
        
        subgraph Subnet Privada - RNF-01
            RDS[(Amazon RDS - PostgreSQL/MySQL)]
        end
        
        subgraph Security Groups - RNF-01
            SGLambda[SG-Lambda: Egress 5432/3306]
            SGRDS[SG-RDS: Ingress 5432/3306 apenas do SG-Lambda]
        end
        
        SGLambda -.->|Aplica a| LambdaCRUD
        SGLambda -.->|Aplica a| LambdaRelatorio
        SGLambda -->|Acesso Autorizado| SGRDS
        SGRDS -.->|Protege| RDS
    end
    
    subgraph Storage & Eventos
        S3Buckets[Amazon S3 - Bucket de Capas/Mídias]
        LambdaS3Process[AWS Lambda - S3 Event Trigger]
    end

    Client -->|3. Upload direto via Pre-signed URL| S3Buckets
    S3Buckets -->|4. Evento s3:ObjectCreated| LambdaS3Process
    LambdaS3Process -->|5. Atualiza Status| RDS
    
    APIGw -->|Rota /livros, /emprestimos| LambdaCRUD
    APIGw -->|Rota /relatorios/...| LambdaRelatorio
    
    LambdaCRUD -->|Persiste e Le| RDS
    LambdaRelatorio -->|SQL JOIN / GROUP BY| RDS

Decisão
Decidimos utilizar AWS Lambda integradas via Amazon API Gateway para executar o CRUD das entidades.

Justificativa
Preservação de Créditos do Learner Lab: A infraestrutura Serverless (Lambda + S3) zera o consumo quando a aplicação não está recebendo chamadas. Uma instância EC2 ligada esquecida pode esgotar o saldo de $100 em poucos dias.

Resiliência entre Sessões (RNF-03): Como as instâncias EC2 no Learner Lab costumam ter seu IP público alterado a cada desligamento/reinício da sessão, o uso do API Gateway + Lambda garante URLs fixas e estáveis para o front-end sem a necessidade de reconfigurar IPs a cada aula.

Simplicidade Operacional: Dispensa a criação de rotinas complexas de script para inicializar daemon do Docker toda vez que o laboratório for religado.

Consequências e Mitigações
Consequência: A Lambda precisa ser configurada dentro das subnets da VPC para ter conectividade com a subnet privada do RDS (exigindo atribuição correta de IAM Role com a política AWSLambdaVPCAccessExecutionRole).

Mitigação: O Security Group sg-lambda foi mapeado e associado explicitamente às Lambdas do CRUD para garantir o acesso à porta 5432/3306 do RDS.