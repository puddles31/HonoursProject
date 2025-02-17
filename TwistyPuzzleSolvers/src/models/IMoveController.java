package models;

/**
 * Interface for a move controller, which handles the logic for making moves on a twisty puzzle.
 */
public interface IMoveController {
    
    /**
     * Intercace for a move enum for a twisty puzzle.
     */
    interface IMove {
        /**
         * Get the inverse of the move.
         * @return The inverse of the move.
         */
        IMove getInverse();

        /**
         * Get the base move of the move.
         * @return The base move of the move.
         */
        IMove getBaseMove();

        /**
         * Get the String representation of the move.
         * @return The String representation of the move.
         */
        String toString();
    }

    /**
     * Get the array of valid moves for the puzzle.
     * @return The array of valid moves.
     */
    IMove[] getMoves();

    /**
     * Parse a string as a Move object.
     * @param moveName - The name of the move.
     * @return The Move object corresponding to the move name.
     */
    IMove parseMove(String moveName);


    /**
     * Make a move on the puzzle.
     * @param move - The move to make.
     */
    void makeMove(IMove move);

    /**
     * Undo a move on the puzzle.
     * @param move - The move to undo.
     */
    void undoMove(IMove move);

    /**
     * Determine if a move should be skipped based on the last move.
     * @param move - The move to check.
     * @param lastMove - The last move made
     * @return {@code true} if the move should be skipped, {@code false} otherwise.
     */
    boolean skipMove(IMove move, IMove lastMove);

    /**
     * Scramble the puzzle by making {@code noMoves} random moves, excluding moves that should be skipped.
     * @param noMoves - The number of moves to make.
     * @return An array of the moves made.
     */
    IMove[] scramble(int noMoves);

}
