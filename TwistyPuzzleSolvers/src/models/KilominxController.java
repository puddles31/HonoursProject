package models;

import models.ITwistyPuzzle.Cubie;
import java.util.Arrays;
import java.util.Random;

/**
 * This class contains methods which handle logic for making moves on a Kilominx.
 */
public class KilominxController implements IMoveController {
    
    private Kilominx kilominx;

    /**
     * Moves that can be made on the Kilominx.
     */
    public static enum Move implements IMove {
        U,   U_PRIME,   U_2,   U_2PRIME,
        L,   L_PRIME,   L_2,   L_2PRIME,
        F,   F_PRIME,   F_2,   F_2PRIME,
        R,   R_PRIME,   R_2,   R_2PRIME,
        BL,  BL_PRIME,  BL_2,  BL_2PRIME,
        BR,  BR_PRIME,  BR_2,  BR_2PRIME,
        DL,  DL_PRIME,  DL_2,  DL_2PRIME,
        DR,  DR_PRIME,  DR_2,  DR_2PRIME,
        DBL, DBL_PRIME, DBL_2, DBL_2PRIME,
        DBR, DBR_PRIME, DBR_2, DBR_2PRIME,
        DB,  DB_PRIME,  DB_2,  DB_2PRIME,
        D,   D_PRIME,   D_2,   D_2PRIME;

        // Set the inverse of each move
        static {
            U.setInverse(U_PRIME);     U_PRIME.setInverse(U);     U_2.setInverse(U_2PRIME);     U_2PRIME.setInverse(U_2);
            L.setInverse(L_PRIME);     L_PRIME.setInverse(L);     L_2.setInverse(L_2PRIME);     L_2PRIME.setInverse(L_2);
            F.setInverse(F_PRIME);     F_PRIME.setInverse(F);     F_2.setInverse(F_2PRIME);     F_2PRIME.setInverse(F_2);
            R.setInverse(R_PRIME);     R_PRIME.setInverse(R);     R_2.setInverse(R_2PRIME);     R_2PRIME.setInverse(R_2);
            BL.setInverse(BL_PRIME);   BL_PRIME.setInverse(BL);   BL_2.setInverse(BL_2PRIME);   BL_2PRIME.setInverse(BL_2);
            BR.setInverse(BR_PRIME);   BR_PRIME.setInverse(BR);   BR_2.setInverse(BR_2PRIME);   BR_2PRIME.setInverse(BR_2);
            DL.setInverse(DL_PRIME);   DL_PRIME.setInverse(DL);   DL_2.setInverse(DL_2PRIME);   DL_2PRIME.setInverse(DL_2);
            DR.setInverse(DR_PRIME);   DR_PRIME.setInverse(DR);   DR_2.setInverse(DR_2PRIME);   DR_2PRIME.setInverse(DR_2);
            DBL.setInverse(DBL_PRIME); DBL_PRIME.setInverse(DBL); DBL_2.setInverse(DBL_2PRIME); DBL_2PRIME.setInverse(DBL_2);
            DBR.setInverse(DBR_PRIME); DBR_PRIME.setInverse(DBR); DBR_2.setInverse(DBR_2PRIME); DBR_2PRIME.setInverse(DBR_2);
            DB.setInverse(DB_PRIME);   DB_PRIME.setInverse(DB);   DB_2.setInverse(DB_2PRIME);   DB_2PRIME.setInverse(DB_2);
            D.setInverse(D_PRIME);     D_PRIME.setInverse(D);     D_2.setInverse(D_2PRIME);     D_2PRIME.setInverse(D_2);
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
         * Get the inverse of the move (e.g. U_2 is the inverse of U_2PRIME).
         * @return The inverse of the move.
         */
        public Move getInverse() {
            return inverse;
        }

        /**
         * Get the base move of the move (e.g. U is the base move of U_2PRIME).
         * @return The base move of the move.
         */
        public Move getBaseMove() {
            switch (toString().replace("'", "").replace("2", "")) {
                case "U":    return Move.U;
                case "L":    return Move.L;
                case "F":    return Move.F;
                case "R":    return Move.R;
                case "BL":   return Move.BL;
                case "BR":   return Move.BR;
                case "DL":   return Move.DL;
                case "DR":   return Move.DR;
                case "DBL":  return Move.DBL;
                case "DBR":  return Move.DBR;
                case "DB":   return Move.DB;
                case "D":    return Move.D;
                default:     return null;
            }
        }

        /**
         * Return the String representation of the move (replaces PRIME with ', and removes underscores).
         * @return The String representation of the move.
         */
        public String toString() {
            return name().replace("PRIME", "'").replace("_", "");
        }
    }

