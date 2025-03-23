package patterndatabases.kilominx;

import patterndatabases.PatternDatabase;
import models.Kilominx;
import models.ITwistyPuzzle;

/**
 * The pattern database for a sparse selection of kubies of a Kilominx. 
 * Depending on the selected set number, it looks at kubie indices:
 *   2 (UFR), 7 (MBR), 13 (DFM), 14 (BLD)   (set 1 - note that UFL replaced by UFR, as UFL is now a fixed kubie)
 *   2 (UFR), 9 (MBM), 16 (FLD), 18 (DFR)   (set 2)
 *   4 (UBR), 8 (FMD), 11 (MBL), 19 (DBR)   (set 3)
 *   6 (UBM), 3 (MFL), 10 (FRD), 17 (DBL)   (set 4)
 *   1 (UBL), 5 (MFR), 12 (BRD), 15 (DFL)   (set 5)
 * 
 * @see PatternDatabase
 */
public class SparseKubiesPatternDatabase extends PatternDatabase {
    
    // For this database, we are looking at 4 out of 19 kubies (the 20th kubie is fixed), so n=19 and k=4
    // There are 19(P)4 * 3^4 (4 of 19 kubies; each of 4 kubies can be in one of 3 orientations) = 7534944 possible states
    // Also note that roughly 7MB storage needed (7534944 bytes / 1024^2 = ~7MB)
    final static int DATABASE_SIZE = 7534944;
    final static byte N = 19, K = 4;

    // Kubie sets
    static final byte[] SET_1 = {Kilominx.KUBIE_UFR, Kilominx.KUBIE_MBR, Kilominx.KUBIE_DFM, Kilominx.KUBIE_BLD};
    static final byte[] SET_2 = {Kilominx.KUBIE_UFR, Kilominx.KUBIE_MBM, Kilominx.KUBIE_FLD, Kilominx.KUBIE_DFR};
    static final byte[] SET_3 = {Kilominx.KUBIE_UBR, Kilominx.KUBIE_FMD, Kilominx.KUBIE_MBL, Kilominx.KUBIE_DBR};
    static final byte[] SET_4 = {Kilominx.KUBIE_UBM, Kilominx.KUBIE_MFL, Kilominx.KUBIE_FRD, Kilominx.KUBIE_DBL};
    static final byte[] SET_5 = {Kilominx.KUBIE_UBL, Kilominx.KUBIE_MFR, Kilominx.KUBIE_BRD, Kilominx.KUBIE_DFL};

    // The set of kubies to look at (see above)
    byte[] set;

    /**
     * Constructor for the sparse kubies pattern database.
     * Sets the database size to 7534944, n to 19, and k to 4.
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

        byte[] kubieIndices = new byte[4];
        byte[] kubieOrientations = new byte[4];

        // Iterate over kubies until the specific kubies are found
        int counter = 0;
        for (byte i = 0; i < 20; i++) {
            int kubieIndex = allKubieIndices[i];

            if (kubieIndex == set[0]) {
                kubieIndices[0] = (byte)(i - 1); // UFL is fixed at position 0, so shift all indices down by 1
                kubieOrientations[0] = allKubieOrientations[i];
                counter++;
            }
            else if (kubieIndex == set[1]) {
                kubieIndices[1] = (byte)(i - 1); // UFL is fixed at position 0, so shift all indices down by 1
                kubieOrientations[1] = allKubieOrientations[i];
                counter++;
            }
            else if (kubieIndex == set[2]) {
                kubieIndices[2] = (byte)(i - 1); // UFL is fixed at position 0, so shift all indices down by 1
                kubieOrientations[2] = allKubieOrientations[i];
                counter++;
            }
            else if (kubieIndex == set[3]) {
                kubieIndices[3] = (byte)(i - 1); // UFL is fixed at position 0, so shift all indices down by 1
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
