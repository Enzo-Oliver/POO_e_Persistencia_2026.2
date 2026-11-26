#!/usr/bin/env bash
cd "$(dirname "$0")" || exit 1

if command -v chcp.com > /dev/null 2>&1; then
    chcp.com 65001 > /dev/null
    SEP=";"
else
    SEP=":"
fi

mkdir -p bin
javac -encoding UTF-8 -d bin $(find Techlab -name "*.java") || exit 1
java --enable-native-access=ALL-UNNAMED -Dstdout.encoding=UTF-8 -Dstdin.encoding=UTF-8 \
    -cp "bin${SEP}lib/sqlite-jdbc.jar" Techlab.Main
