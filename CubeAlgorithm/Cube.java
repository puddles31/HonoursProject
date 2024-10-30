package CubeAlgorithm;
import java.util.Scanner;

/**
 * Cube
 */
public class Cube {

    enum Colour {
        W, R, G, B, Y, O
    };

    final int EDGE_UL = 0,
              EDGE_UF = 1,
              EDGE_UR = 2,
              EDGE_UB = 3,
              EDGE_FL = 4,
              EDGE_FR = 5,
              EDGE_BR = 6,
              EDGE_BL = 7,
              EDGE_DF = 8,
              EDGE_DR = 9,
              EDGE_DB = 10,
              EDGE_DL = 11;

    final int CORNER_UFL = 0,
              CORNER_UFR = 1,
              CORNER_UBR = 2,
              CORNER_UBL = 3,
              CORNER_DFL = 4,
              CORNER_DFR = 5,
              CORNER_DBR = 6,
              CORNER_DBL = 7;

    /*
     
    Todo:
        - test with an interactive mode
        - begin pattern databases / lehmer codes
     */



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
        // UL, UF, UR, UB, FL, FR, BR, BL, DF, DR, DB, DL
        // WG, WR, WB, WY, RG, RB, YB, YG, OR, OB, OY, OG

        // Edge cubies have an orientation of 0 (oriented) or 1 (flipped)
        // (an edge is "oriented" if it can be solved using only R, L, U, D moves)

        // Corner cubies are indexed from 0 to 7 in the following order, with the following initial colours:
        // UFL, UFR, UBR, UBL, DFL, DFR, DBR, DBL
        // WRG, WRB, WYB, WYG, ORG, ORB, OYB, OYG

        // Corner cubies have an orientation of 0 (oriented - red/orange on top/bottom), 1 (red/orange clockwise from nearest up/down face), or 2 (red/orange counter-clockwise from nearest up/down face)

