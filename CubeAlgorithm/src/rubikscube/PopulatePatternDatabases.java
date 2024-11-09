package rubikscube;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;

import rubikscube.Cube.Move;

public class PopulatePatternDatabases {
    
    public static void main(String[] args) {
        // populateCornerDatabase();
        populateFirstEdgeDatabase();
        populateSecondEdgeDatabase();
        // populateSmallFirstEdgeDatabase();
        // populateSmallSecondEdgeDatabase();
    }


    private static void populateCornerDatabase() {
        Cube cube = new Cube();
        CornerPatternDatabase cornerPDB = new CornerPatternDatabase();

        System.out.println("Populating corner database...");
        breadthFirstSearch(cube, cornerPDB);
        cornerPDB.writeDatabaseToFile("corners.pdb");
        System.out.println("Corner database populated.\n");
    }

    private static void populateFirstEdgeDatabase() {
        Cube cube = new Cube();
        FirstEdgePatternDatabase firstEdgePDB = new FirstEdgePatternDatabase();

        System.out.println("Populating first edge database...");
        iterativeDeepeningDepthFirstSearch(cube, firstEdgePDB);
        firstEdgePDB.writeDatabaseToFile("first_edges.pdb");
        System.out.println("First edge database populated.\n");
    }

    private static void populateSecondEdgeDatabase() {
        Cube cube = new Cube();
        SecondEdgePatternDatabase secondEdgePDB = new SecondEdgePatternDatabase();

        System.out.println("Populating second edge database...");
        iterativeDeepeningDepthFirstSearch(cube, secondEdgePDB);
        secondEdgePDB.writeDatabaseToFile("second_edges.pdb");
        System.out.println("Second edge database populated.\n");
    }


    private static void populateSmallFirstEdgeDatabase() {
        Cube cube = new Cube();
        SmallFirstEdgePatternDatabase firstEdgePDB = new SmallFirstEdgePatternDatabase();

        System.out.println("Populating first edge database (6 edges)...");
        breadthFirstSearch(cube, firstEdgePDB);
        firstEdgePDB.writeDatabaseToFile("first_six_edges.pdb");
        System.out.println("First edge database populated.\n");
    }

    private static void populateSmallSecondEdgeDatabase() {
        Cube cube = new Cube();
        SmallSecondEdgePatternDatabase secondEdgePDB = new SmallSecondEdgePatternDatabase();

        System.out.println("Populating second edge database...");
        breadthFirstSearch(cube, secondEdgePDB);
        secondEdgePDB.writeDatabaseToFile("second__six_edges.pdb");
        System.out.println("Second edge database populated.\n");
    }


    private static class BFSNode {
        Move move;
        BFSNode parent;

        public BFSNode(Move move, BFSNode parent) {
            this.move = move;
            this.parent = parent;
        }
    }

