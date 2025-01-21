package kilominx;

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
     * White, Dark Green, Red, Dark Blue, Yellow, Purple, Light Blue, Beige, Pink, Light Green, Orange, Grey
     */
    public static enum Colour {
        Wh, DG, Re, DB, Ye, Pu, LB, Be, Pi, LG, Or, Gy
    };

    /**
     * Moves that can be made on the Kilominx.
     */
    public static enum Move {
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
        D,   D_PRIME,   D_2,   D_2PRIME
    }

    // Kubies are indexed from 0 to 19 in the following order:
    final byte KUBIE_UFL = 0,   // even
               KUBIE_UFR = 2,   // even
               KUBIE_UBL = 1,   // odd
               KUBIE_UBR = 4,   // even
               KUBIE_UBM = 6,   // even
               KUBIE_MFL = 5,
               KUBIE_FMD = 3,   //
               KUBIE_MFR = 7,
               KUBIE_FRD = 8,
               KUBIE_MBR = 9,
               KUBIE_BRD = 10,
               KUBIE_MBM = 11,
               KUBIE_BLD = 12,
               KUBIE_MBL = 16,  //
               KUBIE_FLD = 14,
               KUBIE_DFM = 13,  // odd
               KUBIE_DFL = 15,  // odd
               KUBIE_DFR = 18,  // even
               KUBIE_DBL = 17,  // odd
               KUBIE_DBR = 19;  // odd


    /**
     * A cubie on the Kilominx (aka a Kubie).
     * A kubie has an index (which represents which colours are on the kubie's sides)
     * and an orientation (0, 1, or 2).
     * @see #Kubie(byte, byte)
     * @see #Kilominx
     */
    class Kubie {
        // Kubies are indexed from 0 to 19 in the following order, with the following initial colours:
        
        // 0,      1,      2,      3,      4,      5,      6,      7,      8,      9,      
        // UFL,    UFR,    UBL,    UBR,    UBM,    MFL,    FMD,    MFR,    FRD,    MBR,    
        // WhDGRe, WhReDB, WhDGPu, WhDbYe, WhPuYe, DGReBe, ReBePi, ReDBPi, DBPiLG, DBYeLG, 

        // 10,     11,     12,     13,     14,     15,     16,     17,     18,     19
        // BRD,    MBM,    BLD,    MBL,    FLD,    DFM,    DFL,    DFR,    DBL,    DBR
        // YeLGOr, YePuOr, PuOrLB, PuDGLB, DGLBBe, BePiGy, LBBeGy, PiLGGy, OrLBGy, LGOrGy

        // Kubies have an orientation of:
        //  - 0 (oriented),
        //  - 1 (colours turned CW),
        //  - or 2 (colours turned CCW)
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
        // Initialize kubies
        for (byte i = 0; i < 20; i++) {
            kubies[i] = new Kubie(i, (byte) 0);
        }
    }
    
    /**
     * Copy constructor for a Kilominx.
     * The Kilominx is initialised with the same kubie indices/orientations as the given Kilominx.
     * @param other - The Kilominx to copy.
     */
    public Kilominx(Kilominx other) {
        byte[] otherIndices = other.getKubieIndices();
        byte[] otherOrientations = other.getKubieOrientations();

        for (byte i = 0; i < 20; i++) {
            kubies[i] = new Kubie(otherIndices[i], otherOrientations[i]);
        }
    }

    
    /**
     * Get the indices of the kubies.
     * @return An array of the indices of the kubies.
     * @see #getKubieOrientations()
     */
    public byte[] getKubieIndices() {
        byte[] indices = new byte[20];

        for (byte i = 0; i < 20; i++) {
            indices[i] = kubies[i].index;
        }

        return indices;
    }

    /**
     * Get the orientations of the kubies.
     * @return An array of the orientations of the kubies.
     * @see #getKubieIndices()
     */
    public byte[] getKubieOrientations() {
        byte[] orientations = new byte[20];

        for (byte i = 0; i < 20; i++) {
            orientations[i] = kubies[i].orientation;
        }

        return orientations;
    }


    /**
     * Check if the Kilominx is solved.
     * @return {@code true} if the Kilominx is solved, {@code false} otherwise.
     */
    public boolean isSolved() {
        // Check that all kubies are in the position and orientation
        for (byte i = 0; i < 20; i++) {
            if (kubies[i].index != i || kubies[i].orientation != 0) {
                return false;
            }
        }

        return true;
    }


    /**
     * Increase the orientation of a kubie (modulo 3).
     * @param posIndex - The position index of the kubie.
     * @param incr - The amount to increase the orientation by.
     */
    private void increaseKubieOrientation(byte posIndex, byte incr) {
        Kubie kubie = kubies[posIndex];

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
     * Get the colours of a kubie.
     * @param posIndex - The position index of the kubie.
     * @return An array of the colours of the kubie.
     * The colours are in the order top/bottom, up-front-left/down-back-right, up-front-middle/down-back-middle, 
     * up-front-right/down-back-left, down-front-left/up-back-right, down-front-right/up-back-left.
     */
    private Colour[] getKubieColours(byte posIndex) {
        Kubie kubie = kubies[posIndex];
        Colour[] colours = new Colour[3];


        // ORIENTATION NOTES:
        // Performing 5 of the same move results in the same kubie indices/orientations that you started with
        // Hence each kubie must increase in orientation by 3, 6, or 9 in total over the course of the 5 turns
        //  - 3 is unlikely, as it means some turns don't increase a kubie's orientation (0 + 0 + 0 + 1 + 2 = 3)
        //  - 9 means 4 of the 5 turns increase the kubie's orientation by 2 (2 + 2 + 2 + 2 + 1 = 9)
        //  - 6 means 4 of the 5 turns increase the kubie's orientation by 1 (1 + 1 + 1 + 1 + 2 = 6)


        byte i0, i1, i2;

        // correct orientation
        if (kubie.orientation == 0) {
            i0 = 0;
            i1 = 1;
            i2 = 2;

            // Kubie is an odd number of indices away, so the other two colours are flipped
            if ((kubie.index + posIndex) % 2 == 1) {
                byte temp = i1;
                i1 = i2;
                i2 = temp;
            }
        }
        // colours turned CW
        else if (kubie.orientation == 1) {
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
            if ((kubie.index + posIndex) % 2 == 1) {
                byte temp = i1;
                i1 = i2;
                i2 = temp;
            }
        }
        // colours turned CCW
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
            if ((kubie.index + posIndex) % 2 == 0) {
                byte temp = i1;
                i1 = i2;
                i2 = temp;
            }
        }

        switch (kubie.index) {
            case KUBIE_UFL:
                colours[i0] = Colour.Wh;
                colours[i1] = Colour.DG;
                colours[i2] = Colour.Re;
                break;

            case KUBIE_UFR:
                colours[i0] = Colour.Wh;
                colours[i1] = Colour.Re;
                colours[i2] = Colour.DB;
                break;

            case KUBIE_UBL:
                colours[i0] = Colour.Wh;
                colours[i1] = Colour.DG;
                colours[i2] = Colour.Pu;
                break;

            case KUBIE_UBR:
                colours[i0] = Colour.Wh;
                colours[i1] = Colour.DB;
                colours[i2] = Colour.Ye;
                break;

            case KUBIE_UBM:
                colours[i0] = Colour.Wh;
                colours[i1] = Colour.Ye;
                colours[i2] = Colour.Pu;
                break;

            case KUBIE_MFL:
                colours[i0] = Colour.DG;
                colours[i1] = Colour.Re;
                colours[i2] = Colour.Be;
                break;

            case KUBIE_FMD:
                colours[i0] = Colour.Re;
                colours[i1] = Colour.Be;
                colours[i2] = Colour.Pi;
                break;

            case KUBIE_MFR:
                colours[i0] = Colour.Re;
                colours[i1] = Colour.DB;
                colours[i2] = Colour.Pi;
                break;

            case KUBIE_FRD:
                colours[i0] = Colour.LG;
                colours[i1] = Colour.DB;
                colours[i2] = Colour.Pi;
                break;

            case KUBIE_MBR:
                colours[i0] = Colour.LG;
                colours[i1] = Colour.DB;
                colours[i2] = Colour.Ye;
                break;

            case KUBIE_BRD:
                colours[i0] = Colour.LG;
                colours[i1] = Colour.Or;
                colours[i2] = Colour.Ye;
                break;

            case KUBIE_MBM:
                colours[i0] = Colour.Or;
                colours[i1] = Colour.Ye;
                colours[i2] = Colour.Pu;
                break;

            case KUBIE_BLD:
                colours[i0] = Colour.Or;
                colours[i1] = Colour.LB;
                colours[i2] = Colour.Pu;
                break;

            case KUBIE_MBL:
                colours[i0] = Colour.DG;
                colours[i1] = Colour.LB;
                colours[i2] = Colour.Pu;
                break;

            case KUBIE_FLD:
                colours[i0] = Colour.DG;
                colours[i1] = Colour.LB;
                colours[i2] = Colour.Be;
                break;

            case KUBIE_DFM:
                colours[i0] = Colour.Gy;
                colours[i1] = Colour.Be;
                colours[i2] = Colour.Pi;
                break;

            case KUBIE_DFL:
                colours[i0] = Colour.Gy;
                colours[i1] = Colour.LB;
                colours[i2] = Colour.Be;
                break;

            case KUBIE_DFR:
                colours[i0] = Colour.Gy;
                colours[i1] = Colour.LG;
                colours[i2] = Colour.Pi;
                break;

            case KUBIE_DBL:
                colours[i0] = Colour.Gy;
                colours[i1] = Colour.Or;
                colours[i2] = Colour.LB;
                break;

            case KUBIE_DBR:
                colours[i0] = Colour.Gy;
                colours[i1] = Colour.LG;
                colours[i2] = Colour.Or;
                break; 

        }

        return colours;
    }


    /**
     * Make a move on the Kilominx.
     * @param move - The move to make.
     */
    public void makeMove(Move move) {
        switch (move) {
            case U: moveU(); break;
            case U_PRIME: moveUPrime(); break;
            case U_2: moveU2(); break;
            case U_2PRIME: moveU2Prime(); break;

            case L: moveL(); break;
            case L_PRIME: moveLPrime(); break;
            case L_2: moveL2(); break;
            case L_2PRIME: moveL2Prime(); break;

            case F: moveF(); break;
            case F_PRIME: moveFPrime(); break;
            case F_2: moveF2(); break;
            case F_2PRIME: moveF2Prime(); break;

            case R: moveR(); break;
            case R_PRIME: moveRPrime(); break;
            case R_2: moveR2(); break;
            case R_2PRIME: moveR2Prime(); break;

            case BL: moveBL(); break;
            case BL_PRIME: moveBLPrime(); break;
            case BL_2: moveBL2(); break;
            case BL_2PRIME: moveBL2Prime(); break;

            case BR: moveBR(); break;
            case BR_PRIME: moveBRPrime(); break;
            case BR_2: moveBR2(); break;
            case BR_2PRIME: moveBR2Prime(); break;

            case DL: moveDL(); break;
            case DL_PRIME: moveDLPrime(); break;
            case DL_2: moveDL2(); break;
            case DL_2PRIME: moveDL2Prime(); break;

            case DR: moveDR(); break;
            case DR_PRIME: moveDRPrime(); break;
            case DR_2: moveDR2(); break;
            case DR_2PRIME: moveDR2Prime(); break;

            case DBL: moveDBL(); break;
            case DBL_PRIME: moveDBLPrime(); break;
            case DBL_2: moveDBL2(); break;
            case DBL_2PRIME: moveDBL2Prime(); break;

            case DBR: moveDBR(); break;
            case DBR_PRIME: moveDBRPrime(); break;
            case DBR_2: moveDBR2(); break;
            case DBR_2PRIME: moveDBR2Prime(); break;

            case DB: moveDB(); break;
            case DB_PRIME: moveDBPrime(); break;
            case DB_2: moveDB2(); break;
            case DB_2PRIME: moveDB2Prime(); break;

            case D: moveD(); break;
            case D_PRIME: moveDPrime(); break;
            case D_2: moveD2(); break;
            case D_2PRIME: moveD2Prime(); break;

            default: break;
        }
    }

    /**
     * Undo a move on the Kilominx by performing the inverse move.
     * @param move - The move to undo.
     */
    public void undoMove(Move move) {
        switch (move) {
            case U: moveUPrime(); break;
            case U_PRIME: moveU(); break;
            case U_2: moveU2Prime(); break;
            case U_2PRIME: moveU2(); break;

            case L: moveLPrime(); break;
            case L_PRIME: moveL(); break;
            case L_2: moveL2Prime(); break;
            case L_2PRIME: moveL2(); break;

            case F: moveFPrime(); break;
            case F_PRIME: moveF(); break;
            case F_2: moveF2Prime(); break;
            case F_2PRIME: moveF2(); break;

            case R: moveRPrime(); break;
            case R_PRIME: moveR(); break;
            case R_2: moveR2Prime(); break;
            case R_2PRIME: moveR2(); break;

            case BL: moveBLPrime(); break;
            case BL_PRIME: moveBL(); break;
            case BL_2: moveBL2Prime(); break;
            case BL_2PRIME: moveBL2(); break;

            case BR: moveBRPrime(); break;
            case BR_PRIME: moveBR(); break;
            case BR_2: moveBR2Prime(); break;
            case BR_2PRIME: moveBR2(); break;

            case DL: moveDLPrime(); break;
            case DL_PRIME: moveDL(); break;
            case DL_2: moveDL2Prime(); break;
            case DL_2PRIME: moveDL2(); break;

            case DR: moveDRPrime(); break;
            case DR_PRIME: moveDR(); break;
            case DR_2: moveDR2Prime(); break;
            case DR_2PRIME: moveDR2(); break;

            case DBL: moveDBLPrime(); break;
            case DBL_PRIME: moveDBL(); break;
            case DBL_2: moveDBL2Prime(); break;
            case DBL_2PRIME: moveDBL2(); break;

            case DBR: moveDBRPrime(); break;
            case DBR_PRIME: moveDBR(); break;
            case DBR_2: moveDBR2Prime(); break;
            case DBR_2PRIME: moveDBR2(); break;

            case DB: moveDBPrime(); break;
            case DB_PRIME: moveDB(); break;
            case DB_2: moveDB2Prime(); break;
            case DB_2PRIME: moveDB2(); break;

            case D: moveDPrime(); break;
            case D_PRIME: moveD(); break;
            case D_2: moveD2Prime(); break;
            case D_2PRIME: moveD2(); break;

            default: break;
        }
    }


    /**
     * Perform a clockwise turn of the U face.
     */
    public void moveU() {
        Kubie temp        = kubies[KUBIE_UFL];
        kubies[KUBIE_UFL] = kubies[KUBIE_UFR];
        kubies[KUBIE_UFR] = kubies[KUBIE_UBR];
        kubies[KUBIE_UBR] = kubies[KUBIE_UBM];
        kubies[KUBIE_UBM] = kubies[KUBIE_UBL];
        kubies[KUBIE_UBL] = temp;
    }

    /**
     * Perform a counter-clockwise turn of the U face.
     */
    public void moveUPrime() {
        Kubie temp        = kubies[KUBIE_UFL];
        kubies[KUBIE_UFL] = kubies[KUBIE_UBL];
        kubies[KUBIE_UBL] = kubies[KUBIE_UBM];
        kubies[KUBIE_UBM] = kubies[KUBIE_UBR];
        kubies[KUBIE_UBR] = kubies[KUBIE_UFR];
        kubies[KUBIE_UFR] = temp;
    }

    /**
     * Perform two clockwise turns of the U face.
     */
    public void moveU2() {
        Kubie temp        = kubies[KUBIE_UFL];
        kubies[KUBIE_UFL] = kubies[KUBIE_UBR];
        kubies[KUBIE_UBR] = kubies[KUBIE_UBL];
        kubies[KUBIE_UBL] = kubies[KUBIE_UFR];
        kubies[KUBIE_UFR] = kubies[KUBIE_UBM];
        kubies[KUBIE_UBM] = temp;
    }

    /**
     * Perform two counter-clockwise turns of the U face.
     */
    public void moveU2Prime() {
        Kubie temp        = kubies[KUBIE_UFL];
        kubies[KUBIE_UFL] = kubies[KUBIE_UBM];
        kubies[KUBIE_UBM] = kubies[KUBIE_UFR];
        kubies[KUBIE_UFR] = kubies[KUBIE_UBL];
        kubies[KUBIE_UBL] = kubies[KUBIE_UBR];
        kubies[KUBIE_UBR] = temp;
    }

    /**
     * Perform a clockwise turn of the L face.
     */
    public void moveL() {
        Kubie temp        = kubies[KUBIE_UBL];
        kubies[KUBIE_UBL] = kubies[KUBIE_MBL];
        kubies[KUBIE_MBL] = kubies[KUBIE_FLD];
        kubies[KUBIE_FLD] = kubies[KUBIE_MFL];
        kubies[KUBIE_MFL] = kubies[KUBIE_UFL];
        kubies[KUBIE_UFL] = temp;

        // orientation changes??
    }

    /**
     * Perform a counter-clockwise turn of the L face.
     */
    public void moveLPrime() {
        Kubie temp        = kubies[KUBIE_UBL];
        kubies[KUBIE_UBL] = kubies[KUBIE_UFL];
        kubies[KUBIE_UFL] = kubies[KUBIE_MFL];
        kubies[KUBIE_MFL] = kubies[KUBIE_FLD];
        kubies[KUBIE_FLD] = kubies[KUBIE_MBL];
        kubies[KUBIE_MBL] = temp;

        // orientation changes??
    }

    /**
     * Perform two clockwise turns of the L face.
     */
    public void moveL2() {
        Kubie temp        = kubies[KUBIE_UBL];
        kubies[KUBIE_UBL] = kubies[KUBIE_FLD];
        kubies[KUBIE_FLD] = kubies[KUBIE_UFL];
        kubies[KUBIE_UFL] = kubies[KUBIE_MBL];
        kubies[KUBIE_MBL] = kubies[KUBIE_MFL];
        kubies[KUBIE_MFL] = temp;

        // orientation changes??
    }

    /**
     * Perform two counter-clockwise turns of the L face.
     */
    public void moveL2Prime() {
        Kubie temp        = kubies[KUBIE_UBL];
        kubies[KUBIE_UBL] = kubies[KUBIE_MFL];
        kubies[KUBIE_MFL] = kubies[KUBIE_MBL];
        kubies[KUBIE_MBL] = kubies[KUBIE_UFL];
        kubies[KUBIE_UFL] = kubies[KUBIE_FLD];
        kubies[KUBIE_FLD] = temp;

        // orientation changes??
    }

    /**
     * Perform a clockwise turn of the F face.
     */
    public void moveF() {
        Kubie temp        = kubies[KUBIE_UFL];
        kubies[KUBIE_UFL] = kubies[KUBIE_MFL];
        kubies[KUBIE_MFL] = kubies[KUBIE_FMD];
        kubies[KUBIE_FMD] = kubies[KUBIE_MFR];
        kubies[KUBIE_MFR] = kubies[KUBIE_UFR];
        kubies[KUBIE_UFR] = temp;

        // orientation changes??
    }

    /**
     * Perform a counter-clockwise turn of the F face.
     */
    public void moveFPrime() {
        Kubie temp        = kubies[KUBIE_UFL];
        kubies[KUBIE_UFL] = kubies[KUBIE_UFR];
        kubies[KUBIE_UFR] = kubies[KUBIE_MFR];
        kubies[KUBIE_MFR] = kubies[KUBIE_FMD];
        kubies[KUBIE_FMD] = kubies[KUBIE_MFL];
        kubies[KUBIE_MFL] = temp;

        // orientation changes??
    }

    /**
     * Perform two clockwise turns of the F face.
     */
    public void moveF2() {
        Kubie temp        = kubies[KUBIE_UFL];
        kubies[KUBIE_UFL] = kubies[KUBIE_FMD];
        kubies[KUBIE_FMD] = kubies[KUBIE_UFR];
        kubies[KUBIE_UFR] = kubies[KUBIE_MFL];
        kubies[KUBIE_MFL] = kubies[KUBIE_MFR];
        kubies[KUBIE_MFR] = temp;

        // orientation changes??
    }

    /**
     * Perform two counter-clockwise turns of the F face.
     */
    public void moveF2Prime() {
        Kubie temp        = kubies[KUBIE_UFL];
        kubies[KUBIE_UFL] = kubies[KUBIE_MFR];
        kubies[KUBIE_MFR] = kubies[KUBIE_MFL];
        kubies[KUBIE_MFL] = kubies[KUBIE_UFR];
        kubies[KUBIE_UFR] = kubies[KUBIE_FMD];
        kubies[KUBIE_FMD] = temp;

        // orientation changes??
    }

    /**
     * Perform a clockwise turn of the R face.
     */
    public void moveR() {
        Kubie temp        = kubies[KUBIE_UFR];
        kubies[KUBIE_UFR] = kubies[KUBIE_MFR];
        kubies[KUBIE_MFR] = kubies[KUBIE_FRD];
        kubies[KUBIE_FRD] = kubies[KUBIE_MBR];
        kubies[KUBIE_MBR] = kubies[KUBIE_UBR];
        kubies[KUBIE_UBR] = temp;

        // orientation changes??
    }

    /**
     * Perform a counter-clockwise turn of the R face.
     */
    public void moveRPrime() {
        Kubie temp        = kubies[KUBIE_UFR];
        kubies[KUBIE_UFR] = kubies[KUBIE_UBR];
        kubies[KUBIE_UBR] = kubies[KUBIE_MBR];
        kubies[KUBIE_MBR] = kubies[KUBIE_FRD];
        kubies[KUBIE_FRD] = kubies[KUBIE_MFR];
        kubies[KUBIE_MFR] = temp;

        // orientation changes??
    }

    /**
     * Perform two clockwise turns of the R face.
     */
    public void moveR2() {
        Kubie temp        = kubies[KUBIE_UFR];
        kubies[KUBIE_UFR] = kubies[KUBIE_FRD];
        kubies[KUBIE_FRD] = kubies[KUBIE_UBR];
        kubies[KUBIE_UBR] = kubies[KUBIE_MFR];
        kubies[KUBIE_MFR] = kubies[KUBIE_MBR];
        kubies[KUBIE_MBR] = temp;

        // orientation changes??
    }

    /**
     * Perform two counter-clockwise turns of the R face.
     */
    public void moveR2Prime() {
        Kubie temp        = kubies[KUBIE_UFR];
        kubies[KUBIE_UFR] = kubies[KUBIE_MBR];
        kubies[KUBIE_MBR] = kubies[KUBIE_MFR];
        kubies[KUBIE_MFR] = kubies[KUBIE_UBR];
        kubies[KUBIE_UBR] = kubies[KUBIE_FRD];
        kubies[KUBIE_FRD] = temp;

        // orientation changes??
    }

    /**
     * Perform a clockwise turn of the BL face.
     */
    public void moveBL() {
        Kubie temp        = kubies[KUBIE_UBM];
        kubies[KUBIE_UBM] = kubies[KUBIE_MBM];
        kubies[KUBIE_MBM] = kubies[KUBIE_BLD];
        kubies[KUBIE_BLD] = kubies[KUBIE_MBL];
        kubies[KUBIE_MBL] = kubies[KUBIE_UBL];
        kubies[KUBIE_UBL] = temp;

        // orientation changes??
    }

    /**
     * Perform a counter-clockwise turn of the BL face.
     */
    public void moveBLPrime() {
        Kubie temp        = kubies[KUBIE_UBM];
        kubies[KUBIE_UBM] = kubies[KUBIE_UBL];
        kubies[KUBIE_UBL] = kubies[KUBIE_MBL];
        kubies[KUBIE_MBL] = kubies[KUBIE_BLD];
        kubies[KUBIE_BLD] = kubies[KUBIE_MBM];
        kubies[KUBIE_MBM] = temp;

        // orientation changes??
    }

    /**
     * Perform two clockwise turns of the BL face.
     */
    public void moveBL2() {
        Kubie temp        = kubies[KUBIE_UBM];
        kubies[KUBIE_UBM] = kubies[KUBIE_BLD];
        kubies[KUBIE_BLD] = kubies[KUBIE_UBL];
        kubies[KUBIE_UBL] = kubies[KUBIE_MBM];
        kubies[KUBIE_MBM] = kubies[KUBIE_MBL];
        kubies[KUBIE_MBL] = temp;

        // orientation changes??
    }

    /**
     * Perform two counter-clockwise turns of the BL face.
     */
    public void moveBL2Prime() {
        Kubie temp        = kubies[KUBIE_UBM];
        kubies[KUBIE_UBM] = kubies[KUBIE_MBL];
        kubies[KUBIE_MBL] = kubies[KUBIE_MBM];
        kubies[KUBIE_MBM] = kubies[KUBIE_UBL];
        kubies[KUBIE_UBL] = kubies[KUBIE_BLD];
        kubies[KUBIE_BLD] = temp;

        // orientation changes??
    }

    /**
     * Perform a clockwise turn of the BR face.
     */
    public void moveBR() {
        Kubie temp        = kubies[KUBIE_UBR];
        kubies[KUBIE_UBR] = kubies[KUBIE_MBR];
        kubies[KUBIE_MBR] = kubies[KUBIE_BRD];
        kubies[KUBIE_BRD] = kubies[KUBIE_MBM];
        kubies[KUBIE_MBM] = kubies[KUBIE_UBM];
        kubies[KUBIE_UBM] = temp;

        // orientation changes??
    }

    /**
     * Perform a counter-clockwise turn of the BR face.
     */
    public void moveBRPrime() {
        Kubie temp        = kubies[KUBIE_UBR];
        kubies[KUBIE_UBR] = kubies[KUBIE_UBM];
        kubies[KUBIE_UBM] = kubies[KUBIE_MBM];
        kubies[KUBIE_MBM] = kubies[KUBIE_BRD];
        kubies[KUBIE_BRD] = kubies[KUBIE_MBR];
        kubies[KUBIE_MBR] = temp;

        // orientation changes??
    }

    /**
     * Perform two clockwise turns of the BR face.
     */
    public void moveBR2() {
        Kubie temp        = kubies[KUBIE_UBR];
        kubies[KUBIE_UBR] = kubies[KUBIE_BRD];
        kubies[KUBIE_BRD] = kubies[KUBIE_UBM];
        kubies[KUBIE_UBM] = kubies[KUBIE_MBR];
        kubies[KUBIE_MBR] = kubies[KUBIE_MBM];
        kubies[KUBIE_MBM] = temp;

        // orientation changes??
    }

    /**
     * Perform two counter-clockwise turns of the BR face.
     */
    public void moveBR2Prime() {
        Kubie temp        = kubies[KUBIE_UBR];
        kubies[KUBIE_UBR] = kubies[KUBIE_MBM];
        kubies[KUBIE_MBM] = kubies[KUBIE_MBR];
        kubies[KUBIE_MBR] = kubies[KUBIE_UBM];
        kubies[KUBIE_UBM] = kubies[KUBIE_BRD];
        kubies[KUBIE_BRD] = temp;

        // orientation changes??
    }

    /**
     * Perform a clockwise turn of the DL face.
     */
    public void moveDL() {
        Kubie temp        = kubies[KUBIE_MFL];
        kubies[KUBIE_MFL] = kubies[KUBIE_FLD];
        kubies[KUBIE_FLD] = kubies[KUBIE_DFL];
        kubies[KUBIE_DFL] = kubies[KUBIE_DFM];
        kubies[KUBIE_DFM] = kubies[KUBIE_FMD];
        kubies[KUBIE_FMD] = temp;

        // orientation changes??
    }

    /**
     * Perform a counter-clockwise turn of the DL face.
     */
    public void moveDLPrime() {
        Kubie temp        = kubies[KUBIE_MFL];
        kubies[KUBIE_MFL] = kubies[KUBIE_FMD];
        kubies[KUBIE_FMD] = kubies[KUBIE_DFM];
        kubies[KUBIE_DFM] = kubies[KUBIE_DFL];
        kubies[KUBIE_DFL] = kubies[KUBIE_FLD];
        kubies[KUBIE_FLD] = temp;

        // orientation changes??
    }

    /**
     * Perform two clockwise turns of the DL face.
     */
    public void moveDL2() {
        Kubie temp        = kubies[KUBIE_MFL];
        kubies[KUBIE_MFL] = kubies[KUBIE_DFL];
        kubies[KUBIE_DFL] = kubies[KUBIE_FMD];
        kubies[KUBIE_FMD] = kubies[KUBIE_FLD];
        kubies[KUBIE_FLD] = kubies[KUBIE_DFM];
        kubies[KUBIE_DFM] = temp;

        // orientation changes??
    }

    /**
     * Perform two counter-clockwise turns of the DL face.
     */
    public void moveDL2Prime() {
        Kubie temp        = kubies[KUBIE_MFL];
        kubies[KUBIE_MFL] = kubies[KUBIE_DFM];
        kubies[KUBIE_DFM] = kubies[KUBIE_FLD];
        kubies[KUBIE_FLD] = kubies[KUBIE_FMD];
        kubies[KUBIE_FMD] = kubies[KUBIE_DFL];
        kubies[KUBIE_DFL] = temp;

        // orientation changes??
    }

    /**
     * Perform a clockwise turn of the DR face.
     */
    public void moveDR() {
        Kubie temp        = kubies[KUBIE_MFR];
        kubies[KUBIE_MFR] = kubies[KUBIE_FMD];
        kubies[KUBIE_FMD] = kubies[KUBIE_DFM];
        kubies[KUBIE_DFM] = kubies[KUBIE_DFR];
        kubies[KUBIE_DFR] = kubies[KUBIE_FRD];
        kubies[KUBIE_FRD] = temp;

        // orientation changes??
    }

    /**
     * Perform a counter-clockwise turn of the DR face.
     */
    public void moveDRPrime() {
        Kubie temp        = kubies[KUBIE_MFR];
        kubies[KUBIE_MFR] = kubies[KUBIE_FRD];
        kubies[KUBIE_FRD] = kubies[KUBIE_DFR];
        kubies[KUBIE_DFR] = kubies[KUBIE_DFM];
        kubies[KUBIE_DFM] = kubies[KUBIE_FMD];
        kubies[KUBIE_FMD] = temp;

        // orientation changes??
    }

    /**
     * Perform two clockwise turns of the DR face.
     */
    public void moveDR2() {
        Kubie temp        = kubies[KUBIE_MFR];
        kubies[KUBIE_MFR] = kubies[KUBIE_DFM];
        kubies[KUBIE_DFM] = kubies[KUBIE_FRD];
        kubies[KUBIE_FRD] = kubies[KUBIE_FMD];
        kubies[KUBIE_FMD] = kubies[KUBIE_DFR];
        kubies[KUBIE_DFR] = temp;

        // orientation changes??
    }

    /**
     * Perform two counter-clockwise turns of the DR face.
     */
    public void moveDR2Prime() {
        Kubie temp        = kubies[KUBIE_MFR];
        kubies[KUBIE_MFR] = kubies[KUBIE_DFR];
        kubies[KUBIE_DFR] = kubies[KUBIE_FMD];
        kubies[KUBIE_FMD] = kubies[KUBIE_FRD];
        kubies[KUBIE_FRD] = kubies[KUBIE_DFM];
        kubies[KUBIE_DFM] = temp;

        // orientation changes??
    }

    /**
     * Perform a clockwise turn of the DBL face.
     */
    public void moveDBL() {
        Kubie temp        = kubies[KUBIE_MBL];
        kubies[KUBIE_MBL] = kubies[KUBIE_BLD];
        kubies[KUBIE_BLD] = kubies[KUBIE_DBL];
        kubies[KUBIE_DBL] = kubies[KUBIE_DFL];
        kubies[KUBIE_DFL] = kubies[KUBIE_FLD];
        kubies[KUBIE_FLD] = temp;

        // orientation changes??
    }

    /**
     * Perform a counter-clockwise turn of the DBL face.
     */
    public void moveDBLPrime() {
        Kubie temp        = kubies[KUBIE_MBL];
        kubies[KUBIE_MBL] = kubies[KUBIE_FLD];
        kubies[KUBIE_FLD] = kubies[KUBIE_DFL];
        kubies[KUBIE_DFL] = kubies[KUBIE_DBL];
        kubies[KUBIE_DBL] = kubies[KUBIE_BLD];
        kubies[KUBIE_BLD] = temp;

        // orientation changes??
    }

    /**
     * Perform two clockwise turns of the DBL face.
     */
    public void moveDBL2() {
        Kubie temp        = kubies[KUBIE_MBL];
        kubies[KUBIE_MBL] = kubies[KUBIE_DBL];
        kubies[KUBIE_DBL] = kubies[KUBIE_FLD];
        kubies[KUBIE_FLD] = kubies[KUBIE_BLD];
        kubies[KUBIE_BLD] = kubies[KUBIE_DFL];
        kubies[KUBIE_DFL] = temp;

        // orientation changes??
    }

    /**
     * Perform two counter-clockwise turns of the DBL face.
     */
    public void moveDBL2Prime() {
        Kubie temp        = kubies[KUBIE_MBL];
        kubies[KUBIE_MBL] = kubies[KUBIE_DFL];
        kubies[KUBIE_DFL] = kubies[KUBIE_BLD];
        kubies[KUBIE_BLD] = kubies[KUBIE_FLD];
        kubies[KUBIE_FLD] = kubies[KUBIE_DBL];
        kubies[KUBIE_DBL] = temp;

        // orientation changes??
    }

    /**
     * Perform a clockwise turn of the DBR face.
     */
    public void moveDBR() {
        Kubie temp        = kubies[KUBIE_MBR];
        kubies[KUBIE_MBR] = kubies[KUBIE_FRD];
        kubies[KUBIE_FRD] = kubies[KUBIE_DFR];
        kubies[KUBIE_DFR] = kubies[KUBIE_DBR];
        kubies[KUBIE_DBR] = kubies[KUBIE_BRD];
        kubies[KUBIE_BRD] = temp;

        // orientation changes??
    }

    /**
     * Perform a counter-clockwise turn of the DBR face.
     */
    public void moveDBRPrime() {
        Kubie temp        = kubies[KUBIE_MBR];
        kubies[KUBIE_MBR] = kubies[KUBIE_BRD];
        kubies[KUBIE_BRD] = kubies[KUBIE_DBR];
        kubies[KUBIE_DBR] = kubies[KUBIE_DFR];
        kubies[KUBIE_DFR] = kubies[KUBIE_FRD];
        kubies[KUBIE_FRD] = temp;

        // orientation changes??
    }

    /**
     * Perform two clockwise turns of the DBR face.
     */
    public void moveDBR2() {
        Kubie temp        = kubies[KUBIE_MBR];
        kubies[KUBIE_MBR] = kubies[KUBIE_DFR];
        kubies[KUBIE_DFR] = kubies[KUBIE_BRD];
        kubies[KUBIE_BRD] = kubies[KUBIE_FRD];
        kubies[KUBIE_FRD] = kubies[KUBIE_DBR];
        kubies[KUBIE_DBR] = temp;

        // orientation changes??
    }

    /**
     * Perform two counter-clockwise turns of the DBR face.
     */
    public void moveDBR2Prime() {
        Kubie temp        = kubies[KUBIE_MBR];
        kubies[KUBIE_MBR] = kubies[KUBIE_DBR];
        kubies[KUBIE_DBR] = kubies[KUBIE_FRD];
        kubies[KUBIE_FRD] = kubies[KUBIE_BRD];
        kubies[KUBIE_BRD] = kubies[KUBIE_DFR];
        kubies[KUBIE_DFR] = temp;

        // orientation changes??
    }

    /**
     * Perform a clockwise turn of the DB face.
     */
    public void moveDB() {
        Kubie temp        = kubies[KUBIE_MBM];
        kubies[KUBIE_MBM] = kubies[KUBIE_BRD];
        kubies[KUBIE_BRD] = kubies[KUBIE_DBR];
        kubies[KUBIE_DBR] = kubies[KUBIE_DBL];
        kubies[KUBIE_DBL] = kubies[KUBIE_BLD];
        kubies[KUBIE_BLD] = temp;

        // orientation changes??
    }

    /**
     * Perform a counter-clockwise turn of the DB face.
     */
    public void moveDBPrime() {
        Kubie temp        = kubies[KUBIE_MBM];
        kubies[KUBIE_MBM] = kubies[KUBIE_BLD];
        kubies[KUBIE_BLD] = kubies[KUBIE_DBL];
        kubies[KUBIE_DBL] = kubies[KUBIE_DBR];
        kubies[KUBIE_DBR] = kubies[KUBIE_BRD];
        kubies[KUBIE_BRD] = temp;

        // orientation changes??
    }

    /**
     * Perform two clockwise turns of the DB face.
     */
    public void moveDB2() {
        Kubie temp        = kubies[KUBIE_MBM];
        kubies[KUBIE_MBM] = kubies[KUBIE_DBR];
        kubies[KUBIE_DBR] = kubies[KUBIE_BLD];
        kubies[KUBIE_BLD] = kubies[KUBIE_BRD];
        kubies[KUBIE_BRD] = kubies[KUBIE_DBL];
        kubies[KUBIE_DBL] = temp;

        // orientation changes??
    }

    /**
     * Perform two counter-clockwise turns of the DB face.
     */
    public void moveDB2Prime() {
        Kubie temp        = kubies[KUBIE_MBM];
        kubies[KUBIE_MBM] = kubies[KUBIE_DBL];
        kubies[KUBIE_DBL] = kubies[KUBIE_BRD];
        kubies[KUBIE_BRD] = kubies[KUBIE_BLD];
        kubies[KUBIE_BLD] = kubies[KUBIE_DBR];
        kubies[KUBIE_DBR] = temp;

        // orientation changes??
    }

    /**
     * Perform a clockwise turn of the D face.
     */
    public void moveD() {
        Kubie temp        = kubies[KUBIE_DFM];
        kubies[KUBIE_DFM] = kubies[KUBIE_DFL];
        kubies[KUBIE_DFL] = kubies[KUBIE_DBL];
        kubies[KUBIE_DBL] = kubies[KUBIE_DBR];
        kubies[KUBIE_DBR] = kubies[KUBIE_DFR];
        kubies[KUBIE_DFR] = temp;
    }

    /**
     * Perform a counter-clockwise turn of the D face.
     */
    public void moveDPrime() {
        Kubie temp        = kubies[KUBIE_DFM];
        kubies[KUBIE_DFM] = kubies[KUBIE_DFR];
        kubies[KUBIE_DFR] = kubies[KUBIE_DBR];
        kubies[KUBIE_DBR] = kubies[KUBIE_DBL];
        kubies[KUBIE_DBL] = kubies[KUBIE_DFL];
        kubies[KUBIE_DFL] = temp;
    }

    /**
     * Perform two clockwise turns of the D face.
     */
    public void moveD2() {
        Kubie temp        = kubies[KUBIE_DFM];
        kubies[KUBIE_DFM] = kubies[KUBIE_DBL];
        kubies[KUBIE_DBL] = kubies[KUBIE_DFR];
        kubies[KUBIE_DFR] = kubies[KUBIE_DFL];
        kubies[KUBIE_DFL] = kubies[KUBIE_DBR];
        kubies[KUBIE_DBR] = temp;
    }

    /**
     * Perform two counter-clockwise turns of the D face.
     */
    public void moveD2Prime() {
        Kubie temp        = kubies[KUBIE_DFM];
        kubies[KUBIE_DFM] = kubies[KUBIE_DBR];
        kubies[KUBIE_DBR] = kubies[KUBIE_DFL];
        kubies[KUBIE_DFL] = kubies[KUBIE_DFR];
        kubies[KUBIE_DFR] = kubies[KUBIE_DBL];
        kubies[KUBIE_DBL] = temp;
    }


    /**
     * Print the Kilominx state in a human-readable format.
     */
    public void printKilominxState() {
        
        // Print top face
        System.out.print("                           ");
        System.out.println(getKubieColours(KUBIE_UBM)[0]);
        System.out.print("                        ");
        System.out.print(getKubieColours(KUBIE_UBL)[0] + "    ");
        System.out.println(getKubieColours(KUBIE_UBR)[0]);
        System.out.print("                         ");
        System.out.print(getKubieColours(KUBIE_UFL)[0] + "  ");
        System.out.println(getKubieColours(KUBIE_UFR)[0] + "\n");

        // Print upper row of upper-middle faces
        System.out.print(" " + getKubieColours(KUBIE_UBM)[2]);
        System.out.print("  " + getKubieColours(KUBIE_UBL)[2]);
        System.out.print("      " + getKubieColours(KUBIE_UBL)[1]);
        System.out.print("  " + getKubieColours(KUBIE_UFL)[1]);
        System.out.print("      " + getKubieColours(KUBIE_UFL)[2]);
        System.out.print("  " + getKubieColours(KUBIE_UFR)[1]);
        System.out.print("      " + getKubieColours(KUBIE_UFR)[2]);
        System.out.print("  " + getKubieColours(KUBIE_UBR)[1]);
        System.out.print("      " + getKubieColours(KUBIE_UBR)[2]);
        System.out.println("  " + getKubieColours(KUBIE_UBM)[1]);

        // Print middle row of upper-middle faces
        System.out.print(getKubieColours(KUBIE_MBM)[2] + "    ");
        System.out.print(getKubieColours(KUBIE_MBL)[2] + "    ");
        System.out.print(getKubieColours(KUBIE_MBL)[0] + "    ");
        System.out.print(getKubieColours(KUBIE_MFL)[0] + "    ");
        System.out.print(getKubieColours(KUBIE_MFL)[1] + "    ");
        System.out.print(getKubieColours(KUBIE_MFR)[0] + "    ");
        System.out.print(getKubieColours(KUBIE_MFR)[1] + "    ");
        System.out.print(getKubieColours(KUBIE_MBR)[1] + "    ");
        System.out.print(getKubieColours(KUBIE_MBR)[2] + "    ");
        System.out.println(getKubieColours(KUBIE_MBM)[1] + "    ");

        // Print bottom row of upper-middle faces
        System.out.print("   " + getKubieColours(KUBIE_BLD)[2]);
        System.out.print("          " + getKubieColours(KUBIE_FLD)[0]);
        System.out.print("          " + getKubieColours(KUBIE_FMD)[0]);
        System.out.print("          " + getKubieColours(KUBIE_FRD)[1]);
        System.out.println("          " + getKubieColours(KUBIE_BRD)[2] + "\n");

        // Print upper row of lower-middle faces
        System.out.print("         " + getKubieColours(KUBIE_MBL)[1]);
        System.out.print("          " + getKubieColours(KUBIE_MFL)[2]);
        System.out.print("          " + getKubieColours(KUBIE_MFR)[2]);
        System.out.print("          " + getKubieColours(KUBIE_MBR)[0]);
        System.out.println("          " + getKubieColours(KUBIE_MBM)[0]);

        // Print middle row of lower-middle faces
        System.out.print("      " + getKubieColours(KUBIE_BLD)[1]);
        System.out.print("    " + getKubieColours(KUBIE_FLD)[1]);
        System.out.print("    " + getKubieColours(KUBIE_FLD)[2]);
        System.out.print("    " + getKubieColours(KUBIE_FMD)[1]);
        System.out.print("    " + getKubieColours(KUBIE_FMD)[2]);
        System.out.print("    " + getKubieColours(KUBIE_FRD)[2]);
        System.out.print("    " + getKubieColours(KUBIE_FRD)[0]);
        System.out.print("    " + getKubieColours(KUBIE_BRD)[0]);
        System.out.print("    " + getKubieColours(KUBIE_BRD)[1]);
        System.out.println("    " + getKubieColours(KUBIE_BLD)[0]);

        // Print bottom row of lower-middle faces
        System.out.print("       " + getKubieColours(KUBIE_DBL)[2]);
        System.out.print("  " + getKubieColours(KUBIE_DFL)[1]);
        System.out.print("      " + getKubieColours(KUBIE_DFL)[2]);
        System.out.print("  " + getKubieColours(KUBIE_DFM)[1]);
        System.out.print("      " + getKubieColours(KUBIE_DFM)[2]);
        System.out.print("  " + getKubieColours(KUBIE_DFR)[2]);
        System.out.print("      " + getKubieColours(KUBIE_DFR)[1]);
        System.out.print("  " + getKubieColours(KUBIE_DBR)[1]);
        System.out.print("      " + getKubieColours(KUBIE_DBR)[2]);
        System.out.println("  " + getKubieColours(KUBIE_DBL)[1] + "\n");

        // Print bottom face
        System.out.print("                           ");
        System.out.println(getKubieColours(KUBIE_DFM)[0]);
        System.out.print("                        ");
        System.out.print(getKubieColours(KUBIE_DFL)[0] + "    ");
        System.out.println(getKubieColours(KUBIE_DFR)[0]);
        System.out.print("                         ");
        System.out.print(getKubieColours(KUBIE_DBL)[0] + "  ");
        System.out.println(getKubieColours(KUBIE_DBR)[0] + "\n");        
    }
}
