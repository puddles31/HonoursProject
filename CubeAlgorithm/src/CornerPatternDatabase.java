public class CornerPatternDatabase extends PatternDatabase {

    public CornerPatternDatabase(byte n, byte k) {
        super(n, k);
    }

    public int getDatabaseIndex(Cube cube) {
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
