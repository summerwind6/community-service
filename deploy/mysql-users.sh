#!/usr/bin/env bash
(
    set -euo pipefail
    export MYSQL_PWD="$MYSQL_ROOT_PASSWORD"
    core_password=${CORE_DB_PASSWORD//\'/\'\'}
    guide_password=${GUIDE_DB_PASSWORD//\'/\'\'}

    mysql --protocol=socket -uroot <<SQL
SET SESSION sql_mode = 'NO_BACKSLASH_ESCAPES';
CREATE USER 'community_core'@'%' IDENTIFIED BY '$core_password';
CREATE USER 'community_guide'@'%' IDENTIFIED BY '$guide_password';
GRANT SELECT, INSERT, UPDATE, DELETE ON community_service.guide TO 'community_guide'@'%';
SQL

    tables=$(mysql --protocol=socket -uroot --batch --skip-column-names -e \
        "SELECT table_name FROM information_schema.tables WHERE table_schema='community_service' AND table_name <> 'guide'")
    while IFS= read -r table; do
        mysql --protocol=socket -uroot -e \
            "GRANT SELECT, INSERT, UPDATE, DELETE ON community_service.\`$table\` TO 'community_core'@'%'"
    done <<< "$tables"
)
