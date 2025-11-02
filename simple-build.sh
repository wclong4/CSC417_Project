#!/usr/bin/env bash

# Build the parser and copy parse to project root
SCRIPT_DIR="$(pwd)"

echo "[build.sh] Building Parser"
cd "$SCRIPT_DIR/parser"
make

echo "[build.sh] Copying parse -> $SCRIPT_DIR/parse"
cp -f "parse" "$SCRIPT_DIR/parse"

cd ..

# Make our Java program
echo "[build.sh] Building Java Program"
mkdir -p out
javac -cp "lib/*" -d out $(find src/main/java -name "*.java")

# Let parse get past security
chmod +x parse

# Done!
echo "[build.sh] Done."