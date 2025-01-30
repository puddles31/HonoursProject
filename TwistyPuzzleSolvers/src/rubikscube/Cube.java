package rubikscube;

import java.util.Random;

/**
 * A Rubik's Cube, represented as an array of edge cubies and an array corner cubies.
 * @see #Cube()
 * @see #Cubie
 * @see #CubeInteractive
 * @see #isSolved()
 */
public class Cube {

    /**
     * Colours on the Rubik's Cube (W, Y, R, O, G, B).
     */
    public static enum Colour {
        W, Y, R, O, G, B
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
        D, DPRIME, D2
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
     * Scramble the cube by making 10 random moves, excluding moves that cancel previous moves.
     */
    public void scramble() {
        Random rand = new Random();
        Move[] moves = Move.values();

        Move move, lastMove = null;

        System.out.println("Moves made during scramble:");
        
        for (int i = 0; i < 10; i++) {
            do {
                move = moves[rand.nextInt(moves.length)];
            } while (i > 0 && skipMove(move, lastMove));
            
            makeMove(move);
            System.out.print(move.name() + " ");

            lastMove = move;
        }
        System.out.println();
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
}
