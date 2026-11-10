#!/usr/bin/env bash
set -eu
exec 3<>"/dev/tcp/127.0.0.1/$1"
printf 'GET %s HTTP/1.0\r\nHost: localhost\r\n\r\n' "$2" >&3
read -r status <&3
[[ "$status" == "HTTP/1."*" 200 "* ]]
