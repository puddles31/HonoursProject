package models;

public interface ITwistyPuzzle {

    public interface IColour {
        public int value();
    }

    class Cubie {
        public byte index, orientation;

        /**
         * Constructor for a cubie.
         * @param index - The index of the cubie.
         * @param orientation - The orientation of the cubie.
         */
        public Cubie(byte index, byte orientation) {
            this.index = index;
            this.orientation = orientation;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }

            if (obj == null || obj.getClass() != this.getClass()) {
                return false;
            }

            Cubie other = (Cubie) obj;
            return index == other.index && orientation == other.orientation;
        }
    }

    /**
     * Get the moves object (which holds logic for making moves) for the puzzle.
     * @return The moves object.
     */
    public ITwistyMoves getMovesObj();

    /**
     * Create a copy of the puzzle.
     * @return A copy of the puzzle.
     */
    public ITwistyPuzzle copy();

    /**
     * Reset the puzzle to the solved state.
     * This should be used instead of creating a new object to ensure the current puzzle state is modified.
     */
    public void reset();

    /**
     * Check if the puzzle is solved.
     * @return {@code true} if the puzzle is solved, {@code false} otherwise.
     */
    public boolean isSolved();

    /**
     * Print the puzzle state to stdout in a human readable format.
     */
    public void printState();
}
