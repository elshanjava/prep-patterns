#!/usr/bin/env bash
# Прогон .sql-файла против локальной базы в контейнере.
#   ./sql/run.sh sql/01_user_settled_sum.sql
# Без аргумента — интерактивный psql.
set -euo pipefail
CONTAINER=fintech-db

if [ $# -eq 0 ]; then
    exec docker exec -it "$CONTAINER" psql -U fintech -d fintech
fi

for f in "$@"; do
    echo "── $f ────────────────────────────────────────"
    docker exec -i "$CONTAINER" psql -U fintech -d fintech -v ON_ERROR_STOP=1 < "$f"
done
