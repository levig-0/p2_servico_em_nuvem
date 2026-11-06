import json
import os
import pymysql

# Variáveis de ambiente configuradas no console da Lambda
DB_HOST = os.environ.get("DB_HOST")
DB_USER = os.environ.get("DB_USER", "admin")
DB_PASSWORD = os.environ.get("DB_PASSWORD")
DB_NAME = os.environ.get("DB_NAME", "projeto")

def lambda_handler(event, context):
    print("Invocação da Lambda de Relatório recebida.")

    # Headers padrão para suporte a CORS no API Gateway
    headers = {
        "Content-Type": "application/json",
        "Access-Control-Allow-Origin": "*",
        "Access-Control-Allow-Methods": "GET, OPTIONS",
        "Access-Control-Allow-Headers": "Content-Type"
    }

    # Tratamento de pre-flight CORS OPTIONS se necessário
    http_method = event.get("httpMethod") or event.get("requestContext", {}).get("http", {}).get("method")
    if http_method == "OPTIONS":
        return {
            "statusCode": 200,
            "headers": headers,
            "body": json.dumps({"mensagem": "CORS preflight OK"})
        }

    try:
        conn = pymysql.connect(
            host=DB_HOST,
            user=DB_USER,
            password=DB_PASSWORD,
            database=DB_NAME,
            connect_timeout=5
        )

        relatorio = []
        with conn.cursor(pymysql.cursors.DictCursor) as cursor:
            # Query agregada com JOIN e GROUP BY exigida no RF-04
            sql = """
                SELECT 
                    c.id AS categoria_id,
                    c.nome AS categoria_nome,
                    COUNT(l.id) AS total_livros,
                    COALESCE(SUM(CASE WHEN l.capa_processada = 1 THEN 1 ELSE 0 END), 0) AS capas_processadas
                FROM categorias c
                LEFT JOIN livros l ON l.categoria_id = c.id
                GROUP BY c.id, c.nome
                ORDER BY total_livros DESC, c.nome ASC;
            """
            cursor.execute(sql)
            relatorio = cursor.fetchall()

        conn.close()

        print(f"Relatório gerado com {len(relatorio)} categorias.")

        return {
            "statusCode": 200,
            "headers": headers,
            "body": json.dumps(relatorio, ensure_ascii=False)
        }

    except Exception as e:
        print(f"Erro ao processar relatório: {e}")
        return {
            "statusCode": 500,
            "headers": headers,
            "body": json.dumps({"erro": "Erro ao consultar o relatório no banco de dados.", "detalhes": str(e)})
        }
