// R = F
// F = L
// L = B
// B = R

// f b' l' U f' r2 b2 U' b U2 r2 U2 f2 U'

package rubikscube;

import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.PriorityQueue;

import rubikscube.Cube.Move;

public class CubeSolver {
    
    Cube cube;
    CornerPatternDatabase cornerPDB;
    FirstEdgePatternDatabase firstEdgePDB;
    SecondEdgePatternDatabase secondEdgePDB;

    public CubeSolver(Cube cube) {
        this.cube = cube;

        cornerPDB = new CornerPatternDatabase();
        firstEdgePDB = new FirstEdgePatternDatabase();
        secondEdgePDB = new SecondEdgePatternDatabase();

        System.out.println("Loading pattern databases...");

        cornerPDB.readDatabaseFromFile("corners.pdb");
        firstEdgePDB.readDatabaseFromFile("first_edges.pdb");
        secondEdgePDB.readDatabaseFromFile("second_edges.pdb");

        System.out.println("Pattern databases loaded.");
    }


    /**
     * Get the maximum number of moves required to solve a cube state across all databases.
     * @param cube - The cube to get the maximum number of moves for.
     * @return The maximum number of moves required to solve one of the subsets of cubies.
     */
    private byte getMaxNumberOfMoves(Cube cube) {
        return (byte) Math.max(cornerPDB.getNumberOfMoves(cube), Math.max(firstEdgePDB.getNumberOfMoves(cube), secondEdgePDB.getNumberOfMoves(cube)));
    }

    /**
     * Get the maximum number of moves required to solve a cube state across all databases.
     * This method is faster than {@link #getMaxNumberOfMoves(Cube cube)} because 
     * it returns as soon as a database estimate exceeds the bound hint.
     * @param cube - The cube to get the maximum number of moves for.
     * @param boundHint - The maximum number of moves allowed to solve the cube state.
     * @param depthHint - The depth of the current node in the search tree.
     * @return The maximum number of moves required to solve one of the subsets of cubies.
     */
    private byte getMaxNumberOfMoves(Cube cube, byte boundHint, byte depthHint) {
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


    /**
     * A node in the iterative-deepening A* search tree.
     * Contains the cube state, move used to get to the node, and the depth of the node.
     */
    private static class IDAStarNode {
        Cube cube;
        Move move;
        byte depth;

        /**
         * Constructor for an IDAStarNode.
         * @param cube - The cube state of the node.
         * @param move - The move used to get to the node.
         * @param depth - The depth of the node.
         */
        public IDAStarNode(Cube cube, Move move, byte depth) {
            this.cube = cube;
            this.move = move;
            this.depth = depth;
        }
    }


    private static class PrioritizedMove {
        Cube cube;
        Move move;
        byte estimatedMoves;

        public PrioritizedMove(Cube cube, Move move, byte estimatedMoves) {
            this.cube = cube;
            this.move = move;
            this.estimatedMoves = estimatedMoves;
        } 
    }


    public void solveCube() {
        // Use a deque as a stack for nodes
        ArrayDeque<IDAStarNode> nodeStack = new ArrayDeque<IDAStarNode>();
        IDAStarNode currentNode;

        boolean isSolved = cube.isSolved();
        Move[] moves = new Move[50];
        
        byte bound = 0;
        byte nextBound = getMaxNumberOfMoves(cube);

        long startTime = System.currentTimeMillis();

        System.out.println("IDA*: Beginning search at depth " + nextBound);

        // Keep searching until the cube is solved
        while (!isSolved) {

            // If depth level is complete
            if (nodeStack.isEmpty()) {
                if (bound != 0) {
                    System.out.println("IDA*: Finished bound " + bound + " after " + (System.currentTimeMillis() - startTime) / 1000.0 + "s.");
                }

                // Push the root node (initial scrambled cube) onto the stack
                nodeStack.addFirst(new IDAStarNode(cube, null, (byte) 0));

                // If nextBound is 0, database not initialised correctly
                if (nextBound == 0) {
                    System.out.println("ERROR: nextBound set to 0. Bad database.");
                    return;
                }
                // If nextBound is 0, all branches have been pruned
                if (nextBound == Byte.MAX_VALUE) {
                    System.out.println("ERROR: nextBound set to max value. Bad database.");
                    return;
                }

                // Update the bound to the next bound
                bound = nextBound;
                nextBound = Byte.MAX_VALUE;
            }

            currentNode = nodeStack.removeFirst();

            moves[currentNode.depth] = null;

            if (currentNode.depth != 0) {
                moves[currentNode.depth - 1] = currentNode.move;
            }

            if (currentNode.depth == bound) {
                if (currentNode.cube.isSolved()) {
                    isSolved = true;
                }
            }
            else {
                Comparator<PrioritizedMove> prioritizedMoveComparator = (PrioritizedMove a, PrioritizedMove b) -> {
                    return Byte.compare(a.estimatedMoves, b.estimatedMoves);
                };

                PriorityQueue<PrioritizedMove> children = new PriorityQueue<PrioritizedMove>(prioritizedMoveComparator);

                // Iterate over all possible moves from the current node
                for (Move move : Move.values()) {
                    // If at the root node or the move shouldn't be skipped
                    if (currentNode.depth == 0 || !Cube.skipMove(move, currentNode.move)) {
                        
                        // Create a copy of the current cube state and make the move on the copy
                        Cube cubeCopy = new Cube(currentNode.cube);
                        cubeCopy.makeMove(move);

                        byte estimatedChildMoves = (byte)(currentNode.depth + (byte) 1 + getMaxNumberOfMoves(cubeCopy, bound, (byte)(currentNode.depth + 1)));

                        if (estimatedChildMoves <= bound) {
                            // If twisted cube is estimated to take less moves than the current bound, push to queue
                            children.add(new PrioritizedMove(cubeCopy, move, estimatedChildMoves));
                        }
                        else if (estimatedChildMoves < nextBound) {
                            // nextBound is the minimum of all child node moves greater than the current bound
                            nextBound = estimatedChildMoves;
                        }
                    }
                }

                while (!children.isEmpty()) {
                    // Push the nodes in sorted order onto the stack
                    PrioritizedMove child = children.poll();
                    nodeStack.addFirst(new IDAStarNode(child.cube, child.move, (byte)(currentNode.depth + 1)));
                }

            }
        }

        System.out.println("IDA*: Solution found at depth " + bound + " after " + (System.currentTimeMillis() - startTime) / 1000.0 + "s.");

        for (int i = 0; i < bound; i++) {
            System.out.print(moves[i].name() + " ");
        }
    }

}
