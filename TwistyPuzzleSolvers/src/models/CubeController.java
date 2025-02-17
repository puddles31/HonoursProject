package models;

import models.ITwistyPuzzle.Cubie;
import java.util.Random;

/**
 * This class contains methods which handle logic for making moves on a Rubik's Cube.
 */
public class CubeController implements IMoveController {
    
    private Cube cube;

    /**
     * Moves that can be made on the Rubik's Cube.
     */
    public static enum Move implements IMove {
        U, UPRIME, U2,
        L, LPRIME, L2,
        F, FPRIME, F2,
        R, RPRIME, R2,
        B, BPRIME, B2,
        D, DPRIME, D2;

        // Set the inverse of each move
        static {
            U.setInverse(UPRIME); UPRIME.setInverse(U); U2.setInverse(U2);
            L.setInverse(LPRIME); LPRIME.setInverse(L); L2.setInverse(L2);
            F.setInverse(FPRIME); FPRIME.setInverse(F); F2.setInverse(F2);
            R.setInverse(RPRIME); RPRIME.setInverse(R); R2.setInverse(R2);
            B.setInverse(BPRIME); BPRIME.setInverse(B); B2.setInverse(B2);
            D.setInverse(DPRIME); DPRIME.setInverse(D); D2.setInverse(D2);
        }

        private Move inverse;

        /**
         * Set the inverse of the move.
         * @param inverse - The inverse of the move.
         */
        private void setInverse(Move inverse) {
            this.inverse = inverse;
        }

        /**
         * Get the inverse of the move (e.g. UPRIME is the inverse of U).
         * @return The inverse of the move.
         */
        public Move getInverse() {
            return inverse;
        }

        /**
         * Get the base move of the move (e.g. U is the base move of UPRIME).
         * @return The base move of the move.
         */
        public Move getBaseMove() {
            switch (toString().substring(0, 1)) {
                case "U":  return Move.U;
                case "L":  return Move.L;
                case "F":  return Move.F;
                case "R":  return Move.R;
                case "B":  return Move.B;
                case "D":  return Move.D;
                default:   return null;
            }
        }

        /**
         * Return the String representation of the move (replaces PRIME with ').
         * @returns The String representation of the move.
        */
        public String toString() {
            return name().replace("PRIME", "'");
        }
    }

    /**
     * Get the array of valid moves for the Rubik's Cube.
     * @return The array of valid moves.
     */
    public Move[] getMoves() {
        return Move.values();
    }

    /**
     * Get a Move object from a name of the move.
     * @param moveName - The name of the move.
     * @return The Move object corresponding to the move name.
     */
    public Move parseMove(String moveName) {
        switch (moveName) {
            case "U":  return Move.U;
            case "U'": return Move.UPRIME;
            case "U2": return Move.U2;
            case "L":  return Move.L;
            case "L'": return Move.LPRIME;
            case "L2": return Move.L2;
            case "F":  return Move.F;
            case "F'": return Move.FPRIME;
            case "F2": return Move.F2;
            case "R":  return Move.R;
            case "R'": return Move.RPRIME;
            case "R2": return Move.R2;
            case "B":  return Move.B;
            case "B'": return Move.BPRIME;
            case "B2": return Move.B2;
            case "D":  return Move.D;
            case "D'": return Move.DPRIME;
            case "D2": return Move.D2;
            default:   return null;
        }
    }
    

    /**
     * Constructor for a CubeController object.
     * @param cube - The Rubik's Cube instance to control.
     */
    public CubeController(Cube cube) {
        this.cube = cube;
    }

    /**
     * Make a move on the Rubik's Cube.
     * @param move - The move to make.
     * @throws IllegalArgumentException if the move is not a valid Rubik's Cube move.
     */
    public void makeMove(IMove move) throws IllegalArgumentException {
        if (!(move instanceof Move)) {
            throw new IllegalArgumentException("The move must be a Cube move.");
        }
        Move cubeMove = (Move) move;

        switch (cubeMove) {
            case U:
                moveU(); break;
            case UPRIME:
                moveUPrime(); break;
            case U2:
                moveU2(); break;
            
            case L:
                moveL(); break;
            case LPRIME:
                moveLPrime(); break;
            case L2:
                moveL2(); break;

            case F:
                moveF(); break;
            case FPRIME:
                moveFPrime(); break;
            case F2:
                moveF2(); break;
            
            case R:
                moveR(); break;
            case RPRIME:
                moveRPrime(); break;
            case R2:
                moveR2(); break;
            
            case B:
                moveB(); break;
            case BPRIME:
                moveBPrime(); break;
            case B2:
                moveB2(); break;
            
            case D:
                moveD(); break;
            case DPRIME:
                moveDPrime(); break;
            case D2:
                moveD2(); break;
            
            default:
                break;
        }
    }

