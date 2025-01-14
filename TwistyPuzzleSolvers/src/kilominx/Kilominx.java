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
    // Todo: Figure out what correct index order should be
    final byte KUBIE_UFL = 0,
               KUBIE_UFR = 1,
               KUBIE_UBL = 2,
               KUBIE_UBR = 3,
               KUBIE_UBM = 4,
               KUBIE_MFL = 5,
               KUBIE_FMD = 6,
               KUBIE_MFR = 7,
               KUBIE_FRD = 8,
               KUBIE_MBR = 9,
               KUBIE_BRD = 10,
               KUBIE_MBM = 11,
               KUBIE_BLD = 12,
               KUBIE_MBL = 13,
               KUBIE_FLD = 14,
               KUBIE_DFM = 15,
               KUBIE_DFL = 16,
               KUBIE_DFR = 17,
               KUBIE_DBL = 18,
               KUBIE_DBR = 19;


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

        byte i0, i1, i2;

        // correct orientation
        if (kubie.orientation == 0) {
            i0 = 0;
            i1 = 1;
            i2 = 2;

            // // corner is an odd number of indices away, so the other two colours are flipped
            // if ((kubie.index + posIndex) % 2 == 1) {
            //     byte temp = i1;
            //     i1 = i2;
            //     i2 = temp;
            // }
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

            // // corner is an odd number of indices away, so the other two colours are flipped
            // if ((kubie.index + posIndex) % 2 == 1) {
            //     byte temp = i1;
            //     i1 = i2;
            //     i2 = temp;
            // }
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

            // // corner is an EVEN number of indices away, so the other two colours are flipped
            // if ((kubie.index + posIndex) % 2 == 0) {
            //     byte temp = i1;
            //     i1 = i2;
            //     i2 = temp;
            // }
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
