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

        sparsePDBs = new SparseKubiesPatternDatabase[5];
        for (int i = 0; i < 5; i++) {
            sparsePDBs[i] = new SparseKubiesPatternDatabase(i + 1);
            readSuccess = sparsePDBs[i].readDatabaseFromFile("kilominx/sparse_kubies_" + (i + 1) + ".pdb");
        }

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
            if (estimatedMoves < maxMoves) {
                maxMoves = estimatedMoves;
            }
        }
        for (SparseKubiesPatternDatabase sparsePDB : sparsePDBs) {
            byte estimatedMoves = sparsePDB.getNumberOfMoves(kilominx);
            if (estimatedMoves < maxMoves) {
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

        for (FaceKubiesPatternDatabase facePDB : facePDBs) {
            // Check estimated number of moves from a face PDB
            estimatedMoves = facePDB.getNumberOfMoves(kilominx);

            // If estimate exceeds the bound, return
            if (estimatedMoves + depthHint > boundHint) {
                return estimatedMoves;
            }
            // If estimate is greater than the current max, update max
            if (estimatedMoves > max) {
                max = estimatedMoves;
            }
        }
        for (SparseKubiesPatternDatabase sparsePDB : sparsePDBs) {
            // Check estimated number of moves from a sparse PDB
            estimatedMoves = sparsePDB.getNumberOfMoves(kilominx);

            // If estimate exceeds the bound, return
            if (estimatedMoves + depthHint > boundHint) {
                return estimatedMoves;
            }
            // If estimate is greater than the current max, update max
            if (estimatedMoves > max) {
                max = estimatedMoves;
            }
        }

        // No estimate exceeded the bound, return the maximum estimate
        return max;
    }
}
