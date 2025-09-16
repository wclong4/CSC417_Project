This project uses Java and Jackson to build a simple interpreter. Jackson is in jars in the `lib/` directory, so Maven is no longer required.  This change was made because Maven was really slow to build :<

## Prerequisites
- A JDK that provides `java` and `javac` on your `PATH` (I used JDK 21)
- Ubuntu (or maybe a similar Linux distro like CentOS) to run `./parse` (I used Ubuntu on WSL in VSCode)
- Make sure parse is compiled and in the root of the project

## Compile
Compiling isn't too complicated, just run:
```bash
mkdir -p out
javac -cp "lib/*" -d out $(find src/main/java -name "*.java")
```

## Run
Once compiled, you can pipe whatever you want from the parser through the interpreter like this:
```bash
./parse -2147483648 | java -cp out:lib/* interpreter.Run
```