    private static ArrayList<Move> breadthFirstSearch(Cube cube, PatternDatabase database) {
        // nodeQueue is a queue of nodes to be visited in the breadth-first search
        ArrayDeque<BFSNode> nodeQueue = new ArrayDeque<BFSNode>();

        // movesList is a list of moves to get to the current node
        ArrayList<Move> movesList = new ArrayList<Move>();

        // Additional variables for search stats
        int totalVisitedNodes = 0;
        int maxQueueSize = 0;
        int maxDepth = 0;
        long startTime = System.currentTimeMillis();

        // Create the root node and add it to the queue
        BFSNode root = new BFSNode(Move.NONE, null);
        nodeQueue.push(root);

        // Set the number of moves in the database to solve the initial state to 0
        database.setNumberOfMoves(cube, (byte) 0);


        // Iterate over all nodes in the queue
        while (!nodeQueue.isEmpty()) {

            // Get next node in the queue
            BFSNode currentNode = nodeQueue.removeFirst();   
            
            // Clear the moves list
            movesList.clear();

            // Move to the node (updates movesList and cube)
            moveToNode(currentNode, movesList, cube);
            totalVisitedNodes++;
            System.out.print("nodes visited: " + totalVisitedNodes + "\r");

            // Update the max depth
            if (movesList.size() > maxDepth) {
                System.out.println("BFS: finished depth " + maxDepth + " after " + ((System.currentTimeMillis() - startTime) / 1000.0) + "s.");
                maxDepth++;
            }

            // Iterate over all possible moves from the current node
            for (Move move : Move.values()) {
            
                // Skip the empty move
                if (move == Move.NONE) {
                    continue;
                }

                // If at the root node or the move shouldn't be skipped
                if (movesList.size() == 0 || !skipMove(move, movesList.getLast())) {
                    
                    // Make the move
                    cube.makeMove(move);
                
                    // Try to update the number of moves in the database
                    // If successful, add the node to the queue; if not, the node was visited before with fewer moves
                    if (database.setNumberOfMoves(cube, (byte) (movesList.size() + 1))) {
                        nodeQueue.addLast(new BFSNode(move, currentNode));

                        // If the database is full, search is complete
                        if (database.isFull()) {
                            movesList.add(move);

                            System.out.println("Breadth-first search complete.");
                            System.out.println("Moves to complete the database: " + movesList.size());
                            System.out.println("Visited " + totalVisitedNodes + " nodes.");
                            System.out.println("Max queue size: " + maxQueueSize);
                            System.out.println("Elapsed time: " + ((System.currentTimeMillis() - startTime) / 1000.0) + "s");

                            // Undo all of the moves to get back to the initial cube state
                            undoAllMoves(nodeQueue.getLast(), cube);

                            // Return the list of moves to finish the search
                            return movesList;
                        }
                    }

                    // Undo the move
                    cube.undoMove(move);
                }
            }

            // Update the max queue size
            if (nodeQueue.size() > maxQueueSize) {
                maxQueueSize = nodeQueue.size();
            }

            // Undo all of the moves to get back to the initial cube state
            undoAllMoves(currentNode, cube);
        }

        System.err.println("Error: goal not found in search, but the queue is empty.");
        return null;
    }


    private static class IDDFSNode {
        Cube cube;
        Move move;
        byte depth;

        public IDDFSNode(Cube cube, Move move, byte depth) {
            this.cube = cube;
            this.move = move;
            this.depth = depth;
        }
    }

    private static void iterativeDeepeningDepthFirstSearch(Cube cube, PatternDatabase database) {
        // Use a deque as a stack for nodes
        ArrayDeque<IDDFSNode> nodeStack = new ArrayDeque<IDDFSNode>();
        IDDFSNode currentNode;
        int currentDepth = 0;
        int statesIndexed = 0;
        long startTime = System.currentTimeMillis();
        
        // Set the number of moves in the database to solve the initial state to 0
        database.setNumberOfMoves(cube, (byte) 0);
        statesIndexed++;

        // Keep searching nodes until database is full
        while (!database.isFull()) {

            if (nodeStack.isEmpty()) {
                // Depth level complete
                System.out.println("IDDFS: finished depth " + currentDepth + " after " + ((System.currentTimeMillis() - startTime) / 1000.0) + "s. Indexed " + statesIndexed + " states.");

                // Increate the current depth
                currentDepth++;

                // Push the root node onto the stack
                nodeStack.addFirst(new IDDFSNode(cube, Move.NONE, (byte) 0));
            }

            // Pop node off top of stack
            currentNode = nodeStack.removeFirst();

            // Iterate over all possible moves from the current node
            for (Move move : Move.values()) {

                // Skip the empty move
                if (move == Move.NONE) {
                    continue;
                }

                // If at the root node or the move shouldn't be skipped
                if (currentNode.depth == 0 || !skipMove(move, currentNode.move)) {

                    // Create a copy of the current cube state
                    Cube cubeCopy = new Cube(currentNode.cube);
                    byte cubeCopyDepth = (byte) (currentNode.depth + 1);

                    // Make the move on the copy
                    cubeCopy.makeMove(move);

                    int databaseIndex = database.getDatabaseIndex(cubeCopy);
                    
                    // If the cube state has been encountered at an earlier depth, skip it
                    if (database.getNumberOfMoves(databaseIndex) < cubeCopyDepth) {
                        continue;
                    }

                    // Index states at the depth limit; otherwise, add them to the stack
                    if (cubeCopyDepth == currentDepth) {
                        if (database.setNumberOfMoves(cubeCopy, cubeCopyDepth)) {
                            statesIndexed++;
                            System.out.print("states indexed: " + statesIndexed + "\r");
                        }
                    }
                    else {
                        nodeStack.addFirst(new IDDFSNode(cubeCopy, move, cubeCopyDepth));
                    }
                }
            }
        }
        System.out.println("Iterative-deepening depth-first search complete. Elapsed time: " + ((System.currentTimeMillis() - startTime) / 1000.0) + "s");
    }


