package kilominx;

import rubikscube.Cube;

/**
 * A Kilominx, represented as an array of "kubies" (Kilominx cubies).
 * @see #Kilominx()
 * @see #Kubie
 * @see #KilominxInteractive
 * @see #isSolved()
 */
public class Kilominx {

    /**
     * Colours on the Kilominx.
     */
    public static enum Colour {
        W, E, R, B, V, Y, C, P, T, L, O, G
    };

    /**
     * Moves that can be made on the Kilominx.
     */
    public static enum Move {
        U,   U_PRIME,   U_2,   U_PRIME2,
        L,   L_PRIME,   L_2,   L_PRIME2,
        F,   F_PRIME,   F_2,   F_PRIME2,
        R,   R_PRIME,   R_2,   R_PRIME2,
        BL,  BL_PRIME,  BL_2,  BL_PRIME2,
        BR,  BR_PRIME,  BR_2,  BR_PRIME2,
        DL,  DL_PRIME,  DL_2,  DL_PRIME2,
        DR,  DR_PRIME,  DR_2,  DR_PRIME2,
        DBL, DBL_PRIME, DBL_2, DBL_PRIME2,
        DBR, DBR_PRIME, DBR_2, DBR_PRIME2,
        DB,  DB_PRIME,  DB_2,  DB_PRIME2,
        D,   D_PRIME,   D_2,   D_PRIME2
    }

    // Kubies are indexed from 0 to 19 in the following order:
    // DEFAULT KUBIE INDEXING HERE
    final byte KUBIE_UF;


    /**
     * A cubie on the Kilominx (aka a Kubie).
     * E kubie has an index (which represents which colours are on the kubie's sides)
     * and an orientation (0, 1, or 2).
     * @see #Kubie(byte, byte)
     * @see #Kilominx
     */
    class Kubie {
        // Kubies are indexed from 0 to 19 in the following order, with the following initial colours:

        // INDEX LIST GOES HERE

        // (note: kubies that are an odd number of quarter turns away are also an odd number of indices apart_
        // NEED TO VERIFY THIS AND PROVIDE EXAMPLE 

        // Kubies have an orientation of:
        //  - 0 (),
        //  - 1 (),
        //  - or 2 ()
        private byte index, orientation;

        /**
         * Constructor for a kubie.
         * @param index - The index of the kubie.
         * @param orientation - The orientation of the kubie.
         * @see #Kubie
         */
        private Kubie(byte index, byte orientation) {
            this.index = index;
            this.orientation = orientation;
        }
    }


    // Kilominx state is stored as an array of 20 kubies
    private Kubie[] kubies = new Kubie[20];

    /**
     * Constructor for a Kilominx.
     * The Kilominx is initialised in the solved state, with the white face on top and the red face on front.
     * @see #Kilominx
     * @see #Kubie(byte, byte)
     */
    public Kilominx() {
        
    }
    
    /**
     * Copy constructor for a Kilominx.
     * The Kilominx is initialised with the same kubie indices/orientations as the given Kilominx.
     * @param other - The Kilominx to copy.
     */
    public Kilominx(Cube other) {
        System.out.println("Not implemented yet");
    }

    
    /**
     * Get the indices of the kubies.
     * @return An array of the indices of the kubies.
     * @see #getKubieOrientations()
     */
    public byte[] getKubieIndices() {
        System.out.println("Not implemented yet");
        return null;
    }

    /**
     * Get the orientations of the kubies.
     * @return An array of the orientations of the kubies.
     * @see #getKubieIndices()
     */
    public byte[] getKubieOrientations() {
        System.out.println("Not implemented yet");
        return null;
    }


    /**
     * Check if the Kilominx is solved.
     * @return {@code true} if the Kilominx is solved, {@code false} otherwise.
     */
    public boolean isSolved() {
        System.out.println("Not implemented yet");
        return false;
    }


    /**
     * Increase the orientation of a kubie (modulo 3).
     * @param posIndex - The position index of the kubie.
     * @param incr - The amount to increase the orientation by.
     */
    private void increaseKubieOrientation(byte posIndex, byte incr) {
        System.out.println("Not implemented yet");
    }


    /**
     * Get the colours of a kubie.
     * @param posIndex - The position index of the kubie.
     * @return An array of the colours of the kubie. The colours are in the order ?????
     */
    private Colour[] getKubieColours(byte posIndex) {
        System.out.println("Not implemented yet");
        return null;
    }


    /**
     * Make a move on the Kilominx.
     * @param move - The move to make.
     */
    public void makeMove(Move move) {
        System.out.println("Not implemented yet");
    }

    /**
     * Undo a move on the Kilominx by performing the inverse move.
     * @param move - The move to undo.
     */
    public void undoMove(Move move) {
        System.out.println("Not implemented yet");
    }


    /**
     * Perform a clockwise turn of the U face.
     */
    public void moveU() {
        System.out.println("Not implemented yet");
    }


    /**
     * Print the Kilominx state in a human-readable format.
     */
    public void printCubeState() {
        System.out.println("Not implemented yet");
    }
}
