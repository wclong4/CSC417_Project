This project uses Java, Maven, and Jackson for interpreter development.  Right now it is very simple and can only do stuff for CP1.

You can run very simple commands like:
./parse <<< -2147483649 | mvn exec:java -Dexec.mainClass="interpreter.Run"

Though parse needs to be compiled and in the same directory as where you are running this, which should be the root of the project.

It has to be run on Ubuntu for ./parse to work, though maybe another variety of Linux would work, like CentOS.

I'm running it in WSL.

Make sure to install Maven and the JDK or else this won't run.  Maven should take care of installing Jackson, so don't worry about that.
