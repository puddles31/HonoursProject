package solvers;

import models.Kilominx;
import models.ITwistyPuzzle;
import patterndatabases.kilominx.FaceKubiesPatternDatabase;

/**
 * An optimal solver for a Kilominx.
 */
public class KilominxSolver extends PuzzleSolver {
    
    FaceKubiesPatternDatabase topFacePDB;

    /**
     * Constructor for a KilominxSolver object. Initialises the pattern databases for the kilominx.
     * @param kilominx - The kilominx to solve.
     * @throws IllegalStateException if any of the pattern databases fail to load.
     */
    public KilominxSolver(Kilominx kilominx) throws IllegalStateException {
        super(kilominx);

        topFacePDB = new FaceKubiesPatternDatabase(1);

        System.out.println("Loading pattern databases...");
        boolean readSuccess;

        readSuccess = topFacePDB.readDatabaseFromFile("kilominx/top_face.pdb");

        if (readSuccess) {
            System.out.println("Pattern databases loaded successfully.");
        }
        else {
            throw new IllegalStateException("Failed to load pattern databases.");   
        }
    }


    /**
     * Get the maximum number of moves required to solve a kilominx state across all databases.
     * @param puzzle - The kilominx to get the maximum number of moves for.
     * @return The maximum number of moves required to solve one of the subsets of cubies.
     * @throws IllegalArgumentException if the puzzle is not a Kilominx.
     */
    protected byte getMaxNumberOfMoves(ITwistyPuzzle puzzle) throws IllegalArgumentException {
        if (!(puzzle instanceof Kilominx)) {
            throw new IllegalArgumentException("The puzzle must be a Kilominx.");
        }
        Kilominx kilominx = (Kilominx) puzzle;
        
        return (byte) Math.max(topFacePDB.getNumberOfMoves(kilominx), 0);   // after adding more databases, math.max with each of them (remove 0)
    }

    /**
     * Get the maximum number of moves required to solve a kilominx state across all databases.
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
        
        byte max, estimatedMoves;

        // Check estimated number of moves from top face PDB
        estimatedMoves = topFacePDB.getNumberOfMoves(kilominx);
        max = estimatedMoves;

        // If estimate exceeds the bound, return
        if (estimatedMoves + depthHint > boundHint) {
            return estimatedMoves;
        }

        // Repeat for future pdbs

        // // Check estimated number of moves from first edge PDB
        // estimatedMoves = firstEdgePDB.getNumberOfMoves(kilominx);

        // // If estimate exceeds the bound, return
        // if (estimatedMoves + depthHint > boundHint) {
        //     return estimatedMoves;
        // }
        // // If estimate is greater than the current max, update max
        // if (estimatedMoves > max) {
        //     max = estimatedMoves;
        // }

        // // Check estimated number of moves from second edge PDB
        // estimatedMoves = secondEdgePDB.getNumberOfMoves(kilominx);

        // // If estimate exceeds the bound, return
        // if (estimatedMoves + depthHint > boundHint) {
        //     return estimatedMoves;
        // }
        // // If estimate is greater than the current max, update max
        // if (estimatedMoves > max) {
        //     max = estimatedMoves;
        // }

        // No estimate exceeded the bound, return the maximum estimate
        return max;
    }
}