        int index, orientation;
    }

    // Cube state is stored as an array of 12 edge cubies and an array of 8 corner cubies
    private Cubie[] edgeCubies = new Cubie[12];
    private Cubie[] cornerCubies = new Cubie[8];


    // Initialize a solved cube
    public Cube() {

        // Initialize edge cubies
        for (int i = 0; i < 12; i++) {
            edgeCubies[i] = new Cubie();
            edgeCubies[i].index = i;
            edgeCubies[i].orientation = 0;
        }

        // Initialize corner cubies
        for (int i = 0; i < 8; i++) {
            cornerCubies[i] = new Cubie();
            cornerCubies[i].index = i;
            cornerCubies[i].orientation = 0;
        }
    }


    public void flipEdgeOrientation(int index) {
        Cubie edge = edgeCubies[index];
        edge.orientation ^= 1;  // XOR orientation with 1 results in flip between 0 and 1
    }

    public void increaseCornerOrientation(int index, int incr) {
        Cubie corner = cornerCubies[index];
        corner.orientation = (corner.orientation + incr) % 3;
    }


    public Colour[] getEdgeColours(int posIndex) {
        Cubie edge = edgeCubies[posIndex];
        Colour[] colours = new Colour[2];

        switch (edge.index) {
            case EDGE_UL:
                colours[0] = Colour.W;
                colours[1] = Colour.G;
                break;
                
            case EDGE_UF:
                colours[0] = Colour.W;
                colours[1] = Colour.R;
                break;
                
            case EDGE_UR:
                colours[0] = Colour.W;
                colours[1] = Colour.B;
                break;
                
            case EDGE_UB:
                colours[0] = Colour.W;
                colours[1] = Colour.Y;
                break;
                
            case EDGE_FL:
                colours[0] = Colour.R;
                colours[1] = Colour.G;
                break;
                
            case EDGE_FR:
                colours[0] = Colour.R;
                colours[1] = Colour.B;
                break;
                
            case EDGE_BR:
                colours[0] = Colour.Y;
                colours[1] = Colour.B;
                break;
                
            case EDGE_BL:
                colours[0] = Colour.Y;
                colours[1] = Colour.G;
                break;
                
            case EDGE_DF:
                colours[0] = Colour.O;
                colours[1] = Colour.R;
                break;
                
            case EDGE_DR:
                colours[0] = Colour.O;
                colours[1] = Colour.B;
                break;
                
            case EDGE_DB:
                colours[0] = Colour.O;
                colours[1] = Colour.Y;
                break;
                
            case EDGE_DL:
                colours[0] = Colour.O;
                colours[1] = Colour.G;
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

    public Colour[] getCornerColours(int posIndex) {
        Cubie corner = cornerCubies[posIndex];
        Colour[] colours = new Colour[3];

        int i0, i1, i2;

        if (corner.orientation == 0) {
            i0 = 0;
            i1 = 1;
            i2 = 2;

            if ((corner.index + posIndex) % 2 == 1) {
                int temp = i1;
                i1 = i2;
                i2 = temp;
            }
        }
        else if (corner.orientation == 1) {
            i0 = 1;
            i1 = 2;
            i2 = 0;

            if ((corner.index + posIndex) % 2 == 1) {
                int temp = i0;
                i0 = i1;
                i1 = temp;
            }
        }
        else {
            i0 = 2;
            i1 = 0;
            i2 = 1;

            if ((corner.index + posIndex) % 2 == 1) {
                int temp = i0;
                i0 = i2;
                i2 = temp;
            }
        }


        switch (corner.index) {
            case CORNER_UFL:
                colours[i0] = Colour.W;
                colours[i1] = Colour.R;
                colours[i2] = Colour.G;
                break;

            case CORNER_UFR:
                colours[i0] = Colour.W;
                colours[i1] = Colour.R;
                colours[i2] = Colour.B;
                break;

            case CORNER_UBR:
                colours[i0] = Colour.W;
                colours[i1] = Colour.Y;
                colours[i2] = Colour.B;
                break;

            case CORNER_UBL:
                colours[i0] = Colour.W;
                colours[i1] = Colour.Y;
                colours[i2] = Colour.G;
                break;

            case CORNER_DFL:
                colours[i0] = Colour.O;
                colours[i1] = Colour.R;
                colours[i2] = Colour.G;
                break;

            case CORNER_DFR:
                colours[i0] = Colour.O;
                colours[i1] = Colour.R;
                colours[i2] = Colour.B;
                break;

            case CORNER_DBR:
                colours[i0] = Colour.O;
                colours[i1] = Colour.Y;
                colours[i2] = Colour.B;
                break;

            case CORNER_DBL:
                colours[i0] = Colour.O;
                colours[i1] = Colour.Y;
                colours[i2] = Colour.G;
                break;

        }

        return colours;
    }


    public void moveU() {
        Cubie temp               = cornerCubies[CORNER_UFL];
        cornerCubies[CORNER_UFL] = cornerCubies[CORNER_UFR];
        cornerCubies[CORNER_UFR] = cornerCubies[CORNER_UBR];
        cornerCubies[CORNER_UBR] = cornerCubies[CORNER_UBL];
        cornerCubies[CORNER_UBL] = temp;

        temp                = edgeCubies[EDGE_UL];
        edgeCubies[EDGE_UL] = edgeCubies[EDGE_UF];
        edgeCubies[EDGE_UF] = edgeCubies[EDGE_UR];
        edgeCubies[EDGE_UR] = edgeCubies[EDGE_UB];
        edgeCubies[EDGE_UB] = temp;
    }

    public void moveUPrime() {
        Cubie temp               = cornerCubies[CORNER_UFL];
        cornerCubies[CORNER_UFL] = cornerCubies[CORNER_UBL];
        cornerCubies[CORNER_UBL] = cornerCubies[CORNER_UBR];
        cornerCubies[CORNER_UBR] = cornerCubies[CORNER_UFR];
        cornerCubies[CORNER_UFR] = temp;

        temp                = edgeCubies[EDGE_UL];
        edgeCubies[EDGE_UL] = edgeCubies[EDGE_UB];
        edgeCubies[EDGE_UB] = edgeCubies[EDGE_UR];
        edgeCubies[EDGE_UR] = edgeCubies[EDGE_UF];
        edgeCubies[EDGE_UF] = temp;
    }

    public void moveU2() {
        Cubie temp               = cornerCubies[CORNER_UFL];
        cornerCubies[CORNER_UFL] = cornerCubies[CORNER_UBR];
        cornerCubies[CORNER_UBR] = temp;

        temp                     = cornerCubies[CORNER_UFR];
        cornerCubies[CORNER_UFR] = cornerCubies[CORNER_UBL];
        cornerCubies[CORNER_UBL] = temp;

        temp                = edgeCubies[EDGE_UL];
        edgeCubies[EDGE_UL] = edgeCubies[EDGE_UR];
        edgeCubies[EDGE_UR] = temp;

        temp                = edgeCubies[EDGE_UF];
        edgeCubies[EDGE_UF] = edgeCubies[EDGE_UB];
        edgeCubies[EDGE_UB] = temp;
    }


    public void moveL() {
        Cubie temp               = cornerCubies[CORNER_UFL];
        cornerCubies[CORNER_UFL] = cornerCubies[CORNER_UBL];
        cornerCubies[CORNER_UBL] = cornerCubies[CORNER_DBL];
        cornerCubies[CORNER_DBL] = cornerCubies[CORNER_DFL];
        cornerCubies[CORNER_DFL] = temp;

        temp                = edgeCubies[EDGE_UL];
        edgeCubies[EDGE_UL] = edgeCubies[EDGE_BL];
        edgeCubies[EDGE_BL] = edgeCubies[EDGE_DL];
        edgeCubies[EDGE_DL] = edgeCubies[EDGE_FL];
        edgeCubies[EDGE_FL] = temp;

        increaseCornerOrientation(CORNER_UFL, 1);
        increaseCornerOrientation(CORNER_UBL, 2);
        increaseCornerOrientation(CORNER_DBL, 1);
        increaseCornerOrientation(CORNER_DFL, 2);
    }

    public void moveLPrime() {
        Cubie temp               = cornerCubies[CORNER_UFL];
        cornerCubies[CORNER_UFL] = cornerCubies[CORNER_DFL];
        cornerCubies[CORNER_DFL] = cornerCubies[CORNER_DBL];
        cornerCubies[CORNER_DBL] = cornerCubies[CORNER_UBL];
        cornerCubies[CORNER_UBL] = temp;

        temp                = edgeCubies[EDGE_UL];
        edgeCubies[EDGE_UL] = edgeCubies[EDGE_FL];
        edgeCubies[EDGE_FL] = edgeCubies[EDGE_DL];
        edgeCubies[EDGE_DL] = edgeCubies[EDGE_BL];
        edgeCubies[EDGE_BL] = temp;

        increaseCornerOrientation(CORNER_UFL, 1);
        increaseCornerOrientation(CORNER_UBL, 2);
        increaseCornerOrientation(CORNER_DBL, 1);
        increaseCornerOrientation(CORNER_DFL, 2);
    }

    public void moveL2() {
        Cubie temp               = cornerCubies[CORNER_UFL];
        cornerCubies[CORNER_UFL] = cornerCubies[CORNER_DBL];
        cornerCubies[CORNER_DBL] = temp;

        temp                     = cornerCubies[CORNER_UBL];
        cornerCubies[CORNER_UBL] = cornerCubies[CORNER_DFL];
        cornerCubies[CORNER_DFL] = temp;

        temp                = edgeCubies[EDGE_UL];
        edgeCubies[EDGE_UL] = edgeCubies[EDGE_DL];
        edgeCubies[EDGE_DL] = temp;

        temp                = edgeCubies[EDGE_BL];
        edgeCubies[EDGE_BL] = edgeCubies[EDGE_FL];
        edgeCubies[EDGE_FL] = temp;
    }


    public void moveF() {
        Cubie temp               = cornerCubies[CORNER_UFL];
        cornerCubies[CORNER_UFL] = cornerCubies[CORNER_DFL];
        cornerCubies[CORNER_DFL] = cornerCubies[CORNER_DFR];
        cornerCubies[CORNER_DFR] = cornerCubies[CORNER_UFR];
        cornerCubies[CORNER_UFR] = temp;

        temp                = edgeCubies[EDGE_FL];
        edgeCubies[EDGE_FL] = edgeCubies[EDGE_DF];
        edgeCubies[EDGE_DF] = edgeCubies[EDGE_FR];
        edgeCubies[EDGE_FR] = edgeCubies[EDGE_UF];
        edgeCubies[EDGE_UF] = temp;

        increaseCornerOrientation(CORNER_UFL, 2);
        increaseCornerOrientation(CORNER_DFL, 1);
        increaseCornerOrientation(CORNER_DFR, 2);
        increaseCornerOrientation(CORNER_UFR, 1);

        flipEdgeOrientation(EDGE_FL);
        flipEdgeOrientation(EDGE_DF);
        flipEdgeOrientation(EDGE_FR);
        flipEdgeOrientation(EDGE_UF);
    }

    public void moveFPrime() {
        Cubie temp               = cornerCubies[CORNER_UFL];
        cornerCubies[CORNER_UFL] = cornerCubies[CORNER_UFR];
        cornerCubies[CORNER_UFR] = cornerCubies[CORNER_DFR];
        cornerCubies[CORNER_DFR] = cornerCubies[CORNER_DFL];
        cornerCubies[CORNER_DFL] = temp;

        temp                = edgeCubies[EDGE_FL];
        edgeCubies[EDGE_FL] = edgeCubies[EDGE_UF];
        edgeCubies[EDGE_UF] = edgeCubies[EDGE_FR];
        edgeCubies[EDGE_FR] = edgeCubies[EDGE_DF];
        edgeCubies[EDGE_DF] = temp;

        increaseCornerOrientation(CORNER_UFL, 2);
        increaseCornerOrientation(CORNER_DFL, 1);
        increaseCornerOrientation(CORNER_DFR, 2);
        increaseCornerOrientation(CORNER_UFR, 1);

        flipEdgeOrientation(EDGE_FL);
        flipEdgeOrientation(EDGE_DF);
        flipEdgeOrientation(EDGE_FR);
        flipEdgeOrientation(EDGE_UF);
    }

    public void moveF2() {
        Cubie temp               = cornerCubies[CORNER_UFL];
        cornerCubies[CORNER_UFL] = cornerCubies[CORNER_DFR];
        cornerCubies[CORNER_DFR] = temp;

        temp                     = cornerCubies[CORNER_DFL];
        cornerCubies[CORNER_DFL] = cornerCubies[CORNER_UFR];
        cornerCubies[CORNER_UFR] = temp;

        temp                = edgeCubies[EDGE_FL];
        edgeCubies[EDGE_FL] = edgeCubies[EDGE_FR];
        edgeCubies[EDGE_FR] = temp;

        temp                = edgeCubies[EDGE_UF];
        edgeCubies[EDGE_UF] = edgeCubies[EDGE_DF];
        edgeCubies[EDGE_DF] = temp;
    }


    public void moveR() {
        Cubie temp               = cornerCubies[CORNER_UFR];
        cornerCubies[CORNER_UFR] = cornerCubies[CORNER_DFR];
        cornerCubies[CORNER_DFR] = cornerCubies[CORNER_DBR];
        cornerCubies[CORNER_DBR] = cornerCubies[CORNER_UBR];
        cornerCubies[CORNER_UBR] = temp;

        temp                = edgeCubies[EDGE_UR];
        edgeCubies[EDGE_UR] = edgeCubies[EDGE_FR];
        edgeCubies[EDGE_FR] = edgeCubies[EDGE_DR];
        edgeCubies[EDGE_DR] = edgeCubies[EDGE_BR];
        edgeCubies[EDGE_BR] = temp;

        increaseCornerOrientation(CORNER_UFR, 2);
        increaseCornerOrientation(CORNER_DFR, 1);
        increaseCornerOrientation(CORNER_DBR, 2);
        increaseCornerOrientation(CORNER_UBR, 1);
    }

    public void moveRPrime() {
        Cubie temp               = cornerCubies[CORNER_UFR];
        cornerCubies[CORNER_UFR] = cornerCubies[CORNER_UBR];
        cornerCubies[CORNER_UBR] = cornerCubies[CORNER_DBR];
        cornerCubies[CORNER_DBR] = cornerCubies[CORNER_DFR];
        cornerCubies[CORNER_DFR] = temp;

        temp                = edgeCubies[EDGE_UR];
        edgeCubies[EDGE_UR] = edgeCubies[EDGE_BR];
        edgeCubies[EDGE_BR] = edgeCubies[EDGE_DR];
        edgeCubies[EDGE_DR] = edgeCubies[EDGE_FR];
        edgeCubies[EDGE_FR] = temp;

        increaseCornerOrientation(CORNER_UFR, 2);
        increaseCornerOrientation(CORNER_DFR, 1);
        increaseCornerOrientation(CORNER_DBR, 2);
        increaseCornerOrientation(CORNER_UBR, 1);
    }

    public void moveR2() {
        Cubie temp               = cornerCubies[CORNER_UFR];
        cornerCubies[CORNER_UFR] = cornerCubies[CORNER_DBR];
        cornerCubies[CORNER_DBR] = temp;

        temp                     = cornerCubies[CORNER_DFR];
        cornerCubies[CORNER_DFR] = cornerCubies[CORNER_UBR];
        cornerCubies[CORNER_UBR] = temp;

        temp                = edgeCubies[EDGE_UR];
        edgeCubies[EDGE_UR] = edgeCubies[EDGE_DR];
        edgeCubies[EDGE_DR] = temp;

        temp                = edgeCubies[EDGE_FR];
        edgeCubies[EDGE_FR] = edgeCubies[EDGE_BR];
        edgeCubies[EDGE_BR] = temp;
    }


    public void moveB() {
        Cubie temp               = cornerCubies[CORNER_UBR];
        cornerCubies[CORNER_UBR] = cornerCubies[CORNER_DBR];
        cornerCubies[CORNER_DBR] = cornerCubies[CORNER_DBL];
        cornerCubies[CORNER_DBL] = cornerCubies[CORNER_UBL];
        cornerCubies[CORNER_UBL] = temp;

        temp                = edgeCubies[EDGE_UB];
        edgeCubies[EDGE_UB] = edgeCubies[EDGE_BR];
        edgeCubies[EDGE_BR] = edgeCubies[EDGE_DB];
        edgeCubies[EDGE_DB] = edgeCubies[EDGE_BL];
        edgeCubies[EDGE_BL] = temp;

        increaseCornerOrientation(CORNER_UBR, 2);
        increaseCornerOrientation(CORNER_DBR, 1);
        increaseCornerOrientation(CORNER_DBL, 2);
        increaseCornerOrientation(CORNER_UBL, 1);

        flipEdgeOrientation(EDGE_UB);
        flipEdgeOrientation(EDGE_BR);
        flipEdgeOrientation(EDGE_DB);
        flipEdgeOrientation(EDGE_BL);
    }

    public void moveBPrime() {
        Cubie temp               = cornerCubies[CORNER_UBR];
        cornerCubies[CORNER_UBR] = cornerCubies[CORNER_UBL];
        cornerCubies[CORNER_UBL] = cornerCubies[CORNER_DBL];
        cornerCubies[CORNER_DBL] = cornerCubies[CORNER_DBR];
        cornerCubies[CORNER_DBR] = temp;

        temp                = edgeCubies[EDGE_UB];
        edgeCubies[EDGE_UB] = edgeCubies[EDGE_BL];
        edgeCubies[EDGE_BL] = edgeCubies[EDGE_DB];
        edgeCubies[EDGE_DB] = edgeCubies[EDGE_BR];
        edgeCubies[EDGE_BR] = temp;

        increaseCornerOrientation(CORNER_UBR, 2);
        increaseCornerOrientation(CORNER_DBR, 1);
        increaseCornerOrientation(CORNER_DBL, 2);
        increaseCornerOrientation(CORNER_UBL, 1);

        flipEdgeOrientation(EDGE_UB);
        flipEdgeOrientation(EDGE_BR);
        flipEdgeOrientation(EDGE_DB);
        flipEdgeOrientation(EDGE_BL);
    }

    public void moveB2() {
        Cubie temp               = cornerCubies[CORNER_UBR];
        cornerCubies[CORNER_UBR] = cornerCubies[CORNER_DBL];
        cornerCubies[CORNER_DBL] = temp;

        temp                     = cornerCubies[CORNER_DBR];
        cornerCubies[CORNER_DBR] = cornerCubies[CORNER_UBL];
        cornerCubies[CORNER_UBL] = temp;

        temp                = edgeCubies[EDGE_UB];
        edgeCubies[EDGE_UB] = edgeCubies[EDGE_DB];
        edgeCubies[EDGE_DB] = temp;

        temp                = edgeCubies[EDGE_BR];
        edgeCubies[EDGE_BR] = edgeCubies[EDGE_BL];
        edgeCubies[EDGE_BL] = temp;
    }


    public void moveD() {
        Cubie temp               = cornerCubies[CORNER_DFL];
        cornerCubies[CORNER_DFL] = cornerCubies[CORNER_DBL];
        cornerCubies[CORNER_DBL] = cornerCubies[CORNER_DBR];
        cornerCubies[CORNER_DBR] = cornerCubies[CORNER_DFR];
        cornerCubies[CORNER_DFR] = temp;

        temp                = edgeCubies[EDGE_DL];
        edgeCubies[EDGE_DL] = edgeCubies[EDGE_DB];
        edgeCubies[EDGE_DB] = edgeCubies[EDGE_DR];
        edgeCubies[EDGE_DR] = edgeCubies[EDGE_DF];
        edgeCubies[EDGE_DF] = temp;
    }

    public void moveDPrime() {
        Cubie temp               = cornerCubies[CORNER_DFL];
        cornerCubies[CORNER_DFL] = cornerCubies[CORNER_DFR];
        cornerCubies[CORNER_DFR] = cornerCubies[CORNER_DBR];
        cornerCubies[CORNER_DBR] = cornerCubies[CORNER_DBL];
        cornerCubies[CORNER_DBL] = temp;

        temp                = edgeCubies[EDGE_DL];
        edgeCubies[EDGE_DL] = edgeCubies[EDGE_DF];
        edgeCubies[EDGE_DF] = edgeCubies[EDGE_DR];
        edgeCubies[EDGE_DR] = edgeCubies[EDGE_DB];
        edgeCubies[EDGE_DB] = temp;
    }

    public void moveD2() {
        Cubie temp               = cornerCubies[CORNER_DFL];
        cornerCubies[CORNER_DFL] = cornerCubies[CORNER_DBR];
        cornerCubies[CORNER_DBR] = temp;

        temp                     = cornerCubies[CORNER_DFR];
        cornerCubies[CORNER_DFR] = cornerCubies[CORNER_DBL];
        cornerCubies[CORNER_DBL] = temp;

        temp                = edgeCubies[EDGE_DL];
        edgeCubies[EDGE_DL] = edgeCubies[EDGE_DR];
        edgeCubies[EDGE_DR] = temp;

        temp                = edgeCubies[EDGE_DF];
        edgeCubies[EDGE_DF] = edgeCubies[EDGE_DB];
        edgeCubies[EDGE_DB] = temp;
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
        System.out.print(getCornerColours(3)[0] + " ");
        System.out.print(getEdgeColours(3)[0] + " ");
        System.out.println(getCornerColours(2)[0] + " ");

        System.out.print("       ");
        System.out.print(getEdgeColours(0)[0] + " ");
        System.out.print("W ");
        System.out.println(getEdgeColours(2)[0] + " ");

        System.out.print("       ");
        System.out.print(getCornerColours(0)[0] + " ");
        System.out.print(getEdgeColours(1)[0] + " ");
        System.out.println(getCornerColours(1)[0] + "\n");

        // Print upper row of side faces
        System.out.print(getCornerColours(3)[2] + " ");
        System.out.print(getEdgeColours(0)[1] + " ");
        System.out.print(getCornerColours(0)[2] + "  ");

        System.out.print(getCornerColours(0)[1] + " ");
        System.out.print(getEdgeColours(1)[1] + " ");
        System.out.print(getCornerColours(1)[1] + "  ");

        System.out.print(getCornerColours(1)[2] + " ");
        System.out.print(getEdgeColours(2)[1] + " ");
        System.out.print(getCornerColours(2)[2] + "  ");

        System.out.print(getCornerColours(2)[1] + " ");
        System.out.print(getEdgeColours(3)[1] + " ");
        System.out.println(getCornerColours(3)[1]);

        // Print middle row of side faces
        System.out.print(getEdgeColours(7)[1] + " ");
        System.out.print("G ");
        System.out.print(getEdgeColours(4)[1] + "  ");

        System.out.print(getEdgeColours(4)[0] + " ");
        System.out.print("R ");
        System.out.print(getEdgeColours(5)[0] + "  ");
        
        System.out.print(getEdgeColours(5)[1] + " ");
        System.out.print("B ");
        System.out.print(getEdgeColours(6)[1] + "  ");

        System.out.print(getEdgeColours(6)[0] + " ");
        System.out.print("Y ");
        System.out.println(getEdgeColours(7)[0]);

        // Print bottom row of side faces
        System.out.print(getCornerColours(7)[2] + " ");
        System.out.print(getEdgeColours(11)[1] + " ");
        System.out.print(getCornerColours(4)[2] + "  ");

        System.out.print(getCornerColours(4)[1] + " ");
        System.out.print(getEdgeColours(8)[1] + " ");
        System.out.print(getCornerColours(5)[1] + "  ");

        System.out.print(getCornerColours(5)[2] + " ");
        System.out.print(getEdgeColours(9)[1] + " ");
        System.out.print(getCornerColours(6)[2] + "  ");

        System.out.print(getCornerColours(6)[1] + " ");
        System.out.print(getEdgeColours(10)[1] + " ");
        System.out.println(getCornerColours(7)[1] + "\n");

        // Print bottom face
        System.out.print("       ");
        System.out.print(getCornerColours(4)[0] + " ");
        System.out.print(getEdgeColours(8)[0] + " ");
        System.out.println(getCornerColours(5)[0] + " ");

        System.out.print("       ");
        System.out.print(getEdgeColours(11)[0] + " ");
        System.out.print("O ");
        System.out.println(getEdgeColours(9)[0] + " ");

        System.out.print("       ");
        System.out.print(getCornerColours(7)[0] + " ");
        System.out.print(getEdgeColours(10)[0] + " ");
        System.out.println(getCornerColours(6)[0]);
        System.out.println("\n");
    }

}