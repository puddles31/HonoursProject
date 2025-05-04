
# An Optimal Solver for the Kilominx

This project contains my work from my Senior Honours project at the University of St Andrews in developing an optimal solver for the Kilominx, a 2x2 dodecahedron-shaped variant of the Rubik's Cube. The solver adapts Korf's Algorithm for the Rubik's Cube, making use of an IDA* search algorithm and a number of pattern databases as heuristics. The solver is able to find optimal solutions for the Kilominx up to a depth of 14 moves in a reasonable amount of time, but it could theoretically find any solutions given enough time.

This project is accompanied by my dissertation, which gives more detail into the design, implementation and results of the solver.
➤ https://github.com/puddles31/HonoursProject/blob/main/Report/main.pdf

[comment]: <> (Replace the above link with updated static URL: https://docs.google.com/viewer?url=https://raw.githubusercontent.com/puddles31/HonoursProject/main/Report/main.pdf)
## System Requirements & Dependencies

The large sizes of the pattern database files requires them to be stored using [Git Large File Storage (Git LFS)](https://git-lfs.com/). In order to clone the repository, you must first install Git LFS on your system so that the pattern database files can be successfully downloaded. Alternatively, you can recreate the pattern databases yourself by using the pattern database populator program (although this can take a very long time).

The solver itself requires a large amount of available memory in order to load the pattern databases; I would recommend setting the maximum Java heap size for the JVM to at least 8GB.
## Quick Start

After cloning the repository, you can use the bash script `run.sh` to quickly compile the project and run any of the programs. The bash script must be run while you are inside the `HonoursProject/` directory (i.e. running the script will look like `$ TwistyPuzzleSolvers/run.sh ...`).

If you are on a Linux system, you might need to update the execute permissions of the script in order to run it. This can be done with the following command: `$ chmod 777 TwistyPuzzleSolvers/run.sh`

The bash script `run.sh` has the syntax `run.sh <program> [args]` with the following arguments:
 - `cube` (or `cube terminal`): Runs the Rubik's Cube terminal program.
 - `cube gui`: Runs the Rubik's Cube GUI program.
 - `kilominx`: Runs the Kilominx terminal program.
 - `pdb [pdb-type]`: Runs the pattern database populator program for the provided pattern database type (see below).
 - `test [scramble-length] [no.-of-test-runs]`: Runs the Kilominx test run program, which generates Kilominx scrambles of the specified length and then solves them, repeating for the specified number of test runs.

## Program Usage
The terminal programs for the Rubik's Cube and Kilominx allow you to make moves and enter commands to interact with the puzzles.

If you are following along with a physical puzzle, make sure you are holding it in the correct orientation:
 - For the Rubik's Cube, the face with the white centre should always be on top, and the face with the red centre should always be at the front.
 - For the Kilominx, the cubie with the white, dark-green and red coloured stickers should always be in the top-front-left position, with the white sticker on the top face and the red sticker on the front face.

### Rubik's Cube Reference
The Rubik's Cube terminal program uses the following letters to denote different colours on the puzzle:
 - `W`: White
 - `G`: Green
 - `R`: Red
 - `B`: Blue
 - `O`: Orange
 - `Y`: Yellow

Moves can be made on the Rubik's Cube using standard move notation, where a letter represents a move on a specific face of the cube, and an optional modifier determines the type of move made.

The letters used to represent the faces of the Rubik's Cube are:
 - `U`: Up
 - `F`: Front
 - `L`: Left
 - `R`: Right
 - `B`: Back
 - `D`: Down

The modifiers you can use to determine the type of move are:
- *(nothing)*: Clockwise twist of the face (90° turn).
- `'`: Counter-clockwise twist of the face (270° turn).
- `2`: Two clockwise twists of the face (180° turn).

**Examples:**\
The move `L'` means a counter-clockwise turn (270°) of the left face.\
The move `D` means a clockwise turn (90°) of the down (bottom) face.\
The move `B2` means two clockwise turns (180°) of the back face.

### Kilominx Reference
The Kilominx terminal program uses the following two-letter codes to denote different colours on the puzzle:
 - `Wh`: White
 - `DG`: Dark Green
 - `Re`: Red
 - `DB`: Dark Blue
 - `Ye`: Yellow
 - `Pu`: Purple
 - `Be`: Beige
 - `Pi`: Pink
 - `LG`: Light Green
 - `Or`: Orange
 - `LB`: Light Blue
 - `Gy`: Grey

Moves can be made on the Kilominx using the following move notation, where one or more letters represents a move on a specific face of the Kilominx, and an optional modifier determines the type of move made.

The letters used to represent the faces of the Kilominx are:
 - `U`: Up
 - `F`: Front
 - `L`: Left
 - `R`: Right
 - `BL`: Back Left
 - `BR`: Back Right
 - `DL`: Down Left
 - `DR`: Down Right
 - `DBL`: Down Back Left
 - `DBR`: Down Back Right
 - `DB`: Down Back
 - `D`: Down

The modifiers you can use to determine the type of move are:
- *(nothing)*: Clockwise twist of the face (72° turn).
- `'`: Counter-clockwise twist of the face (288° turn).
- `2`: Two clockwise twists of the face (144° turn).
- `2'`: Two counter-clockwise twists of the face (216° turn).

**Examples:**\
The move `BL2` means a clockwise turn (144°) of the back-left face.\
The move `R2'` means two counter-clockwise turns (216°) of the right face.\
The move `U'` means a counter-clockwise turn (288°) of the up (top) face.\
The move `DBR` means a clockwise turn (72°) of the down-back-right face.

> **Note: When making moves on the `U`, `F` and `L` faces, a full rotation of the Kilominx is also performed in order to keep the white, dark-green and red cubie in the top-front-left position with the correct orientation.**

### Commands
The available commands for the programs are as follows:
 - `HELP`: Display a help message.
 - `RESET`: Reset the puzzle to the solved state.
 - `EDIT`: Enter edit mode (see below).
 - `SCRAMBLE [N]`: Scramble the puzzle by making `[N]` random moves.
 - `SOLVE`: Invoke the solver to find an optimal solution to the current state. *(Requires all of the pattern databases associated with the puzzle to have been generated.)*
 - `QUIT`: Exit the program.

### Edit Mode
Entering edit mode with the `EDIT` command allows you to manually change the puzzle state by editing the colours of each of the cubie facelets. The highlighted cubie facelet is shown on the state diagram with an empty space (`_`). You can edit the colour of the facelet by entering the colour you wish to change it to (e.g. on a Rubik's Cube, `R` would change the facelet to red), or you can press `ENTER` without typing in a colour to skip to the next facelet. You can also enter `-` to move back to the previous facelet.

When you have filled out all of the cubie facelets (or after you enter `DONE`), the new puzzle state is validated with a number of parity checks to ensure the state is solvable. If all of the parity checks succeed, then the puzzle state is updated.

### Rubik's Cube GUI
The Rubik's Cube GUI program works similarly to the terminal program, but includes a better display of the current cube state with coloured cubies, along with a separate terminal panel which can be used to enter terminal commands. Note that the `EDIT` command is not available in the GUI program. 

The GUI program also includes a number of hotkeys to allow you to quickly make moves on the cube. The hotkeys are the same as the letters associated with each of the faces on the cube (see **Rubik's Cube Reference** above), with each performing a clockwise turn on the corresponding face. You can also perform counter-clockwise moves by holding down the `SHIFT` key while making moves, and double clockwise moves by holding down the `CTRL` key while making moves (e.g. `CTRL + R` performs the move `R2`). 

### Pattern Database Populator
This program is used to create the pattern databases which are used as heuristics for the IDA* search algorithms when solving the puzzles. These pattern databases are already generated and included within the project, so **this program does not need to be run unless the pattern database files are deleted/corrupted, or you if you did not use Git LFS to download the pattern database files when cloning the repository**. For larger pattern databases, this program can take a very long time to finish population, upwards of 24 hours per pattern database. Pattern databases are saved in the `TwistyPuzzleSolvers/databases/` directory.

The available pattern database types are as follows:
 - `cube-corners`
 - `cube-first-edges`
 - `cube-second-edges`
 - `kilominx-face-[N]` (where `[N]` is a number from 1 to 12)
 - `kilominx-sparse-[N]` (where `[N]` is a number from 1 to 5)

## Results
The Kilominx solver is able to find solutions up to a depth of 14 within a reasonable amount of time. While it is theoretically possible for optimal solutions to be found at higher depths, the exponential growth in solve times as the depth increases makes it less feasible to find optimal solutions at higher depths.

A summary of results obtained from a series of experimental test runs are shown in the table below. The full results can be found within the dissertation.

|Scramble Length|Number of Tests|Average Solve Time (HH:MM:SS)|Standard Deviation (HH:MM:SS)|
|-|-|-|-|
|10|10|00:00:00|00:00:00|
|11|10|00:00:23|00:00:14|
|12|10|00:05:22|00:02:38|
|13|50|01:42:12|01:31:13|
|14|2|33:28:37|27:17:02|