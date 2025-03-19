package models;

import java.util.Arrays;
import java.util.stream.Stream;

/**
 * A Kilominx, represented as an array of "kubies" (Kilominx cubies).
 */
public class Kilominx implements ITwistyPuzzle {

    /**
     * Colours on the Kilominx.
     * White, Dark Green, Red, Dark Blue, Yellow, Purple, Light Blue, Beige, Pink, Light Green, Orange, Grey
     */
    public static enum Colour implements IColour {
        Wh(1), Gy(2048), DG(2), LG(512), Re(4), Or(1024), DB(8), LB(64), Ye(16), Be(128), Pu(32), Pi(256);

        private final int value;

        private Colour(int value) {
            this.value = value;
        }
        
        /**
         * Returns the "value" of the colour.
         * This is used for converting between a indices/orientations model type and a colours model type.
         * @return The value of the colour.
         */
        public int value() {
            return value;
        }
    };

    // Kubies are indexed from 0 to 19 in the following order. The order itself is irrelevant, but the even/odd parity of the indices is important:
    public final static byte KUBIE_UFL = 0,   // even - this kubie is always fixed in the same position and orientation
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
    // WhDGRe, WhReDB, WhDGPu, WhDBYe, WhPuYe, DGReBe, ReBePi, ReDBPi, DBPiLG, DBYeLG, 

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

    /**
     * Constructor for a Kilominx from an array of kubie colours. 
     * This constructor is used for creating a kilominx from Edit mode.
     * @param colours - The array of kubie colours.
     * @throws IllegalArgumentException if the kilominx state is invalid (invalid kilominx stickering, duplicate cubie(s), or parity errors).
     */
    public Kilominx(Colour[] colours) throws IllegalArgumentException {
        for (int i = 0; i < 20; i++) {
            kubies[i] = kubieFromColours(colours, i);
            if (kubies[i] == null) {
                throw new IllegalArgumentException("Invalid kilominx stickering.");
            }
        }

        // Check for duplicate kubies
        if (Stream.of(kubies).sorted().distinct().count() != 20) {
            throw new IllegalArgumentException("Duplicate kubie(s).");
        }
        // Kubie parity: the sum of the orientations of the kubies must be a multiple of 3
        if (Stream.of(kubies).mapToInt(cubie -> cubie.orientation).sum() % 3 != 0) {
            throw new IllegalArgumentException("Kubie parity error.");
        }
        // Permutation parity: the sum of the number of swaps needed to place the kubies in the correst positions must be even
        // https://puzzling.stackexchange.com/questions/53846/how-to-determine-whether-a-rubiks-cube-is-solvable (last accessed on 14-03-2025)
        if ((countSwaps(kubies)) % 2 != 0) {
            throw new IllegalArgumentException("Permutation parity error.");
        }

        moveController = new KilominxController(this);
    }

    /**
     * Count the number of swaps needed to place the kubies in their correct positions.
     * This method is used for checking the permutation parity of the kilominx.
     * @param kubies - The array of kubies to check.
     * @return The number of swaps needed to place the kubies in their correct positions.
     */
    private static int countSwaps(Cubie[] kubies) {
        byte[] positions = new byte[kubies.length];
        for (int i = 0; i < kubies.length; i++) {
            positions[i] = kubies[i].index;
        }

        int swaps = 0;
        for (int i = 0; i < positions.length; i++) {
            // If kubie not in correct position, swap it with the kubie in its correct position
            if (positions[i] != i) {
                byte temp = positions[i];
                positions[i] = positions[temp];
                positions[temp] = temp;

                swaps++;
                i--;
            }
        }
        return swaps;
    }

