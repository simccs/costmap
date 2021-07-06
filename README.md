# CostMAP

[![Java CI with Gradle](https://github.com/daniellivingston/costmap/actions/workflows/gradle.yml/badge.svg)](https://github.com/daniellivingston/costmap/actions/workflows/gradle.yml)

A tool for creating Cost Networks and Surfaces for SimCCS.

## Running CostMAP with Gradle

Download [JDK 11 or later](http://jdk.java.net/) for your operating system.
Make sure `JAVA_HOME` is properly set to the JDK installation directory.

### Linux / Mac

To run the project:

    ./gradlew run

### Windows

To run the project:

    gradlew run

### Other options

Other options are available with the command:

    ./gradlew <task>

where `<task>` can be one of:

- run - Runs this project as a JVM application
- build - Assembles and tests this project.
- distZip - Bundles the project as a distribution.
- test - Runs the unit tests.

More options are available by running `./gradlew --help`.
