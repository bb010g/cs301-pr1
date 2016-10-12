Usage Instructions
===

To run a program, you can use `./run` or the jar distribution.
`./run` is the simpler option:
  To use the class PolyMultP1, simply run `./run polymult`.
  To use the class FactorP1, simply run `./run factor`.
These will be slow, however, as they are calling `./gradlew run` behind the
scenes and have the overhead of spinning up Gradle & (re)compiling if necessary.

To avoid this slowdown, you can use the distribution archive & included jars.
Simply unpack gbuild/distributions/pr1-1.0.tar to your desired location,
`cd` in, and run the script at `./bin/pr1` (appending `.bat` if on Windows).
  To use the class PolyMultP1, give the argument 'polymult'.
  To use the class FactorP1, give the argument 'factor'.

If you wish to run tests and view the results, run
`./gradlew test; ./gradlew jacocoTestReport`. Java 8 is _required_ to run tests.
Test results will be available in `gbuild/reports`.