    /**
     * Get the array of valid moves for the kilominx.
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
            case "U":    return Move.U;       case "U'":    return Move.U_PRIME;
            case "U2":   return Move.U_2;     case "U2'":   return Move.U_2PRIME;
            case "L":    return Move.L;       case "L'":    return Move.L_PRIME;
            case "L2":   return Move.L_2;     case "L2'":   return Move.L_2PRIME;
            case "F":    return Move.F;       case "F'":    return Move.F_PRIME;
            case "F2":   return Move.F_2;     case "F2'":   return Move.F_2PRIME;
            case "R":    return Move.R;       case "R'":    return Move.R_PRIME;
            case "R2":   return Move.R_2;     case "R2'":   return Move.R_2PRIME;
            case "BL":   return Move.BL;      case "BL'":   return Move.BL_PRIME;
            case "BL2":  return Move.BL_2;    case "BL2'":  return Move.BL_2PRIME;
            case "BR":   return Move.BR;      case "BR'":   return Move.BR_PRIME;
            case "BR2":  return Move.BR_2;    case "BR2'":  return Move.BR_2PRIME;
            case "DL":   return Move.DL;      case "DL'":   return Move.DL_PRIME;
            case "DL2":  return Move.DL_2;    case "DL2'":  return Move.DL_2PRIME;
            case "DR":   return Move.DR;      case "DR'":   return Move.DR_PRIME;
            case "DR2":  return Move.DR_2;    case "DR2'":  return Move.DR_2PRIME;
            case "DBL":  return Move.DBL;     case "DBL'":  return Move.DBL_PRIME;
            case "DBL2": return Move.DBL_2;   case "DBL2'": return Move.DBL_2PRIME;
            case "DBR":  return Move.DBR;     case "DBR'":  return Move.DBR_PRIME;
            case "DBR2": return Move.DBR_2;   case "DBR2'": return Move.DBR_2PRIME;
            case "DB":   return Move.DB;      case "DB'":   return Move.DB_PRIME;
            case "DB2":  return Move.DB_2;    case "DB2'":  return Move.DB_2PRIME;
            case "D":    return Move.D;       case "D'":    return Move.D_PRIME;
            case "D2":   return Move.D_2;     case "D2'":   return Move.D_2PRIME;
            default:     return null;
        }
    }


    /**
     * Constructor for a KilominxController object.
     * @param kilominx - The Kilominx instance to control.
     */
    public KilominxController(Kilominx kilominx) {
        this.kilominx = kilominx;
    }

    /**
     * Make a move on the Kilominx.
     * @param move - The move to make.
     * @throws IllegalArgumentException if the move is not a valid kilominx move.
     */
    public void makeMove(IMove move) throws IllegalArgumentException {
        if (!(move instanceof Move)) {
            throw new IllegalArgumentException("The move must be a Kilominx move.");
        }
        Move kilominxMove = (Move) move;

        switch (kilominxMove) {
            case U:        moveU();       break;
            case U_PRIME:  moveUPrime();  break;
            case U_2:      moveU2();      break;
            case U_2PRIME: moveU2Prime(); break;

            case L:        moveL();       break;
            case L_PRIME:  moveLPrime();  break;
            case L_2:      moveL2();      break;
            case L_2PRIME: moveL2Prime(); break;

            case F:        moveF();       break;
            case F_PRIME:  moveFPrime();  break;
            case F_2:      moveF2();      break;
            case F_2PRIME: moveF2Prime(); break;

            case R:        moveR();       break;
            case R_PRIME:  moveRPrime();  break;
            case R_2:      moveR2();      break;
            case R_2PRIME: moveR2Prime(); break;

            case BL:        moveBL();       break;
            case BL_PRIME:  moveBLPrime();  break;
            case BL_2:      moveBL2();      break;
            case BL_2PRIME: moveBL2Prime(); break;

            case BR:        moveBR();       break;
            case BR_PRIME:  moveBRPrime();  break;
            case BR_2:      moveBR2();      break;
            case BR_2PRIME: moveBR2Prime(); break;

            case DL:        moveDL();       break;
            case DL_PRIME:  moveDLPrime();  break;
            case DL_2:      moveDL2();      break;
            case DL_2PRIME: moveDL2Prime(); break;

            case DR:        moveDR();       break;
            case DR_PRIME:  moveDRPrime();  break;
            case DR_2:      moveDR2();      break;
            case DR_2PRIME: moveDR2Prime(); break;

            case DBL:        moveDBL();       break;
            case DBL_PRIME:  moveDBLPrime();  break;
            case DBL_2:      moveDBL2();      break;
            case DBL_2PRIME: moveDBL2Prime(); break;

            case DBR:        moveDBR();       break;
            case DBR_PRIME:  moveDBRPrime();  break;
            case DBR_2:      moveDBR2();      break;
            case DBR_2PRIME: moveDBR2Prime(); break;

            case DB:        moveDB();       break;
            case DB_PRIME:  moveDBPrime();  break;
            case DB_2:      moveDB2();      break;
            case DB_2PRIME: moveDB2Prime(); break;

            case D:        moveD();       break;
            case D_PRIME:  moveDPrime();  break;
            case D_2:      moveD2();      break;
            case D_2PRIME: moveD2Prime(); break;

            default: break;
        }
    }

    /**
     * Undo a move on the Kilominx by performing the inverse move (e.g. DR2' is the inverse of DR2).
     * @param move - The move to undo.
     * @throws IllegalArgumentException if the move is not a valid kilominx move.
     */
    public void undoMove(IMove move) throws IllegalArgumentException {
        if (!(move instanceof Move)) {
            throw new IllegalArgumentException("The move must be a Kilominx move.");
        }
        Move kilominxMove = (Move) move;

        makeMove(kilominxMove.getInverse());
    }

