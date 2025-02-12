package models;

public interface ITwistyMoves {
    
    public interface IMove {
        public IMove getInverse();
        public IMove getBaseMove();
        public String toString();
    }

    /**
     * Get the array of valid moves for the puzzle.
     * @return The array of valid moves.
     */
    public IMove[] getMoves();

    /**
     * Get the array of valid moves for the puzzle.
     * @return The array of valid moves.
     */
    public IMove fromString(String moveName);


    /**
     * Make a move on the puzzle.
     * @param move - The move to make.
     */
    public void makeMove(IMove move);

    /**
     * Undo a move on the puzzle.
     * @param move - The move to undo.
     */
    public void undoMove(IMove move);

    /**
     * Determine if a move should be skipped based on the last move.
     * @param move - The move to check.
     * @param lastMove - The last move made
     * @return {@code true} if the move should be skipped, {@code false} otherwise.
     */
    public boolean skipMove(IMove move, IMove lastMove);

    /**
     * Scramble the puzzle by making {@code noMoves} random moves, excluding moves that should be skipped.
     * @param noMoves - The number of moves to make.
     * @return An array of the moves made.
     */
    public IMove[] scramble(int noMoves);


}
