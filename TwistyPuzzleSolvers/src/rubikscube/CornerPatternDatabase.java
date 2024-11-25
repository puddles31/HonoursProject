package rubikscube;

/**
 * The pattern database for the corner cubies.
 * @see PatternDatabase
 */
public class CornerPatternDatabase extends PatternDatabase {

    // For this database, we are looking at all 8 corners, so n=8 and k=8
    // There are 8! * 3^7 (8 corners; 7 of which can be in one of 3 states (the 8th is fixed)) = 88179840 possible states
    // Also note that roughly 84MB storage needed (88179840 bytes / 1024^2 = ~84MB)
    final static int DATABASE_SIZE = 88179840;
    final static byte N = 8, K = 8;

    /**
     * Constructor for the corner pattern database.
     * Sets the database size to 88179840, n to 8, and k to 8.
     */
    public CornerPatternDatabase() {
        super(DATABASE_SIZE, N, K);
    }

    protected int getDatabaseIndex(Cube cube) {
        // Get the corner indices and orientations from the cube
        byte[] cornerIndices = cube.getCornerIndices();
        byte[] cornerOrientations = cube.getCornerOrientations();

        // Calculate the rank of the corner indices
        int indexRank = calculateLehmerRank(cornerIndices);

        // The orientation rank is calculated by using the orientations as base-3, and converting to base-10
        int orientationRank =
            cornerOrientations[0] * 729 +   // 3^6
            cornerOrientations[1] * 243 +   // 3^5
            cornerOrientations[2] * 81 +    // 3^4
            cornerOrientations[3] * 27 +    // 3^3
            cornerOrientations[4] * 9 +     // 3^2
            cornerOrientations[5] * 3 +     // 3^1 
            cornerOrientations[6];          // 3^0
            // (the orientation of the last corner is fixed by the other corners)
        
        // (2187 = 3^7)
        return indexRank * 2187 + orientationRank;
    }
}