    /**
     * Determine if a move should be skipped based on the previous move.
     * Moves are skipped if they are on the same face as the last move, or if they are on non-adjacent faces and are not in the correct order.
     * @param move - The move to check.
     * @param lastMove - The previous move made.
     * @return {@code true} if the move should be skipped, {@code false} otherwise.
     * @throws IllegalArgumentException if the move or last move is not a valid kilominx move.
     */
    public boolean skipMove(IMove move, IMove lastMove) throws IllegalArgumentException {
        if (!(move instanceof Move) || !(lastMove instanceof Move)) {
            throw new IllegalArgumentException("Both moves must be Kilominx moves.");
        }     
        Move baseMove = (Move) move.getBaseMove();
        Move lastBaseMove = (Move) lastMove.getBaseMove();

        // Skip moves that are on the same face as the last move
        if (baseMove == lastBaseMove) {
            return true;
        }

        // Skip moves that are on non-adjacent faces to the last move, and are not in the correct order
        if (lastBaseMove == Move.R && 
            Arrays.stream(new Move[] {Move.L}).anyMatch(baseMove::equals)) {
            return true;
        }
        else if (lastBaseMove == Move.BL && 
            Arrays.stream(new Move[] {Move.F, Move.R}).anyMatch(baseMove::equals)) {
            return true;
        }
        else if (lastBaseMove == Move.BR && 
            Arrays.stream(new Move[] {Move.L, Move.F}).anyMatch(baseMove::equals)) {
            return true;
        }
        else if (lastBaseMove == Move.DL && 
            Arrays.stream(new Move[] {Move.U, Move.R, Move.BL, Move.BR}).anyMatch(baseMove::equals)) {
            return true;
        }
        else if (lastBaseMove == Move.DR && 
            Arrays.stream(new Move[] {Move.U, Move.L, Move.BL, Move.BR}).anyMatch(baseMove::equals)) {
            return true;
        }
        else if (lastBaseMove == Move.DBL && 
            Arrays.stream(new Move[] {Move.U, Move.F, Move.R, Move.BR, Move.DR}).anyMatch(baseMove::equals)) {
            return true;
        }
        else if (lastBaseMove == Move.DBR && 
            Arrays.stream(new Move[] {Move.U, Move.L, Move.F, Move.BL, Move.DL, Move.DBL}).anyMatch(baseMove::equals)) {
            return true;
        }
        else if (lastBaseMove == Move.DB && 
            Arrays.stream(new Move[] {Move.U, Move.L, Move.F, Move.R, Move.DL, Move.DR}).anyMatch(baseMove::equals)) {
            return true;
        }
        else if (lastBaseMove == Move.D && 
            Arrays.stream(new Move[] {Move.U, Move.L, Move.F, Move.R, Move.BL, Move.BR}).anyMatch(baseMove::equals)) {
            return true;
        }

        return false;
    }

    /**
     * Scramble the kilomix by making {@code noMoves} random moves, excluding moves that should be skipped.
     * @param noMoves - The number of random moves to make.
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
     * Rotate a set of kubies in the order specified by the position indices.
     * @param posIndices - An array of position indices in the order to rotate the kubies. The array should be of length 5. 
     *  (e.g. [KUBIE_UFL, KUBIE_UFR, KUBIE_UBR, KUBIE_UBM, KUBIE_UBL] would rotate these kubies in this order, 
     *  such that UFL is replaced by UFR, which is replaced by UBM, etc.).
     */
    private void rotateKubies(byte[] posIndices) {
        Cubie temp = kilominx.kubies[posIndices[0]];

        for (int i = 0; i < posIndices.length - 1; i++) {
            kilominx.kubies[posIndices[i]] = kilominx.kubies[posIndices[(i + 1)]];
        }
        kilominx.kubies[posIndices[posIndices.length - 1]] = temp;
    }

    /**
     * Increase the orientation of a kubie (modulo 3).
     * @param posIndex - The position index of the kubie.
     * @param incr - The amount to increase the orientation by.
     */
    private void increaseKubieOrientation(byte posIndex, byte incr) {
        Cubie kubie = kilominx.kubies[posIndex];

        kubie.orientation += incr;

        // faster equivalent to kubie.orientation = (kubie.orientation + incr) % 3
        if (kubie.orientation == 3) {
            kubie.orientation = 0;
        }
        else if (kubie.orientation == 4) {
            kubie.orientation = 1;
        }
    }


    /**
     * Perform a clockwise turn of the U face.
     */
    public void moveU() {
        rotateKubies(new byte[] {Kilominx.KUBIE_UFL, Kilominx.KUBIE_UFR, Kilominx.KUBIE_UBR, Kilominx.KUBIE_UBM, Kilominx.KUBIE_UBL});
    }

    /**
     * Perform a counter-clockwise turn of the U face.
     */
    public void moveUPrime() {
        rotateKubies(new byte[] {Kilominx.KUBIE_UFL, Kilominx.KUBIE_UBL, Kilominx.KUBIE_UBM, Kilominx.KUBIE_UBR, Kilominx.KUBIE_UFR});
    }

    /**
     * Perform two clockwise turns of the U face.
     */
    public void moveU2() {
        rotateKubies(new byte[] {Kilominx.KUBIE_UFL, Kilominx.KUBIE_UBR, Kilominx.KUBIE_UBL, Kilominx.KUBIE_UFR, Kilominx.KUBIE_UBM});
    }

    /**
     * Perform two counter-clockwise turns of the U face.
     */
    public void moveU2Prime() {
        rotateKubies(new byte[] {Kilominx.KUBIE_UFL, Kilominx.KUBIE_UBM, Kilominx.KUBIE_UFR, Kilominx.KUBIE_UBL, Kilominx.KUBIE_UBR});
    }

