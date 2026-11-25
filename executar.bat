@echo off
chcp 65001 >nul
if not exist bin mkdir bin
dir /s /b Techlab\*.java > fontes.txt
javac -encoding UTF-8 -d bin @fontes.txt
if errorlevel 1 (
    echo Erro de compilacao.
    del fontes.txt
    exit /b 1
)
del fontes.txt
java --enable-native-access=ALL-UNNAMED -Dstdout.encoding=UTF-8 -cp "bin;lib\sqlite-jdbc.jar" Techlab.Main
