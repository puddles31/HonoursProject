#!/bin/bash

srcdir="TwistyPuzzleSolvers/src"

javac -d TwistyPuzzleSolvers/classes $srcdir/models/*.java $srcdir/patterndatabases/*.java $srcdir/patterndatabases/cube/*.java $srcdir/patterndatabases/kilominx/*.java $srcdir/solvers/*.java $srcdir/interactive/*.java

if [[ $1 == "cube" && $2 == "terminal" ]]; then
    java -cp TwistyPuzzleSolvers/classes interactive.CubeTerminal
elif [[ $1 == "cube" && $2 == "gui" ]]; then
    java -cp TwistyPuzzleSolvers/classes interactive.CubeGUI
elif [[ $1 == "kilominx" && $2 == "terminal" ]]; then
    java -cp TwistyPuzzleSolvers/classes interactive.KilominxTerminal
elif [[ $1 == "kilominx" && $2 == "gui" ]]; then
    # java -cp TwistyPuzzleSolvers/classes interactive.KilominxGUI
    echo "GUI not implemented yet!"
elif [[ $1 == "pdb" ]]; then
    java -cp TwistyPuzzleSolvers/classes patterndatabases.PopulatePatternDatabases $2
else 
    echo "Usage: run.sh [cube|kilominx|pdb] [terminal|gui|pdb-flag]"
fi