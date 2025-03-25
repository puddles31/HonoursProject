#!/bin/bash

srcdir="TwistyPuzzleSolvers/src"

javac -d TwistyPuzzleSolvers/classes $srcdir/models/*.java $srcdir/patterndatabases/*.java $srcdir/patterndatabases/cube/*.java $srcdir/patterndatabases/kilominx/*.java $srcdir/solvers/*.java $srcdir/interactive/*.java

if [[ $1 == "cube" && ($# == 1 || $2 == "terminal" || $2 == "-t") ]]; then
    java -cp TwistyPuzzleSolvers/classes interactive.CubeTerminal
elif [[ $1 == "cube" && ($2 == "gui" || $2 == "-g") ]]; then
    java -cp TwistyPuzzleSolvers/classes interactive.CubeGUI
elif [[ $1 == "kilominx" && ($# == 1 || $2 == "terminal" || $2 == "-t") ]]; then
    java -cp TwistyPuzzleSolvers/classes interactive.KilominxTerminal
elif [[ $1 == "pdb" ]]; then
    java -cp TwistyPuzzleSolvers/classes patterndatabases.PopulatePatternDatabases $2
elif [[ $1 == "test" ]]; then
    java -cp TwistyPuzzleSolvers/classes interactive.KilominxTester
else 
    echo "Usage: run.sh [cube|kilominx|pdb|test] [terminal|gui|pdb-flag]"
fi