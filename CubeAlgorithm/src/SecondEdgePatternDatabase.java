/**
 * The pattern database for the last 7 edge cubies.
 * @see PatternDatabase
 */
public class SecondEdgePatternDatabase extends PatternDatabase {

    // For this database, we are looking at the last 7 edges out of 12, so n=12 and k=7
    // There are 12(P)7 * 2^7 (7 of 12 edges; each of 7 edges can be in one of 2 states) = 510935040 possible states
    // Also note that roughly 487MB storage needed (510935040 bytes / 1024^2 = ~487MB)
    final static int DATABASE_SIZE = 510935040;
    final static byte N = 12, K = 7;

    /**
     * Constructor for the second edge pattern database.
     * Sets the database size to 510935040, n to 12, and k to 7.
     */
    public SecondEdgePatternDatabase() {
        super(DATABASE_SIZE, N, K);
    }

    protected int getDatabaseIndex(Cube cube) {
        // Get the edge indices and orientations from the cube
        byte[] allEdgeIndices = cube.getEdgeIndices();
        byte[] allEdgeOrientations = cube.getEdgeOrientations();

        // Get the last 7 edge indices and orientations from the cube
        byte[] edgeIndices = new byte[7];
        byte[] edgeOrientations = new byte[7];

        // Iterate over edges until the last 7 are found
        int counter = 0;
        for (byte i = 0; i < 12; i++) {
            int edgeIndex = allEdgeIndices[i];

            if (edgeIndex > 4) {
                edgeIndices[edgeIndex - 5] = i;
                edgeOrientations[edgeIndex - 5] = allEdgeOrientations[i];
                counter++;
            }
            if (counter == 7) {
                break;
            }
        }

        // Calculate the rank of the edge indices
        int indexRank = calculateLehmerRank(edgeIndices);

        // The orientation rank is calculated by using the orientations as base-2, and converting to base-10
        int orientationRank =
            edgeOrientations[0] * 64 +   // 2^6
            edgeOrientations[1] * 32 +   // 2^5
            edgeOrientations[2] * 16 +   // 2^4
            edgeOrientations[3] * 8 +    // 2^3
            edgeOrientations[4] * 4 +    // 2^2
            edgeOrientations[5] * 2 +    // 2^1
            edgeOrientations[6];         // 2^0
        
        // (128 = 2^7)
        return indexRank * 128 + orientationRank;
    }
}