    /**
     * Get the kubie at the given position index by checking the colours of the cubie.
     * @param colours - The array of all cubie face colours.
     * @param index - The position index of the corner cubie.
     * @return The corner cubie at the given position index, or {@code null} if the cube state is invalid
     */
    private static Cubie kubieFromColours(Colour[] colours, int index) {
        // Get the three colours of the kubie at the given position index
        Colour[] kubieColours = new Colour[3];

        switch (index) {
            case KUBIE_UFL:
                kubieColours = new Colour[]{colours[3], colours[8], colours[9]}; break;
            case KUBIE_UFR:
                kubieColours = new Colour[]{colours[4], colours[10], colours[11]}; break;
            case KUBIE_UBL:
                kubieColours = new Colour[]{colours[1], colours[7], colours[6]}; break;
            case KUBIE_UBR:
                kubieColours = new Colour[]{colours[2], colours[12], colours[13]}; break;
            case KUBIE_UBM:
                kubieColours = new Colour[]{colours[0], colours[14], colours[5]}; break;
            case KUBIE_MFL:
                kubieColours = new Colour[]{colours[18], colours[19], colours[31]}; break;
            case KUBIE_FMD:
                kubieColours = new Colour[]{colours[27], colours[38], colours[39]}; break;
            case KUBIE_MFR:
                kubieColours = new Colour[]{colours[20], colours[21], colours[32]}; break;
            case KUBIE_FRD:
                kubieColours = new Colour[]{colours[41], colours[28], colours[40]}; break;
            case KUBIE_MBR:
                kubieColours = new Colour[]{colours[33], colours[22], colours[23]}; break;
            case KUBIE_BRD:
                kubieColours = new Colour[]{colours[42], colours[43], colours[29]}; break;
            case KUBIE_MBM:
                kubieColours = new Colour[]{colours[34], colours[24], colours[15]}; break;
            case KUBIE_BLD:
                kubieColours = new Colour[]{colours[44], colours[35], colours[25]}; break;
            case KUBIE_MBL:
                kubieColours = new Colour[]{colours[17], colours[30], colours[16]}; break;
            case KUBIE_FLD:
                kubieColours = new Colour[]{colours[26], colours[36], colours[37]}; break;
            case KUBIE_DFM:
                kubieColours = new Colour[]{colours[55], colours[48], colours[49]}; break;
            case KUBIE_DFL:
                kubieColours = new Colour[]{colours[56], colours[46], colours[47]}; break;
            case KUBIE_DFR:
                kubieColours = new Colour[]{colours[57], colours[51], colours[50]}; break;
            case KUBIE_DBL:
                kubieColours = new Colour[]{colours[58], colours[54], colours[45]}; break;
            case KUBIE_DBR:
                kubieColours = new Colour[]{colours[59], colours[52], colours[53]}; break;
        }

        // Add the values of the three colours together to get a unique value for the kubie
        int kubieValue = kubieColours[0].value() + kubieColours[1].value() + kubieColours[2].value();
        byte kubieIndex = (byte) -1;

        switch (kubieValue) {
            case 7:    kubieIndex = KUBIE_UFL; break; // WhDGRe
            case 13:   kubieIndex = KUBIE_UFR; break; // WhReDB
            case 35:   kubieIndex = KUBIE_UBL; break; // WhDGPu
            case 25:   kubieIndex = KUBIE_UBR; break; // WhDBYe
            case 49:   kubieIndex = KUBIE_UBM; break; // WhPuYe
            case 134:  kubieIndex = KUBIE_MFL; break; // DGReBe
            case 388:  kubieIndex = KUBIE_FMD; break; // ReBePi
            case 268:  kubieIndex = KUBIE_MFR; break; // ReDBPi
            case 776:  kubieIndex = KUBIE_FRD; break; // DBPiLG
            case 536:  kubieIndex = KUBIE_MBR; break; // DBYeLG
            case 1552: kubieIndex = KUBIE_BRD; break; // YeLGOr
            case 1072: kubieIndex = KUBIE_MBM; break; // YePuOr
            case 1120: kubieIndex = KUBIE_BLD; break; // PuOrLB
            case 98:   kubieIndex = KUBIE_MBL; break; // PuDGLB
            case 194:  kubieIndex = KUBIE_FLD; break; // DGLBBe
            case 2432: kubieIndex = KUBIE_DFM; break; // BePiGy
            case 2240: kubieIndex = KUBIE_DFL; break; // LBBeGy
            case 2816: kubieIndex = KUBIE_DFR; break; // PiLGGy
            case 3136: kubieIndex = KUBIE_DBL; break; // OrLBGy
            case 3584: kubieIndex = KUBIE_DBR; break; // LGOrGy
            default: return null; // Invalid kilominx state
        }

        // Determine the orientation of the kubie
        // Get the colours of the kubie in order of priority (default enum sorting)
        Colour[] sortedColours = new Colour[3];
        System.arraycopy(kubieColours, 0, sortedColours, 0, 3);
        Arrays.sort(sortedColours);

        // If highest priority colour is on the highest priority face, orientation is 0
        if (kubieColours[0] == sortedColours[0]) {
            return new Cubie(kubieIndex, (byte) 0);
        }

        switch (kubieIndex) {
            // For even kubies:
            case KUBIE_UFL:
            case KUBIE_UFR:
            case KUBIE_UBR:
            case KUBIE_UBM:
            case KUBIE_FMD:
            case KUBIE_FRD:
            case KUBIE_BRD:
            case KUBIE_BLD:
            case KUBIE_FLD:
            case KUBIE_DFR:
                // If second highest priority colour is on the highest priority face, orientation is 1, otherwise 2
                if (kubieColours[0] == sortedColours[1]) {
                    return new Cubie(kubieIndex, (byte) 1);
                }
                else {
                    return new Cubie(kubieIndex, (byte) 2);
                }
            
            // For odd kubies:
            case KUBIE_UBL:
            case KUBIE_MFL:
            case KUBIE_MFR:
            case KUBIE_MBR:
            case KUBIE_MBM:
            case KUBIE_MBL:
            case KUBIE_DFM:
            case KUBIE_DFL:
            case KUBIE_DBL: 
            case KUBIE_DBR:
                // If second highest priority colour is on the highest priority face, orientation is 2, otherwise 1
                if (kubieColours[0] == sortedColours[1]) {
                    return new Cubie(kubieIndex, (byte) 2);
                }
                else {
                    return new Cubie(kubieIndex, (byte) 1);
                }
            
            default: return null;   // Invalid index
        }
    }


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

