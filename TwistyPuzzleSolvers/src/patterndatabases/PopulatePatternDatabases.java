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
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class contains methods to populate the pattern databases for the Rubik's Cube and Kilominx.
 * Usage: java PopulatePatternDatabases [cube-corners | cube-first-edges | cube-second-edges | kilominx-face-# (with # = 1-12) | kilominx-sparse]
 */
public class PopulatePatternDatabases {
    
    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("Usage: java PopulatePatternDatabases [cube-corners | cube-first-edges | cube-second-edges | kilominx-face-# (with # = 1-12) | kilominx-sparse]");
            System.exit(1);
        }

        Pattern facePdbPattern = Pattern.compile("^kilominx-face-([1-9]|1[0-2])$");
        Matcher facePdbMatcher = facePdbPattern.matcher(args[0]);

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
        else if (args[0].equals("kilominx-sparse")) {
            populateSparseKubiesDatabases();
        }
        else {
            System.err.println("Usage: java PopulatePatternDatabases [cube-corners | cube-first-edges | cube-second-edges | kilominx-face-# (with # = 1-12) | kilominx-sparse]");
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

        breadthFirstSearch(cube, cornerPDB);
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
     * Populate the sparse kubies pattern dataabases for the Kilominx.
     */
    private static void populateSparseKubiesDatabases() {
        Kilominx kilominx = new Kilominx();

        SparseKubiesPatternDatabase firstSparseKubiesPDB = new SparseKubiesPatternDatabase(1);
        System.out.println("Populating first sparse kubies database...");
        iterativeDeepeningDepthFirstSearch(kilominx, firstSparseKubiesPDB);
        firstSparseKubiesPDB.writeDatabaseToFile("kilominx/", "sparse_kubies_1.pdb");
        System.out.println("First sparse kubies database populated.\n");

        kilominx = new Kilominx();

        SparseKubiesPatternDatabase secondSparseKubiesPDB = new SparseKubiesPatternDatabase(2);
        System.out.println("Populating second sparse kubies database...");
        iterativeDeepeningDepthFirstSearch(kilominx, secondSparseKubiesPDB);
        secondSparseKubiesPDB.writeDatabaseToFile("kilominx/", "sparse_kubies_2.pdb");
        System.out.println("Second sparse kubies database populated.\n");

        kilominx = new Kilominx();

        SparseKubiesPatternDatabase thirdSparseKubiesPDB = new SparseKubiesPatternDatabase(3);
        System.out.println("Populating third sparse kubies database...");
        iterativeDeepeningDepthFirstSearch(kilominx, thirdSparseKubiesPDB);
        thirdSparseKubiesPDB.writeDatabaseToFile("kilominx/", "sparse_kubies_3.pdb");
        System.out.println("Third sparse kubies database populated.\n");

        kilominx = new Kilominx();

        SparseKubiesPatternDatabase fourthSparseKubiesPDB = new SparseKubiesPatternDatabase(4);
        System.out.println("Populating fourth sparse kubies database...");
        iterativeDeepeningDepthFirstSearch(kilominx, fourthSparseKubiesPDB);
        fourthSparseKubiesPDB.writeDatabaseToFile("kilominx/", "sparse_kubies_4.pdb");
        System.out.println("Fourth sparse kubies database populated.\n");

        kilominx = new Kilominx();

        SparseKubiesPatternDatabase fifthSparseKubiesPDB = new SparseKubiesPatternDatabase(5);
        System.out.println("Populating fifth sparse kubies database...");
        iterativeDeepeningDepthFirstSearch(kilominx, fifthSparseKubiesPDB);
        fifthSparseKubiesPDB.writeDatabaseToFile("kilominx/", "sparse_kubies_5.pdb");
        System.out.println("Fifth sparse kubies database populated.\n");
    }


    /**
     * A node in the breadth-first search tree.
     * Contains the move used to get to the node and the parent node.
     */
    private static class BFSNode {
        IMove move;
        BFSNode parent;

        /**
         * Constructor for a BFSNode.
         * @param move - The move used to get to the node.
         * @param parent - The parent node.
         */
        public BFSNode(IMove move, BFSNode parent) {
            this.move = move;
            this.parent = parent;
        }
    }

    /**
     * Perform a breadth-first search to populate a pattern database.
     * This method should only be used for the Rubik's Cube corner pattern database, as it is not memory-efficient enough for the other pattern databases.
     * @param puzzle - The initial (solved) puzzle state to search from.
     * @param database - The pattern database to populate.
     */
    private static void breadthFirstSearch(ITwistyPuzzle puzzle, PatternDatabase database) {
        // Queue of nodes to be visited
        ArrayDeque<BFSNode> nodeQueue = new ArrayDeque<BFSNode>();
        // List of moves to get to the current node
        ArrayList<IMove> movesList = new ArrayList<IMove>();

        // Additional variables for search stats
        int totalVisitedNodes = 0;
        int maxQueueSize = 0;
        int maxDepth = 0;
        long startTime = System.currentTimeMillis();

        // Create the root node and add it to the queue
        BFSNode root = new BFSNode(null, null);
        nodeQueue.push(root);

        // Set the number of moves in the database to solve the initial state to 0
        database.setNumberOfMoves(puzzle, (byte) 0);

        // Iterate over all nodes in the queue
        while (!nodeQueue.isEmpty()) {
            // Get next node in the queue
            BFSNode currentNode = nodeQueue.removeFirst();   

            // Clear the moves list
            movesList.clear();

            // Move to the node (updates movesList and puzzle)
            moveToNode(currentNode, movesList, puzzle);
            totalVisitedNodes++;
            System.out.print("nodes visited: " + totalVisitedNodes + "\r");

            // Update the max depth
            if (movesList.size() > maxDepth) {
                System.out.println("BFS: finished depth " + maxDepth + " after " + ((System.currentTimeMillis() - startTime) / 1000.0) + "s.");
                maxDepth++;
            }

            // Iterate over all possible moves from the current node
            for (IMove move : puzzle.getMoveController().getMoves()) {
                // If at the root node or the move shouldn't be skipped
                if (movesList.size() == 0 || !puzzle.getMoveController().skipMove(move, movesList.getLast())) {
                    
                    // Make the move
                    puzzle.getMoveController().makeMove(move);
                
                    // Try to update the number of moves in the database
                    // If successful, add the node to the queue; if not, the node was visited before with fewer moves
                    if (database.setNumberOfMoves(puzzle, (byte) (movesList.size() + 1))) {
                        nodeQueue.addLast(new BFSNode(move, currentNode));

                        // If the database is full, search is complete
                        if (database.isFull()) {
                            System.out.println("Breadth-first search complete. Visited " + totalVisitedNodes + " nodes. " +
                                               "Max queue size: " + maxQueueSize + ". " +
                                               "Elapsed time: " + ((System.currentTimeMillis() - startTime) / 1000.0) + "s");
                            return;
                        }
                    }
                    // Undo the move
                    puzzle.getMoveController().undoMove(move);
                }
            }

            // Update the max queue size
            if (nodeQueue.size() > maxQueueSize) {
                maxQueueSize = nodeQueue.size();
            }

            // Undo all of the moves to get back to the initial puzzle state
            undoAllMoves(currentNode, puzzle);
        }

        System.err.println("Error: goal not found in search, but the queue is empty.");
        return;
    }

    /**
     * Apply the moves to get to the target node by traversing from the root node to the target
     * @param target - The target node to move to.
     * @param movesList - The list to store the moves in.
     * @param puzzle - The puzzle to apply the moves to.
     */
    private static void moveToNode(BFSNode target, ArrayList<IMove> movesList, ITwistyPuzzle puzzle) {
        // Use a deque as a stack to store the moves
        ArrayDeque<IMove> movesStack = new ArrayDeque<IMove>();
        BFSNode currentNode = target;

        // First go from the target node to the root, adding the moves to the stack
        while (currentNode.parent != null) {
            movesStack.addFirst(currentNode.move);
            currentNode = currentNode.parent;
        }

        // Then pop the moves from the stack to the moves list
        while (!movesStack.isEmpty()) {
            IMove move = movesStack.removeFirst();
            movesList.add(move);
            puzzle.getMoveController().makeMove(move);
        }
    }

    /**
     * Undo all of the moves starting at the target node, going back to the root node
     * @param target - The target node to undo moves from.
     * @param puzzle - The puzzle to undo the moves on.
     */
    private static void undoAllMoves(BFSNode target, ITwistyPuzzle puzzle) {
        BFSNode currentNode = target;

        while (currentNode.parent != null) {
            puzzle.getMoveController().undoMove(currentNode.move);
            currentNode = currentNode.parent;
        }
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
