import java.util.Scanner;


public class Cube {

    enum Colour {
        W, Y, R, O, G, B
    };

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

    final byte CORNER_ULB = 0,
               CORNER_URB = 1,
               CORNER_URF = 2,
               CORNER_ULF = 3,
               CORNER_DLF = 4,
               CORNER_DLB = 5,
               CORNER_DRB = 6,
               CORNER_DRF = 7;


    public static void main(String[] args) {
        Cube cube = new Cube();

        Scanner sc = new Scanner(System.in);
        String input = "";

        while (!input.equals("QUIT")) {
            cube.printCubeState();

            System.out.println("Make a move:");
            input = sc.nextLine().toUpperCase();

            switch (input) {
                case "U":
                    cube.moveU();
                    break;
                case "U'":
                    cube.moveUPrime();
                    break;
                case "U2":
                    cube.moveU2();
                    break;

                case "L":
                    cube.moveL();
                    break;
                case "L'":
                    cube.moveLPrime();
                    break;
                case "L2":
                    cube.moveL2();
                    break;

                case "F":
                    cube.moveF();
                    break;
                case "F'":
                    cube.moveFPrime();
                    break;
                case "F2":
                    cube.moveF2();
                    break;

                case "R":
                    cube.moveR();
                    break;
                case "R'":
                    cube.moveRPrime();
                    break;
                case "R2":
                    cube.moveR2();
                    break;

                case "B":
                    cube.moveB();
                    break;
                case "B'":
                    cube.moveBPrime();
                    break;
                case "B2":
                    cube.moveB2();
                    break;

                case "D":
                    cube.moveD();
                    break;
                case "D'":
                    cube.moveDPrime();
                    break;
                case "D2":
                    cube.moveD2();
                    break;

                case "QUIT":
                    break;

                default:
                    System.out.println("ERROR: '" + input + "' is not a valid move.");
                    break;
            }
        }

        sc.close();
    }


    // Default cube orientation has white on top, red on front

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

        byte index, orientation;
    }

    // Cube state is stored as an array of 12 edge cubies and an array of 8 corner cubies
    private Cubie[] edgeCubies = new Cubie[12];
    private Cubie[] cornerCubies = new Cubie[8];


    // Initialize a solved cube
    public Cube() {

        // Initialize edge cubies
        for (byte i = 0; i < 12; i++) {
            edgeCubies[i] = new Cubie();
            edgeCubies[i].index = i;
            edgeCubies[i].orientation = 0;
        }

        // Initialize corner cubies
        for (byte i = 0; i < 8; i++) {
            cornerCubies[i] = new Cubie();
            cornerCubies[i].index = i;
            cornerCubies[i].orientation = 0;
        }
    }


    public byte[] getEdgeIndices() {
        byte[] indices = new byte[12];

        for (byte i = 0; i < 12; i++) {
            indices[i] = edgeCubies[i].index;
        }

        return indices;
    }

    public byte[] getEdgeOrientations() {
        byte[] orientations = new byte[12];

        for (byte i = 0; i < 12; i++) {
            orientations[i] = edgeCubies[i].orientation;
        }

        return orientations;
    }
    
    public byte[] getCornerIndices() {
        byte[] indices = new byte[8];

        for (byte i = 0; i < 8; i++) {
            indices[i] = cornerCubies[i].index;
        }

        return indices;
    }

    public byte[] getCornerOrientations() {
        byte[] orientations = new byte[8];

        for (byte i = 0; i < 8; i++) {
            orientations[i] = cornerCubies[i].orientation;
        }

        return orientations;
    }


    public boolean isSolved() {
        
        for (byte i = 0; i < cornerCubies.length; i++) {
            if (cornerCubies[i].index != i || cornerCubies[i].orientation != 0) {
                return false;
            }
        }

        for (byte i = 0; i < edgeCubies.length; i++) {
            if (edgeCubies[i].index != i || edgeCubies[i].orientation != 0) {
                return false;
            }
        }

        return true;
    }


    public void flipEdgeOrientation(byte index) {
        Cubie edge = edgeCubies[index];
        edge.orientation ^= 1;  // XOR orientation with 1 results in flip between 0 and 1
    }

    public void increaseCornerOrientation(byte index, byte incr) {
        Cubie corner = cornerCubies[index];

        // faster equivalent to corner.orientation = (corner.orientation + incr) % 3
        if (corner.orientation + incr == 3) {
            corner.orientation = 0;
        }
        else if (corner.orientation + incr == 4) {
            corner.orientation = 1;
        }
    }


    public Colour[] getEdgeColours(byte posIndex) {
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

    public Colour[] getCornerColours(byte posIndex) {
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



    /* Print the cube state in the following format:
               U U U
               U U U
               U U U
        L L L  F F F  R R R  B B B
        L L L  F F F  R R R  B B B
        L L L  F F F  R R R  B B B
               D D D
               D D D
               D D D
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