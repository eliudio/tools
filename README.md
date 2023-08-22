# EliudTools

This repository contains tools to assit with eliud development. Included:

- Git tools to run git commands on all eliud directories at ones
- Miscellaneous tools including tools to manipulate files, such as replace the version of a package accross all packages that use that package

To build: mvn package 

To use, example: java -cp tools/target/eliudtools-1.0.0-jar-with-dependencies.jar io.eliud.misc.yaml.ChangeVersion