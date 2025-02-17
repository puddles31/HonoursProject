package solvers;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Comparator;
import java.util.PriorityQueue;

import models.ITwistyPuzzle;
import models.IMoveController.IMove;

/**
 * This class contains methods used to solve a twisty puzzle using an IDA* search method, guaranteeing an optimal solution.
 * @see patterndatabases.PatternDatabase
 */
public abstract class PuzzleSolver {

    ITwistyPuzzle puzzle;

    /**
     * Constructor for a PuzzleSolver object.
     * @param puzzle - The puzzle to solve.
     */
    public PuzzleSolver(ITwistyPuzzle puzzle) {
        this.puzzle = puzzle;
    }

    /**
     * Get the maximum number of moves required to solve a puzzle state across all databases.
     * @param puzzle - The puzzle to get the maximum number of moves for.
     * @return The maximum number of moves required to solve one of the subsets of cubies.
     * @throws IllegalArgumentException if the puzzle does not match the puzzle type of the solver.
     */
    public abstract byte getMaxNumberOfMoves(ITwistyPuzzle puzzle) throws IllegalArgumentException;

    /**
     * Get the maximum number of moves required to solve a puzzle state across all databases.
     * This method is faster than {@link #getMaxNumberOfMoves(ITwistyPuzzle puzzle)} because 
     * it returns as soon as a database estimate exceeds the bound hint.
     * @param puzzle - The puzzle to get the maximum number of moves for.
     * @param boundHint - The maximum number of moves allowed to solve the puzzle state.
     * @param depthHint - The depth of the current node in the search tree.
     * @return The maximum number of moves required to solve one of the subsets of cubies.
     * @throws IllegalArgumentException if the puzzle does not match the puzzle type of the solver.
     */
    public abstract byte getMaxNumberOfMoves(ITwistyPuzzle puzzle, byte boundHint, byte depthHint) throws IllegalArgumentException;


    /**
     * A node in the iterative-deepening A* search tree.
     * Contains the puzzle state, move used to get to the node, and the depth of the node.
     */
    private static class IDAStarNode {
        ITwistyPuzzle puzzle;
        IMove move;
        byte depth;

        /**
         * Constructor for an IDAStarNode.
         * @param puzzle - The puzzle state of the node.
         * @param move - The move used to get to the node.
         * @param depth - The depth of the node.
         */
        public IDAStarNode(ITwistyPuzzle puzzle, IMove move, byte depth) {
            this.puzzle = puzzle;
            this.move = move;
            this.depth = depth;
        }
    }

    /**
     * A move with an estimated number of moves required to solve the puzzle state after the move.
     * Used to prioritize moves in the IDA* search.
     */
    private static class PrioritizedMove {
        ITwistyPuzzle puzzle;
        IMove move;
        byte estimatedMoves;

        public PrioritizedMove(ITwistyPuzzle puzzle, IMove move, byte estimatedMoves) {
            this.puzzle = puzzle;
            this.move = move;
            this.estimatedMoves = estimatedMoves;
        } 
    }

    /**
     * Perform an iterative-deepening A* (IDA*) search to find an optimal solution to the puzzle state.
     * @return An array of moves which can be performed to solve the puzzle.
     * @throws IllegalStateException If the pattern databases are not initialised correctly.
     */
    public IMove[] solve() throws IllegalStateException {
        // Use a deque as a stack for nodes
        ArrayDeque<IDAStarNode> nodeStack = new ArrayDeque<IDAStarNode>();
        IDAStarNode currentNode;

        boolean isSolved = puzzle.isSolved();
        IMove[] moves = new IMove[100]; // Assume a maximum of 100 moves to optimally solve the puzzle
        
        byte bound = 0;
        byte nextBound = getMaxNumberOfMoves(puzzle);

        long startTime = System.currentTimeMillis();

        System.out.println("IDA*: Beginning search at depth " + nextBound);

        // Keep searching until the cube is solved
        while (!isSolved) {

            // If depth level is complete
            if (nodeStack.isEmpty()) {
                if (bound != 0) {
                    System.out.println("IDA*: Finished bound " + bound + " after " + (System.currentTimeMillis() - startTime) / 1000.0 + "s.");
                }

                // Push the root node (initial scrambled puzzle state) onto the stack
                nodeStack.addFirst(new IDAStarNode(puzzle, null, (byte) 0));

                // If nextBound is 0, database not initialised correctly
                if (nextBound == 0) {
                    throw new IllegalStateException("nextBound set to 0. Bad database.");
                }
                // If nextBound was not updated from max value, all branches were pruned
                if (nextBound == Byte.MAX_VALUE) {
                    throw new IllegalStateException("nextBound set to max value. Bad database.");
                }

                // Update the bound to the next bound
                bound = nextBound;
                nextBound = Byte.MAX_VALUE;
            }

            // Pop node off of top of stac
            currentNode = nodeStack.removeFirst();

            // Update the moves array
            moves[currentNode.depth] = null;
            if (currentNode.depth != 0) {
                moves[currentNode.depth - 1] = currentNode.move;
            }

            // If the current node is at the bound depth, check if the puzzle is solved
            if (currentNode.depth == bound) {
                if (currentNode.puzzle.isSolved()) {
                    isSolved = true;
                }
            }
            else {
                // Create a comparator to sort the children nodes by estimated moves in ascending order
                Comparator<PrioritizedMove> prioritizedMoveComparator = (PrioritizedMove a, PrioritizedMove b) -> {
                    return Byte.compare(a.estimatedMoves, b.estimatedMoves);
                };

                // Create a priority queue using the comparator to store the children nodes
                PriorityQueue<PrioritizedMove> children = new PriorityQueue<PrioritizedMove>(prioritizedMoveComparator);

                // Iterate over all possible moves from the current node
                for (IMove move : puzzle.getMoveController().getMoves()) {
                    // If at the root node or the move shouldn't be skipped
                    if (currentNode.depth == 0 || !puzzle.getMoveController().skipMove(move, currentNode.move)) {
                        
                        // Create a copy of the current puzzle state and make the move on the copy
                        ITwistyPuzzle puzzleCopy = currentNode.puzzle.copy();
                        puzzleCopy.getMoveController().makeMove(move);

                        // Calculate an estimate for the number of moves required to solve the child node
                        byte estimatedChildMoves = (byte)(currentNode.depth + (byte) 1 + getMaxNumberOfMoves(puzzleCopy, bound, (byte)(currentNode.depth + 1)));

                        if (estimatedChildMoves <= bound) {
                            // If child node is estimated to take less moves than the current bound, push to queue
                            children.add(new PrioritizedMove(puzzleCopy, move, estimatedChildMoves));
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
                    nodeStack.addFirst(new IDAStarNode(child.puzzle, child.move, (byte)(currentNode.depth + 1)));
                }

            }
        }

        System.out.println("IDA*: Solution found at depth " + bound + " after " + (System.currentTimeMillis() - startTime) / 1000.0 + "s.");
        return Arrays.copyOf(moves, bound);
    }

}
