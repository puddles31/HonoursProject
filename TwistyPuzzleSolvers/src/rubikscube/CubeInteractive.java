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

                case "SCRAMBLE":
                    cube.scramble();
                    break;

                case "SOLVE":
                    CubeSolver solver = new CubeSolver(cube);
                    solver.solveCube();
                    input = "QUIT";
                    break;

                case "QUIT":
                    break;

                default:
                    System.out.println("ERROR: '" + input + "' is not a valid move.");
                    break;
            }
        }

        sc.close();
    }
}
