import json
import os
import urllib.parse
import pymysql

# Variáveis de ambiente configuradas no console da Lambda
DB_HOST = os.environ.get("DB_HOST")
DB_USER = os.environ.get("DB_USER", "admin")
DB_PASSWORD = os.environ.get("DB_PASSWORD")
DB_NAME = os.environ.get("DB_NAME", "projeto")

def lambda_handler(event, context):
    print("Evento S3 recebido:", json.dumps(event))

    # Itera sobre os registros de evento S3
    for record in event.get("Records", []):
        bucket_name = record["s3"]["bucket"]["name"]
        object_key = urllib.parse.unquote_plus(record["s3"]["object"]["key"])
        object_size = record["s3"]["object"].get("size", 0)

        print(f"Processando arquivo: s3://{bucket_name}/{object_key} (Tamanho: {object_size} bytes)")

        # Espera padrão: capas/livro-{id}.jpg ou capas/livro-{id}.png
        if not object_key.startswith("capas/livro-"):
            print(f"Arquivo {object_key} fora do padrão de capas. Ignorando.")
            continue

        try:
            # Extrai o ID do livro do nome do arquivo
            nome_arquivo = object_key.split("/")[-1] # livro-12.jpg
            parte_id = nome_arquivo.replace("livro-", "").split(".")[0]
            livro_id = int(parte_id)
        except Exception as e:
            print(f"Erro ao extrair ID do livro a partir de '{object_key}': {e}")
            continue

        # Monta a URL pública/referência do arquivo no S3
        capa_url = f"https://{bucket_name}.s3.amazonaws.com/{object_key}"

        # Atualiza o registro no RDS MySQL
        try:
            conn = pymysql.connect(
                host=DB_HOST,
                user=DB_USER,
                password=DB_PASSWORD,
                database=DB_NAME,
                connect_timeout=5,
                autocommit=True
            )
            with conn.cursor() as cursor:
                sql = """
                    UPDATE livros 
                    SET capa_url = %s,
                        capa_processada = TRUE,
                        capa_tamanho_bytes = %s
                    WHERE id = %s
                """
                cursor.execute(sql, (capa_url, object_size, livro_id))
                print(f"Sucesso: Livro ID {livro_id} atualizado no RDS com capa processada.")
            conn.close()
        except Exception as e:
            print(f"Erro ao conectar ou gravar no RDS: {e}")
            raise e

    return {
        "statusCode": 200,
        "body": json.dumps({"mensagem": "Processamento concluído com sucesso."})
    }
