package solvers;

import models.Kilominx;
import models.ITwistyPuzzle;
import patterndatabases.kilominx.FaceKubiesPatternDatabase;
import patterndatabases.kilominx.SparseKubiesPatternDatabase;

/**
 * An optimal solver for a Kilominx.
 */
public class KilominxSolver extends PuzzleSolver {
    
    FaceKubiesPatternDatabase[] facePDBs;
    SparseKubiesPatternDatabase[] sparsePDBs;

    /**
     * Constructor for a KilominxSolver object. Initialises the pattern databases for the kilominx.
     * @param kilominx - The kilominx to solve.
     * @throws IllegalStateException if any of the pattern databases fail to load.
     */
    public KilominxSolver(Kilominx kilominx) throws IllegalStateException {
        super(kilominx);

        System.out.println("Loading pattern databases...");
        boolean readSuccess = false;

        facePDBs = new FaceKubiesPatternDatabase[12];
        for (int i = 0; i < 12; i++) {
            facePDBs[i] = new FaceKubiesPatternDatabase(i + 1);
            readSuccess = facePDBs[i].readDatabaseFromFile("kilominx/face_kubies_" + (i + 1) + ".pdb");
        }

        // TODO: Currently ignoring sparse PDB 4 as it is wrong
        sparsePDBs = new SparseKubiesPatternDatabase[4];
        for (int i = 0; i < 3; i++) {
            sparsePDBs[i] = new SparseKubiesPatternDatabase(i + 1);
            readSuccess = sparsePDBs[i].readDatabaseFromFile("kilominx/sparse_kubies_" + (i + 1) + ".pdb");
        }
        sparsePDBs[3] = new SparseKubiesPatternDatabase(5);
        readSuccess = sparsePDBs[3].readDatabaseFromFile("kilominx/sparse_kubies_" + (5) + ".pdb");

        if (readSuccess) {
            System.out.println("Pattern databases loaded successfully.");
        }
        else {
            throw new IllegalStateException("Failed to load pattern databases.");   
        }
    }


    /**
     * Get the maximum number of moves required to solve a subset of cubies across all databases.
     * @param puzzle - The kilominx to get the maximum number of moves for.
     * @return The maximum number of moves required to solve one of the subsets of cubies.
     * @throws IllegalArgumentException if the puzzle is not a Kilominx.
     */
    protected byte getMaxNumberOfMoves(ITwistyPuzzle puzzle) throws IllegalArgumentException {
        if (!(puzzle instanceof Kilominx)) {
            throw new IllegalArgumentException("The puzzle must be a Kilominx.");
        }
        Kilominx kilominx = (Kilominx) puzzle;
        
        byte maxMoves = 0;

        for (FaceKubiesPatternDatabase facePDB : facePDBs) {
            byte estimatedMoves = facePDB.getNumberOfMoves(kilominx);
            if (estimatedMoves > maxMoves) {
                maxMoves = estimatedMoves;
            }
        }
        for (SparseKubiesPatternDatabase sparsePDB : sparsePDBs) {
            byte estimatedMoves = sparsePDB.getNumberOfMoves(kilominx);
            if (estimatedMoves > maxMoves) {
                maxMoves = estimatedMoves;
            }
        }

        return maxMoves;
    }

    /**
     * Get the maximum number of moves required to solve a subset of cubies across all databases.
     * This method is faster than {@link #getMaxNumberOfMoves(Kilominx kilominx)} because 
     * it returns as soon as a database estimate exceeds the bound hint.
     * @param puzzle - The kilominx to get the maximum number of moves for.
     * @param boundHint - The maximum number of moves allowed to solve the kilominx state.
     * @param depthHint - The depth of the current node in the search tree.
     * @return The maximum number of moves required to solve one of the subsets of cubies.
     * @throws IllegalArgumentException if the puzzle is not a Kilominx.
     */
    protected byte getMaxNumberOfMoves(ITwistyPuzzle puzzle, byte boundHint, byte depthHint) throws IllegalArgumentException {
        if (!(puzzle instanceof Kilominx)) {
            throw new IllegalArgumentException("The puzzle must be a Kilominx.");
        }
        Kilominx kilominx = (Kilominx) puzzle;

        byte estimatedMoves, max = 0;

        FaceKubiesPatternDatabase facePDB;
        for (int i = 0; i < facePDBs.length; i++) {
            facePDB = facePDBs[i];

            // Check estimated number of moves from a face PDB
            estimatedMoves = facePDB.getNumberOfMoves(kilominx);
            // System.out.print("\t Face PDB " + i + " has estimate " + estimatedMoves);

            // If estimate exceeds the bound, return
            if (estimatedMoves + depthHint > boundHint) {
                // System.out.println(" (exceeded bound)");
                return estimatedMoves;
            }
            // If estimate is greater than the current max, update max
            if (estimatedMoves > max) {
                // System.out.println(" (new max)");
                max = estimatedMoves;
            }
            // else {
            //     System.out.println();
            // }
        }

        SparseKubiesPatternDatabase sparsePDB;
        for (int i = 0; i < sparsePDBs.length; i++) {
            sparsePDB = sparsePDBs[i];

            // Check estimated number of moves from a sparse PDB
            estimatedMoves = sparsePDB.getNumberOfMoves(kilominx);
            // System.out.print("\t Sparse PDB " + i + " has estimate " + estimatedMoves);

            // If estimate exceeds the bound, return
            if (estimatedMoves + depthHint > boundHint) {
                // System.out.println(" (exceeded bound)");
                return estimatedMoves;
            }
            // If estimate is greater than the current max, update max
            if (estimatedMoves > max) {
                // System.out.println(" (new max)");
                max = estimatedMoves;
            }
            // else {
            //     System.out.println();
            // }
        }

        // System.out.println("\t No estimate exceeded the bound, returning max estimate of " + max);
        // No estimate exceeded the bound, return the maximum estimate
        return max;
    }
}
