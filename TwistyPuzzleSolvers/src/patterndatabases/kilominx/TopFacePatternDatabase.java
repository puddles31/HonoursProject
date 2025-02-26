package patterndatabases.kilominx;

import patterndatabases.PatternDatabase;
import models.Kilominx;
import models.ITwistyPuzzle;

/**
 * The pattern database for the top 5 kubies (on the top/white face) of a Kilominx.
 * @see PatternDatabase
 */
public class TopFacePatternDatabase extends PatternDatabase {
    
    // For this database, we are looking at 5 out of 20 kubies, so n=20 and k=5
    // There are 20(P)5 * 3^5 (5 of 20 kubies; each of 5 kubies can be in one of 3 orientations) = 452096640 possible states
    // Also note that roughly 431MB storage needed (452096640 bytes / 1024^2 = ~431MB)
    final static int DATABASE_SIZE = 452096640;
    final static byte N = 20, K = 5;

    /**
     * Constructor for the top face pattern database.
     * Sets the database size to 452096640, n to 20, and k to 5.
     */
    public TopFacePatternDatabase() {
        super(DATABASE_SIZE, N, K);
    }

    protected int getDatabaseIndex(ITwistyPuzzle puzzle) throws IllegalArgumentException {
        if (!(puzzle instanceof Kilominx)) {
            throw new IllegalArgumentException("The puzzle must be a Kilominx.");
        }
        Kilominx kilominx = (Kilominx) puzzle;
        
        // Get the kubie indices and orientations from the kilominx
        byte[] allKubieIndices = kilominx.getKubieIndices();
        byte[] allKubieOrientations = kilominx.getKubieOrientations();

        // Get the top face kubie indices and orientations from the kilominx
        byte[] kubieIndices = new byte[5];
        byte[] kubieOrientations = new byte[5];

        // Iterate over kubies until the 5 on the top face are found
        int counter = 0;
        for (byte i = 0; i < 20; i++) {
            int kubieIndex = allKubieIndices[i];

            if (kubieIndex < 3) {
                kubieIndices[kubieIndex] = i;
                kubieOrientations[kubieIndex] = allKubieOrientations[i];
                counter++;
            }
            else if (kubieIndex == 4) {
                kubieIndices[3] = i;
                kubieOrientations[3] = allKubieOrientations[i];
                counter++;
            }
            else if (kubieIndex == 6) {
                kubieIndices[4] = i;
                kubieOrientations[4] = allKubieOrientations[i];
                counter++;
            }

            if (counter == 5) {
                break;
            }
        }

        // Calculate the rank of the kubie indices
        int indexRank = calculateLehmerRank(kubieIndices);

        // The orientation rank is calculated by using the orientations as base-3, and converting to base-10
        int orientationRank =
            kubieOrientations[0] * 81 +   // 3^4
            kubieOrientations[1] * 27 +   // 3^3
            kubieOrientations[2] * 9 +    // 3^2
            kubieOrientations[3] * 3 +    // 3^1
            kubieOrientations[4];         // 3^0
        
        // (243 = 3^5)
        return indexRank * 243 + orientationRank;
    }
}
