package models;

import java.util.Objects;

/**
 * Interface for a twisty puzzle (e.g. a Rubik's Cube, or a Kilominx).
 */
public interface ITwistyPuzzle {

    /**
     * Interface for a colour enum for cubies on a twisty puzzle.
     */
    interface IColour {
        /**
         * Return the "value" of the colour.
         * This is used for converting between different puzzle model types.
         * @return The value of the colour.
         */
        int value();
    }

    /**
     * Class for a cubie on a twisty puzzle.
     */
    class Cubie implements Comparable<Cubie> {
        // Cubies have an index (determines initial position and colours of cubie) and an orientation.
        byte index, orientation;

        /**
         * Constructor for a cubie.
         * @param index - The index of the cubie.
         * @param orientation - The orientation of the cubie.
         */
        Cubie(byte index, byte orientation) {
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

        @Override
        public int hashCode() {
            return Objects.hash(index, orientation);
        }

        @Override
        public int compareTo(Cubie other) {
            int i = Byte.compare(index, other.index);
            if (i != 0) {
                return i;
            }
            return Byte.compare(orientation, other.orientation);
        }
    }

    /**
     * Get the move controller (which holds logic for making moves) for the puzzle.
     * @return The move controller for the puzzle.
     */
    IMoveController getMoveController();

    /**
     * Create a copy of the puzzle.
     * @return A copy of the puzzle.
     */
    ITwistyPuzzle copy();

    /**
     * Reset the puzzle to the solved state.
     * This should be used instead of creating a new object to ensure the current puzzle state is modified.
     */
    void reset();

    /**
     * Check if the puzzle is solved.
     * @return {@code true} if the puzzle is solved, {@code false} otherwise.
     */
    boolean isSolved();

    /**
     * Print the puzzle state to stdout in a human readable format.
     */
    void printState();
}
