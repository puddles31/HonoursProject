package rubikscube;

import java.util.Random;
import java.util.stream.Stream;

/**
 * A Rubik's Cube, represented as an array of edge cubies and an array corner cubies.
 * @see #Cube()
 * @see #Cubie
 * @see #CubeInteractive
 * @see #isSolved()
 */
public class Cube {

    /**
     * Colours on the Rubik's Cube (W, G, R, B, O, Y).
     */
    public static enum Colour {
        W(1), G(2), R(4), B(8), O(16), Y(32);

        private final int value;

        private Colour(int value) {
            this.value = value;
        }

        /**
         * Return the "value" of the colour.
         * This is used for converting between different cube model types.
         * @return The value of the colour.
         */
        public int value() {
            return value;
        }
    };

    /**
     * Moves that can be made on the Rubik's Cube.
     */
    public static enum Move {
        U, UPRIME, U2,
        L, LPRIME, L2,
        F, FPRIME, F2,
        R, RPRIME, R2,
        B, BPRIME, B2,
        D, DPRIME, D2;

        /**
         * Return the String representation of the move.
         * (replaces PRIME with ')
        */
        public String toString() {
            return name().replace("PRIME", "'");
        }
    }

    // Edge cubies are indexed from 0 to 11 in the following order:
    final byte EDGE_UB = 0,
               EDGE_UR = 1,
               EDGE_UF = 2,
               EDGE_UL = 3,
               EDGE_FR = 4,
               EDGE_FL = 5,
               EDGE_BL = 6,
               EDGE_BR = 7,
               EDGE_DF = 8,
               EDGE_DL = 9,
               EDGE_DB = 10,
               EDGE_DR = 11;

    // Corner cubies are indexed from 0 to 7 in the following order:
    final byte CORNER_ULB = 0,
               CORNER_URB = 1,
               CORNER_URF = 2,
               CORNER_ULF = 3,
               CORNER_DLF = 4,
               CORNER_DLB = 5,
               CORNER_DRB = 6,
               CORNER_DRF = 7;


    /**
     * A cubie on the Rubik's Cube.
     * The cubie has an index (which represents which colours are on the cubie's sides)
     * and an orientation ({0, 1} for edge cubies, {0, 1, 2} for corner cubies).
     * @see #Cubie(byte, byte)
     * @see #Cube
     */
    class Cubie {
        // Edge cubies are indexed from 0 to 11 in the following order, with the following initial colours:
        // 0,  1,  2,  3,  4,  5,  6,  7,  8,  9,  10, 11
        // UB, UR, UF, UL, FR, FL, BL, BR, DF, DL, DB, DR
        // WO, WB, WR, WG, RB, RG, OG, OB, YR, YG, YO, YB

        // Edge cubies have an orientation of 0 (oriented) or 1 (flipped)
        // (an edge is "oriented" if it can be solved using only R, L, U, D moves)
        // (edges are flipped after quarter turns on the F and B faces)

        // Corner cubies are indexed from 0 to 7 in the following order, with the following initial colours:
        // 0,   1,   2,   3,   4,   5,   6,   7
        // ULB, URB, URF, ULF, DLF, DLB, DRB, DRF
        // WGO, WBO, WBR, WGR, YGR, YGO, YBO, YBR

        // (note: corners that are an odd number of quarter turns away are also an odd number of indices apart
        //  e.g. ULB can turn to corners URB, ULF and DLB in 1 turn, and to DRF in 3 turns;
        //  ULB is index 0 (even), so URB (1), ULF (3), DLB (5) and DRF (7) are all an odd number of indices away)

        // Corner cubies have an orientation of 0 (oriented - white/yellow on top/bottom), 1 (white/yellow turned CW from nearest up/down face), or 2 (white/yellow turned CCW from nearest up/down face)
        private byte index, orientation;

