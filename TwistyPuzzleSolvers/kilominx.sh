#!/bin/bash

javac -d TwistyPuzzleSolvers/classes TwistyPuzzleSolvers/src/kilominx/*.java TwistyPuzzleSolvers/src/kilominx/interactive/*.java
java -cp TwistyPuzzleSolvers/classes kilominx.interactive.KilominxTerminal