    /**
     * Perform a clockwise turn of the L face.
     */
    public void moveL() {
        rotateKubies(new byte[] {Kilominx.KUBIE_UBL, Kilominx.KUBIE_MBL, Kilominx.KUBIE_FLD, Kilominx.KUBIE_MFL, Kilominx.KUBIE_UFL});

        increaseKubieOrientation(Kilominx.KUBIE_UBL, (byte) 1);
        increaseKubieOrientation(Kilominx.KUBIE_UFL, (byte) 1);
        increaseKubieOrientation(Kilominx.KUBIE_MFL, (byte) 1);
    }

    /**
     * Perform a counter-clockwise turn of the L face.
     */
    public void moveLPrime() {
        rotateKubies(new byte[] {Kilominx.KUBIE_UBL, Kilominx.KUBIE_UFL, Kilominx.KUBIE_MFL, Kilominx.KUBIE_FLD, Kilominx.KUBIE_MBL});

        increaseKubieOrientation(Kilominx.KUBIE_UBL, (byte) 2);
        increaseKubieOrientation(Kilominx.KUBIE_UFL, (byte) 2);
        increaseKubieOrientation(Kilominx.KUBIE_MBL, (byte) 2);
    }

    /**
     * Perform two clockwise turns of the L face.
     */
    public void moveL2() {
        rotateKubies(new byte[] {Kilominx.KUBIE_UBL, Kilominx.KUBIE_FLD, Kilominx.KUBIE_UFL, Kilominx.KUBIE_MBL, Kilominx.KUBIE_MFL});

        increaseKubieOrientation(Kilominx.KUBIE_UBL, (byte) 1);
        increaseKubieOrientation(Kilominx.KUBIE_UFL, (byte) 2);
        increaseKubieOrientation(Kilominx.KUBIE_MFL, (byte) 2);
        increaseKubieOrientation(Kilominx.KUBIE_FLD, (byte) 1);
    }

    /**
     * Perform two counter-clockwise turns of the L face.
     */
    public void moveL2Prime() {
        rotateKubies(new byte[] {Kilominx.KUBIE_UBL, Kilominx.KUBIE_MFL, Kilominx.KUBIE_MBL, Kilominx.KUBIE_UFL, Kilominx.KUBIE_FLD});

        increaseKubieOrientation(Kilominx.KUBIE_UBL, (byte) 1);
        increaseKubieOrientation(Kilominx.KUBIE_UFL, (byte) 2);
        increaseKubieOrientation(Kilominx.KUBIE_MBL, (byte) 1);
        increaseKubieOrientation(Kilominx.KUBIE_FLD, (byte) 2);
    }

    /**
     * Perform a clockwise turn of the F face.
     */
    public void moveF() {
        rotateKubies(new byte[] {Kilominx.KUBIE_UFL, Kilominx.KUBIE_MFL, Kilominx.KUBIE_FMD, Kilominx.KUBIE_MFR, Kilominx.KUBIE_UFR});

        increaseKubieOrientation(Kilominx.KUBIE_UFR, (byte) 1);
        increaseKubieOrientation(Kilominx.KUBIE_MFR, (byte) 1);
        increaseKubieOrientation(Kilominx.KUBIE_MFL, (byte) 1);
    }

    /**
     * Perform a counter-clockwise turn of the F face.
     */
    public void moveFPrime() {
        rotateKubies(new byte[] {Kilominx.KUBIE_UFL, Kilominx.KUBIE_UFR, Kilominx.KUBIE_MFR, Kilominx.KUBIE_FMD, Kilominx.KUBIE_MFL});

        increaseKubieOrientation(Kilominx.KUBIE_UFL, (byte) 2);
        increaseKubieOrientation(Kilominx.KUBIE_UFR, (byte) 2);
        increaseKubieOrientation(Kilominx.KUBIE_FMD, (byte) 2);
    }

    /**
     * Perform two clockwise turns of the F face.
     */
    public void moveF2() {
        rotateKubies(new byte[] {Kilominx.KUBIE_UFL, Kilominx.KUBIE_FMD, Kilominx.KUBIE_UFR, Kilominx.KUBIE_MFL, Kilominx.KUBIE_MFR});
        
        increaseKubieOrientation(Kilominx.KUBIE_UFL, (byte) 1);
        increaseKubieOrientation(Kilominx.KUBIE_UFR, (byte) 1);
        increaseKubieOrientation(Kilominx.KUBIE_MFL, (byte) 1);
        increaseKubieOrientation(Kilominx.KUBIE_MFR, (byte) 2);
        increaseKubieOrientation(Kilominx.KUBIE_FMD, (byte) 1);
    }

    /**
     * Perform two counter-clockwise turns of the F face.
     */
    public void moveF2Prime() {
        rotateKubies(new byte[] {Kilominx.KUBIE_UFL, Kilominx.KUBIE_MFR, Kilominx.KUBIE_MFL, Kilominx.KUBIE_UFR, Kilominx.KUBIE_FMD});

        increaseKubieOrientation(Kilominx.KUBIE_UFL, (byte) 1);
        increaseKubieOrientation(Kilominx.KUBIE_UFR, (byte) 2);
        increaseKubieOrientation(Kilominx.KUBIE_MFL, (byte) 2);
        increaseKubieOrientation(Kilominx.KUBIE_MFR, (byte) 2);
        increaseKubieOrientation(Kilominx.KUBIE_FMD, (byte) 2);
    }