        /**
         * Constructor for a cubie.
         * @param index - The index of the cubie.
         * @param orientation - The orientation of the cubie.
         * @see #Cubie
         */
        private Cubie(byte index, byte orientation) {
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


    // Cube state is stored as an array of 12 edge cubies and an array of 8 corner cubies
    private Cubie[] edgeCubies = new Cubie[12];
    private Cubie[] cornerCubies = new Cubie[8];

    /**
     * Constructor for a Rubik's Cube.
     * The cube is initialised in the solved state, with the white face on top and the red face on front.
     * @see #Cube
     * @see #Cubie(byte, byte)
     */
    public Cube() {
        // Initialize edge cubies
        for (byte i = 0; i < 12; i++) {
            edgeCubies[i] = new Cubie(i, (byte) 0);
        }

        // Initialize corner cubies
        for (byte i = 0; i < 8; i++) {
            cornerCubies[i] = new Cubie(i, (byte) 0);
        }
    }

    /**
     * Constructor for a Rubik's Cube from an array of cubie colours. 
     * This constructor is used for creating a cube from Edit mode.
     * @param colours - The array of cubie colours.
     */
    public Cube(Colour[] colours) {

        for (int i = 0; i < 8; i++) {
            cornerCubies[i] = getCornerCubie(colours, i);
            if (cornerCubies[i] == null) {
                throw new IllegalArgumentException("Invalid cube stickering.");
            }
        }

        for (int i = 0; i < 12; i++) {
            edgeCubies[i] = getEdgeCubie(colours, i);
            if (edgeCubies[i] == null) {
                throw new IllegalArgumentException("Invalid cube stickering.");
            }
        }

        // Check for duplicate cubies
        if (Stream.of(edgeCubies).distinct().count() != 12 || Stream.of(cornerCubies).distinct().count() != 8) {
            throw new IllegalArgumentException("Duplicate cubie(s).");
        }

        // Check for parity errors
        // Corner parity: the sum of the orientations of the corner cubies must be a multiple of 3
        if (Stream.of(cornerCubies).mapToInt(cubie -> cubie.orientation).sum() % 3 != 0) {
            throw new IllegalArgumentException("Corner parity error.");
        }
        // Edge parity: the sum of the orientations of the edge cubies must be even
        if (Stream.of(edgeCubies).mapToInt(cubie -> cubie.orientation).sum() % 2 != 0) {
            throw new IllegalArgumentException("Edge parity error.");
        }
        // Permutation parity: the sum of the number of swaps needed to solve the corner and edge cubies must be even
        // https://puzzling.stackexchange.com/questions/53846/how-to-determine-whether-a-rubiks-cube-is-solvable
        if (countSwaps(cornerCubies) + countSwaps(edgeCubies) % 2 != 0) {
            throw new IllegalArgumentException("Permutation parity error.");
        }
    }


    public Colour[] getColours() {
        Colour[] colours = new Colour[48];

        // Edge cubies
        for (byte i = 0; i < 12; i++) {
            Colour[] edges = getEdgeColours(i);
            switch (i) {
                case EDGE_UB:
                    colours[1] = edges[0];
                    colours[18] = edges[1];
                    break;
                case EDGE_UR:
                    colours[4] = edges[0];
                    colours[15] = edges[1];
                    break;
                case EDGE_UF:
                    colours[6] = edges[0];
                    colours[12] = edges[1];
                    break;
                case EDGE_UL:
                    colours[3] = edges[0];
                    colours[9] = edges[1];
                    break;
                case EDGE_FR:
                    colours[23] = edges[0];
                    colours[24] = edges[1];
                    break;
                case EDGE_FL:
                    colours[22] = edges[0];
                    colours[21] = edges[1];
                    break;
                case EDGE_BL:
                    colours[27] = edges[0];
                    colours[20] = edges[1];
                    break;
                case EDGE_BR:
                    colours[26] = edges[0];
                    colours[25] = edges[1];
                    break;
                case EDGE_DF:
                    colours[41] = edges[0];
                    colours[32] = edges[1];
                    break;
                case EDGE_DL:
                    colours[43] = edges[0];
                    colours[29] = edges[1];
                    break;
                case EDGE_DB:
                    colours[46] = edges[0];
                    colours[38] = edges[1];
                    break;
                case EDGE_DR:
                    colours[44] = edges[0];
                    colours[35] = edges[1];
                    break;
            }
        }

        // Corner cubies
        for (byte i = 0; i < 8; i++) {
            Colour[] corners = getCornerColours(i);
            switch (i) {
                case CORNER_ULB:
                    colours[0] = corners[0];
                    colours[8] = corners[1];
                    colours[19] = corners[2];
                    break;
                case CORNER_URB:
                    colours[2] = corners[0];
                    colours[16] = corners[1];
                    colours[17] = corners[2];
                    break;
                case CORNER_URF:
                    colours[7] = corners[0];
                    colours[14] = corners[1];
                    colours[13] = corners[2];
                    break;
                case CORNER_ULF:
                    colours[5] = corners[0];
                    colours[10] = corners[1];
                    colours[11] = corners[2];
                    break;
                case CORNER_DLF:
                    colours[40] = corners[0];
                    colours[30] = corners[1];
                    colours[31] = corners[2];
                    break;
                case CORNER_DLB:
                    colours[45] = corners[0];
                    colours[28] = corners[1];
                    colours[39] = corners[2];
                    break;
                case CORNER_DRB:
                    colours[47] = corners[0];
                    colours[36] = corners[1];
                    colours[37] = corners[2];
                    break;
                case CORNER_DRF:
                    colours[42] = corners[0];
                    colours[34] = corners[1];
                    colours[33] = corners[2];
                    break;
            }
        }

        return colours;
    }


    private int countSwaps(Cubie[] cubies) {
        int swaps = 0;
        for (int i = 0; i < cubies.length; i++) {
            // If cubie not in correct position, swap it with the cubie in its correct position
            if (cubies[i].index != i) {
                byte temp = cubies[i].index;
                cubies[i].index = cubies[temp].index;
                cubies[temp].index = temp;
                swaps++;
                i--;
            }
        }
    return swaps;
}


    private Cubie getCornerCubie(Colour[] colours, int index) {
        // Get the three colours of the cubie at the given position index
        Colour[] cubieColours = new Colour[3];

        switch (index) {
            case CORNER_ULB:
                cubieColours = new Colour[]{colours[0], colours[8], colours[19]};
                break;
            case CORNER_URB:
                cubieColours = new Colour[]{colours[2], colours[16], colours[17]};
                break;
            case CORNER_URF:
                cubieColours = new Colour[]{colours[7], colours[14], colours[13]};
                break;
            case CORNER_ULF:
                cubieColours = new Colour[]{colours[5], colours[10], colours[11]};
                break;
            case CORNER_DLF:
                cubieColours = new Colour[]{colours[40], colours[30], colours[31]};
                break;
            case CORNER_DLB:
                cubieColours = new Colour[]{colours[45], colours[28], colours[39]};
                break;
            case CORNER_DRB:
                cubieColours = new Colour[]{colours[47], colours[36], colours[37]};
                break;
            case CORNER_DRF:
                cubieColours = new Colour[]{colours[42], colours[34], colours[33]};
                break;
        }

        // Add the values of the three colours together to get a unique value for the cubie
        int cubieValue = cubieColours[0].value() + cubieColours[1].value() + cubieColours[2].value();
        byte cubieIndex = (byte) -1;

        switch (cubieValue) {
            case 19: cubieIndex = CORNER_ULB; break; // WGO 
            case 25: cubieIndex = CORNER_URB; break; // WBO 
            case 13: cubieIndex = CORNER_URF; break; // WBR 
            case 7:  cubieIndex = CORNER_ULF; break; // WGR 
            case 38: cubieIndex = CORNER_DLF; break; // YGR 
            case 50: cubieIndex = CORNER_DLB; break; // YGO 
            case 56: cubieIndex = CORNER_DRB; break; // YBO 
            case 44: cubieIndex = CORNER_DRF; break; // YBR 
            default: return null; // Invalid cube state
        }

        // Determine the orientation of the corner cubie
        // If white/yellow on top/bottom, orientation is 0
        if (cubieColours[0] == Colour.W || cubieColours[0] == Colour.Y) {
            return new Cubie(cubieIndex, (byte) 0);
        }
        
        switch (index) {
            // For this subset of cubies, if white/yellow is on the left/right, orientation is 1, otherwise 2
            case CORNER_ULB:
            case CORNER_URF:
            case CORNER_DLF:
            case CORNER_DRB:
                if (cubieColours[1] == Colour.W || cubieColours[1] == Colour.Y) {
                    return new Cubie(cubieIndex, (byte) 1);
                }
                else {
                    return new Cubie(cubieIndex, (byte) 2);
                }
            
            // For this subset of cubies, if white/yellow is on the front/back, orientation is 1, otherwise 2
            case CORNER_URB:
            case CORNER_ULF:
            case CORNER_DLB:
            case CORNER_DRF:
                if (cubieColours[2] == Colour.W || cubieColours[2] == Colour.Y) {
                    return new Cubie(cubieIndex, (byte) 1);
                }
                else {
                    return new Cubie(cubieIndex, (byte) 2);
                }

            default: return null; // Invalid index
        }
    }


    private Cubie getEdgeCubie(Colour[]colours, int index) {
        // Get the two colours of the cubie at the given position index
        Colour[] cubieColours = new Colour[2];

        switch (index) {
            case EDGE_UB:
                cubieColours = new Colour[]{colours[1], colours[18]};
                break;
            case EDGE_UR:
                cubieColours = new Colour[]{colours[4], colours[15]};
                break;
            case EDGE_UF:
                cubieColours = new Colour[]{colours[6], colours[12]};
                break;
            case EDGE_UL:
                cubieColours = new Colour[]{colours[3], colours[9]};
                break;
            case EDGE_FR:
                cubieColours = new Colour[]{colours[23], colours[24]};
                break;
            case EDGE_FL:
                cubieColours = new Colour[]{colours[22], colours[21]};
                break;
            case EDGE_BL:
                cubieColours = new Colour[]{colours[27], colours[20]};
                break;
            case EDGE_BR:
                cubieColours = new Colour[]{colours[26], colours[25]};
                break;
            case EDGE_DF:
                cubieColours = new Colour[]{colours[41], colours[32]};
                break;
            case EDGE_DL:
                cubieColours = new Colour[]{colours[43], colours[29]};
                break;
            case EDGE_DB:
                cubieColours = new Colour[]{colours[46], colours[38]};
                break;
            case EDGE_DR:
                cubieColours = new Colour[]{colours[44], colours[35]};
                break;
        }

        // Add the values of the two colours together to get a unique value for the cubie
        int cubieValue = cubieColours[0].value() + cubieColours[1].value();
        byte cubieIndex = (byte) -1;

        switch (cubieValue) {
            case 17: cubieIndex = EDGE_UB; break; // WO
            case 9:  cubieIndex = EDGE_UR; break; // WB
            case 5:  cubieIndex = EDGE_UF; break; // WR
            case 3:  cubieIndex = EDGE_UL; break; // WG
            case 12: cubieIndex = EDGE_FR; break; // RB
            case 6:  cubieIndex = EDGE_FL; break; // RG
            case 18: cubieIndex = EDGE_BL; break; // OG
            case 24: cubieIndex = EDGE_BR; break; // OB
            case 36: cubieIndex = EDGE_DF; break; // YR
            case 34: cubieIndex = EDGE_DL; break; // YG
            case 48: cubieIndex = EDGE_DB; break; // YO
            case 40: cubieIndex = EDGE_DR; break; // YB
            default: return null; // Invalid cube state
        }

        // Determine the orientation of the edge cubie
        // https://stackoverflow.com/questions/17305071/how-can-i-determine-optimally-if-an-edge-is-correctly-oriented-on-the-rubiks-cu
        
        // If the first colour is green/blue, orientation is 1
        if (cubieColours[0] == Colour.G || cubieColours[0] == Colour.B) {
            return new Cubie(cubieIndex, (byte) 1);
        }

        // If the first colour is red/orange, and second colour is white/yellow, orientation is 1
        if ((cubieColours[0] == Colour.R || cubieColours[0] == Colour.O) &&
            (cubieColours[1] == Colour.W || cubieColours[1] == Colour.Y)) {
            return new Cubie(cubieIndex, (byte) 1);
        }

        // Otherwise orientation is 0
        return new Cubie(cubieIndex, (byte) 0);
    }


    
    /**
     * Copy constructor for a Rubik's Cube.
     * The cube is initialised with the same edge/corner cubie indices/orientations as the given cube.
     * @param otherCube - The cube to copy.
     */
    public Cube(Cube otherCube) {
        byte[] otherEdgeIndices = otherCube.getEdgeIndices();
        byte[] otherEdgeOrientations = otherCube.getEdgeOrientations();

        for (byte i = 0; i < 12; i++) {
            edgeCubies[i] = new Cubie(otherEdgeIndices[i], otherEdgeOrientations[i]);
        }

        byte[] otherCornerIndices = otherCube.getCornerIndices();
        byte[] otherCornerOrientations = otherCube.getCornerOrientations();

        for (byte i = 0; i < 8; i++) {
            cornerCubies[i] = new Cubie(otherCornerIndices[i], otherCornerOrientations[i]);
        }
    }

    /**
     * Get the indices of the edge cubies.
     * @return An array of the indices of the edge cubies.
     * @see #getEdgeOrientations()
     * @see #getCornerIndices()
     */
    public byte[] getEdgeIndices() {
        byte[] indices = new byte[12];

        for (byte i = 0; i < 12; i++) {
            indices[i] = edgeCubies[i].index;
        }

        return indices;
    }

    /**
     * Get the orientations of the edge cubies.
     * @return An array of the orientations of the edge cubies.
     * @see #getEdgeIndices()
     * @see #getCornerOrientations()
     */
    public byte[] getEdgeOrientations() {
        byte[] orientations = new byte[12];

        for (byte i = 0; i < 12; i++) {
            orientations[i] = edgeCubies[i].orientation;
        }

        return orientations;
    }
    
    /**
     * Get the indices of the corner cubies.
     * @return An array of the indices of the corner cubies.
     * @see #getCornerOrientations()
     * @see #getEdgeIndices()
     */
    public byte[] getCornerIndices() {
        byte[] indices = new byte[8];

        for (byte i = 0; i < 8; i++) {
            indices[i] = cornerCubies[i].index;
        }

        return indices;
    }

    /**
     * Get the orientations of the corner cubies.
     * @return An array of the orientations of the corner cubies.
     * @see #getCornerIndices()
     * @see #getEdgeOrientations()
     */
    public byte[] getCornerOrientations() {
        byte[] orientations = new byte[8];

        for (byte i = 0; i < 8; i++) {
            orientations[i] = cornerCubies[i].orientation;
        }

        return orientations;
    }


    /**
     * Check if the cube is solved.
     * @return {@code true} if the cube is solved, {@code false} otherwise.
     */
    public boolean isSolved() {
        // Check that all corner cubies are in the correct position and orientation
        for (byte i = 0; i < cornerCubies.length; i++) {
            if (cornerCubies[i].index != i || cornerCubies[i].orientation != 0) {
                return false;
            }
        }

        // Check that all edge cubies are in the correct position and orientation
        for (byte i = 0; i < edgeCubies.length; i++) {
            if (edgeCubies[i].index != i || edgeCubies[i].orientation != 0) {
                return false;
            }
        }

        return true;
    }


    /**
     * Flip the orientation of an edge cubie.
     * @param posIndex - The position index of the edge cubie.
     * @see #increaseCornerOrientation(byte, byte)
     */
    private void flipEdgeOrientation(byte posIndex) {
        Cubie edge = edgeCubies[posIndex];
        edge.orientation ^= 1;  // XOR orientation with 1 results in flip between 0 and 1
    }

    /**
     * Increase the orientation of a corner cubie (modulo 3).
     * @param posIndex - The position index of the corner cubie.
     * @param incr - The amount to increase the orientation by.
     * @see #flipEdgeOrientation(byte)
     */
    private void increaseCornerOrientation(byte posIndex, byte incr) {
        Cubie corner = cornerCubies[posIndex];

        corner.orientation += incr;

        // faster equivalent to corner.orientation = (corner.orientation + incr) % 3
        if (corner.orientation == 3) {
            corner.orientation = 0;
        }
        else if (corner.orientation == 4) {
            corner.orientation = 1;
        }
    }


    /**
     * Get the colours of an edge cubie.
     * @param posIndex - The position index of the edge cubie.
     * @return An array of the colours of the edge cubie.
     * @see #getCornerColours(byte)
     */
    private Colour[] getEdgeColours(byte posIndex) {
        Cubie edge = edgeCubies[posIndex];
        Colour[] colours = new Colour[2];

        switch (edge.index) {
            case EDGE_UB:
                colours[0] = Colour.W;
                colours[1] = Colour.O;
                break;
                
            case EDGE_UR:
                colours[0] = Colour.W;
                colours[1] = Colour.B;
                break;
                
            case EDGE_UF:
                colours[0] = Colour.W;
                colours[1] = Colour.R;
                break;
                
            case EDGE_UL:
                colours[0] = Colour.W;
                colours[1] = Colour.G;
                break;
                
            case EDGE_FR:
                colours[0] = Colour.R;
                colours[1] = Colour.B;
                break;
                
            case EDGE_FL:
                colours[0] = Colour.R;
                colours[1] = Colour.G;
                break;
                
            case EDGE_BL:
                colours[0] = Colour.O;
                colours[1] = Colour.G;
                break;
                
            case EDGE_BR:
                colours[0] = Colour.O;
                colours[1] = Colour.B;
                break;
                
            case EDGE_DF:
                colours[0] = Colour.Y;
                colours[1] = Colour.R;
                break;
                
            case EDGE_DL:
                colours[0] = Colour.Y;
                colours[1] = Colour.G;
                break;
                
            case EDGE_DB:
                colours[0] = Colour.Y;
                colours[1] = Colour.O;
                break;
                
            case EDGE_DR:
                colours[0] = Colour.Y;
                colours[1] = Colour.B;
                break;
        }

        // Swap the colours if the edge is flipped
        if (edge.orientation == 1) {
            Colour temp = colours[0];
            colours[0] = colours[1];
            colours[1] = temp;
        }

        return colours;
    }


    /**
     * Get the colours of a corner cubie.
     * @param posIndex - The position index of the corner cubie.
     * @return An array of the colours of the corner cubie. The colours are in the order top/bottom, left/right, front/back. 
     * @see #getEdgeColours(byte)
     */
    private Colour[] getCornerColours(byte posIndex) {
        Cubie corner = cornerCubies[posIndex];
        Colour[] colours = new Colour[3];

        byte i0, i1, i2;

        // white/yellow on top/bottom
        if (corner.orientation == 0) {
            i0 = 0;
            i1 = 1;
            i2 = 2;

            // corner is an odd number of indices away, so the other two colours are flipped
            if ((corner.index + posIndex) % 2 == 1) {
                byte temp = i1;
                i1 = i2;
                i2 = temp;
            }
        }
        // white/yellow turned CW from nearest up/down face
        else if (corner.orientation == 1) {
            if ((posIndex % 2) == 0) {
                i0 = 1;
                i1 = 2;
                i2 = 0;
            }
            else {
                i0 = 2;
                i1 = 0;
                i2 = 1;
            }

            // corner is an odd number of indices away, so the other two colours are flipped
            if ((corner.index + posIndex) % 2 == 1) {
                byte temp = i1;
                i1 = i2;
                i2 = temp;
            }
        }
        // white/yellow turned CCW from nearest up/down face
        else {
            if ((posIndex % 2) == 0) {
                i0 = 2;
                i1 = 1;
                i2 = 0;
            }
            else {
                i0 = 1;
                i1 = 0;
                i2 = 2;
            }

            // corner is an EVEN number of indices away, so the other two colours are flipped
            if ((corner.index + posIndex) % 2 == 0) {
                byte temp = i1;
                i1 = i2;
                i2 = temp;
            }
        }

        switch (corner.index) {
            case CORNER_ULB:
                colours[i0] = Colour.W;
                colours[i1] = Colour.G;
                colours[i2] = Colour.O;
                break;

            case CORNER_URB:
                colours[i0] = Colour.W;
                colours[i1] = Colour.B;
                colours[i2] = Colour.O;
                break;

            case CORNER_URF:
                colours[i0] = Colour.W;
                colours[i1] = Colour.B;
                colours[i2] = Colour.R;
                break;

            case CORNER_ULF:
                colours[i0] = Colour.W;
                colours[i1] = Colour.G;
                colours[i2] = Colour.R;
                break;

            case CORNER_DLF:
                colours[i0] = Colour.Y;
                colours[i1] = Colour.G;
                colours[i2] = Colour.R;
                break;

            case CORNER_DLB:
                colours[i0] = Colour.Y;
                colours[i1] = Colour.G;
                colours[i2] = Colour.O;
                break;

            case CORNER_DRB:
                colours[i0] = Colour.Y;
                colours[i1] = Colour.B;
                colours[i2] = Colour.O;
                break;

            case CORNER_DRF:
                colours[i0] = Colour.Y;
                colours[i1] = Colour.B;
                colours[i2] = Colour.R;
                break;

        }

        return colours;
    }


    /**
     * Make a move on the Rubik's Cube.
     * @param move - The move to make.
     */
    public void makeMove(Move move) {
        switch (move) {
            case U:
                moveU();
                break;
            case UPRIME:
                moveUPrime();
                break;
            case U2:
                moveU2();
                break;
            
            case L:
                moveL();
                break;
            case LPRIME:
                moveLPrime();
                break;
            case L2:
                moveL2();
                break;

            case F:
                moveF();
                break;
            case FPRIME:
                moveFPrime();
                break;
            case F2:
                moveF2();
                break;
            
            case R:
                moveR();
                break;
            case RPRIME:
                moveRPrime();
                break;
            case R2:
                moveR2();
                break;
            
            case B:
                moveB();
                break;
            case BPRIME:
                moveBPrime();
                break;
            case B2:
                moveB2();
                break;
            
            case D:
                moveD();
                break;
            case DPRIME:
                moveDPrime();
                break;
            case D2:
                moveD2();
                break;
            
            default:
                break;
        }
    }

    /**
     * Undo a move on the Rubik's Cube by performing the inverse move (e.g. L' is the inverse of L).
     * @param move - The move to undo.
     */
    public void undoMove(Move move) {
        switch (move) {
            case U:
                moveUPrime();
                break;
            case UPRIME:
                moveU();
                break;
            case U2:
                moveU2();
                break;
            
            case L:
                moveLPrime();
                break;
            case LPRIME:
                moveL();
                break;
            case L2:
                moveL2();
                break;

            case F:
                moveFPrime();
                break;
            case FPRIME:
                moveF();
                break;
            case F2:
                moveF2();
                break;
            
            case R:
                moveRPrime();
                break;
            case RPRIME:
                moveR();
                break;
            case R2:
                moveR2();
                break;
            
            case B:
                moveBPrime();
                break;
            case BPRIME:
                moveB();
                break;
            case B2:
                moveB2();
                break;
            
            case D:
                moveDPrime();
                break;
            case DPRIME:
                moveD();
                break;
            case D2:
                moveD2();
                break;
            
            default:
                break;
        }
    }

    /**
     * Scramble the cube by making {@code noMoves} random moves, excluding moves that cancel previous moves.
     * @param noMoves - The number of moves to make.
     */
    public void scramble(int noMoves) {
        Random rand = new Random();
        Move[] moves = Move.values();

        Move move, lastMove = null;

        System.out.println("\nMoves made during scramble:");
        
        for (int i = 0; i < noMoves; i++) {
            do {
                move = moves[rand.nextInt(moves.length)];
            } while (i > 0 && skipMove(move, lastMove));
            
            makeMove(move);
            System.out.print(move.toString() + " ");

            lastMove = move;
        }
        System.out.println();
    }

    /**
     * Scramble the cube by making 20 random moves, excluding moves that cancel previous moves.
     */
    public void scramble() {
        scramble(20);
    }

    /**
     * Determine if a move should be skipped based on the last move.
     * Moves are skipped if they are on the same face as the last move, or if they are on opposite faces in specific orders ([R, L], [D, U], [B, F]).
     * @param move - The move to check.
     * @param lastMove - The last move made.
     * @return {@code true} if the move should be skipped, {@code false} otherwise.
     */
    public static boolean skipMove(Move move, Move lastMove) {
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

    /**
     * Perform a clockwise turn of the U face.
     */
    public void moveU() {
        Cubie temp               = cornerCubies[CORNER_ULF];
        cornerCubies[CORNER_ULF] = cornerCubies[CORNER_URF];
        cornerCubies[CORNER_URF] = cornerCubies[CORNER_URB];
        cornerCubies[CORNER_URB] = cornerCubies[CORNER_ULB];
        cornerCubies[CORNER_ULB] = temp;

        temp                = edgeCubies[EDGE_UL];
        edgeCubies[EDGE_UL] = edgeCubies[EDGE_UF];
        edgeCubies[EDGE_UF] = edgeCubies[EDGE_UR];
        edgeCubies[EDGE_UR] = edgeCubies[EDGE_UB];
        edgeCubies[EDGE_UB] = temp;
    }

    /**
     * Perform a counter-clockwise turn of the U face.
     */
    public void moveUPrime() {
        Cubie temp               = cornerCubies[CORNER_ULB];
        cornerCubies[CORNER_ULB] = cornerCubies[CORNER_URB];
        cornerCubies[CORNER_URB] = cornerCubies[CORNER_URF];
        cornerCubies[CORNER_URF] = cornerCubies[CORNER_ULF];
        cornerCubies[CORNER_ULF] = temp;

        temp                = edgeCubies[EDGE_UB];
        edgeCubies[EDGE_UB] = edgeCubies[EDGE_UR];
        edgeCubies[EDGE_UR] = edgeCubies[EDGE_UF];
        edgeCubies[EDGE_UF] = edgeCubies[EDGE_UL];
        edgeCubies[EDGE_UL] = temp;
    }

    /**
     * Perform a double turn of the U face.
     */
    public void moveU2() {
        Cubie temp               = cornerCubies[CORNER_URF];
        cornerCubies[CORNER_URF] = cornerCubies[CORNER_ULB];
        cornerCubies[CORNER_ULB] = temp;

        temp                     = cornerCubies[CORNER_ULF];
        cornerCubies[CORNER_ULF] = cornerCubies[CORNER_URB];
        cornerCubies[CORNER_URB] = temp;

        temp                = edgeCubies[EDGE_UF];
        edgeCubies[EDGE_UF] = edgeCubies[EDGE_UB];
        edgeCubies[EDGE_UB] = temp;

        temp                = edgeCubies[EDGE_UL];
        edgeCubies[EDGE_UL] = edgeCubies[EDGE_UR];
        edgeCubies[EDGE_UR] = temp;
    }

    /**
     * Perform a clockwise turn of the L face.
     */
    public void moveL() {
        Cubie temp               = cornerCubies[CORNER_DLB];
        cornerCubies[CORNER_DLB] = cornerCubies[CORNER_DLF];
        cornerCubies[CORNER_DLF] = cornerCubies[CORNER_ULF];
        cornerCubies[CORNER_ULF] = cornerCubies[CORNER_ULB];
        cornerCubies[CORNER_ULB] = temp;

        temp                = edgeCubies[EDGE_BL];
        edgeCubies[EDGE_BL] = edgeCubies[EDGE_DL];
        edgeCubies[EDGE_DL] = edgeCubies[EDGE_FL];
        edgeCubies[EDGE_FL] = edgeCubies[EDGE_UL];
        edgeCubies[EDGE_UL] = temp;

        increaseCornerOrientation(CORNER_DLB, (byte) 1);
        increaseCornerOrientation(CORNER_DLF, (byte) 2);
        increaseCornerOrientation(CORNER_ULF, (byte) 1);
        increaseCornerOrientation(CORNER_ULB, (byte) 2);
    }

    /**
     * Perform a counter-clockwise turn of the L face.
     */
    public void moveLPrime() {
        Cubie temp               = cornerCubies[CORNER_DLB];
        cornerCubies[CORNER_DLB] = cornerCubies[CORNER_ULB];
        cornerCubies[CORNER_ULB] = cornerCubies[CORNER_ULF];
        cornerCubies[CORNER_ULF] = cornerCubies[CORNER_DLF];
        cornerCubies[CORNER_DLF] = temp;

        temp                = edgeCubies[EDGE_BL];
        edgeCubies[EDGE_BL] = edgeCubies[EDGE_UL];
        edgeCubies[EDGE_UL] = edgeCubies[EDGE_FL];
        edgeCubies[EDGE_FL] = edgeCubies[EDGE_DL];
        edgeCubies[EDGE_DL] = temp;

        increaseCornerOrientation(CORNER_DLB, (byte) 1);
        increaseCornerOrientation(CORNER_DLF, (byte) 2);
        increaseCornerOrientation(CORNER_ULF, (byte) 1);
        increaseCornerOrientation(CORNER_ULB, (byte) 2);
    }

    /**
     * Perform a double turn of the L face.
     */
    public void moveL2() {
        Cubie temp               = cornerCubies[CORNER_ULF];
        cornerCubies[CORNER_ULF] = cornerCubies[CORNER_DLB];
        cornerCubies[CORNER_DLB] = temp;

        temp                     = cornerCubies[CORNER_DLF];
        cornerCubies[CORNER_DLF] = cornerCubies[CORNER_ULB];
        cornerCubies[CORNER_ULB] = temp;

        temp                = edgeCubies[EDGE_FL];
        edgeCubies[EDGE_FL] = edgeCubies[EDGE_BL];
        edgeCubies[EDGE_BL] = temp;

        temp                = edgeCubies[EDGE_DL];
        edgeCubies[EDGE_DL] = edgeCubies[EDGE_UL];
        edgeCubies[EDGE_UL] = temp;
    }

    /**
     * Perform a clockwise turn of the F face.
     */
    public void moveF() {
        Cubie temp               = cornerCubies[CORNER_ULF];
        cornerCubies[CORNER_ULF] = cornerCubies[CORNER_DLF];
        cornerCubies[CORNER_DLF] = cornerCubies[CORNER_DRF];
        cornerCubies[CORNER_DRF] = cornerCubies[CORNER_URF];
        cornerCubies[CORNER_URF] = temp;

        temp                = edgeCubies[EDGE_UF];
        edgeCubies[EDGE_UF] = edgeCubies[EDGE_FL];
        edgeCubies[EDGE_FL] = edgeCubies[EDGE_DF];
        edgeCubies[EDGE_DF] = edgeCubies[EDGE_FR];
        edgeCubies[EDGE_FR] = temp;

        increaseCornerOrientation(CORNER_ULF, (byte) 2);
        increaseCornerOrientation(CORNER_URF, (byte) 1);
        increaseCornerOrientation(CORNER_DRF, (byte) 2);
        increaseCornerOrientation(CORNER_DLF, (byte) 1);

        flipEdgeOrientation(EDGE_UF);
        flipEdgeOrientation(EDGE_FL);
        flipEdgeOrientation(EDGE_DF);
        flipEdgeOrientation(EDGE_FR);
    }

    /**
     * Perform a counter-clockwise turn of the F face.
     */
    public void moveFPrime() {
        Cubie temp               = cornerCubies[CORNER_ULF];
        cornerCubies[CORNER_ULF] = cornerCubies[CORNER_URF];
        cornerCubies[CORNER_URF] = cornerCubies[CORNER_DRF];
        cornerCubies[CORNER_DRF] = cornerCubies[CORNER_DLF];
        cornerCubies[CORNER_DLF] = temp;

        temp                = edgeCubies[EDGE_UF];
        edgeCubies[EDGE_UF] = edgeCubies[EDGE_FR];
        edgeCubies[EDGE_FR] = edgeCubies[EDGE_DF];
        edgeCubies[EDGE_DF] = edgeCubies[EDGE_FL];
        edgeCubies[EDGE_FL] = temp;

        increaseCornerOrientation(CORNER_ULF, (byte) 2);
        increaseCornerOrientation(CORNER_URF, (byte) 1);
        increaseCornerOrientation(CORNER_DRF, (byte) 2);
        increaseCornerOrientation(CORNER_DLF, (byte) 1);

        flipEdgeOrientation(EDGE_UF);
        flipEdgeOrientation(EDGE_FL);
        flipEdgeOrientation(EDGE_DF);
        flipEdgeOrientation(EDGE_FR);
    }

    /**
     * Perform a double turn of the F face.
     */
    public void moveF2() {
        Cubie temp               = cornerCubies[CORNER_DRF];
        cornerCubies[CORNER_DRF] = cornerCubies[CORNER_ULF];
        cornerCubies[CORNER_ULF] = temp;

        temp                     = cornerCubies[CORNER_DLF];
        cornerCubies[CORNER_DLF] = cornerCubies[CORNER_URF];
        cornerCubies[CORNER_URF] = temp;

        temp                = edgeCubies[EDGE_DF];
        edgeCubies[EDGE_DF] = edgeCubies[EDGE_UF];
        edgeCubies[EDGE_UF] = temp;

        temp                = edgeCubies[EDGE_FR];
        edgeCubies[EDGE_FR] = edgeCubies[EDGE_FL];
        edgeCubies[EDGE_FL] = temp;
    }

    /**
     * Perform a clockwise turn of the R face.
     */
    public void moveR() {
        Cubie temp               = cornerCubies[CORNER_DRB];
        cornerCubies[CORNER_DRB] = cornerCubies[CORNER_URB];
        cornerCubies[CORNER_URB] = cornerCubies[CORNER_URF];
        cornerCubies[CORNER_URF] = cornerCubies[CORNER_DRF];
        cornerCubies[CORNER_DRF] = temp;

        temp                = edgeCubies[EDGE_BR];
        edgeCubies[EDGE_BR] = edgeCubies[EDGE_UR];
        edgeCubies[EDGE_UR] = edgeCubies[EDGE_FR];
        edgeCubies[EDGE_FR] = edgeCubies[EDGE_DR];
        edgeCubies[EDGE_DR] = temp;

        increaseCornerOrientation(CORNER_DRB, (byte) 2);
        increaseCornerOrientation(CORNER_DRF, (byte) 1);
        increaseCornerOrientation(CORNER_URF, (byte) 2);
        increaseCornerOrientation(CORNER_URB, (byte) 1);
    }

    /**
     * Perform a counter-clockwise turn of the R face.
     */
    public void moveRPrime() {
        Cubie temp               = cornerCubies[CORNER_DRB];
        cornerCubies[CORNER_DRB] = cornerCubies[CORNER_DRF];
        cornerCubies[CORNER_DRF] = cornerCubies[CORNER_URF];
        cornerCubies[CORNER_URF] = cornerCubies[CORNER_URB];
        cornerCubies[CORNER_URB] = temp;

        temp                = edgeCubies[EDGE_BR];
        edgeCubies[EDGE_BR] = edgeCubies[EDGE_DR];
        edgeCubies[EDGE_DR] = edgeCubies[EDGE_FR];
        edgeCubies[EDGE_FR] = edgeCubies[EDGE_UR];
        edgeCubies[EDGE_UR] = temp;

        increaseCornerOrientation(CORNER_DRB, (byte) 2);
        increaseCornerOrientation(CORNER_DRF, (byte) 1);
        increaseCornerOrientation(CORNER_URF, (byte) 2);
        increaseCornerOrientation(CORNER_URB, (byte) 1);
    }

    /**
     * Perform a double turn of the R face.
     */
    public void moveR2() {
        Cubie temp               = cornerCubies[CORNER_URF];
        cornerCubies[CORNER_URF] = cornerCubies[CORNER_DRB];
        cornerCubies[CORNER_DRB] = temp;

        temp                     = cornerCubies[CORNER_DRF];
        cornerCubies[CORNER_DRF] = cornerCubies[CORNER_URB];
        cornerCubies[CORNER_URB] = temp;

        temp                = edgeCubies[EDGE_FR];
        edgeCubies[EDGE_FR] = edgeCubies[EDGE_BR];
        edgeCubies[EDGE_BR] = temp;

        temp                = edgeCubies[EDGE_DR];
        edgeCubies[EDGE_DR] = edgeCubies[EDGE_UR];
        edgeCubies[EDGE_UR] = temp;
    }

    /**
     * Perform a clockwise turn of the B face.
     */
    public void moveB() {
        Cubie temp               = cornerCubies[CORNER_ULB];
        cornerCubies[CORNER_ULB] = cornerCubies[CORNER_URB];
        cornerCubies[CORNER_URB] = cornerCubies[CORNER_DRB];
        cornerCubies[CORNER_DRB] = cornerCubies[CORNER_DLB];
        cornerCubies[CORNER_DLB] = temp;

        temp                = edgeCubies[EDGE_UB];
        edgeCubies[EDGE_UB] = edgeCubies[EDGE_BR];
        edgeCubies[EDGE_BR] = edgeCubies[EDGE_DB];
        edgeCubies[EDGE_DB] = edgeCubies[EDGE_BL];
        edgeCubies[EDGE_BL] = temp;

        increaseCornerOrientation(CORNER_ULB, (byte) 1);
        increaseCornerOrientation(CORNER_URB, (byte) 2);
        increaseCornerOrientation(CORNER_DRB, (byte) 1);
        increaseCornerOrientation(CORNER_DLB, (byte) 2);

        flipEdgeOrientation(EDGE_UB);
        flipEdgeOrientation(EDGE_BL);
        flipEdgeOrientation(EDGE_DB);
        flipEdgeOrientation(EDGE_BR);
    }

    /**
     * Perform a counter-clockwise turn of the B face.
     */
    public void moveBPrime() {
        Cubie temp               = cornerCubies[CORNER_ULB];
        cornerCubies[CORNER_ULB] = cornerCubies[CORNER_DLB];
        cornerCubies[CORNER_DLB] = cornerCubies[CORNER_DRB];
        cornerCubies[CORNER_DRB] = cornerCubies[CORNER_URB];
        cornerCubies[CORNER_URB] = temp;

        temp                = edgeCubies[EDGE_UB];
        edgeCubies[EDGE_UB] = edgeCubies[EDGE_BL];
        edgeCubies[EDGE_BL] = edgeCubies[EDGE_DB];
        edgeCubies[EDGE_DB] = edgeCubies[EDGE_BR];
        edgeCubies[EDGE_BR] = temp;

        increaseCornerOrientation(CORNER_ULB, (byte) 1);
        increaseCornerOrientation(CORNER_URB, (byte) 2);
        increaseCornerOrientation(CORNER_DRB, (byte) 1);
        increaseCornerOrientation(CORNER_DLB, (byte) 2);

        flipEdgeOrientation(EDGE_UB);
        flipEdgeOrientation(EDGE_BL);
        flipEdgeOrientation(EDGE_DB);
        flipEdgeOrientation(EDGE_BR);
    }

    /**
     * Perform a double turn of the B face.
     */
    public void moveB2() {
        Cubie temp               = cornerCubies[CORNER_DRB];
        cornerCubies[CORNER_DRB] = cornerCubies[CORNER_ULB];
        cornerCubies[CORNER_ULB] = temp;

        temp                     = cornerCubies[CORNER_DLB];
        cornerCubies[CORNER_DLB] = cornerCubies[CORNER_URB];
        cornerCubies[CORNER_URB] = temp;

        temp                = edgeCubies[EDGE_DB];
        edgeCubies[EDGE_DB] = edgeCubies[EDGE_UB];
        edgeCubies[EDGE_UB] = temp;

        temp                = edgeCubies[EDGE_BR];
        edgeCubies[EDGE_BR] = edgeCubies[EDGE_BL];
        edgeCubies[EDGE_BL] = temp;
    }

    /**
     * Perform a clockwise turn of the D face.
     */
    public void moveD() {
        Cubie temp               = cornerCubies[CORNER_DLB];
        cornerCubies[CORNER_DLB] = cornerCubies[CORNER_DRB];
        cornerCubies[CORNER_DRB] = cornerCubies[CORNER_DRF];
        cornerCubies[CORNER_DRF] = cornerCubies[CORNER_DLF];
        cornerCubies[CORNER_DLF] = temp;

        temp                = edgeCubies[EDGE_DB];
        edgeCubies[EDGE_DB] = edgeCubies[EDGE_DR];
        edgeCubies[EDGE_DR] = edgeCubies[EDGE_DF];
        edgeCubies[EDGE_DF] = edgeCubies[EDGE_DL];
        edgeCubies[EDGE_DL] = temp;
    }

    /**
     * Perform a counter-clockwise turn of the D face.
     */
    public void moveDPrime() {
        Cubie temp               = cornerCubies[CORNER_DLF];
        cornerCubies[CORNER_DLF] = cornerCubies[CORNER_DRF];
        cornerCubies[CORNER_DRF] = cornerCubies[CORNER_DRB];
        cornerCubies[CORNER_DRB] = cornerCubies[CORNER_DLB];
        cornerCubies[CORNER_DLB] = temp;

        temp                = edgeCubies[EDGE_DL];
        edgeCubies[EDGE_DL] = edgeCubies[EDGE_DF];
        edgeCubies[EDGE_DF] = edgeCubies[EDGE_DR];
        edgeCubies[EDGE_DR] = edgeCubies[EDGE_DB];
        edgeCubies[EDGE_DB] = temp;
    }

    /**
     * Perform a double turn of the D face.
     */
    public void moveD2() {
        Cubie temp               = cornerCubies[CORNER_DRF];
        cornerCubies[CORNER_DRF] = cornerCubies[CORNER_DLB];
        cornerCubies[CORNER_DLB] = temp;

        temp                     = cornerCubies[CORNER_DLF];
        cornerCubies[CORNER_DLF] = cornerCubies[CORNER_DRB];
        cornerCubies[CORNER_DRB] = temp;

        temp                = edgeCubies[EDGE_DF];
        edgeCubies[EDGE_DF] = edgeCubies[EDGE_DB];
        edgeCubies[EDGE_DB] = temp;

        temp                = edgeCubies[EDGE_DL];
        edgeCubies[EDGE_DL] = edgeCubies[EDGE_DR];
        edgeCubies[EDGE_DR] = temp;
    }


    /**
     * Print the cube state in a human-readable format.
     */
    public void printCubeState() {
        
        // Print top face
        System.out.print("       ");
        System.out.print(getCornerColours(CORNER_ULB)[0] + " ");
        System.out.print(getEdgeColours(EDGE_UB)[0] + " ");
        System.out.println(getCornerColours(CORNER_URB)[0] + " ");

        System.out.print("       ");
        System.out.print(getEdgeColours(EDGE_UL)[0] + " ");
        System.out.print("W ");
        System.out.println(getEdgeColours(EDGE_UR)[0] + " ");

        System.out.print("       ");
        System.out.print(getCornerColours(CORNER_ULF)[0] + " ");
        System.out.print(getEdgeColours(EDGE_UF)[0] + " ");
        System.out.println(getCornerColours(CORNER_URF)[0] + "\n");

        // Print upper row of side faces
        System.out.print(getCornerColours(CORNER_ULB)[1] + " ");
        System.out.print(getEdgeColours(EDGE_UL)[1] + " ");
        System.out.print(getCornerColours(CORNER_ULF)[1] + "  ");

        System.out.print(getCornerColours(CORNER_ULF)[2] + " ");
        System.out.print(getEdgeColours(EDGE_UF)[1] + " ");
        System.out.print(getCornerColours(CORNER_URF)[2] + "  ");

        System.out.print(getCornerColours(CORNER_URF)[1] + " ");
        System.out.print(getEdgeColours(EDGE_UR)[1] + " ");
        System.out.print(getCornerColours(CORNER_URB)[1] + "  ");

        System.out.print(getCornerColours(CORNER_URB)[2] + " ");
        System.out.print(getEdgeColours(EDGE_UB)[1] + " ");
        System.out.println(getCornerColours(CORNER_ULB)[2]);

        // Print middle row of side faces
        System.out.print(getEdgeColours(EDGE_BL)[1] + " ");
        System.out.print("G ");
        System.out.print(getEdgeColours(EDGE_FL)[1] + "  ");

        System.out.print(getEdgeColours(EDGE_FL)[0] + " ");
        System.out.print("R ");
        System.out.print(getEdgeColours(EDGE_FR)[0] + "  ");
        
        System.out.print(getEdgeColours(EDGE_FR)[1] + " ");
        System.out.print("B ");
        System.out.print(getEdgeColours(EDGE_BR)[1] + "  ");

        System.out.print(getEdgeColours(EDGE_BR)[0] + " ");
        System.out.print("O ");
        System.out.println(getEdgeColours(EDGE_BL)[0]);

        // Print bottom row of side faces
        System.out.print(getCornerColours(CORNER_DLB)[1] + " ");
        System.out.print(getEdgeColours(EDGE_DL)[1] + " ");
        System.out.print(getCornerColours(CORNER_DLF)[1] + "  ");

        System.out.print(getCornerColours(CORNER_DLF)[2] + " ");
        System.out.print(getEdgeColours(EDGE_DF)[1] + " ");
        System.out.print(getCornerColours(CORNER_DRF)[2] + "  ");

        System.out.print(getCornerColours(CORNER_DRF)[1] + " ");
        System.out.print(getEdgeColours(EDGE_DR)[1] + " ");
        System.out.print(getCornerColours(CORNER_DRB)[1] + "  ");

        System.out.print(getCornerColours(CORNER_DRB)[2] + " ");
        System.out.print(getEdgeColours(EDGE_DB)[1] + " ");
        System.out.println(getCornerColours(CORNER_DLB)[2] + "\n");

        // Print bottom face
        System.out.print("       ");
        System.out.print(getCornerColours(CORNER_DLF)[0] + " ");
        System.out.print(getEdgeColours(EDGE_DF)[0] + " ");
        System.out.println(getCornerColours(CORNER_DRF)[0] + " ");

        System.out.print("       ");
        System.out.print(getEdgeColours(EDGE_DL)[0] + " ");
        System.out.print("Y ");
        System.out.println(getEdgeColours(EDGE_DR)[0] + " ");

        System.out.print("       ");
        System.out.print(getCornerColours(CORNER_DLB)[0] + " ");
        System.out.print(getEdgeColours(EDGE_DB)[0] + " ");
        System.out.println(getCornerColours(CORNER_DRB)[0]);
        System.out.println("\n");
    }

    /**
     * Print an editable version of the cube state in a human-readable format.
     * @param index - The index of the cubie face to highlight for editing. Set to -1 to not highlight any face.
     */
    public static void printEditCubeState(Colour[] colours, int index) {
        
        // Print top face
        System.out.print("       ");
        System.out.print((index == 0 ? "_" : colours[0].name()) + " ");
        System.out.print((index == 1 ? "_" : colours[1].name()) + " ");
        System.out.println((index == 2 ? "_" : colours[2].name()) + " ");

        System.out.print("       ");
        System.out.print((index == 3 ? "_" : colours[3].name()) + " ");
        System.out.print("W ");
        System.out.println((index == 4 ? "_" : colours[4].name()) + " ");

        System.out.print("       ");
        System.out.print((index == 5 ? "_" : colours[5].name()) + " ");
        System.out.print((index == 6 ? "_" : colours[6].name()) + " ");
        System.out.println((index == 7 ? "_" : colours[7].name()) + "\n");

        // Print upper row of side faces
        System.out.print((index == 8 ? "_" : colours[8].name()) + " ");
        System.out.print((index == 9 ? "_" : colours[9].name()) + " ");
        System.out.print((index == 10 ? "_" : colours[10].name()) + "  ");

        System.out.print((index == 11 ? "_" : colours[11].name()) + " ");
        System.out.print((index == 12 ? "_" : colours[12].name()) + " ");
        System.out.print((index == 13 ? "_" : colours[13].name()) + "  ");

        System.out.print((index == 14 ? "_" : colours[14].name()) + " ");
        System.out.print((index == 15 ? "_" : colours[15].name()) + " ");
        System.out.print((index == 16 ? "_" : colours[16].name()) + "  ");

        System.out.print((index == 17 ? "_" : colours[17].name()) + " ");
        System.out.print((index == 18 ? "_" : colours[18].name()) + " ");
        System.out.println((index == 19 ? "_" : colours[19].name()));

        // Print middle row of side faces
        System.out.print((index == 20 ? "_" : colours[20].name()) + " ");
        System.out.print("G ");
        System.out.print((index == 21 ? "_" : colours[21].name()) + "  ");

        System.out.print((index == 22 ? "_" : colours[22].name()) + " ");
        System.out.print("R ");
        System.out.print((index == 23 ? "_" : colours[23].name()) + "  ");
        
        System.out.print((index == 24 ? "_" : colours[24].name()) + " ");
        System.out.print("B ");
        System.out.print((index == 25 ? "_" : colours[25].name()) + "  ");

        System.out.print((index == 26 ? "_" : colours[26].name()) + " ");
        System.out.print("O ");
        System.out.println((index == 27 ? "_" : colours[27].name()));

        // Print bottom row of side faces
        System.out.print((index == 28 ? "_" : colours[28].name()) + " ");
        System.out.print((index == 29 ? "_" : colours[29].name()) + " ");
        System.out.print((index == 30 ? "_" : colours[30].name()) + "  ");

        System.out.print((index == 31 ? "_" : colours[31].name()) + " ");
        System.out.print((index == 32 ? "_" : colours[32].name()) + " ");
        System.out.print((index == 33 ? "_" : colours[33].name()) + "  ");

        System.out.print((index == 34 ? "_" : colours[34].name()) + " ");
        System.out.print((index == 35 ? "_" : colours[35].name()) + " ");
        System.out.print((index == 36 ? "_" : colours[36].name()) + "  ");

        System.out.print((index == 37 ? "_" : colours[37].name()) + " ");
        System.out.print((index == 38 ? "_" : colours[38].name()) + " ");
        System.out.println((index == 39 ? "_" : colours[39].name()) + "\n");

        // Print bottom face
        System.out.print("       ");
        System.out.print((index == 40 ? "_" : colours[40].name()) + " ");
        System.out.print((index == 41 ? "_" : colours[41].name()) + " ");
        System.out.println((index == 42 ? "_" : colours[42].name()) + " ");

        System.out.print("       ");
        System.out.print((index == 43 ? "_" : colours[43].name()) + " ");
        System.out.print("Y ");
        System.out.println((index == 44 ? "_" : colours[44].name()) + " ");

        System.out.print("       ");
        System.out.print((index == 45 ? "_" : colours[45].name()) + " ");
        System.out.print((index == 46 ? "_" : colours[46].name()) + " ");
        System.out.println((index == 47 ? "_" : colours[47].name()));
        System.out.println("\n");
    }
}
