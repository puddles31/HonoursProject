package rubikscube;
import java.util.Scanner;

/**
 * This class contains a main method which allows the user to interact with a Rubik's Cube.
 */
public class CubeInteractive {
    public static void main(String[] args) {
        Cube cube = new Cube();

        Scanner sc = new Scanner(System.in);
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

                case "EDIT":
                    System.out.println("Cube is now in EDIT mode. Enter a colour (W, G, R, B, O, Y) to change the cube state. Enter 'DONE' to exit EDIT mode.");    
                    int index = 0;

                    while (!input.equals("DONE")) {
                        cube.printEditCubeState(index);

                        input = sc.nextLine().toUpperCase();
                        
                        switch (input) {
                            case "W": index++; break;
                            case "G": index++; break;
                            case "R": index++; break;
                            case "B": index++; break;
                            case "O": index++; break;
                            case "Y": index++; break;
                            case "DONE": break;
                            default:
                                break;
                        }

                        index = Math.max(0, Math.min(index, 47));
                    }

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
}
