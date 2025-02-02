package rubikscube;
import java.util.Arrays;
import java.util.Scanner;

import rubikscube.Cube.Colour;

/**
 * This class contains a main method which allows the user to interact with a Rubik's Cube.
 */
public class CubeInteractive {

    private static Scanner sc;

    public static void main(String[] args) {
        Cube cube = new Cube();

        sc = new Scanner(System.in);
        String input = "";

        // Keep asking for moves until the user types "QUIT"
        while (!input.equals("QUIT")) {
            cube.printCubeState();

            System.out.println("Make a move:");
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

                case "EDIT":
                    cube = editCube(cube);
                    continue;

                case "SOLVE":
                    CubeSolver solver = new CubeSolver(cube);
                    solver.solveCube();
                    input = "QUIT";
                    break;

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
        // remove this in future, allow user to edit current state instead of creating a new cube
        cube = new Cube(); 

        System.out.println("Cube is now in EDIT mode. Enter a colour (W, G, R, B, O, Y) to change the selected cell. " +
                           "Enter '-' or 'BACK' to go to the previous cell. Enter 'DONE' to exit EDIT mode.");    
        int index = 0;
        Colour[] colours = new Colour[48];

        // Temp: set colours array to a solved cube state
        Arrays.fill(colours, 0, 8, Colour.W);

        Arrays.fill(colours, 8, 11, Colour.G);
        Arrays.fill(colours, 11, 14, Colour.R);
        Arrays.fill(colours, 14, 17, Colour.B);
        Arrays.fill(colours, 17, 20, Colour.O);

        Arrays.fill(colours, 20, 22, Colour.G);
        Arrays.fill(colours, 22, 24, Colour.R);
        Arrays.fill(colours, 24, 26, Colour.B);
        Arrays.fill(colours, 26, 28, Colour.O);

        Arrays.fill(colours, 28, 31, Colour.G);
        Arrays.fill(colours, 31, 34, Colour.R);
        Arrays.fill(colours, 34, 37, Colour.B);
        Arrays.fill(colours, 37, 40, Colour.O);

        Arrays.fill(colours, 40, 48, Colour.Y);

        // // Copy the cube state to the colours array
        // for (int i = 0; i < 48; i++) {
        //     colours[i] = cube.getColour(i);
        // }

        String input = "";

        while (!input.equals("DONE")) {
            Cube.printEditCubeState(colours, index);

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
                System.out.println("Enter 'DONE' to exit EDIT mode.");
                if (input.equals("-") || input.equals("BACK")) {
                    index--;
                }
                else if (input.equals("DONE")) {
                    break;
                }
            }

            // Keep index between 0 and 48
            index = Math.max(0, Math.min(index, 48));
        }

        // Convert the colours array to a cube state
        return new Cube(colours);
    }

}
