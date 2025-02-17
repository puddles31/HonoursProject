package solvers;

import models.Cube;
import models.ITwistyPuzzle;
import patterndatabases.cube.CornerPatternDatabase;
import patterndatabases.cube.FirstEdgePatternDatabase;
import patterndatabases.cube.SecondEdgePatternDatabase;

/**
 * An optimal solver for a Rubik's Cube.
 */
public class CubeSolver extends PuzzleSolver {
    
    CornerPatternDatabase cornerPDB;
    FirstEdgePatternDatabase firstEdgePDB;
    SecondEdgePatternDatabase secondEdgePDB;

    /**
     * Constructor for a CubeSolver object. Initialises the pattern databases for the cube.
     * @param cube - The cube to solve.
     * @throws IllegalStateException if any of the pattern databases fail to load.
     */
    public CubeSolver(Cube cube) throws IllegalStateException {
        super(cube);

        cornerPDB = new CornerPatternDatabase();
        firstEdgePDB = new FirstEdgePatternDatabase();
        secondEdgePDB = new SecondEdgePatternDatabase();

        System.out.println("Loading pattern databases...");
        boolean readSuccess;

        readSuccess = cornerPDB.readDatabaseFromFile("cube/corners.pdb");
        readSuccess = firstEdgePDB.readDatabaseFromFile("cube/first_edges.pdb");
        readSuccess = secondEdgePDB.readDatabaseFromFile("cube/second_edges.pdb");

        if (readSuccess) {
            System.out.println("Pattern databases loaded successfully.");
        }
        else {
            throw new IllegalStateException("Failed to load pattern databases.");   
        }
    }


    /**
     * Get the maximum number of moves required to solve a cube state across all databases.
     * @param puzzle - The cube to get the maximum number of moves for.
     * @return The maximum number of moves required to solve one of the subsets of cubies.
     * @throws IllegalArgumentException if the puzzle is not a Cube.
     */
    protected byte getMaxNumberOfMoves(ITwistyPuzzle puzzle) throws IllegalArgumentException {
        if (!(puzzle instanceof Cube)) {
            throw new IllegalArgumentException("The puzzle must be a Cube.");
        }
        Cube cube = (Cube) puzzle;
        
        return (byte) Math.max(cornerPDB.getNumberOfMoves(cube), Math.max(firstEdgePDB.getNumberOfMoves(cube), secondEdgePDB.getNumberOfMoves(cube)));
    }

    /**
     * Get the maximum number of moves required to solve a cube state across all databases.
     * This method is faster than {@link #getMaxNumberOfMoves(Cube cube)} because 
     * it returns as soon as a database estimate exceeds the bound hint.
     * @param puzzle - The cube to get the maximum number of moves for.
     * @param boundHint - The maximum number of moves allowed to solve the cube state.
     * @param depthHint - The depth of the current node in the search tree.
     * @return The maximum number of moves required to solve one of the subsets of cubies.
     * @throws IllegalArgumentException if the puzzle is not a Cube.
     */
    protected byte getMaxNumberOfMoves(ITwistyPuzzle puzzle, byte boundHint, byte depthHint) throws IllegalArgumentException {
        if (!(puzzle instanceof Cube)) {
            throw new IllegalArgumentException("The puzzle must be a Cube.");
        }
        Cube cube = (Cube) puzzle;
        
        byte max, estimatedMoves;

        // Check estimated number of moves from corner PDB
        estimatedMoves = cornerPDB.getNumberOfMoves(cube);
        max = estimatedMoves;

        // If estimate exceeds the bound, return
        if (estimatedMoves + depthHint > boundHint) {
            return estimatedMoves;
        }

        // Check estimated number of moves from first edge PDB
        estimatedMoves = firstEdgePDB.getNumberOfMoves(cube);

        // If estimate exceeds the bound, return
        if (estimatedMoves + depthHint > boundHint) {
            return estimatedMoves;
        }
        // If estimate is greater than the current max, update max
        if (estimatedMoves > max) {
            max = estimatedMoves;
        }

        // Check estimated number of moves from second edge PDB
        estimatedMoves = secondEdgePDB.getNumberOfMoves(cube);

        // If estimate exceeds the bound, return
        if (estimatedMoves + depthHint > boundHint) {
            return estimatedMoves;
        }
        // If estimate is greater than the current max, update max
        if (estimatedMoves > max) {
            max = estimatedMoves;
        }

        // No estimate exceeded the bound, return the maximum estimate
        return max;
    }
}
