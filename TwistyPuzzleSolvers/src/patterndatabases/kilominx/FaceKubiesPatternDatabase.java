package patterndatabases.kilominx;

import patterndatabases.PatternDatabase;
import models.Kilominx;
import models.ITwistyPuzzle;

/**
 * The pattern database for the 5 kubies on a specified face of a Kilominx. (on faces U, F and L, kubie UFL is replaced by a kubie close to the other kubies on the face)
 * Depending on the selected set number, it looks at the kubie indices that are part of a specific face on the Kilominx.
 * @see PatternDatabase
 */
public class FaceKubiesPatternDatabase extends PatternDatabase {
    
    // For this database, we are looking at 5 out of 19 kubies (the 20th kubie is fixed), so n=19 and k=5
    // There are 19(P)5 * 3^5 (5 of 19 kubies; each of 5 kubies can be in one of 3 orientations) = 339072480 possible states
    // Also note that roughly 323MB storage needed (339072480 bytes / 1024^2 = ~323MB)
    final static int DATABASE_SIZE = 339072480;
    final static byte N = 19, K = 5;

    // Kubie sets
    static final byte[] SET_1 =  {Kilominx.KUBIE_MBR, Kilominx.KUBIE_UBL, Kilominx.KUBIE_UFR, Kilominx.KUBIE_UBR, Kilominx.KUBIE_UBM}; // top face; UFL is fixed, so replaced by MBR
    static final byte[] SET_2 =  {Kilominx.KUBIE_DFL, Kilominx.KUBIE_UBL, Kilominx.KUBIE_MFL, Kilominx.KUBIE_MBL, Kilominx.KUBIE_FLD}; // left face; UFL is fixed, so replaced by DFL
    static final byte[] SET_3 =  {Kilominx.KUBIE_DFM, Kilominx.KUBIE_UFR, Kilominx.KUBIE_MFL, Kilominx.KUBIE_MFR, Kilominx.KUBIE_FMD}; // front face; UFL is fixed, so replaced by DFM
    static final byte[] SET_4 =  {Kilominx.KUBIE_UFR, Kilominx.KUBIE_UBR, Kilominx.KUBIE_MFR, Kilominx.KUBIE_MBR, Kilominx.KUBIE_FRD}; // right face
    static final byte[] SET_5 =  {Kilominx.KUBIE_UBL, Kilominx.KUBIE_UBM, Kilominx.KUBIE_MBM, Kilominx.KUBIE_MBL, Kilominx.KUBIE_BLD}; // back-left face
    static final byte[] SET_6 =  {Kilominx.KUBIE_UBR, Kilominx.KUBIE_UBM, Kilominx.KUBIE_MBR, Kilominx.KUBIE_MBM, Kilominx.KUBIE_BRD}; // back-right face
    static final byte[] SET_7 =  {Kilominx.KUBIE_MFL, Kilominx.KUBIE_FMD, Kilominx.KUBIE_DFM, Kilominx.KUBIE_DFL, Kilominx.KUBIE_FLD}; // down-left face
    static final byte[] SET_8 =  {Kilominx.KUBIE_MFR, Kilominx.KUBIE_FMD, Kilominx.KUBIE_FRD, Kilominx.KUBIE_DFM, Kilominx.KUBIE_DFR}; // down-right face
    static final byte[] SET_9 =  {Kilominx.KUBIE_MBL, Kilominx.KUBIE_BLD, Kilominx.KUBIE_DFL, Kilominx.KUBIE_FLD, Kilominx.KUBIE_DBL}; // down-back-left face
    static final byte[] SET_10 = {Kilominx.KUBIE_MBR, Kilominx.KUBIE_FRD, Kilominx.KUBIE_BRD, Kilominx.KUBIE_DFR, Kilominx.KUBIE_DBR}; // down-back-right face
    static final byte[] SET_11 = {Kilominx.KUBIE_MBM, Kilominx.KUBIE_BRD, Kilominx.KUBIE_BLD, Kilominx.KUBIE_DBL, Kilominx.KUBIE_DBR}; // down-back face
    static final byte[] SET_12 = {Kilominx.KUBIE_DFM, Kilominx.KUBIE_DFL, Kilominx.KUBIE_DBL, Kilominx.KUBIE_DFR, Kilominx.KUBIE_DBR}; // down face

    // The set of kubies to look at (see above)
    byte[] set;

    /**
     * Constructor for a face kubies pattern database.
     * Sets the database size to 339072480, n to 19, and k to 5.
     * @param setNo The set number of the kubies to look at (1-12)
     * @throws IllegalArgumentException If the set number is not between 1 and 12
     */
    public FaceKubiesPatternDatabase(int setNo) throws IllegalArgumentException {
        super(DATABASE_SIZE, N, K);

        switch (setNo) {
            case 1:
                set = SET_1; break;
            case 2:
                set = SET_2; break;
            case 3:
                set = SET_3; break;
            case 4:
                set = SET_4; break;
            case 5:
                set = SET_5; break;
            case 6:
                set = SET_6; break;
            case 7:
                set = SET_7; break;
            case 8:
                set = SET_8; break;
            case 9:
                set = SET_9; break;
            case 10:
                set = SET_10; break;
            case 11:
                set = SET_11; break;
            case 12:
                set = SET_12; break;
            default:
                throw new IllegalArgumentException("The set number must be between 1 and 12.");
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

        byte[] kubieIndices = new byte[5];
        byte[] kubieOrientations = new byte[5];

        // Iterate over kubies until the specific kubies on the face are found
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
            else if (kubieIndex == set[4]) {
                kubieIndices[4] = (byte)(i - 1); // UFL is fixed at position 0, so shift all indices down by 1
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
