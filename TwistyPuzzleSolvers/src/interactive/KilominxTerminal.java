package interactive;

import models.Kilominx;
import models.Kilominx.Colour;
import models.KilominxController.Move;
import models.IMoveController.IMove;
import solvers.KilominxSolver;

import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class contains methods which allow the user to interact with a Kilominx.
 * This will allow the user to make moves and enter commands via the terminal.
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
    private Pattern movePattern, scramblePattern;

    /**
     * The main method which allows the user to interact with a Kilominx via the terminal by making moves or entering commands.
     */
    public static void main(String[] args) {
        KilominxTerminal terminal = new KilominxTerminal(new Kilominx());

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
     */
    public KilominxTerminal(Kilominx kilominx) {
        this.kilominx = kilominx;

        sc = new Scanner(System.in);

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
                editKilominx();
                break;
            
            case "SOLVE":
                KilominxSolver solver = new KilominxSolver(kilominx);
                try {
                    IMove[] moves = solver.solve();
                
                    // Output and perform the moves to solve the kilominx
                    out = "Moves to solve the kilominx:\n";
                    for (int i = 0; i < moves.length; i++) {
                        out += moves[i] + "\n";
                        kilominx.getMoveController().makeMove(moves[i]);
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

    /**
     * Enter EDIT mode to allow the user to change the colours of the kilominx.
     */
    private void editKilominx() {
        Colour[] colours = kilominx.getColours();
        
        System.out.println("Kilominx is now in EDIT mode. Enter a colour (Wh, DG, Re, DB, Ye, Pu, LB, Be, Pi, LG, Or, Gy) to change the selected cell. " +
        "Enter '-' to go to the previous cell. Enter 'DONE' to exit EDIT mode.");    
        
        int index = 0;
        String input = "";

        // Keep asking for input until the user types "DONE"
        while (!input.equals("DONE")) {
            Kilominx.printEditState(colours, index);
            if (index == 60) {
                System.out.println("Press ENTER to finish editing. Enter '-' to go back and continue editing.");
            }

            input = sc.nextLine().toUpperCase();
            
            // Change the colour of the selected cell or move to next/previous cell based on the input
            // If the current index is before or after an uneditable cell, skip to the next editable cell
            if (index < 60) {
                switch (input) {
                    case "WH": colours[index] = Colour.Wh; index = (index == 2 ? index + 2 : (index == 7 ? index + 3 : index + 1)); break;
                    case "DG": colours[index] = Colour.DG; index = (index == 2 ? index + 2 : (index == 7 ? index + 3 : index + 1)); break;
                    case "RE": colours[index] = Colour.Re; index = (index == 2 ? index + 2 : (index == 7 ? index + 3 : index + 1)); break;
                    case "DB": colours[index] = Colour.DB; index = (index == 2 ? index + 2 : (index == 7 ? index + 3 : index + 1)); break;
                    case "YE": colours[index] = Colour.Ye; index = (index == 2 ? index + 2 : (index == 7 ? index + 3 : index + 1)); break;
                    case "PU": colours[index] = Colour.Pu; index = (index == 2 ? index + 2 : (index == 7 ? index + 3 : index + 1)); break;
                    case "LB": colours[index] = Colour.LB; index = (index == 2 ? index + 2 : (index == 7 ? index + 3 : index + 1)); break;
                    case "BE": colours[index] = Colour.Be; index = (index == 2 ? index + 2 : (index == 7 ? index + 3 : index + 1)); break;
                    case "PI": colours[index] = Colour.Pi; index = (index == 2 ? index + 2 : (index == 7 ? index + 3 : index + 1)); break;
                    case "LG": colours[index] = Colour.LG; index = (index == 2 ? index + 2 : (index == 7 ? index + 3 : index + 1)); break;
                    case "OR": colours[index] = Colour.Or; index = (index == 2 ? index + 2 : (index == 7 ? index + 3 : index + 1)); break;
                    case "GY": colours[index] = Colour.Gy; index = (index == 2 ? index + 2 : (index == 7 ? index + 3 : index + 1)); break;
                    case "": index = (index == 2 ? index + 2 : (index == 7 ? index + 3 : index + 1)); break;
                    case "-": index = (index == 4 ? index - 2 : (index == 10 ? index - 3 : index - 1)); break;
                    case "DONE": break;
                    default: break;
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

            // Keep index between 0 and 60
            index = Math.max(0, Math.min(index, 60));
        }

        // Convert the colours array to a kilominx state
        try {
            kilominx = new Kilominx(colours);
        }
        catch (IllegalArgumentException e) {
            System.out.println("ERROR: " + e.getMessage() + " Kilominx reset to previous state.\n");
        }
    }
}
