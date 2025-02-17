package interactive;

import models.Kilominx;
import models.KilominxController.Move;

import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class contains methods which allow the user to interact with a Kilominx.
 * If running the main method, the constructor should be called with {@code guiMode} set to {@code false}.
 * This will allow the user to make moves and enter commands via the terminal.
 * If {@code guiMode} is set to {@code true}, the user will not be able to edit the kilominx, but will be able to make moves and enter commands via the GUI.
 */
public class KilominxTerminal {

    private static final String USAGE_MESSAGE = "\nCommands:\n" +
                                                "  HELP         - Display this help message\n" +
                                                "  RESET        - Reset the kilominx to the solved state\n" +
                                                "  EDIT         - Edit the kilominx\n" +
                                                "  SCRAMBLE [n] - Scramble the kilominx with n random moves\n" +
                                                "  SOLVE        - Solve the kilominx\n" +
                                                "  QUIT         - Exit the program\n\n" +
                                                "Make moves on the kilominx with the syntax: <FACE>[MODIFIER]\n" +
                                                "  where <FACE> is one of U, L, F, R, BL, BR, DL, DR, DBL, DBR, DB, D\n" +
                                                "  and [MODIFIER] (optional argument) is either ' (counter-clockwise turn), 2 (double turn), or 2' (double counter-clockwise turn).\n" +
                                                "  For example, DBL2' is the double counter-clockwise turn of the Down-Back-Left face.\n";

    private Kilominx kilominx;
    private static Scanner sc;
    private boolean guiMode;
    private Pattern movePattern, scramblePattern;

    /**
     * The main method which allows the user to interact with a Kilominx via the terminal by making moves or entering commands.
     */
    public static void main(String[] args) {
        KilominxTerminal terminal = new KilominxTerminal(new Kilominx(), false);

        String input = ".";

        // Keep asking for moves until the user types "QUIT"
        while (!input.equals("QUIT")) {

            if (!input.equals("")) {
                terminal.kilominx.printState();
                System.out.println("Make a move, or enter a command:");
            }

            input = sc.nextLine().toUpperCase().trim();

            String out = terminal.handleInput(input);
            if(!out.equals("")) {
                System.out.println(out);
            }
        }
        sc.close();
    }

    /**
     * Constructor for a kilominx terminal.
     * @param kilominx The kilominx to interact with.
     * @param guiMode Whether the terminal is in GUI mode or not.
     */
    public KilominxTerminal(Kilominx kilominx, boolean guiMode) {
        this.kilominx = kilominx;
        this.guiMode = guiMode;

        if (!guiMode) {
            sc = new Scanner(System.in);
        }
        // TODO: remove else statement after GUI implemented
        else {
            System.out.println("ERROR: GUI mode not yet implemented.");
            System.exit(0);
        }

        movePattern = Pattern.compile("^(U|L|F|R|BL|BR|DL|DR|DBL|DBR|DB|D)(2?'?)$");
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
                Move[] scrambleMoves = kilominx.getMoveController().scramble(10);
                out = "Move made during scramble:\n";
                for (int i = 0; i < scrambleMoves.length; i++) {
                    out += scrambleMoves[i] + "\n";
                }

                break;
            
            case "RESET":
                kilominx.reset();
                break;
            
            case "EDIT":
                //TODO: update after implementing EDIT mode
                out = "ERROR: Edit mode not implemented yet.\n";

                // if (!guiMode) {
                //     editCube();
                // }
                // else {
                //     out = "ERROR: Cannot edit cube in GUI mode.\n";
                // }

                break;
            
            case "SOLVE":
                // TODO: update after implementing solver
                out = "ERROR: Solver not implemented yet.\n";
                
                // KilominxSolver solver = new KilominxSolver(kilominx);
                // try {
                //     Move[] moves = solver.solveKilominx();
                
                //     // Output and perform the moves to solve the kilominx
                //     out = "Moves to solve the kilominx:\n";
                //     for (int i = 0; i < moves.length; i++) {
                //         out += moves[i] + "\n";
                //         kilominx.moves.makeMove(moves[i]);
                //     }
                //     out += "\n";
                    
                // }
                // catch (IllegalStateException e) {
                //     out = "ERROR: " + e.getMessage() + "\n";
                // }

                break;

            case "HELP":
                out = USAGE_MESSAGE;
                break;
            
            case "QUIT":
                System.exit(0);
                break;
            
            case "":
                break;

            default:
                // Check if input is a valid move
                Matcher moveMatcher = movePattern.matcher(input);
                if (moveMatcher.matches()) {
                    String move = moveMatcher.group(1) + moveMatcher.group(2);

                    kilominx.getMoveController().makeMove(kilominx.getMoveController().parseMove(move));
                    break;
                }

                // Check if input is a SCRAMBLE command with a number
                Matcher scrambleMatcher = scramblePattern.matcher(input);
                if (scrambleMatcher.matches()) {
                    int n = Integer.parseInt(scrambleMatcher.group(1));

                    if (n > 0 && n <= 100) {
                        Move[] scramble = kilominx.getMoveController().scramble(n);
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

    // TODO: implement private void editCube() method
}
