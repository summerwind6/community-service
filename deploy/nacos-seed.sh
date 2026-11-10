#!/bin/sh
set -eu
namespace=community-demo
base=http://nacos:8848/nacos/v1
result=$(curl -fsS -X POST "$base/console/namespaces" \
    --data-urlencode "customNamespaceId=$namespace" \
    --data-urlencode "namespaceName=Community Demo")
if [ "$result" != true ]; then
    curl -fsS "$base/console/namespaces" | grep -q "\"namespace\":\"$namespace\""
fi

for file in /seed/*.yaml; do
    data_id=$(basename "$file")
    status=$(curl -sS -o /dev/null -w '%{http_code}' -G "$base/cs/configs" \
        --data-urlencode "tenant=$namespace" --data-urlencode "dataId=$data_id" \
        --data-urlencode 'group=DEFAULT_GROUP')
    case "$status" in
        200) continue ;;
        404) ;;
        *) echo "Unexpected Nacos status $status for $data_id" >&2; exit 1 ;;
    esac
    result=$(curl -fsS -X POST "$base/cs/configs" \
        --data-urlencode "tenant=$namespace" --data-urlencode "dataId=$data_id" \
        --data-urlencode 'group=DEFAULT_GROUP' --data-urlencode 'type=yaml' \
        --data-urlencode "content@$file")
    [ "$result" = true ]
done
