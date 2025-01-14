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
            
            break;  // temp break point for now
            
            // System.out.println("Make a move:");
            // input = sc.nextLine().toUpperCase();

            // switch (input) {
                
            //     case "QUIT":
            //         break;

            //     default:
            //         System.out.println("ERROR: '" + input + "' is not a valid move.");
            //         break;
            // }
        }

        sc.close();
    }
}
