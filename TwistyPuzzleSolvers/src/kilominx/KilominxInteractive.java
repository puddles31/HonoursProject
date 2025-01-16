package kilominx;
import java.util.Scanner;

/**
 * This class contains a main method which allows the user to interact with a Kilominx.
 */
public class KilominxInteractive {
    public static void main(String[] args) {
        Kilominx kilominx = new Kilominx();

        Scanner sc = new Scanner(System.in);
        String input = "";

        // Keep asking for moves until the user types "QUIT"
        while (!input.equals("QUIT")) {
            kilominx.printKilominxState();
            
            System.out.println("Make a move:");
            input = sc.nextLine().toUpperCase();

            switch (input) {
                case "U":
                    kilominx.moveU();
                    break;
                case "U'":
                    kilominx.moveUPrime();
                    break;
                case "U2":
                    kilominx.moveU2();
                    break;
                case "U2'":
                    kilominx.moveU2Prime();
                    break;

                case "D":
                    kilominx.moveD();
                    break;
                case "D'":
                    kilominx.moveDPrime();
                    break;
                case "D2":
                    kilominx.moveD2();
                    break;
                case "D2'":
                    kilominx.moveD2Prime();
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