    /**
     * Perform a clockwise turn of the R face.
     */
    public void moveR() {
        rotateKubies(new byte[] {Kilominx.KUBIE_UFR, Kilominx.KUBIE_MFR, Kilominx.KUBIE_FRD, Kilominx.KUBIE_MBR, Kilominx.KUBIE_UBR});

        increaseKubieOrientation(Kilominx.KUBIE_UBR, (byte) 1);
        increaseKubieOrientation(Kilominx.KUBIE_MBR, (byte) 2);
        increaseKubieOrientation(Kilominx.KUBIE_MFR, (byte) 2);
        increaseKubieOrientation(Kilominx.KUBIE_FRD, (byte) 1);
    }

    /**
     * Perform a counter-clockwise turn of the R face.
     */
    public void moveRPrime() {
        rotateKubies(new byte[] {Kilominx.KUBIE_UFR, Kilominx.KUBIE_UBR, Kilominx.KUBIE_MBR, Kilominx.KUBIE_FRD, Kilominx.KUBIE_MFR});

        increaseKubieOrientation(Kilominx.KUBIE_UFR, (byte) 2);
        increaseKubieOrientation(Kilominx.KUBIE_UBR, (byte) 1);
        increaseKubieOrientation(Kilominx.KUBIE_MBR, (byte) 2);
        increaseKubieOrientation(Kilominx.KUBIE_FRD, (byte) 1);
    }

    /**
     * Perform two clockwise turns of the R face.
     */
    public void moveR2() {
        rotateKubies(new byte[] {Kilominx.KUBIE_UFR, Kilominx.KUBIE_FRD, Kilominx.KUBIE_UBR, Kilominx.KUBIE_MFR, Kilominx.KUBIE_MBR});

        increaseKubieOrientation(Kilominx.KUBIE_UFR, (byte) 2);
        increaseKubieOrientation(Kilominx.KUBIE_UBR, (byte) 1);
    }

    /**
     * Perform two counter-clockwise turns of the R face.
     */
    public void moveR2Prime() {
        rotateKubies(new byte[] {Kilominx.KUBIE_UFR, Kilominx.KUBIE_MBR, Kilominx.KUBIE_MFR, Kilominx.KUBIE_UBR, Kilominx.KUBIE_FRD});

        increaseKubieOrientation(Kilominx.KUBIE_MFR, (byte) 2);
        increaseKubieOrientation(Kilominx.KUBIE_FRD, (byte) 1);
    }

    /**
     * Perform a clockwise turn of the BL face.
     */
    public void moveBL() {
        rotateKubies(new byte[] {Kilominx.KUBIE_UBM, Kilominx.KUBIE_MBM, Kilominx.KUBIE_BLD, Kilominx.KUBIE_MBL, Kilominx.KUBIE_UBL});

        increaseKubieOrientation(Kilominx.KUBIE_UBL, (byte) 1);
        increaseKubieOrientation(Kilominx.KUBIE_UBM, (byte) 2);
        increaseKubieOrientation(Kilominx.KUBIE_MBM, (byte) 1);
        increaseKubieOrientation(Kilominx.KUBIE_BLD, (byte) 2);
    }

    /**
     * Perform a counter-clockwise turn of the BL face.
     */
    public void moveBLPrime() {
        rotateKubies(new byte[] {Kilominx.KUBIE_UBM, Kilominx.KUBIE_UBL, Kilominx.KUBIE_MBL, Kilominx.KUBIE_BLD, Kilominx.KUBIE_MBM});

        increaseKubieOrientation(Kilominx.KUBIE_MBL, (byte) 1);
        increaseKubieOrientation(Kilominx.KUBIE_UBM, (byte) 2);
        increaseKubieOrientation(Kilominx.KUBIE_MBM, (byte) 1);
        increaseKubieOrientation(Kilominx.KUBIE_BLD, (byte) 2);
    }

    /**
     * Perform two clockwise turns of the BL face.
     */
    public void moveBL2() {
        rotateKubies(new byte[] {Kilominx.KUBIE_UBM, Kilominx.KUBIE_BLD, Kilominx.KUBIE_UBL, Kilominx.KUBIE_MBM, Kilominx.KUBIE_MBL});

        increaseKubieOrientation(Kilominx.KUBIE_MBL, (byte) 1);
        increaseKubieOrientation(Kilominx.KUBIE_BLD, (byte) 2);
    }

    /**
     * Perform two counter-clockwise turns of the BL face.
     */
    public void moveBL2Prime() {
        rotateKubies(new byte[] {Kilominx.KUBIE_UBM, Kilominx.KUBIE_MBL, Kilominx.KUBIE_MBM, Kilominx.KUBIE_UBL, Kilominx.KUBIE_BLD});

        increaseKubieOrientation(Kilominx.KUBIE_UBL, (byte) 1);
        increaseKubieOrientation(Kilominx.KUBIE_UBM, (byte) 2);
    }

    /**
     * Perform a clockwise turn of the BR face.
     */
    public void moveBR() {
        rotateKubies(new byte[] {Kilominx.KUBIE_UBR, Kilominx.KUBIE_MBR, Kilominx.KUBIE_BRD, Kilominx.KUBIE_MBM, Kilominx.KUBIE_UBM});

        increaseKubieOrientation(Kilominx.KUBIE_MBR, (byte) 1);
        increaseKubieOrientation(Kilominx.KUBIE_UBR, (byte) 2);
        increaseKubieOrientation(Kilominx.KUBIE_UBM, (byte) 1);
        increaseKubieOrientation(Kilominx.KUBIE_MBM, (byte) 2);
    }