    /**
     * Get the colours of the kubie faces on the kilominx.
     * @return An array of the colours of the kubie faces on the kilominx. 
     * The order of the colours is the same order which kubies are printed to the console (e.g. top face, upper row of side faces, etc.).
     */
    public Colour[] getColours() {
        Colour[] colours = new Colour[60];

        for (byte i = 0; i < 20; i++) {
            Colour[] kubieColours = getKubieColours(i);
            switch (i) {
                case KUBIE_UFL:
                    colours[3] = kubieColours[0];
                    colours[8] = kubieColours[1];
                    colours[9] = kubieColours[2];
                    break;
                case KUBIE_UFR:
                    colours[4] = kubieColours[0];
                    colours[10] = kubieColours[1];
                    colours[11] = kubieColours[2];
                    break;
                case KUBIE_UBL:
                    colours[1] = kubieColours[0];
                    colours[7] = kubieColours[1];
                    colours[6] = kubieColours[2];
                    break;
                case KUBIE_UBR:
                    colours[2] = kubieColours[0];
                    colours[12] = kubieColours[1];
                    colours[13] = kubieColours[2];
                    break;
                case KUBIE_UBM:
                    colours[0] = kubieColours[0];
                    colours[14] = kubieColours[1];
                    colours[5] = kubieColours[2];
                    break;
                case KUBIE_MFL:
                    colours[18] = kubieColours[0];
                    colours[19] = kubieColours[1];
                    colours[31] = kubieColours[2];
                    break;
                case KUBIE_FMD:
                    colours[27] = kubieColours[0];
                    colours[38] = kubieColours[1];
                    colours[39] = kubieColours[2];
                    break;
                case KUBIE_MFR:
                    colours[20] = kubieColours[0];
                    colours[21] = kubieColours[1];
                    colours[32] = kubieColours[2];
                    break;
                case KUBIE_FRD:
                    colours[41] = kubieColours[0];
                    colours[28] = kubieColours[1];
                    colours[40] = kubieColours[2];
                    break;
                case KUBIE_MBR:
                    colours[33] = kubieColours[0];
                    colours[22] = kubieColours[1];
                    colours[23] = kubieColours[2];
                    break;
                case KUBIE_BRD:
                    colours[42] = kubieColours[0];
                    colours[43] = kubieColours[1];
                    colours[29] = kubieColours[2];
                    break;
                case KUBIE_MBM:
                    colours[34] = kubieColours[0];
                    colours[24] = kubieColours[1];
                    colours[15] = kubieColours[2];
                    break;
                case KUBIE_BLD:
                    colours[44] = kubieColours[0];
                    colours[35] = kubieColours[1];
                    colours[25] = kubieColours[2];
                    break;
                case KUBIE_MBL:
                    colours[17] = kubieColours[0];
                    colours[30] = kubieColours[1];
                    colours[16] = kubieColours[2];
                    break;
                case KUBIE_FLD:
                    colours[26] = kubieColours[0];
                    colours[36] = kubieColours[1];
                    colours[37] = kubieColours[2];
                    break;
                case KUBIE_DFM:
                    colours[55] = kubieColours[0];
                    colours[48] = kubieColours[1];
                    colours[49] = kubieColours[2];
                    break;
                case KUBIE_DFL:
                    colours[56] = kubieColours[0];
                    colours[46] = kubieColours[1];
                    colours[47] = kubieColours[2];
                    break;
                case KUBIE_DFR:
                    colours[57] = kubieColours[0];
                    colours[51] = kubieColours[1];
                    colours[50] = kubieColours[2];
                    break;
                case KUBIE_DBL:
                    colours[58] = kubieColours[0];
                    colours[54] = kubieColours[1];
                    colours[45] = kubieColours[2];
                    break;
                case KUBIE_DBR:
                    colours[59] = kubieColours[0];
                    colours[52] = kubieColours[1];
                    colours[53] = kubieColours[2];
                    break;
            }
        }

        return colours;
    }

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

