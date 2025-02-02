#!/bin/bash

javac -d TwistyPuzzleSolvers/classes TwistyPuzzleSolvers/src/kilominx/*.java
java -cp TwistyPuzzleSolvers/classes kilominx.KilominxInteractive