    /**
     * Perform a counter-clockwise turn of the BR face.
     */
    public void moveBRPrime() {
        rotateKubies(new byte[] {Kilominx.KUBIE_UBR, Kilominx.KUBIE_UBM, Kilominx.KUBIE_MBM, Kilominx.KUBIE_BRD, Kilominx.KUBIE_MBR});

        increaseKubieOrientation(Kilominx.KUBIE_MBR, (byte) 1);
        increaseKubieOrientation(Kilominx.KUBIE_UBR, (byte) 2);
        increaseKubieOrientation(Kilominx.KUBIE_UBM, (byte) 1);
        increaseKubieOrientation(Kilominx.KUBIE_BRD, (byte) 2);
    }

    /**
     * Perform two clockwise turns of the BR face.
     */
    public void moveBR2() {
        rotateKubies(new byte[] {Kilominx.KUBIE_UBR, Kilominx.KUBIE_BRD, Kilominx.KUBIE_UBM, Kilominx.KUBIE_MBR, Kilominx.KUBIE_MBM});

        increaseKubieOrientation(Kilominx.KUBIE_MBR, (byte) 1);
        increaseKubieOrientation(Kilominx.KUBIE_BRD, (byte) 2);
    }

    /**
     * Perform two counter-clockwise turns of the BR face.
     */
    public void moveBR2Prime() {
        rotateKubies(new byte[] {Kilominx.KUBIE_UBR, Kilominx.KUBIE_MBM, Kilominx.KUBIE_MBR, Kilominx.KUBIE_UBM, Kilominx.KUBIE_BRD});

        increaseKubieOrientation(Kilominx.KUBIE_UBM, (byte) 1);
        increaseKubieOrientation(Kilominx.KUBIE_MBM, (byte) 2);
    }

    /**
     * Perform a clockwise turn of the DL face.
     */
    public void moveDL() {
        rotateKubies(new byte[] {Kilominx.KUBIE_MFL, Kilominx.KUBIE_FLD, Kilominx.KUBIE_DFL, Kilominx.KUBIE_DFM, Kilominx.KUBIE_FMD});

        increaseKubieOrientation(Kilominx.KUBIE_DFM, (byte) 2);
        increaseKubieOrientation(Kilominx.KUBIE_DFL, (byte) 1);
        increaseKubieOrientation(Kilominx.KUBIE_FLD, (byte) 2);
        increaseKubieOrientation(Kilominx.KUBIE_MFL, (byte) 1);
    }

    /**
     * Perform a counter-clockwise turn of the DL face.
     */
    public void moveDLPrime() {
        rotateKubies(new byte[] {Kilominx.KUBIE_MFL, Kilominx.KUBIE_FMD, Kilominx.KUBIE_DFM, Kilominx.KUBIE_DFL, Kilominx.KUBIE_FLD});

        increaseKubieOrientation(Kilominx.KUBIE_DFM, (byte) 2);
        increaseKubieOrientation(Kilominx.KUBIE_DFL, (byte) 1);
        increaseKubieOrientation(Kilominx.KUBIE_FLD, (byte) 2);
        increaseKubieOrientation(Kilominx.KUBIE_FMD, (byte) 1);
    }

    /**
     * Perform two clockwise turns of the DL face.
     */
    public void moveDL2() {
        rotateKubies(new byte[] {Kilominx.KUBIE_MFL, Kilominx.KUBIE_DFL, Kilominx.KUBIE_FMD, Kilominx.KUBIE_FLD, Kilominx.KUBIE_DFM});

        increaseKubieOrientation(Kilominx.KUBIE_DFM, (byte) 2);
        increaseKubieOrientation(Kilominx.KUBIE_FMD, (byte) 1);
    }

    /**
     * Perform two counter-clockwise turns of the DL face.
     */
    public void moveDL2Prime() {
        rotateKubies(new byte[] {Kilominx.KUBIE_MFL, Kilominx.KUBIE_DFM, Kilominx.KUBIE_FLD, Kilominx.KUBIE_FMD, Kilominx.KUBIE_DFL});

        increaseKubieOrientation(Kilominx.KUBIE_FLD, (byte) 2);
        increaseKubieOrientation(Kilominx.KUBIE_MFL, (byte) 1);
    }

    /**
     * Perform a clockwise turn of the DR face.
     */
    public void moveDR() {
        rotateKubies(new byte[] {Kilominx.KUBIE_MFR, Kilominx.KUBIE_FMD, Kilominx.KUBIE_DFM, Kilominx.KUBIE_DFR, Kilominx.KUBIE_FRD});

        increaseKubieOrientation(Kilominx.KUBIE_DFM, (byte) 1);
        increaseKubieOrientation(Kilominx.KUBIE_FMD, (byte) 2);
        increaseKubieOrientation(Kilominx.KUBIE_MFR, (byte) 1);
        increaseKubieOrientation(Kilominx.KUBIE_FRD, (byte) 2);
    }