    /**
     * Undo a move on the Rubik's Cube by performing the inverse move (e.g. L' is the inverse of L).
     * @param move - The move to undo.
     * @throws IllegalArgumentException if the move is not a valid Rubik's Cube move.
     */
    public void undoMove(IMove move) throws IllegalArgumentException {
        if (!(move instanceof Move)) {
            throw new IllegalArgumentException("The move must be a Cube move.");
        }
        Move cubeMove = (Move) move;

        makeMove(cubeMove.getInverse());
    }

    /**
     * Determine if a move should be skipped based on the last move.
     * Moves are skipped if they are on the same face as the last move, or if they are on opposite faces in specific orders ([R, L], [D, U], [B, F]).
     * @param move - The move to check.
     * @param lastMove - The last move made.
     * @return {@code true} if the move should be skipped, {@code false} otherwise.
     * @throws IllegalArgumentException if the move or last move is not a valid Rubik's Cube move.
     */
    public boolean skipMove(IMove move, IMove lastMove) throws IllegalArgumentException {
        if (!(move instanceof Move) || !(lastMove instanceof Move)) {
            throw new IllegalArgumentException("Both moves must be Cube moves.");
        }
        Move baseMove = (Move) move.getBaseMove();
        Move lastBaseMove = (Move) lastMove.getBaseMove();

        // Skip moves that are on the same face as the last move (e.g. L, L2 = L')
        if (baseMove == lastBaseMove) {
            return true;
        }

        // Skip moves on opposite faces in specific orders (e.g. L, R = R, L; so prevent R, L)
        if (baseMove == Move.L && lastBaseMove == Move.R) {
            return true;
        }
        else if (baseMove == Move.U && lastBaseMove == Move.D) {
            return true;
        }
        else if (baseMove == Move.F && lastBaseMove == Move.B) {
            return true;
        }

        return false;
    }

    /**
     * Scramble the cube by making {@code noMoves} random moves, excluding moves that cancel previous moves.
     * @param noMoves - The number of moves to make.
     * @return An array of the moves made.
     */
    public Move[] scramble(int noMoves) {
        Random rand = new Random();
        Move[] moves = Move.values();
        Move[] scramble = new Move[noMoves];

        Move move, lastMove = null;
        
        for (int i = 0; i < noMoves; i++) {
            // Pick a random move, and check that it shouldn't be skipped
            do {
                move = moves[rand.nextInt(moves.length)];
            } while (i > 0 && skipMove(move, lastMove));
            
            makeMove(move);
            scramble[i] = move;

            lastMove = move;
        }
        return scramble;
    }


    /**
     * Rotate a set of cubies in the order specified by the position indices.
     * @param cubies - The array of cubies to edit (either {@code cornerCubies} or {@code edgeCubies}).
     * @param posIndices - An array of position indices in the order to rotate the cubies 
     * (e.g. [EDGE_UL, EDGE_UF, EDGE_UR, EDGE_UB] would rotate these cubies in this order, 
     * such that UL is replaced by UF, which is replaced by UR, etc.).
     */
    private void rotateCubies(Cubie[] cubies, byte[] posIndices) {
        Cubie temp = cubies[posIndices[0]];

        for (int i = 0; i < posIndices.length - 1; i++) {
            cubies[posIndices[i]] = cubies[posIndices[(i + 1)]];
        }
        cubies[posIndices[posIndices.length - 1]] = temp;
    }

    /**
     * Flip the orientation of an edge cubie.
     * @param posIndex - The position index of the edge cubie.
     */
    private void flipEdgeOrientation(byte posIndex) {
        Cubie edge = cube.edgeCubies[posIndex];
        edge.orientation ^= 1;  // XOR orientation with 1 results in flip between 0 and 1
    }

