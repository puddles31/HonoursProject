public class SecondEdgePatternDatabase extends PatternDatabase {
    
    public SecondEdgePatternDatabase(byte n, byte k) {
        super(n, k);
    }

    public int getDatabaseIndex(Cube cube) {
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
