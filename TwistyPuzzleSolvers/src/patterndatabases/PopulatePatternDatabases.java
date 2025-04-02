package patterndatabases;

import models.Cube;
import models.Kilominx;
import models.ITwistyPuzzle;
import models.IMoveController.IMove;
import patterndatabases.cube.CornerPatternDatabase;
import patterndatabases.cube.FirstEdgePatternDatabase;
import patterndatabases.cube.SecondEdgePatternDatabase;
import patterndatabases.kilominx.FaceKubiesPatternDatabase;
import patterndatabases.kilominx.SparseKubiesPatternDatabase;
import java.time.Duration;
import java.util.ArrayDeque;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class contains methods to populate the pattern databases for the Rubik's Cube and Kilominx.
 * Usage: java PopulatePatternDatabases [cube-corners | cube-first-edges | cube-second-edges | kilominx-face-# (with # = 1-12) | kilominx-sparse-# (with # = 1-5)]
 */
public class PopulatePatternDatabases {
    
    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("Usage: java PopulatePatternDatabases [cube-corners | cube-first-edges | cube-second-edges | kilominx-face-# (with # = 1-12) | kilominx-sparse-# (with # = 1-5)]");
            System.exit(1);
        }

        Pattern facePdbPattern = Pattern.compile("^kilominx-face-([1-9]|1[0-2])$");
        Matcher facePdbMatcher = facePdbPattern.matcher(args[0]);

        Pattern sparsePdbPattern = Pattern.compile("^kilominx-sparse-([1-5])$");
        Matcher sparsePdbMatcher = sparsePdbPattern.matcher(args[0]);

        if (args[0].equals("cube-corners")) {
            populateCornerDatabase();
        }
        else if (args[0].equals("cube-first-edges")) {
            populateFirstEdgeDatabase();
        }
        else if (args[0].equals("cube-second-edges")) {
            populateSecondEdgeDatabase();
        }
        else if (facePdbMatcher.matches()) {
            int setNo = Integer.valueOf(facePdbMatcher.group(1));
            populateFaceKubiesDatabase(setNo);
        }
        else if (sparsePdbMatcher.matches()) {
            int setNo = Integer.valueOf(sparsePdbMatcher.group(1));
            populateSparseKubiesDatabase(setNo);
        }
        else {
            System.err.println("Usage: java PopulatePatternDatabases [cube-corners | cube-first-edges | cube-second-edges | kilominx-face-# (with # = 1-12) | kilominx-sparse-# (with # = 1-5)]");
            System.exit(1);
        }
    }


    /**
     * Populate the corner pattern database for the Rubik's Cube.
     */
    private static void populateCornerDatabase() {
        Cube cube = new Cube();
        CornerPatternDatabase cornerPDB = new CornerPatternDatabase();
        System.out.println("Populating corner database...");

        iterativeDeepeningDepthFirstSearch(cube, cornerPDB);
        cornerPDB.writeDatabaseToFile("cube/", "corners.pdb");
        System.out.println("Corner database populated.");
    }

    /**
     * Populate the first edge pattern database for the Rubik's Cube.
     */
    private static void populateFirstEdgeDatabase() {
        Cube cube = new Cube();
        FirstEdgePatternDatabase firstEdgePDB = new FirstEdgePatternDatabase();
        System.out.println("Populating first edge database...");

        iterativeDeepeningDepthFirstSearch(cube, firstEdgePDB);
        firstEdgePDB.writeDatabaseToFile("cube/", "first_edges.pdb");
        System.out.println("First edge database populated.\n");
    }

    /**
     * Populate the second edge pattern database.
     */
    private static void populateSecondEdgeDatabase() {
        Cube cube = new Cube();
        SecondEdgePatternDatabase secondEdgePDB = new SecondEdgePatternDatabase();
        System.out.println("Populating second edge database...");

        iterativeDeepeningDepthFirstSearch(cube, secondEdgePDB);
        secondEdgePDB.writeDatabaseToFile("cube/", "second_edges.pdb");
        System.out.println("Second edge database populated.\n");
    }


    /**
     * Populate a face kubies pattern dataabase for the Kilominx.
     * @param setNo - The set number for the face patttern database to populate (1-12).
     * @throws IllegalArgumentException If the set number is not between 1 and 12
     * @see FaceKubiesPatternDatabase
     */
    private static void populateFaceKubiesDatabase(int setNo) throws IllegalArgumentException {
        if (setNo < 1 || setNo > 12) {
            throw new IllegalArgumentException("The set number must be between 1 and 12.");
        }

        Kilominx kilominx = new Kilominx();
        FaceKubiesPatternDatabase facePDB = new FaceKubiesPatternDatabase(setNo);
        System.out.println("Populating face " + setNo + " database...");

        iterativeDeepeningDepthFirstSearch(kilominx, facePDB);
        facePDB.writeDatabaseToFile("kilominx/", "face_kubies_" + setNo + ".pdb");
        System.out.println("Face " + setNo + " database populated.\n");
    }

    /**
     * Populate a sparse kubies pattern dataabase for the Kilominx.
     * @param setNo - The set number for the sparse patttern database to populate (1-5).
     * @throws IllegalArgumentException If the set number is not between 1 and 5
     * @see SparseKubiesPatternDatabase
     */
    private static void populateSparseKubiesDatabase(int setNo) throws IllegalArgumentException {
        if (setNo < 1 || setNo > 5) {
            throw new IllegalArgumentException("The set number must be between 1 and 5.");
        }

        Kilominx kilominx = new Kilominx();
        SparseKubiesPatternDatabase sparsePDB = new SparseKubiesPatternDatabase(setNo);
        System.out.println("Populating sparse " + setNo + " database...");
    
        iterativeDeepeningDepthFirstSearch(kilominx, sparsePDB);
        sparsePDB.writeDatabaseToFile("kilominx/", "sparse_kubies_" + setNo + ".pdb");
        System.out.println("Sparse " + setNo + " database populated.\n");
    }


    /**
     * A node in the iterative-deepening depth-first search tree.
     * Contains the puzzle state, move used to get to the node, and the depth of the node.
     */
    private static class IDDFSNode {
        ITwistyPuzzle puzzle;
        IMove move;
        byte depth;

        /**
         * Constructor for an IDDFSNode.
         * @param puzzle - The puzzle state of the node.
         * @param move - The move used to get to the node.
         * @param depth - The depth of the node.
         */
        public IDDFSNode(ITwistyPuzzle puzzle, IMove move, byte depth) {
            this.puzzle = puzzle;
            this.move = move;
            this.depth = depth;
        }
    }

    /**
     * Perform an iterative-deepening depth-first search to populate a pattern database.
     * The method should be used for most pattern databases, as it is more memory efficient than breadth-first search.
     * @param puzzle - The initial (solved) puzzle state to search from.
     * @param database - The pattern database to populate.
     */
    private static void iterativeDeepeningDepthFirstSearch(ITwistyPuzzle puzzle, PatternDatabase database) {
        // Use a deque as a stack for nodes
        ArrayDeque<IDDFSNode> nodeStack = new ArrayDeque<IDDFSNode>();
        IDDFSNode currentNode;

        // Additional variables for search stats
        int currentDepth = 0;
        int statesIndexed = 0;
        long startTime = System.currentTimeMillis();
        Duration dur;
        
        // Set the number of moves in the database to solve the initial state to 0
        database.setNumberOfMoves(puzzle, (byte) 0);
        statesIndexed++;

        // Keep searching nodes until database is full
        while (!database.isFull()) {

            // If depth level is complete
            if (nodeStack.isEmpty()) {
                dur = Duration.ofMillis(System.currentTimeMillis() - startTime);
                System.out.println("IDDFS: finished depth " + currentDepth + " after " + 
                                   String.format("%02d:%02d:%02d", dur.toHours(), dur.toMinutesPart(), dur.toSecondsPart()) + ". Indexed " + statesIndexed + " states.");
                currentDepth++;

                // Push the root node onto the stack
                nodeStack.addFirst(new IDDFSNode(puzzle, null, (byte) 0));
            }

            // Pop node off top of stack
            currentNode = nodeStack.removeFirst();

            // Iterate over all possible moves from the current node
            for (IMove move : puzzle.getMoveController().getMoves()) {
                // If at the root node or the move shouldn't be skipped
                if (currentNode.depth == 0 || !puzzle.getMoveController().skipMove(move, currentNode.move)) {

                    // Create a copy of the current puzzle state
                    ITwistyPuzzle puzzleCopy = currentNode.puzzle.copy();
                    byte puzzleCopyDepth = (byte) (currentNode.depth + 1);

                    // Make the move on the copy
                    puzzleCopy.getMoveController().makeMove(move);

                    int databaseIndex = database.getDatabaseIndex(puzzleCopy);
                    
                    // If the puzzle state has been encountered at an earlier depth, skip it
                    if (database.getNumberOfMoves(databaseIndex) < puzzleCopyDepth) {
                        continue;
                    }

                    // Index states at the depth limit; otherwise, add them to the stack
                    if (puzzleCopyDepth == currentDepth) {
                        if (database.setNumberOfMoves(puzzleCopy, puzzleCopyDepth)) {
                            statesIndexed++;
                            dur = Duration.ofMillis(System.currentTimeMillis() - startTime);
                            System.out.print("states indexed: " + statesIndexed + 
                                             ", time elapsed: " + String.format("%02d:%02d:%02d", dur.toHours(), dur.toMinutesPart(), dur.toSecondsPart()) + "\r");
                        }
                    }
                    else {
                        nodeStack.addFirst(new IDDFSNode(puzzleCopy, move, puzzleCopyDepth));
                    }
                }
            }
        }
        dur = Duration.ofMillis(System.currentTimeMillis() - startTime);
        System.out.println("Iterative-deepening depth-first search complete. " +
                           "Indexed " + statesIndexed + " states. " +
                           "Elapsed time: " + String.format("%02d:%02d:%02d", dur.toHours(), dur.toMinutesPart(), dur.toSecondsPart()));
    }
    
}
