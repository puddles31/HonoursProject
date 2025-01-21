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
                    kilominx.moveU(); break;
                case "U'":
                    kilominx.moveUPrime(); break;
                case "U2":
                    kilominx.moveU2(); break;
                case "U2'":
                    kilominx.moveU2Prime(); break;

                case "L":
                    kilominx.moveL(); break;
                case "L'":
                    kilominx.moveLPrime(); break;
                case "L2":
                    kilominx.moveL2(); break;
                case "L2'":
                    kilominx.moveL2Prime(); break;

                case "F":
                    kilominx.moveF(); break;
                case "F'":
                    kilominx.moveFPrime(); break;
                case "F2":
                    kilominx.moveF2(); break;
                case "F2'":
                    kilominx.moveF2Prime(); break;

                case "R":
                    kilominx.moveR(); break;
                case "R'":
                    kilominx.moveRPrime(); break;
                case "R2":
                    kilominx.moveR2(); break;
                case "R2'":
                    kilominx.moveR2Prime(); break;

                case "BL":
                    kilominx.moveBLPrime(); break;
                case "BL'":
                    kilominx.moveBL(); break;
                case "BL2":
                    kilominx.moveBL2Prime(); break;
                case "BL2'":
                    kilominx.moveBL2(); break;
    
                case "BR":
                    kilominx.moveBRPrime(); break;
                case "BR'":
                    kilominx.moveBR(); break;
                case "BR2":
                    kilominx.moveBR2Prime(); break;
                case "BR2'":
                    kilominx.moveBR2(); break;
    
                case "DL":
                    kilominx.moveDLPrime(); break;
                case "DL'":
                    kilominx.moveDL(); break;
                case "DL2":
                    kilominx.moveDL2Prime(); break;
                case "DL2'":
                    kilominx.moveDL2(); break;
    
                case "DR":
                    kilominx.moveDRPrime(); break;
                case "DR'":
                    kilominx.moveDR(); break;
                case "DR2":
                    kilominx.moveDR2Prime(); break;
                case "DR2'":
                    kilominx.moveDR2(); break;
    
                case "DBL":
                    kilominx.moveDBLPrime(); break;
                case "DBL'":
                    kilominx.moveDBL(); break;
                case "DBL2":
                    kilominx.moveDBL2Prime(); break;
                case "DBL2'":
                    kilominx.moveDBL2(); break;
    
                case "DBR":
                    kilominx.moveDBRPrime(); break;
                case "DBR'":
                    kilominx.moveDBR(); break;
                case "DBR2":
                    kilominx.moveDBR2Prime(); break;
                case "DBR2'":
                    kilominx.moveDBR2(); break;
    
                case "DB":
                    kilominx.moveDBPrime(); break;
                case "DB'":
                    kilominx.moveDB(); break;
                case "DB2":
                    kilominx.moveDB2Prime(); break;
                case "DB2'":
                    kilominx.moveDB2(); break;

                case "D":
                    kilominx.moveD(); break;
                case "D'":
                    kilominx.moveDPrime(); break;
                case "D2":
                    kilominx.moveD2(); break;
                case "D2'":
                    kilominx.moveD2Prime(); break;
                
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
