package patterndatabases.kilominx;

import patterndatabases.PatternDatabase;
import models.Kilominx;
import models.ITwistyPuzzle;

/**
 * The pattern database for a sparse selection of kubies of a Kilominx. 
 * Depending on the selected set number, it looks at kubie indices:
 *   0 (UFL), 7 (MBR), 13 (DFM), 14 (BLD)   (set 1)
 *   2 (UFR), 9 (MBM), 16 (FLD), 18 (DFR)   (set 2)
 *   4 (UBR), 8 (FMD), 11 (MBL), 19 (DBR)   (set 3)
 *   6 (UBM), 3 (MFL), 10 (FRD), 17 (DBL)   (set 4)
 *   1 (UBL), 5 (MFR), 12 (BRD), 15 (DFL)   (set 5)
 * 
 * @see PatternDatabase
 */
public class SparseKubiesPatternDatabase extends PatternDatabase {
    
    // For this database, we are looking at 4 out of 20 kubies, so n=20 and k=4
    // There are 20(P)4 * 3^4 (4 of 20 kubies; each of 4 kubies can be in one of 3 orientations) = 9418680 possible states
    // Also note that roughly 9MB storage needed (9418680 bytes / 1024^2 = ~9MB)
    final static int DATABASE_SIZE = 9418680;
    final static byte N = 20, K = 4;

    // Kubie sets
    static final byte[] SET_1 = {0, 7, 13, 14};
    static final byte[] SET_2 = {2, 9, 16, 18};
    static final byte[] SET_3 = {4, 8, 11, 19};
    static final byte[] SET_4 = {3, 6, 10, 17};
    static final byte[] SET_5 = {1, 5, 12, 15};

    // The set of kubies to look at (see above)
    byte[] set;

    /**
     * Constructor for the sparse kubies pattern database.
     * Sets the database size to 9418680, n to 20, and k to 4.
     * @param setNo The set number of the kubies to look at (1-5)
     * @throws IllegalArgumentException If the set number is not between 1 and 5
     */
    public SparseKubiesPatternDatabase(int setNo) throws IllegalArgumentException {
        super(DATABASE_SIZE, N, K);

        switch (setNo) {
            case 1:
                set = SET_1;
                break;
            case 2:
                set = SET_2;
                break;
            case 3:
                set = SET_3;
                break;
            case 4:
                set = SET_4;
                break;
            case 5:
                set = SET_5;
                break;
            default:
                throw new IllegalArgumentException("The set number must be between 1 and 5.");
        }
    }

    protected int getDatabaseIndex(ITwistyPuzzle puzzle) throws IllegalArgumentException {
        if (!(puzzle instanceof Kilominx)) {
            throw new IllegalArgumentException("The puzzle must be a Kilominx.");
        }
        Kilominx kilominx = (Kilominx) puzzle;
        
        // Get the kubie indices and orientations from the kilominx
        byte[] allKubieIndices = kilominx.getKubieIndices();
        byte[] allKubieOrientations = kilominx.getKubieOrientations();

        // Get the kubie indices and orientations from the kilominx
        byte[] kubieIndices = new byte[4];
        byte[] kubieOrientations = new byte[4];

        // Iterate over kubies until the specific kubies are found
        int counter = 0;
        for (byte i = 0; i < 20; i++) {
            int kubieIndex = allKubieIndices[i];

            if (kubieIndex == set[0]) {
                kubieIndices[0] = i;
                kubieOrientations[0] = allKubieOrientations[i];
                counter++;
            }
            else if (kubieIndex == set[1]) {
                kubieIndices[1] = i;
                kubieOrientations[1] = allKubieOrientations[i];
                counter++;
            }
            else if (kubieIndex == set[2]) {
                kubieIndices[2] = i;
                kubieOrientations[2] = allKubieOrientations[i];
                counter++;
            }
            else if (kubieIndex == set[3]) {
                kubieIndices[3] = i;
                kubieOrientations[3] = allKubieOrientations[i];
                counter++;
            }

            if (counter == 4) {
                break;
            }
        }

        // Calculate the rank of the kubie indices
        int indexRank = calculateLehmerRank(kubieIndices);

        // The orientation rank is calculated by using the orientations as base-3, and converting to base-10
        int orientationRank =
            kubieOrientations[0] * 27 +   // 3^3
            kubieOrientations[1] * 9 +    // 3^2
            kubieOrientations[2] * 3 +    // 3^1
            kubieOrientations[3];         // 3^0
        
        // (81 = 3^4)
        return indexRank * 81 + orientationRank;
    }
}
