public class FirstEdgePatternDatabase extends PatternDatabase {
    
    public FirstEdgePatternDatabase(byte n, byte k) {
        super(n, k);
    }

    public int getDatabaseIndex(Cube cube) {
        // Get the edge indices and orientations from the cube
        byte[] allEdgeIndices = cube.getEdgeIndices();
        byte[] allEdgeOrientations = cube.getEdgeOrientations();

        System.out.println("allEdgeIndices: " + java.util.Arrays.toString(allEdgeIndices));
        System.out.println("allEdgeOrientations: " + java.util.Arrays.toString(allEdgeOrientations));

        // Get the first 7 edge indices and orientations from the cube
        byte[] edgeIndices = new byte[7];
        byte[] edgeOrientations = new byte[7];

        // Iterate over edges until the first 7 are found
        int counter = 0;
        for (byte i = 0; i < 12; i++) {
            int edgeIndex = allEdgeIndices[i];

            if (edgeIndex < 7) {
                edgeIndices[edgeIndex] = i;
                edgeOrientations[edgeIndex] = allEdgeOrientations[i];
                counter++;
            }
            if (counter == 7) {
                break;
            }
        }

        System.out.println("edgeIndices: " + java.util.Arrays.toString(edgeIndices));
        System.out.println("edgeOrientations: " + java.util.Arrays.toString(edgeOrientations));

        // Calculate the rank of the edge indices
        int indexRank = calculateLehmerRank(edgeIndices);

        System.out.println("indexRank: " + indexRank);

        // The orientation rank is calculated by using the orientations as base-2, and converting to base-10
        int orientationRank =
            edgeOrientations[0] * 64 +   // 2^6
            edgeOrientations[1] * 32 +   // 2^5
            edgeOrientations[2] * 16 +   // 2^4
            edgeOrientations[3] * 8 +    // 2^3
            edgeOrientations[4] * 4 +    // 2^2
            edgeOrientations[5] * 2 +    // 2^1
            edgeOrientations[6];         // 2^0

        System.out.println("orientationRank: " + orientationRank);
        
        // (128 = 2^7)
        return indexRank * 128 + orientationRank;
    }


}
