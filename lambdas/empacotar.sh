#!/usr/bin/env bash
# ==============================================================================
# Script de Empacotamento das Funções AWS Lambda (Python 3.12)
# ==============================================================================
# Este script gera os arquivos .zip prontos para upload no Console AWS Lambda
# contendo o código-fonte (lambda_function.py) e a biblioteca pymysql.
# ==============================================================================

set -e

DIR_RAIZ="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
DIR_TEMP="/tmp/lambda_build_$$"

echo "=== Iniciando empacotamento das Lambdas ==="

# 1. Empacotar Lambda de Processamento de Capa (RF-03)
echo "-> Preparando processa_capa.zip..."
rm -rf "$DIR_TEMP"
mkdir -p "$DIR_TEMP"
pip install -r "$DIR_RAIZ/processa_capa/requirements.txt" -t "$DIR_TEMP" --quiet
cp "$DIR_RAIZ/processa_capa/lambda_function.py" "$DIR_TEMP/"
(cd "$DIR_TEMP" && zip -r -q "$DIR_RAIZ/processa_capa.zip" .)
echo "   [OK] $DIR_RAIZ/processa_capa.zip gerado com sucesso."

# 2. Empacotar Lambda de Relatório Analítico (RF-04)
echo "-> Preparando relatorio.zip..."
rm -rf "$DIR_TEMP"
mkdir -p "$DIR_TEMP"
pip install -r "$DIR_RAIZ/relatorio/requirements.txt" -t "$DIR_TEMP" --quiet
cp "$DIR_RAIZ/relatorio/lambda_function.py" "$DIR_TEMP/"
(cd "$DIR_TEMP" && zip -r -q "$DIR_RAIZ/relatorio.zip" .)
echo "   [OK] $DIR_RAIZ/relatorio.zip gerado com sucesso."

# Limpeza
rm -rf "$DIR_TEMP"

echo "=== Empacotamento concluído! ==="
echo "Você pode carregar 'processa_capa.zip' e 'relatorio.zip' diretamente pelo Console AWS Lambda (Upload from > .zip file)."