    // Apply the moves to get to the target by traversing from the root node to the target
    private static void moveToNode(BFSNode target, ArrayList<Move> movesList, Cube cube) {
        // Use a deque as a stack to store the moves
        ArrayDeque<Move> movesStack = new ArrayDeque<Move>();
        BFSNode currentNode = target;

        // First go from the target node to the root, adding the moves to the stack
        while (currentNode.parent != null) {
            movesStack.addFirst(currentNode.move);
            currentNode = currentNode.parent;
        }

        // Then pop the moves from the stack to the moves list
        while (!movesStack.isEmpty()) {
            Move move = movesStack.removeFirst();
            movesList.add(move);
            cube.makeMove(move);
        }
    }

    // Undo all of the moves starting with the target, going back to the root node
    private static void undoAllMoves(BFSNode target, Cube cube) {
        BFSNode currentNode = target;

        while (currentNode.parent != null) {
            cube.undoMove(currentNode.move);
            currentNode = currentNode.parent;
        }
    }

    // Check if a move should be skipped
    private static boolean skipMove(Move move, Move lastMove) {
        // Skip moves that are on the same face as the last move (e.g. L, L2 = L')
        if ((move == Move.L || move == Move.LPRIME || move == Move.L2) &&
            (lastMove == Move.L || lastMove == Move.LPRIME || lastMove == Move.L2)) {
            return true;
        }

        if ((move == Move.R || move == Move.RPRIME || move == Move.R2) &&
            (lastMove == Move.R || lastMove == Move.RPRIME || lastMove == Move.R2)) {
            return true;
        }

        if ((move == Move.U || move == Move.UPRIME || move == Move.U2) &&
            (lastMove == Move.U || lastMove == Move.UPRIME || lastMove == Move.U2)) {
            return true;
        }

        if ((move == Move.D || move == Move.DPRIME || move == Move.D2) &&
            (lastMove == Move.D || lastMove == Move.DPRIME || lastMove == Move.D2)) {
            return true;
        }

        if ((move == Move.F || move == Move.FPRIME || move == Move.F2) &&
            (lastMove == Move.F || lastMove == Move.FPRIME || lastMove == Move.F2)) {
            return true;
        }

        if ((move == Move.B || move == Move.BPRIME || move == Move.B2) &&
            (lastMove == Move.B || lastMove == Move.BPRIME || lastMove == Move.B2)) {
            return true;
        }

        // Skip moves on opposite faces in specific orders (e.g. L, R = R, L; so prevent R, L)
        if ((move == Move.L || move == Move.LPRIME || move == Move.L2) &&
            (lastMove == Move.R || lastMove == Move.RPRIME || lastMove == Move.R2)) {
            return true;
        }

        if ((move == Move.U || move == Move.UPRIME || move == Move.U2) &&
            (lastMove == Move.D || lastMove == Move.DPRIME || lastMove == Move.D2)) {
            return true;
        }

        if ((move == Move.F || move == Move.FPRIME || move == Move.F2) &&
            (lastMove == Move.B || lastMove == Move.BPRIME || lastMove == Move.B2)) {
            return true;
        }

        return false;
    }

}
