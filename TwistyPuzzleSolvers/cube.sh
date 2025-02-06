#!/bin/bash

if [[ $1 == "gui" ]]; then
    javac -d TwistyPuzzleSolvers/classes TwistyPuzzleSolvers/src/rubikscube/*.java TwistyPuzzleSolvers/src/rubikscube/patterndatabases/*.java TwistyPuzzleSolvers/src/rubikscube/interactive/*.java
    java -cp TwistyPuzzleSolvers/classes rubikscube.interactive.CubeGUI
elif [[ $1 == "terminal" ]]; then
    javac -d TwistyPuzzleSolvers/classes TwistyPuzzleSolvers/src/rubikscube/*.java TwistyPuzzleSolvers/src/rubikscube/patterndatabases/*.java TwistyPuzzleSolvers/src/rubikscube/interactive/*.java
    java -cp TwistyPuzzleSolvers/classes rubikscube.interactive.CubeTerminal
else 
    echo "Usage: cube.sh [gui|terminal]"
fi