package models;

/**
 * A Kilominx, represented as an array of "kubies" (Kilominx cubies).
 */
public class Kilominx implements ITwistyPuzzle {

    /**
     * Colours on the Kilominx.
     * White, Dark Green, Red, Dark Blue, Yellow, Purple, Light Blue, Beige, Pink, Light Green, Orange, Grey
     */
    public static enum Colour implements IColour {
        Wh, DG, Re, DB, Ye, Pu, LB, Be, Pi, LG, Or, Gy;
        
        // TODO: if adding EDIT feature, then need to add colour values
        public int value() {
            return -1;
        }
    };

    // Kubies are indexed from 0 to 19 in the following order. The order itself is irrelevant, but the even/odd parity of the indices is important:
    public final static byte KUBIE_UFL = 0,   // even
                             KUBIE_UFR = 2,   // even
                             KUBIE_UBL = 1,   // odd
                             KUBIE_UBR = 4,   // even
                             KUBIE_UBM = 6,   // even
                             KUBIE_MFL = 3,   // odd
                             KUBIE_FMD = 8,   // even
                             KUBIE_MFR = 5,   // odd
                             KUBIE_FRD = 10,  // even
                             KUBIE_MBR = 7,   // odd
                             KUBIE_BRD = 12,  // even
                             KUBIE_MBM = 9,   // odd
                             KUBIE_BLD = 14,  // even
                             KUBIE_MBL = 11,  // odd
                             KUBIE_FLD = 16,  // even
                             KUBIE_DFM = 13,  // odd
                             KUBIE_DFL = 15,  // odd
                             KUBIE_DFR = 18,  // even
                             KUBIE_DBL = 17,  // odd 
                             KUBIE_DBR = 19;  // odd

    // Kubies are indexed from 0 to 19 in the following order, with the following initial colours:
    // 0,      2,      1,      4,      6,      3,      8,      5,      10,     7,      
    // UFL,    UFR,    UBL,    UBR,    UBM,    MFL,    FMD,    MFR,    FRD,    MBR,    
    // WhDGRe, WhReDB, WhDGPu, WhDbYe, WhPuYe, DGReBe, ReBePi, ReDBPi, DBPiLG, DBYeLG, 

    // 12,     9,      14,     11,     16,     13,     15,     18,     17,     19
    // BRD,    MBM,    BLD,    MBL,    FLD,    DFM,    DFL,    DFR,    DBL,    DBR
    // YeLGOr, YePuOr, PuOrLB, PuDGLB, DGLBBe, BePiGy, LBBeGy, PiLGGy, OrLBGy, LGOrGy

    // Kubies have an orientation of:
    //  - 0 (oriented),
    //  - 1 (colours turned CW),
    //  - or 2 (colours turned CCW)

    // The orientation of a kubie can be determined by comparing the orientation of the highest-priority colour (white/grey, then light/dark green, then red/orange) 
    // on the kubie compared to the orientation of the highest-priority face (the highest-priority-colour at that position on a solved Kilominx).
    //  - For example, the MFR kubie initally has colours ReDBPi, with the highest-priority colour being red, which is on the the front face.
    //    After performing the F move, the kubie at MFR has colours WhReDB, with the highest-priority colour being white, which is on the right face.
    //    The white colour of the kubie has been turned clockwise from the solved state's red colour, so the orientation of the kubie is 1.


    // Kilominx state is stored as an array of 20 kubies
    Cubie[] kubies = new Cubie[20];

    // moveController handles logic for making moves on the kilominx
    private KilominxController moveController;


    /**
     * Constructor for a Kilominx.
     * The Kilominx is initialised in the solved state, with the white face on top and the red face on front.
     */
    public Kilominx() {
        // Initialize kubies
        for (byte i = 0; i < 20; i++) {
            kubies[i] = new Cubie(i, (byte) 0);
        }

        moveController = new KilominxController(this);
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
            kubies[i] = new Cubie(otherIndices[i], otherOrientations[i]);
        }