    /**
     * Print a version of the kilominx state for use during edit mode in a human-readable format.
     * @param colours - The array of kubie face colours. Represents a kilominx state that is being edited.
     * @param index - The index of the kubie face to highlight for editing. Set to -1 to not highlight any face.
     */
    public static void printEditState(Colour[] colours, int index) {
        
        // Print top face
        System.out.print("                           ");
        System.out.println((index == 0 ? "__" : colours[0].name()));
        System.out.print("                        ");
        System.out.print((index == 1 ? "__" : colours[1].name()) + "    ");
        System.out.println((index == 2 ? "__" : colours[2].name()));
        System.out.print("                         ");
        System.out.print((index == 3 ? "__" : colours[3].name()) + "  ");
        System.out.println((index == 4 ? "__" : colours[4].name()) + "\n");

        // Print upper row of upper-middle faces
        System.out.print(" " + (index == 5 ? "__" : colours[5].name()));
        System.out.print("  " + (index == 6 ? "__" : colours[6].name()));
        System.out.print("      " + (index == 7 ? "__" : colours[7].name()));
        System.out.print("  " + (index == 8 ? "__" : colours[8].name()));
        System.out.print("      " + (index == 9 ? "__" : colours[9].name()));
        System.out.print("  " + (index == 10 ? "__" : colours[10].name()));
        System.out.print("      " + (index == 11 ? "__" : colours[11].name()));
        System.out.print("  " + (index == 12 ? "__" : colours[12].name()));
        System.out.print("      " + (index == 13 ? "__" : colours[13].name()));
        System.out.println("  " + (index == 14 ? "__" : colours[14].name()));

        // Print middle row of upper-middle faces
        System.out.print((index == 15 ? "__" : colours[15].name()) + "    ");
        System.out.print((index == 16 ? "__" : colours[16].name()) + "    ");
        System.out.print((index == 17 ? "__" : colours[17].name()) + "    ");
        System.out.print((index == 18 ? "__" : colours[18].name()) + "    ");
        System.out.print((index == 19 ? "__" : colours[19].name()) + "    ");
        System.out.print((index == 20 ? "__" : colours[20].name()) + "    ");
        System.out.print((index == 21 ? "__" : colours[21].name()) + "    ");
        System.out.print((index == 22 ? "__" : colours[22].name()) + "    ");
        System.out.print((index == 23 ? "__" : colours[23].name()) + "    ");
        System.out.println((index == 24 ? "__" : colours[24].name()) + "    ");

        // Print bottom row of upper-middle faces
        System.out.print("   " + (index == 25 ? "__" : colours[25].name()));
        System.out.print("          " + (index == 26 ? "__" : colours[26].name()));
        System.out.print("          " + (index == 27 ? "__" : colours[27].name()));
        System.out.print("          " + (index == 28 ? "__" : colours[28].name()));
        System.out.println("          " + (index == 29 ? "__" : colours[29].name()) + "\n");

        // Print upper row of lower-middle faces
        System.out.print("         " + (index == 30 ? "__" : colours[30].name()));
        System.out.print("          " + (index == 31 ? "__" : colours[31].name()));
        System.out.print("          " + (index == 32 ? "__" : colours[32].name()));
        System.out.print("          " + (index == 33 ? "__" : colours[33].name()));
        System.out.println("          " + (index == 34 ? "__" : colours[34].name()));

        // Print middle row of lower-middle faces
        System.out.print("      " + (index == 35 ? "__" : colours[35].name()));
        System.out.print("    " + (index == 36 ? "__" : colours[36].name()));
        System.out.print("    " + (index == 37 ? "__" : colours[37].name()));
        System.out.print("    " + (index == 38 ? "__" : colours[38].name()));
        System.out.print("    " + (index == 39 ? "__" : colours[39].name()));
        System.out.print("    " + (index == 40 ? "__" : colours[40].name()));
        System.out.print("    " + (index == 41 ? "__" : colours[41].name()));
        System.out.print("    " + (index == 42 ? "__" : colours[42].name()));
        System.out.print("    " + (index == 43 ? "__" : colours[43].name()));
        System.out.println("    " + (index == 44 ? "__" : colours[44].name()));

        // Print bottom row of lower-middle faces
        System.out.print("       " + (index == 45 ? "__" : colours[45].name()));
        System.out.print("  " + (index == 46 ? "__" : colours[46].name()));
        System.out.print("      " + (index == 47 ? "__" : colours[47].name()));
        System.out.print("  " + (index == 48 ? "__" : colours[48].name()));
        System.out.print("      " + (index == 49 ? "__" : colours[49].name()));
        System.out.print("  " + (index == 50 ? "__" : colours[50].name()));
        System.out.print("      " + (index == 51 ? "__" : colours[51].name()));
        System.out.print("  " + (index == 52 ? "__" : colours[52].name()));
        System.out.print("      " + (index == 53 ? "__" : colours[53].name()));
        System.out.println("  " + (index == 54 ? "__" : colours[54].name()) + "\n");

        // Print bottom face
        System.out.print("                           ");
        System.out.println((index == 55 ? "__" : colours[55].name()));
        System.out.print("                        ");
        System.out.print((index == 56 ? "__" : colours[56].name()) + "    ");
        System.out.println((index == 57 ? "__" : colours[57].name()));
        System.out.print("                         ");
        System.out.print((index == 58 ? "__" : colours[58].name()) + "  ");
        System.out.println((index == 59 ? "__" : colours[59].name()) + "\n"); 
    }
}
