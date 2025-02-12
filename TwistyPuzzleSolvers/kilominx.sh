#!/bin/bash

srcdir="TwistyPuzzleSolvers/src"

if [[ $1 == "gui" ]]; then
    # javac -d TwistyPuzzleSolvers/classes $srcdir/models/*.java $srcdir/patterndatabases/*.java $srcdir/patterndatabases/cube/*.java $srcdir/solvers/*.java $srcdir/interactive/*.java
    # java -cp TwistyPuzzleSolvers/classes interactive.KilominxGUI
    echo "GUI not implemented yet!"
elif [[ $1 == "terminal" ]]; then
    javac -d TwistyPuzzleSolvers/classes $srcdir/models/*.java $srcdir/patterndatabases/*.java $srcdir/patterndatabases/cube/*.java $srcdir/solvers/*.java $srcdir/interactive/*.java
    java -cp TwistyPuzzleSolvers/classes interactive.KilominxTerminal
else 
    echo "Usage: kilominx.sh [gui|terminal]"
fi