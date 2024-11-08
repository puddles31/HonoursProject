package rubikscube;
import java.util.Arrays;
import java.util.BitSet;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;

/**
 * Abstract class for a pattern database.
 * A pattern database is a hash table which stores the number of moves required to solve a subset of cubies.
 * The hash function used is {@link #getDatabaseIndex}, which maps subsets of cubies to unique integers.
 * @see CornerPatternDatabase
 * @see FirstEdgePatternDatabase
 * @see SecondEdgePatternDatabase
 */
public abstract class PatternDatabase {

    private byte[] database;
    private int entriesSet;

    private byte n, k;
    private int[] binaryOnesTable, factorialsTable;


    /**
     * Constructor for a pattern database.
     * Cannot be instantiated directly (as implementation differs between corner/edge databases).
     * @param databaseSize - The size of the database. Should be equal to the number of possible states for the subset of cubies.
     * @param n - The number of elements in the permutation. Should be 8 for corners, 12 for edges.
     * @param k - The number of elements picked in the partial permutation. Should be 8 for corners, 7 for edges.
     * @see CornerPatternDatabase#CornerPatternDatabase()
     * @see FirstEdgePatternDatabase#FirstEdgePatternDatabase()
     * @see SecondEdgePatternDatabase#SecondEdgePatternDatabase()
     */
    protected PatternDatabase(int databaseSize, byte n, byte k) {
        // Initialise the array with max byte values
        database = new byte[databaseSize];
        Arrays.fill(database, Byte.MAX_VALUE);
        entriesSet = 0;

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

    /**
     * Calculate the factorial of a number recursively.
     * @param n - The number to calculate the factorial of.
     * @return The factorial of the given number.
     */
    private static int factorial(int n) {
        if (n <= 1) {
            return 1;
        }
        else {
            return n * factorial(n - 1);
        }
    }


    /**
     * Set the number of moves required to solve a cube state in the database.
     * Only succeeds if the number of moves is less than the current number of moves stored.
     * Also increases a counter for the number of set entries if the number of moves was not already set.
     * @param cube - The cube to set the number of moves for.
     * @param noMoves - The number of moves required to solve the subset of cubies.
     * @return {@code true} if the number of moves was set successfully, {@code false} if the number of moves was already set.
     */
    public boolean setNumberOfMoves(Cube cube, byte noMoves) {
        int index = getDatabaseIndex(cube);

        // If database entry is not set (MAX_VALUE is the initial val), increment the number of set entries
        if (database[index] == Byte.MAX_VALUE) {
            entriesSet++;
        }

        // If the number of moves is less than the current number of moves stored, update the database
        if (noMoves < database[index]) {
            database[index] = noMoves;
            return true;
        }
        else {
            return false;
        }
    }
    
    /**
     * Get the number of moves required to solve a cube state from the database.
     * @param cube - The cube to get the number of moves for.
     * @return The number of moves required to solve the subset of cubies.
     */
    public byte getNumberOfMoves(Cube cube) {
        return database[getDatabaseIndex(cube)];
    }

    /**
     * Get the size of the database.
     * @return The size of the database.
     */
    public int getDatabaseSize() {
        return database.length;
    }

    /**
     * Get the number of entries set in the database.
     * @return The number of entries set in the database.
     */
    public int getEntriesSet() {
        return entriesSet;
    }

    /**
     * Check if the database is full (all entries have been set).
     * @return {@code true} if the database is full, {@code false} if there are still unset entries.
     */
    public boolean isFull() {
        return entriesSet == database.length;
    }


    /**
     * Calculate the database index for a cube (by using the indices and orientations of a subset of cubies).
     * Subsets are indexed based on a lexicographical ordering, calculated using Lehmer codes (see {@link #calculateLehmerRank}).
     * This is a perfect hash function, which maps subsets of cubies to unique integers without collisions.
     * Note that this function has different implementations for corner and edge databases.
     * @param cube - The cube to calculate the database index for.
     * @return The database index for the given cube.
     * @see #calculateLehmerRank
     */
    protected abstract int getDatabaseIndex(Cube cube);

    
    /**
     * Calculate the Lehmer rank of a full/partial permutation of cubie indices.
     * @param perm - The permutation of cubie indices to calculate the Lehmer rank of.
     * @return The base 10 value of the Lehmer code for the given permutation.
     */
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


    /**
     * Write the pattern database to a file.
     * @param path - The name of the file to write the database to (should end in .pdb).
     * @see #readDatabaseFromFile(String)
     */
    public void writeDatabaseToFile(String filename) {
        try {
            final String DATABASES_PATH = "CubeAlgorithm/databases/";

            // Create the file and directories if they don't exist
            new File(DATABASES_PATH).mkdirs();
            File databaseFile = new File(DATABASES_PATH + filename);
            databaseFile.createNewFile();

            // Write the database to the file
            FileOutputStream outStream = new FileOutputStream(databaseFile, false);
            outStream.write(database);
            outStream.close();
        }
        catch (Exception e) {
            System.err.println("Error writing database to file:");
            e.printStackTrace();
        }
    }

    /**
     * Read the pattern database from a file.
     * @param path - The path to read the database from.
     * @return {@code true} if the database was read successfully, {@code false} if an error occurred.
     * @see #writeDatabaseToFile(String)
     */
    public boolean readDatabaseFromFile(String path) {
        File file = new File(path);

        try (FileInputStream in = new FileInputStream(file)) {
            // If the file size (number of bytes) does not match the database size (number of entries, each 1 byte)
            if (file.length() != database.length) {
                System.err.println("Error reading database from file: file size does not match database size");
                return false;
            }

            // Read the file into the database array
            in.read(database);
            entriesSet = database.length;
            return true;
        }
        catch (Exception e) {
            System.err.println("Error reading database from file:");
            e.printStackTrace();
            return false;
        }
    }
}
