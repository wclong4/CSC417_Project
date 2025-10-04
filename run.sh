#!/usr/bin/env bash
# Run the interpreter
java -XX:MaxJavaStackTraceDepth=1000000 -cp out:lib/* interpreter.Run 