package rubikscube;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class CubeGUI extends JFrame {

    private Cube cube;
    private CubeFacePanel[] faces;

    public static void main(String[] args) {
        new CubeGUI();
    }

    public CubeGUI() {
        super("Rubik's Cube GUI");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 750);

        JPanel panel = new JPanel(new GridLayout(3, 4, 10, 10));
        panel.setBackground(Color.decode("#cccccc"));
        panel.setBorder(new EmptyBorder(50, 50, 50, 50));
        
        CubeFacePanel wFace = new CubeFacePanel(Color.WHITE);
        CubeFacePanel gFace = new CubeFacePanel(Color.GREEN);
        CubeFacePanel rFace = new CubeFacePanel(Color.RED);
        CubeFacePanel bFace = new CubeFacePanel(Color.BLUE);
        CubeFacePanel oFace = new CubeFacePanel(Color.ORANGE);
        CubeFacePanel yFace = new CubeFacePanel(Color.YELLOW);

        faces = new CubeFacePanel[] {wFace, gFace, rFace, bFace, oFace, yFace};
        
        panel.add(new JLabel());
        panel.add(wFace);
        panel.add(new JLabel());
        panel.add(new JLabel());

        panel.add(gFace);
        panel.add(rFace);
        panel.add(bFace);
        panel.add(oFace);

        panel.add(new JLabel());
        panel.add(yFace);

        add(panel);
        setVisible(true);

        addKeyListener(new CubeKeyListener());

        cube = new Cube();
    }

    private class CubeFacePanel extends JPanel {
        JLabel[] cells;

        public CubeFacePanel(Color color) {
            super(new GridLayout(3, 3, 3, 3));
            this.setBackground(Color.decode("#1e1e1e"));
            this.setSize(250, 250);

            cells = new JLabel[8];

            for (int i = 0; i < 9; i++) {
                JLabel cell = new JLabel();
                cell.setOpaque(true);
                cell.setBackground(color);
                this.add(cell);

                if (i < 4) {
                    cells[i] = cell;
                }
                else if (i > 4) {
                    cells[i - 1] = cell;
                }
            }
        }
    }


    private Color getColour(Cube.Colour colour) {
        switch (colour) {
            case Cube.Colour.W: return Color.WHITE;
            case Cube.Colour.G: return Color.GREEN;
            case Cube.Colour.R: return Color.RED;
            case Cube.Colour.B: return Color.BLUE;
            case Cube.Colour.O: return Color.ORANGE;
            case Cube.Colour.Y: return Color.YELLOW;
            default: return Color.BLACK;
        }
    }

    private void renderCube() {
        // Top face
        faces[0].cells[0].setBackground(getColour(cube.getCornerColours(cube.CORNER_ULB)[0]));
        faces[0].cells[1].setBackground(getColour(cube.getEdgeColours(cube.EDGE_UB)[0]));
        faces[0].cells[2].setBackground(getColour(cube.getCornerColours(cube.CORNER_URB)[0]));
        faces[0].cells[3].setBackground(getColour(cube.getEdgeColours(cube.EDGE_UL)[0]));
        faces[0].cells[4].setBackground(getColour(cube.getEdgeColours(cube.EDGE_UR)[0]));
        faces[0].cells[5].setBackground(getColour(cube.getCornerColours(cube.CORNER_ULF)[0]));
        faces[0].cells[6].setBackground(getColour(cube.getEdgeColours(cube.EDGE_UF)[0]));
        faces[0].cells[7].setBackground(getColour(cube.getCornerColours(cube.CORNER_URF)[0]));

        // Left face
        faces[1].cells[0].setBackground(getColour(cube.getCornerColours(cube.CORNER_ULB)[1]));
        faces[1].cells[1].setBackground(getColour(cube.getEdgeColours(cube.EDGE_UL)[1]));
        faces[1].cells[2].setBackground(getColour(cube.getCornerColours(cube.CORNER_ULF)[1]));
        faces[1].cells[3].setBackground(getColour(cube.getEdgeColours(cube.EDGE_BL)[1]));
        faces[1].cells[4].setBackground(getColour(cube.getEdgeColours(cube.EDGE_FL)[1]));
        faces[1].cells[5].setBackground(getColour(cube.getCornerColours(cube.CORNER_DLB)[1]));
        faces[1].cells[6].setBackground(getColour(cube.getEdgeColours(cube.EDGE_DL)[1]));
        faces[1].cells[7].setBackground(getColour(cube.getCornerColours(cube.CORNER_DLF)[1]));

        // Front face
        faces[2].cells[0].setBackground(getColour(cube.getCornerColours(cube.CORNER_ULF)[2]));
        faces[2].cells[1].setBackground(getColour(cube.getEdgeColours(cube.EDGE_UF)[1]));
        faces[2].cells[2].setBackground(getColour(cube.getCornerColours(cube.CORNER_URF)[2]));
        faces[2].cells[3].setBackground(getColour(cube.getEdgeColours(cube.EDGE_FL)[0]));
        faces[2].cells[4].setBackground(getColour(cube.getEdgeColours(cube.EDGE_FR)[0]));
        faces[2].cells[5].setBackground(getColour(cube.getCornerColours(cube.CORNER_DLF)[2]));
        faces[2].cells[6].setBackground(getColour(cube.getEdgeColours(cube.EDGE_DF)[1]));
        faces[2].cells[7].setBackground(getColour(cube.getCornerColours(cube.CORNER_DRF)[2]));

        // Right face
        faces[3].cells[0].setBackground(getColour(cube.getCornerColours(cube.CORNER_URF)[1]));
        faces[3].cells[1].setBackground(getColour(cube.getEdgeColours(cube.EDGE_UR)[1]));
        faces[3].cells[2].setBackground(getColour(cube.getCornerColours(cube.CORNER_URB)[1]));
        faces[3].cells[3].setBackground(getColour(cube.getEdgeColours(cube.EDGE_FR)[1]));
        faces[3].cells[4].setBackground(getColour(cube.getEdgeColours(cube.EDGE_BR)[1]));
        faces[3].cells[5].setBackground(getColour(cube.getCornerColours(cube.CORNER_DRF)[1]));
        faces[3].cells[6].setBackground(getColour(cube.getEdgeColours(cube.EDGE_DR)[1]));
        faces[3].cells[7].setBackground(getColour(cube.getCornerColours(cube.CORNER_DRB)[1]));

        // Back face
        faces[4].cells[0].setBackground(getColour(cube.getCornerColours(cube.CORNER_URB)[2]));
        faces[4].cells[1].setBackground(getColour(cube.getEdgeColours(cube.EDGE_UB)[1]));
        faces[4].cells[2].setBackground(getColour(cube.getCornerColours(cube.CORNER_ULB)[2]));
        faces[4].cells[3].setBackground(getColour(cube.getEdgeColours(cube.EDGE_BR)[0]));
        faces[4].cells[4].setBackground(getColour(cube.getEdgeColours(cube.EDGE_BL)[0]));
        faces[4].cells[5].setBackground(getColour(cube.getCornerColours(cube.CORNER_DRB)[2]));
        faces[4].cells[6].setBackground(getColour(cube.getEdgeColours(cube.EDGE_DB)[1]));
        faces[4].cells[7].setBackground(getColour(cube.getCornerColours(cube.CORNER_DLB)[2]));
        
        // Bottom face
        faces[5].cells[0].setBackground(getColour(cube.getCornerColours(cube.CORNER_DLF)[0]));
        faces[5].cells[1].setBackground(getColour(cube.getEdgeColours(cube.EDGE_DF)[0]));
        faces[5].cells[2].setBackground(getColour(cube.getCornerColours(cube.CORNER_DRF)[0]));
        faces[5].cells[3].setBackground(getColour(cube.getEdgeColours(cube.EDGE_DL)[0]));
        faces[5].cells[4].setBackground(getColour(cube.getEdgeColours(cube.EDGE_DR)[0]));
        faces[5].cells[5].setBackground(getColour(cube.getCornerColours(cube.CORNER_DLB)[0]));
        faces[5].cells[6].setBackground(getColour(cube.getEdgeColours(cube.EDGE_DB)[0]));
        faces[5].cells[7].setBackground(getColour(cube.getCornerColours(cube.CORNER_DRB)[0]));
    }


    private class CubeKeyListener implements KeyListener {
        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_U) {
                if (e.isShiftDown()) {
                    cube.moveU2();
                }
                else if (e.isControlDown()) {
                    cube.moveUPrime();
                }
                else {
                    cube.moveU();
                }
                renderCube();
            }
            else if (e.getKeyCode() == KeyEvent.VK_L) {
                if (e.isShiftDown()) {
                    cube.moveL2();
                }
                else if (e.isControlDown()) {
                    cube.moveLPrime();
                }
                else {
                    cube.moveL();
                }
                renderCube();
            }
            else if (e.getKeyCode() == KeyEvent.VK_F) {
                if (e.isShiftDown()) {
                    cube.moveF2();
                }
                else if (e.isControlDown()) {
                    cube.moveFPrime();
                }
                else {
                    cube.moveF();
                }
                renderCube();
            }
            else if (e.getKeyCode() == KeyEvent.VK_R) {
                if (e.isShiftDown()) {
                    cube.moveR2();
                }
                else if (e.isControlDown()) {
                    cube.moveRPrime();
                }
                else {
                    cube.moveR();
                }
                renderCube();
            }
            else if (e.getKeyCode() == KeyEvent.VK_B) {
                if (e.isShiftDown()) {
                    cube.moveB2();
                }
                else if (e.isControlDown()) {
                    cube.moveBPrime();
                }
                else {
                    cube.moveB();
                }
                renderCube();
            }
            else if (e.getKeyCode() == KeyEvent.VK_D) {
                if (e.isShiftDown()) {
                    cube.moveD2();
                }
                else if (e.isControlDown()) {
                    cube.moveDPrime();
                }
                else {
                    cube.moveD();
                }
                renderCube();
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {}

        @Override
        public void keyTyped(KeyEvent e) {}
    }
}