        moveController = new KilominxController(this);
    }

    // TODO: if adding EDIT feature, then need to add:
    //  - public Kilominx() constructor from array of kubie colours
    //  - private int countSwaps() method (if needed still)
    //  - private Kubie kubieFromColours() method


    /**
     * Get the move controller for the kilominx.
     * @return The move controller for the kilominx.
     */
    public KilominxController getMoveController() {
        return moveController;
    }

    /**
     * Create a copy of the kilominx.
     * @return A copy of the kilominx.
     */
    public Kilominx copy() {
        return new Kilominx(this);
    }

    /**
     * Reset the kilominx to the solved state.
     * This should be used instead of creating a new Kilominx object to ensure the current kilominx is modified, rather than creating a new kilominx.
     */
    public void reset() {
        for (byte i = 0; i < 20; i++) {
            kubies[i].index = i;
            kubies[i].orientation = 0;
        }
    }

    /**
     * Get the indices of the kubies.
     * @return An array of the indices of the kubies.
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
     * Get the colours of a kubie.
     * @param posIndex - The position index of the kubie.
     * @return An array of the colours of the kubie.
     * The colours are returned in the order top/bottom, up-front-left/down-back-right, up-front-middle/down-back-middle, 
     * up-front-right/down-back-left, down-front-left/up-back-right, down-front-right/up-back-left.
     */
    private Colour[] getKubieColours(byte posIndex) {
        Cubie kubie = kubies[posIndex];
        Colour[] colours = new Colour[3];

        /* 
        Colour priorities: White/Grey, Light/Dark Green,     Red/Orange,          Light/Dark Blue,      Beige/Yellow,         Pink/Purple
        Face priorities:   Top/bottom, Left/Down-back-right, Front/Down-back-mid, Right/Down-back-left, Down-left/Back-right, Down-right/Back-left


        Orientation 0 means:
            The highest priority colour (i0) is on the highest priority face (i0 = 0).

            EVEN kubie, EVEN pos / ODD kubie, ODD pos:   i0 = 0, i1 = 1, i2 = 2
            EVEN kubie, ODD pos  / ODD kubie, EVEN pos:  i0 = 0, i1 = 2, i2 = 1


        Orientation 1 means:
            The highest priority colour (i0) is rotated CLOCKWISE from the highest priority face.
            
            EVEN kubie, EVEN pos:   i0 = 2, i1 = 0, i2 = 1
            EVEN kubie, ODD pos:    i0 = 1, i1 = 0, i2 = 2
            ODD kubie, EVEN pos:    i0 = 2, i1 = 1, i2 = 0
            ODD kubie, ODD pos:     i0 = 1, i1 = 2, i2 = 0
            

        Orientation 2 means:
            The highest priority colour (i0) is rotated ANTI-CLOCKWISE from the highest priority face.

            EVEN kubie, EVEN pos:   i0 = 1, i1 = 2, i2 = 0
            EVEN kubie, ODD pos:    i0 = 2, i1 = 1, i2 = 0
            ODD kubie, EVEN pos:    i0 = 1, i1 = 0, i2 = 2
            ODD kubie, ODD pos:     i0 = 2, i1 = 0, i2 = 1

        */

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
                i0 = 2;
                i1 = 0;
                i2 = 1;
            }
            else {
                i0 = 1;
                i1 = 2;
                i2 = 0;
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
                i0 = 1;
                i1 = 2;
                i2 = 0;
            }
            else {
                i0 = 2;
                i1 = 0;
                i2 = 1;
            }

            // corner is an ODD number of indices away, so the other two colours are flipped
            if ((kubie.index + posIndex) % 2 == 1) {
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

    // TODO: if adding EDIT feature, then need to add public Colour[] getColours() method

    /**
     * Print the Kilominx state to stdout in a human-readable format.
     */
    public void printState() {
        
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

    // TODO: if adding EDIT feature, then need to add public static void printEditState() method
}
