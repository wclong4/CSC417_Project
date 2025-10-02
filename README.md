This project uses Java and Jackson to build a simple interpreter, with JUnit used to test it. Jackson is in jars in the `lib/` directory, so Maven is no longer required.  This change was made because Maven was really slow to build :<

## Build
The build script makes the parser, puts the parse file
into the right directory for Interpreter to see it, and then 
downloads Java 21.

If it doesn't work you may need
to set the path to Java manually.

Hopefully that doesn't happen.

- Add permissions 
```bash
chmod +x build.sh
```

- And then build
```bash
./build.sh
``` 
- The Jackson jars are already in the repo and don't need to be downloaded.
- Ubuntu (or maybe a similar Linux distro like CentOS, I used Ubuntu on WSL in VSCode)

## Run
- Add permissions 
```bash
chmod +x run.sh
```
And then run
```bash
./run.sh
```

## Test
You should be able to run the tests in VSCode using the RedHat java extension.  Make sure you have JUnit4 installed.
They currently cover all the functionality for CP2.

There is no build or run script for these as they are not required, and I am lazy.