    /**
     * Increase the orientation of a corner cubie (modulo 3).
     * @param posIndex - The position index of the corner cubie.
     * @param incr - The amount to increase the orientation by.
     */
    private void increaseCornerOrientation(byte posIndex, byte incr) {
        Cubie corner = cube.cornerCubies[posIndex];

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
     * Perform a clockwise turn of the U face.
     */
    public void moveU() {
        rotateCubies(cube.cornerCubies, new byte[] {Cube.CORNER_ULF, Cube.CORNER_URF, Cube.CORNER_URB, Cube.CORNER_ULB});
        rotateCubies(cube.edgeCubies, new byte[] {Cube.EDGE_UL, Cube.EDGE_UF, Cube.EDGE_UR, Cube.EDGE_UB});
    }

    /**
     * Perform a counter-clockwise turn of the U face.
     */
    public void moveUPrime() {
        rotateCubies(cube.cornerCubies, new byte[] {Cube.CORNER_ULB, Cube.CORNER_URB, Cube.CORNER_URF, Cube.CORNER_ULF});
        rotateCubies(cube.edgeCubies, new byte[] {Cube.EDGE_UB, Cube.EDGE_UR, Cube.EDGE_UF, Cube.EDGE_UL});
    }

    /**
     * Perform a double turn of the U face.
     */
    public void moveU2() {
        rotateCubies(cube.cornerCubies, new byte[] {Cube.CORNER_URF, Cube.CORNER_ULB});
        rotateCubies(cube.cornerCubies, new byte[] {Cube.CORNER_ULF, Cube.CORNER_URB});
        rotateCubies(cube.edgeCubies, new byte[] {Cube.EDGE_UF, Cube.EDGE_UB});
        rotateCubies(cube.edgeCubies, new byte[] {Cube.EDGE_UL, Cube.EDGE_UR});
    }

    /**
     * Perform a clockwise turn of the L face.
     */
    public void moveL() {
        rotateCubies(cube.cornerCubies, new byte[] {Cube.CORNER_DLB, Cube.CORNER_DLF, Cube.CORNER_ULF, Cube.CORNER_ULB});
        rotateCubies(cube.edgeCubies, new byte[] {Cube.EDGE_BL, Cube.EDGE_DL, Cube.EDGE_FL, Cube.EDGE_UL});

        increaseCornerOrientation(Cube.CORNER_DLB, (byte) 1);
        increaseCornerOrientation(Cube.CORNER_DLF, (byte) 2);
        increaseCornerOrientation(Cube.CORNER_ULF, (byte) 1);
        increaseCornerOrientation(Cube.CORNER_ULB, (byte) 2);
    }

    /**
     * Perform a counter-clockwise turn of the L face.
     */
    public void moveLPrime() {
        rotateCubies(cube.cornerCubies, new byte[] {Cube.CORNER_DLB, Cube.CORNER_ULB, Cube.CORNER_ULF, Cube.CORNER_DLF});
        rotateCubies(cube.edgeCubies, new byte[] {Cube.EDGE_BL, Cube.EDGE_UL, Cube.EDGE_FL, Cube.EDGE_DL});

        increaseCornerOrientation(Cube.CORNER_DLB, (byte) 1);
        increaseCornerOrientation(Cube.CORNER_DLF, (byte) 2);
        increaseCornerOrientation(Cube.CORNER_ULF, (byte) 1);
        increaseCornerOrientation(Cube.CORNER_ULB, (byte) 2);
    }

    /**
     * Perform a double turn of the L face.
     */
    public void moveL2() {
        rotateCubies(cube.cornerCubies, new byte[] {Cube.CORNER_ULF, Cube.CORNER_DLB});
        rotateCubies(cube.cornerCubies, new byte[] {Cube.CORNER_DLF, Cube.CORNER_ULB});
        rotateCubies(cube.edgeCubies, new byte[] {Cube.EDGE_FL, Cube.EDGE_BL});
        rotateCubies(cube.edgeCubies, new byte[] {Cube.EDGE_DL, Cube.EDGE_UL});
    }

    /**
     * Perform a clockwise turn of the F face.
     */
    public void moveF() {
        rotateCubies(cube.cornerCubies, new byte[] {Cube.CORNER_ULF, Cube.CORNER_DLF, Cube.CORNER_DRF, Cube.CORNER_URF});
        rotateCubies(cube.edgeCubies, new byte[] {Cube.EDGE_UF, Cube.EDGE_FL, Cube.EDGE_DF, Cube.EDGE_FR});

        increaseCornerOrientation(Cube.CORNER_ULF, (byte) 2);
        increaseCornerOrientation(Cube.CORNER_URF, (byte) 1);
        increaseCornerOrientation(Cube.CORNER_DRF, (byte) 2);
        increaseCornerOrientation(Cube.CORNER_DLF, (byte) 1);

        flipEdgeOrientation(Cube.EDGE_UF);
        flipEdgeOrientation(Cube.EDGE_FL);
        flipEdgeOrientation(Cube.EDGE_DF);
        flipEdgeOrientation(Cube.EDGE_FR);
    }

    /**
     * Perform a counter-clockwise turn of the F face.
     */
    public void moveFPrime() {
        rotateCubies(cube.cornerCubies, new byte[] {Cube.CORNER_ULF, Cube.CORNER_URF, Cube.CORNER_DRF, Cube.CORNER_DLF});
        rotateCubies(cube.edgeCubies, new byte[] {Cube.EDGE_UF, Cube.EDGE_FR, Cube.EDGE_DF, Cube.EDGE_FL});

        increaseCornerOrientation(Cube.CORNER_ULF, (byte) 2);
        increaseCornerOrientation(Cube.CORNER_URF, (byte) 1);
        increaseCornerOrientation(Cube.CORNER_DRF, (byte) 2);
        increaseCornerOrientation(Cube.CORNER_DLF, (byte) 1);

        flipEdgeOrientation(Cube.EDGE_UF);
        flipEdgeOrientation(Cube.EDGE_FL);
        flipEdgeOrientation(Cube.EDGE_DF);
        flipEdgeOrientation(Cube.EDGE_FR);
    }

    /**
     * Perform a double turn of the F face.
     */
    public void moveF2() {
        rotateCubies(cube.cornerCubies, new byte[] {Cube.CORNER_DRF, Cube.CORNER_ULF});
        rotateCubies(cube.cornerCubies, new byte[] {Cube.CORNER_DLF, Cube.CORNER_URF});
        rotateCubies(cube.edgeCubies, new byte[] {Cube.EDGE_DF, Cube.EDGE_UF});
        rotateCubies(cube.edgeCubies, new byte[] {Cube.EDGE_FR, Cube.EDGE_FL});
    }

    /**
     * Perform a clockwise turn of the R face.
     */
    public void moveR() {
        rotateCubies(cube.cornerCubies, new byte[] {Cube.CORNER_DRB, Cube.CORNER_URB, Cube.CORNER_URF, Cube.CORNER_DRF});
        rotateCubies(cube.edgeCubies, new byte[] {Cube.EDGE_BR, Cube.EDGE_UR, Cube.EDGE_FR, Cube.EDGE_DR});

        increaseCornerOrientation(Cube.CORNER_DRB, (byte) 2);
        increaseCornerOrientation(Cube.CORNER_DRF, (byte) 1);
        increaseCornerOrientation(Cube.CORNER_URF, (byte) 2);
        increaseCornerOrientation(Cube.CORNER_URB, (byte) 1);
    }

    /**
     * Perform a counter-clockwise turn of the R face.
     */
    public void moveRPrime() {
        rotateCubies(cube.cornerCubies, new byte[] {Cube.CORNER_DRB, Cube.CORNER_DRF, Cube.CORNER_URF, Cube.CORNER_URB});
        rotateCubies(cube.edgeCubies, new byte[] {Cube.EDGE_BR, Cube.EDGE_DR, Cube.EDGE_FR, Cube.EDGE_UR});

        increaseCornerOrientation(Cube.CORNER_DRB, (byte) 2);
        increaseCornerOrientation(Cube.CORNER_DRF, (byte) 1);
        increaseCornerOrientation(Cube.CORNER_URF, (byte) 2);
        increaseCornerOrientation(Cube.CORNER_URB, (byte) 1);
    }

    /**
     * Perform a double turn of the R face.
     */
    public void moveR2() {
        rotateCubies(cube.cornerCubies, new byte[] {Cube.CORNER_URF, Cube.CORNER_DRB});
        rotateCubies(cube.cornerCubies, new byte[] {Cube.CORNER_DRF, Cube.CORNER_URB});
        rotateCubies(cube.edgeCubies, new byte[] {Cube.EDGE_FR, Cube.EDGE_BR});
        rotateCubies(cube.edgeCubies, new byte[] {Cube.EDGE_DR, Cube.EDGE_UR});
    }

    /**
     * Perform a clockwise turn of the B face.
     */
    public void moveB() {
        rotateCubies(cube.cornerCubies, new byte[] {Cube.CORNER_ULB, Cube.CORNER_URB, Cube.CORNER_DRB, Cube.CORNER_DLB});
        rotateCubies(cube.edgeCubies, new byte[] {Cube.EDGE_UB, Cube.EDGE_BR, Cube.EDGE_DB, Cube.EDGE_BL});

        increaseCornerOrientation(Cube.CORNER_ULB, (byte) 1);
        increaseCornerOrientation(Cube.CORNER_URB, (byte) 2);
        increaseCornerOrientation(Cube.CORNER_DRB, (byte) 1);
        increaseCornerOrientation(Cube.CORNER_DLB, (byte) 2);

        flipEdgeOrientation(Cube.EDGE_UB);
        flipEdgeOrientation(Cube.EDGE_BL);
        flipEdgeOrientation(Cube.EDGE_DB);
        flipEdgeOrientation(Cube.EDGE_BR);
    }

    /**
     * Perform a counter-clockwise turn of the B face.
     */
    public void moveBPrime() {
        rotateCubies(cube.cornerCubies, new byte[] {Cube.CORNER_ULB, Cube.CORNER_DLB, Cube.CORNER_DRB, Cube.CORNER_URB});
        rotateCubies(cube.edgeCubies, new byte[] {Cube.EDGE_UB, Cube.EDGE_BL, Cube.EDGE_DB, Cube.EDGE_BR});

        increaseCornerOrientation(Cube.CORNER_ULB, (byte) 1);
        increaseCornerOrientation(Cube.CORNER_URB, (byte) 2);
        increaseCornerOrientation(Cube.CORNER_DRB, (byte) 1);
        increaseCornerOrientation(Cube.CORNER_DLB, (byte) 2);

        flipEdgeOrientation(Cube.EDGE_UB);
        flipEdgeOrientation(Cube.EDGE_BL);
        flipEdgeOrientation(Cube.EDGE_DB);
        flipEdgeOrientation(Cube.EDGE_BR);
    }

    /**
     * Perform a double turn of the B face.
     */
    public void moveB2() {
        rotateCubies(cube.cornerCubies, new byte[] {Cube.CORNER_DRB, Cube.CORNER_ULB});
        rotateCubies(cube.cornerCubies, new byte[] {Cube.CORNER_DLB, Cube.CORNER_URB});
        rotateCubies(cube.edgeCubies, new byte[] {Cube.EDGE_DB, Cube.EDGE_UB});
        rotateCubies(cube.edgeCubies, new byte[] {Cube.EDGE_BR, Cube.EDGE_BL});
    }

    /**
     * Perform a clockwise turn of the D face.
     */
    public void moveD() {
        rotateCubies(cube.cornerCubies, new byte[] {Cube.CORNER_DLB, Cube.CORNER_DRB, Cube.CORNER_DRF, Cube.CORNER_DLF});
        rotateCubies(cube.edgeCubies, new byte[] {Cube.EDGE_DB, Cube.EDGE_DR, Cube.EDGE_DF, Cube.EDGE_DL});
    }

    /**
     * Perform a counter-clockwise turn of the D face.
     */
    public void moveDPrime() {
        rotateCubies(cube.cornerCubies, new byte[] {Cube.CORNER_DLF, Cube.CORNER_DRF, Cube.CORNER_DRB, Cube.CORNER_DLB});
        rotateCubies(cube.edgeCubies, new byte[] {Cube.EDGE_DL, Cube.EDGE_DF, Cube.EDGE_DR, Cube.EDGE_DB});
    }

    /**
     * Perform a double turn of the D face.
     */
    public void moveD2() {
        rotateCubies(cube.cornerCubies, new byte[] {Cube.CORNER_DRF, Cube.CORNER_DLB});
        rotateCubies(cube.cornerCubies, new byte[] {Cube.CORNER_DLF, Cube.CORNER_DRB});
        rotateCubies(cube.edgeCubies, new byte[] {Cube.EDGE_DF, Cube.EDGE_DB});
        rotateCubies(cube.edgeCubies, new byte[] {Cube.EDGE_DL, Cube.EDGE_DR});
    }
}
