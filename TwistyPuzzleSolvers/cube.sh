#!/bin/bash

javac -d TwistyPuzzleSolvers/classes TwistyPuzzleSolvers/src/rubikscube/*.java
java -cp TwistyPuzzleSolvers/classes rubikscube.CubeInteractive