    /**
     * Perform a counter-clockwise turn of the DR face.
     */
    public void moveDRPrime() {
        rotateKubies(new byte[] {Kilominx.KUBIE_MFR, Kilominx.KUBIE_FRD, Kilominx.KUBIE_DFR, Kilominx.KUBIE_DFM, Kilominx.KUBIE_FMD});

        increaseKubieOrientation(Kilominx.KUBIE_DFM, (byte) 1);
        increaseKubieOrientation(Kilominx.KUBIE_FMD, (byte) 2);
        increaseKubieOrientation(Kilominx.KUBIE_MFR, (byte) 1);
        increaseKubieOrientation(Kilominx.KUBIE_DFR, (byte) 2);
    }

    /**
     * Perform two clockwise turns of the DR face.
     */
    public void moveDR2() {
        rotateKubies(new byte[] {Kilominx.KUBIE_MFR, Kilominx.KUBIE_DFM, Kilominx.KUBIE_FRD, Kilominx.KUBIE_FMD, Kilominx.KUBIE_DFR});

        increaseKubieOrientation(Kilominx.KUBIE_DFM, (byte) 1);
        increaseKubieOrientation(Kilominx.KUBIE_DFR, (byte) 2);
    }

    /**
     * Perform two counter-clockwise turns of the DR face.
     */
    public void moveDR2Prime() {
        rotateKubies(new byte[] {Kilominx.KUBIE_MFR, Kilominx.KUBIE_DFR, Kilominx.KUBIE_FMD, Kilominx.KUBIE_FRD, Kilominx.KUBIE_DFM});

        increaseKubieOrientation(Kilominx.KUBIE_MFR, (byte) 1);
        increaseKubieOrientation(Kilominx.KUBIE_FRD, (byte) 2);
    }

    /**
     * Perform a clockwise turn of the DBL face.
     */
    public void moveDBL() {
        rotateKubies(new byte[] {Kilominx.KUBIE_MBL, Kilominx.KUBIE_BLD, Kilominx.KUBIE_DBL, Kilominx.KUBIE_DFL, Kilominx.KUBIE_FLD});

        increaseKubieOrientation(Kilominx.KUBIE_MBL, (byte) 2);
        increaseKubieOrientation(Kilominx.KUBIE_FLD, (byte) 1);
        increaseKubieOrientation(Kilominx.KUBIE_DFL, (byte) 2);
        increaseKubieOrientation(Kilominx.KUBIE_DBL, (byte) 1);
    }

    /**
     * Perform a counter-clockwise turn of the DBL face.
     */
    public void moveDBLPrime() {
        rotateKubies(new byte[] {Kilominx.KUBIE_MBL, Kilominx.KUBIE_FLD, Kilominx.KUBIE_DFL, Kilominx.KUBIE_DBL, Kilominx.KUBIE_BLD});

        increaseKubieOrientation(Kilominx.KUBIE_MBL, (byte) 2);
        increaseKubieOrientation(Kilominx.KUBIE_FLD, (byte) 1);
        increaseKubieOrientation(Kilominx.KUBIE_DFL, (byte) 2);
        increaseKubieOrientation(Kilominx.KUBIE_BLD, (byte) 1);
    }

    /**
     * Perform two clockwise turns of the DBL face.
     */
    public void moveDBL2() {
        rotateKubies(new byte[] {Kilominx.KUBIE_MBL, Kilominx.KUBIE_DBL, Kilominx.KUBIE_FLD, Kilominx.KUBIE_BLD, Kilominx.KUBIE_DFL});

        increaseKubieOrientation(Kilominx.KUBIE_MBL, (byte) 2);
        increaseKubieOrientation(Kilominx.KUBIE_BLD, (byte) 1);
    }

    /**
     * Perform two counter-clockwise turns of the DBL face.
     */
    public void moveDBL2Prime() {
        rotateKubies(new byte[] {Kilominx.KUBIE_MBL, Kilominx.KUBIE_DFL, Kilominx.KUBIE_BLD, Kilominx.KUBIE_FLD, Kilominx.KUBIE_DBL});

        increaseKubieOrientation(Kilominx.KUBIE_DFL, (byte) 2);
        increaseKubieOrientation(Kilominx.KUBIE_DBL, (byte) 1);
    }

    /**
     * Perform a clockwise turn of the DBR face.
     */
    public void moveDBR() {
        rotateKubies(new byte[] {Kilominx.KUBIE_MBR, Kilominx.KUBIE_FRD, Kilominx.KUBIE_DFR, Kilominx.KUBIE_DBR, Kilominx.KUBIE_BRD});

        increaseKubieOrientation(Kilominx.KUBIE_DBR, (byte) 1);
        increaseKubieOrientation(Kilominx.KUBIE_DFR, (byte) 1);
        increaseKubieOrientation(Kilominx.KUBIE_FRD, (byte) 1);
    }

    /**
     * Perform a counter-clockwise turn of the DBR face.
     */
    public void moveDBRPrime() {
        rotateKubies(new byte[] {Kilominx.KUBIE_MBR, Kilominx.KUBIE_BRD, Kilominx.KUBIE_DBR, Kilominx.KUBIE_DFR, Kilominx.KUBIE_FRD});

        increaseKubieOrientation(Kilominx.KUBIE_DBR, (byte) 2);
        increaseKubieOrientation(Kilominx.KUBIE_DFR, (byte) 2);
        increaseKubieOrientation(Kilominx.KUBIE_BRD, (byte) 2);
    }

