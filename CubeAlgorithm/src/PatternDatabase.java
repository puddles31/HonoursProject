import java.util.BitSet;

public abstract class PatternDatabase {
    
    public static void main(String[] args) {
        Cube cube = new Cube();
        FirstEdgePatternDatabase edgePDB = new FirstEdgePatternDatabase((byte) 12, (byte) 7);

        cube.moveF();

        int ind = edgePDB.getDatabaseIndex(cube);
        System.out.println("index: " + ind);
    }


    private byte n, k;
    private int[] binaryOnesTable, factorialsTable;


    protected PatternDatabase(byte n, byte k) {
        // n is the number of elements in the permutation; k is the number of elements picked in this partial permutation
        this.n = n;
        this.k = k;

        // Precomputed table for number of ones in binary representation of a number
        // (1 << n) - 1 is the maximum number that can be represented by n bits
        binaryOnesTable = new int[(1 << n) - 1];

        for (int i = 0; i < (1 << n) - 1; i++) {
            BitSet bits = BitSet.valueOf(new long[] {i});
            binaryOnesTable[i] = bits.cardinality();
        }


        // Precomputed table for factorials
        factorialsTable = new int[k];

        for (int i = 0; i < k; i++) {
            factorialsTable[i] = factorial(n - 1 - i) / factorial((n - 1 - i) - (k - 1 - i));
        }
    }

    private int factorial(int n) {
        if (n <= 1) {
            return 1;
        }
        else {
            return n * factorial(n - 1);
        }
    }


    // Calculate the database index for a partial permutation of cubie indices/orientations
    public abstract int getDatabaseIndex(Cube cube);



    // Calculate the Lehmer rank of a partial permutation
    protected int calculateLehmerRank(byte[] perm) {
        int[] lehmerCode = new int[k];
        BitSet seen = new BitSet(n);

        // The first digit of the Lehmer code is the first digit of the permutation
        lehmerCode[0] = perm[0];
        // Set the bit corresponding to the first digit as seen
        seen.set(n - 1 - perm[0], true);


        for (int i = 1; i < k; i++) {
            // Set the bit corresponding to the digit as seen
            seen.set(n - 1 - perm[i], true);
            
            // Count the number of digits that have been seen
            // (count the number of ones to the left of the current digit when in binary)
            int onesCount = binaryOnesTable[(int) (seen.toLongArray()[0] >> (n - perm[i]))];

            // The Lehmer code is the digit minus the number of digits that have been seen
            lehmerCode[i] = perm[i] - onesCount;
        }

        int lehmerRank = 0;

        // Calculate the rank of the Lehmer code by coverting to base-10
        for (int i = 0; i < k; i++) {
            lehmerRank += lehmerCode[i] * factorialsTable[i];
        }

        return lehmerRank;
    }


}
