package rubikscube;

/**
 * The pattern database for the last 6 edge cubies.
 * @see PatternDatabase
 */
public class SmallSecondEdgePatternDatabase extends PatternDatabase {

    // For this database, we are looking at the last 6 edges out of 12, so n=12 and k=6
    // There are 12(P)6 * 2^6 (6 of 12 edges; each of 6 edges can be in one of 2 states) = 42577920 possible states
    // Also note that roughly 41MB storage needed (42577920 bytes / 1024^2 = ~41MB)
    final static int DATABASE_SIZE = 42577920;
    final static byte N = 12, K = 6;

    /**
     * Constructor for the second edge pattern database.
     * Sets the database size to 42577920, n to 12, and k to 6.
     */
    public SmallSecondEdgePatternDatabase() {
        super(DATABASE_SIZE, N, K);
    }

    protected int getDatabaseIndex(Cube cube) {
        // Get the edge indices and orientations from the cube
        byte[] allEdgeIndices = cube.getEdgeIndices();
        byte[] allEdgeOrientations = cube.getEdgeOrientations();

        // Get the last 6 edge indices and orientations from the cube
        byte[] edgeIndices = new byte[6];
        byte[] edgeOrientations = new byte[6];

        // Iterate over edges until the last 6 are found
        int counter = 0;
        for (byte i = 0; i < 12; i++) {
            int edgeIndex = allEdgeIndices[i];

            if (edgeIndex >= 6) {
                edgeIndices[edgeIndex - 6] = i;
                edgeOrientations[edgeIndex - 6] = allEdgeOrientations[i];
                counter++;
            }
            if (counter == 6) {
                break;
            }
        }

        // Calculate the rank of the edge indices
        int indexRank = calculateLehmerRank(edgeIndices);

        // The orientation rank is calculated by using the orientations as base-2, and converting to base-10
        int orientationRank =
            edgeOrientations[0] * 32 +   // 2^5
            edgeOrientations[1] * 16 +   // 2^4
            edgeOrientations[2] * 8 +    // 2^3
            edgeOrientations[3] * 4 +    // 2^2
            edgeOrientations[4] * 2 +    // 2^1
            edgeOrientations[5];         // 2^0
        
        // (64 = 2^6)
        return indexRank * 64 + orientationRank;
    }
}