    /**
     * Perform two clockwise turns of the DBR face.
     */
    public void moveDBR2() {
        rotateKubies(new byte[] {Kilominx.KUBIE_MBR, Kilominx.KUBIE_DFR, Kilominx.KUBIE_BRD, Kilominx.KUBIE_FRD, Kilominx.KUBIE_DBR});

        increaseKubieOrientation(Kilominx.KUBIE_DBR, (byte) 1);
        increaseKubieOrientation(Kilominx.KUBIE_DFR, (byte) 2);
        increaseKubieOrientation(Kilominx.KUBIE_FRD, (byte) 2);
        increaseKubieOrientation(Kilominx.KUBIE_MBR, (byte) 1);
    }

    /**
     * Perform two counter-clockwise turns of the DBR face.
     */
    public void moveDBR2Prime() {
        rotateKubies(new byte[] {Kilominx.KUBIE_MBR, Kilominx.KUBIE_DBR, Kilominx.KUBIE_FRD, Kilominx.KUBIE_BRD, Kilominx.KUBIE_DFR});

        increaseKubieOrientation(Kilominx.KUBIE_DBR, (byte) 1);
        increaseKubieOrientation(Kilominx.KUBIE_DFR, (byte) 2);
        increaseKubieOrientation(Kilominx.KUBIE_BRD, (byte) 1);
        increaseKubieOrientation(Kilominx.KUBIE_MBR, (byte) 2);
    }

    /**
     * Perform a clockwise turn of the DB face.
     */
    public void moveDB() {
        rotateKubies(new byte[] {Kilominx.KUBIE_MBM, Kilominx.KUBIE_BRD, Kilominx.KUBIE_DBR, Kilominx.KUBIE_DBL, Kilominx.KUBIE_BLD});

        increaseKubieOrientation(Kilominx.KUBIE_MBM, (byte) 1);
        increaseKubieOrientation(Kilominx.KUBIE_DBL, (byte) 1);
        increaseKubieOrientation(Kilominx.KUBIE_DBR, (byte) 1);
    }

    /**
     * Perform a counter-clockwise turn of the DB face.
     */
    public void moveDBPrime() {
        rotateKubies(new byte[] {Kilominx.KUBIE_MBM, Kilominx.KUBIE_BLD, Kilominx.KUBIE_DBL, Kilominx.KUBIE_DBR, Kilominx.KUBIE_BRD});

        increaseKubieOrientation(Kilominx.KUBIE_BRD, (byte) 2);
        increaseKubieOrientation(Kilominx.KUBIE_DBL, (byte) 2);
        increaseKubieOrientation(Kilominx.KUBIE_BLD, (byte) 2);
    }

    /**
     * Perform two clockwise turns of the DB face.
     */
    public void moveDB2() {
        rotateKubies(new byte[] {Kilominx.KUBIE_MBM, Kilominx.KUBIE_DBR, Kilominx.KUBIE_BLD, Kilominx.KUBIE_BRD, Kilominx.KUBIE_DBL});

        increaseKubieOrientation(Kilominx.KUBIE_MBM, (byte) 1);
        increaseKubieOrientation(Kilominx.KUBIE_BLD, (byte) 1);
        increaseKubieOrientation(Kilominx.KUBIE_DBL, (byte) 1);
        increaseKubieOrientation(Kilominx.KUBIE_DBR, (byte) 2);
        increaseKubieOrientation(Kilominx.KUBIE_BRD, (byte) 1);
    }

    /**
     * Perform two counter-clockwise turns of the DB face.
     */
    public void moveDB2Prime() {
        rotateKubies(new byte[] {Kilominx.KUBIE_MBM, Kilominx.KUBIE_DBL, Kilominx.KUBIE_BRD, Kilominx.KUBIE_BLD, Kilominx.KUBIE_DBR});

        increaseKubieOrientation(Kilominx.KUBIE_MBM, (byte) 2);
        increaseKubieOrientation(Kilominx.KUBIE_BLD, (byte) 1);
        increaseKubieOrientation(Kilominx.KUBIE_DBL, (byte) 2);
        increaseKubieOrientation(Kilominx.KUBIE_DBR, (byte) 2);
        increaseKubieOrientation(Kilominx.KUBIE_BRD, (byte) 2);
    }

    /**
     * Perform a clockwise turn of the D face.
     */
    public void moveD() {
        rotateKubies(new byte[] {Kilominx.KUBIE_DFM, Kilominx.KUBIE_DFL, Kilominx.KUBIE_DBL, Kilominx.KUBIE_DBR, Kilominx.KUBIE_DFR});
    }

    /**
     * Perform a counter-clockwise turn of the D face.
     */
    public void moveDPrime() {
        rotateKubies(new byte[] {Kilominx.KUBIE_DFM, Kilominx.KUBIE_DFR, Kilominx.KUBIE_DBR, Kilominx.KUBIE_DBL, Kilominx.KUBIE_DFL});
    }

    /**
     * Perform two clockwise turns of the D face.
     */
    public void moveD2() {
        rotateKubies(new byte[] {Kilominx.KUBIE_DFM, Kilominx.KUBIE_DBL, Kilominx.KUBIE_DFR, Kilominx.KUBIE_DFL, Kilominx.KUBIE_DBR});
    }

    /**
     * Perform two counter-clockwise turns of the D face.
     */
    public void moveD2Prime() {
        rotateKubies(new byte[] {Kilominx.KUBIE_DFM, Kilominx.KUBIE_DBR, Kilominx.KUBIE_DFL, Kilominx.KUBIE_DFR, Kilominx.KUBIE_DBL});
    }
}
