// Arquivo de configuração dinâmico da aplicação (RNF-03)
// Ao reiniciar a sessão do Learner Lab, basta atualizar as variáveis abaixo
// sem precisar alterar o código-fonte do index.html.

const CONFIG = {
    // URL base da API Spring Boot rodando na EC2 (Porta 8080)
    // Exemplo: "http://54.234.12.34:8080"
    API_BASE_URL: "http://SEU-IP-EC2:8080",

    // URL base do API Gateway da Lambda de relatório (RF-04)
    // Exemplo: "https://abc123xyz.execute-api.us-east-1.amazonaws.com"
    APIGW_URL: "https://SEU-API-GATEWAY.execute-api.us-east-1.amazonaws.com"
};
