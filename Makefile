JC = javac
JVM = java
BIN_DIR = bin
LIB = lib/sqlite-jdbc.jar
MAIN_CLASS = Techlab.Main
FONTES = $(shell find Techlab -name "*.java")

ifeq ($(OS),Windows_NT)
	SEP = ;
else
	SEP = :
endif

all:
	@mkdir -p $(BIN_DIR)
	$(JC) -encoding UTF-8 -d $(BIN_DIR) $(FONTES)

run: all
	$(JVM) --enable-native-access=ALL-UNNAMED -cp "$(BIN_DIR)$(SEP)$(LIB)" $(MAIN_CLASS)

clean:
	rm -rf $(BIN_DIR)

.PHONY: all run clean
