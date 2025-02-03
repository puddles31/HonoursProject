package rubikscube;
import java.util.Scanner;

import rubikscube.Cube.Colour;
import rubikscube.Cube.Move;

/**
 * This class contains a main method which allows the user to interact with a Rubik's Cube by making moves or entering commands.
 */
public class CubeTerminal {

    private static Scanner sc;
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

    public static void main(String[] args) {
        Cube cube = new Cube();

        sc = new Scanner(System.in);
        String input = "";

        // Keep asking for moves until the user types "QUIT"
        while (!input.equals("QUIT")) {
            cube.printCubeState();

            System.out.println("Make a move, or enter a command:");
            input = sc.nextLine().toUpperCase();

            switch (input) {
                case "U":
                    cube.moveU();
                    break;
                case "U'":
                    cube.moveUPrime();
                    break;
                case "U2":
                    cube.moveU2();
                    break;

                case "L":
                    cube.moveL();
                    break;
                case "L'":
                    cube.moveLPrime();
                    break;
                case "L2":
                    cube.moveL2();
                    break;

                case "F":
                    cube.moveF();
                    break;
                case "F'":
                    cube.moveFPrime();
                    break;
                case "F2":
                    cube.moveF2();
                    break;

                case "R":
                    cube.moveR();
                    break;
                case "R'":
                    cube.moveRPrime();
                    break;
                case "R2":
                    cube.moveR2();
                    break;

                case "B":
                    cube.moveB();
                    break;
                case "B'":
                    cube.moveBPrime();
                    break;
                case "B2":
                    cube.moveB2();
                    break;

                case "D":
                    cube.moveD();
                    break;
                case "D'":
                    cube.moveDPrime();
                    break;
                case "D2":
                    cube.moveD2();
                    break;

                case "SCRAMBLE":
                    cube.scramble(10);
                    break;
                
                case "RESET":
                    cube = new Cube();
                    break;

                case "EDIT":
                    cube = editCube(cube);
                    continue;

                case "SOLVE":
                    CubeSolver solver = new CubeSolver(cube);
                    try {
                        Move[] moves = solver.solveCube();
                    
                        // Print the moves to solve the cube
                        System.out.println("Moves to solve the cube:");
                        for (int i = 0; i < moves.length; i++) {
                            System.out.print(moves[i].toString() + " ");
                        }
                        System.out.println("\n");

                        // TODO: Perform moves to solve the cube? Or leave to user?
                    }
                    catch (IllegalStateException e) {
                        System.out.println("ERROR: " + e.getMessage() + "\n");
                    }
                    break;
                
                case "HELP":
                    System.out.println(USAGE_MESSAGE);

                case "QUIT":
                    break;

                default:
                    if (input.startsWith("SCRAMBLE") && input.split(" ").length == 2) {
                        int n = Integer.parseInt(input.split(" ")[1]);
                        cube.scramble(n);
                    }
                    else {
                        System.out.println("ERROR: '" + input + "' is not a valid move.");
                    }
                    break;
            }
        }

        sc.close();
    }


    private static Cube editCube(Cube cube) {
        Colour[] colours = cube.getColours();
        
        System.out.println("Cube is now in EDIT mode. Enter a colour (W, G, R, B, O, Y) to change the selected cell. " +
        "Enter '-' or 'BACK' to go to the previous cell. Enter 'DONE' to exit EDIT mode.");    
        
        int index = 0;
        String input = "";

        while (!input.equals("DONE")) {
            Cube.printEditCubeState(colours, index);
            if (index == 48) {
                System.out.println("Press ENTER to finish editing. Enter '-' or 'BACK' to go back and continue editing.");
            }

            input = sc.nextLine().toUpperCase();
            
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
                    case "BACK": index--; break;
                    case "DONE": break;
                    default:
                        break;
                }
            }
            else {
                if (input.equals("-") || input.equals("BACK")) {
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
            return new Cube(colours);
        }
        catch (IllegalArgumentException e) {
            System.out.println("ERROR: " + e.getMessage() + " Cube reset to previous state.\n");
            return cube;
        }
    }

}
