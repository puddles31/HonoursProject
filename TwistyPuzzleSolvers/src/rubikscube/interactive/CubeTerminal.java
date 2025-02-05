package rubikscube.interactive;

import rubikscube.Cube;
import rubikscube.CubeSolver;
import rubikscube.Cube.Colour;
import rubikscube.CubeMoves.Move;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class contains methods which allow the user to interact with a Rubik's Cube.
 * If running the main method, the constructor should be called with {@code guiMode} set to {@code false}.
 * This will allow the user to make moves and enter commands via the terminal.
 * If {@code guiMode} is set to {@code true}, the user will not be able to edit the cube, but will be able to make moves and enter commands via the GUI.
 */
public class CubeTerminal {

    private static final String USAGE_MESSAGE = "\nCommands:\n" +
                                                "  HELP           -   Display this help message\n" +
                                                "  RESET          -   Reset the cube to the solved state\n" +
                                                "  EDIT           -   Edit the cube\n" +
                                                "  SCRAMBLE [n]   -   Scramble the cube with n random moves\n" +
                                                "  SOLVE          -   Solve the cube\n" +
                                                "  QUIT           -   Exit the program\n\n" +
                                                "Make moves on the cube with the syntax: <FACE>[MODIFIER]\n" +
                                                "  where <FACE> is one of U, L, F, R, B, D\n" +
                                                "  and [MODIFIER] (optional argument) is either ' (counter-clockwise turn), or 2 (double turn).\n" +
                                                "  For example, D' is the counter-clockwise turn of the Down face.\n";

    private Cube cube;
    private static Scanner sc;
    private boolean guiMode;
    private Pattern movePattern, scramblePattern;

    /**
     * The main method which allows the user to interact with a Rubik's Cube via the terminal by making moves or entering commands.
     */
    public static void main(String[] args) {
        CubeTerminal terminal = new CubeTerminal(new Cube(), false);

        String input = "";

        // Keep asking for moves until the user types "QUIT"
        while (!input.equals("QUIT")) {
            terminal.cube.printCubeState();

            System.out.println("Make a move, or enter a command:");
            input = sc.nextLine().toUpperCase().trim();
            
            String out = terminal.handleInput(input);
            System.out.println(out);
        }

        sc.close();
    }

    /** 
     * Constructor for a cube terminal.
     * @param cube - The cube to interact with.
     * @param guiMode - Whether the terminal is in GUI mode or not.
     */
    public CubeTerminal(Cube cube, boolean guiMode) {
        this.cube = cube;
        this.guiMode = guiMode;

        if (!guiMode) {
            sc = new Scanner(System.in);
        }

        movePattern = Pattern.compile("^([ULFRBD]['2]?)$");
        scramblePattern = Pattern.compile("^SCRAMBLE ([0-9]+)$");
    }


    /**
     * Handle the user input by making a move or executing a command.
     * @param input - The user input.
     * @return The output message (if any) after the input is processed.
     */
    public String handleInput(String input) {
        String out = "";

        switch (input) {
            case "SCRAMBLE":
                // Default scramble is 10 moves
                Move[] scrambleMoves = cube.moves.scramble(10);
                out = "Moves made during scramble:\n";
                for (int i = 0; i < scrambleMoves.length; i++) {
                    out += scrambleMoves[i] + "\n";
                }
                break;
            
            case "RESET":
                cube.resetCube();
                break;

            case "EDIT":
                if (!guiMode) {
                    editCube();
                }
                else {
                    out = "ERROR: Cannot edit cube in GUI mode.\n";
                }
                break;

            case "SOLVE":
                CubeSolver solver = new CubeSolver(cube);
                try {
                    Move[] moves = solver.solveCube();
                
                    // Output and perform the moves to solve the cube
                    out = "Moves to solve the cube:\n";
                    for (int i = 0; i < moves.length; i++) {
                        out += moves[i] + "\n";
                        cube.moves.makeMove(moves[i]);
                    }
                    out += "\n";
                    
                }
                catch (IllegalStateException e) {
                    out = "ERROR: " + e.getMessage() + "\n";
                }
                break;
            
            case "HELP":
                out = USAGE_MESSAGE;
                break;

            case "QUIT":
                break;

            default:
                // Check if input is a valid move
                Matcher moveMatcher = movePattern.matcher(input);
                if (moveMatcher.matches()) {
                    String move = moveMatcher.group(1);

                    cube.moves.makeMove(Move.fromString(move));
                    break;
                }

                // Check if input is a SCRAMBLE command with a number
                Matcher scrambleMatcher = scramblePattern.matcher(input);
                if (scrambleMatcher.matches()) {
                    int n = Integer.parseInt(scrambleMatcher.group(1));

                    if (n > 0 && n <= 100) {
                        Move[] scramble = cube.moves.scramble(n);
                        out = "Moves made during scramble:\n";
                        for (int i = 0; i < scramble.length; i++) {
                            out += scramble[i] + "\n";
                        }
                    }
                    else {
                        out = "ERROR: Number of moves must be between 1 and 100.\n";
                    }
                    break;
                }

                out = "ERROR: '" + input + "' is not a valid move or command.";
        }

        return out;
    }

    /**
     * Enter EDIT mode to allow the user to change the colours of the cube.
     */
    private void editCube() {
        Colour[] colours = cube.getColours();
        
        System.out.println("Cube is now in EDIT mode. Enter a colour (W, G, R, B, O, Y) to change the selected cell. " +
        "Enter '-' to go to the previous cell. Enter 'DONE' to exit EDIT mode.");    
        
        int index = 0;
        String input = "";

        // Keep asking for input until the user types "DONE"
        while (!input.equals("DONE")) {
            Cube.printEditCubeState(colours, index);
            if (index == 48) {
                System.out.println("Press ENTER to finish editing. Enter '-' to go back and continue editing.");
            }

            input = sc.nextLine().toUpperCase();
            
            // Change the colour of the selected cell or move to next/previous cell based on the input
            if (index < 48) {
                switch (input) {
                    case "W": colours[index] = Colour.W; index++; break;
                    case "G": colours[index] = Colour.G; index++; break;
                    case "R": colours[index] = Colour.R; index++; break;
                    case "B": colours[index] = Colour.B; index++; break;
                    case "O": colours[index] = Colour.O; index++; break;
                    case "Y": colours[index] = Colour.Y; index++; break;
                    case "": index++; break;
                    case "-": index--; break;
                    case "DONE": break;
                    default:
                        break;
                }
            }
            // If the user is at the last cell, allow them to finish editing by pressing ENTER
            else {
                if (input.equals("-")) {
                    index--;
                }
                else if (input.equals("") || input.equals("DONE")) {
                    break;
                }
            }

            // Keep index between 0 and 48
            index = Math.max(0, Math.min(index, 48));
        }

        // Convert the colours array to a cube state
        try {
            cube = new Cube(colours);
        }
        catch (IllegalArgumentException e) {
            System.out.println("ERROR: " + e.getMessage() + " Cube reset to previous state.\n");
        }
    }

}
