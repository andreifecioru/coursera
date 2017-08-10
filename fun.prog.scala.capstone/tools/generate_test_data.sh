#!/usr/bin/env bash

main() {
  local src_path=${1:?"src path not defined"}
  local dst_path=${2:?"dst path not defined"}
  local line_count=${3:-"10"}

  echo "Source path is: $src_path"
  echo "Destination path is: $dst_path"
  echo "Line count: $line_count"

  mkdir -p $dst_path
  for f in $(ls $src_path); do
    echo "Processing file: $src_path/$f"
    head -$line_count $src_path/$f > $dst_path/$f
  done
}

main $@
