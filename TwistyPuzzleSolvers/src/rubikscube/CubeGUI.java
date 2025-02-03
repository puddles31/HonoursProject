package rubikscube;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.stream.Stream;

public class CubeGUI extends JFrame {

    private Cube cube;
    private CubeFacePanel[] faces;

    public static void main(String[] args) {
        new CubeGUI();
    }

    public CubeGUI() {
        super("Rubik's Cube GUI");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1250, 750);
        setLayout(new GridLayout(1, 2));


        JPanel cubePanel = new JPanel(new GridLayout(3, 4, 10, 10));
        cubePanel.setBackground(Color.decode("#cccccc"));
        cubePanel.setBorder(new EmptyBorder(50, 50, 50, 50));
        cubePanel.setSize(1000, 750);
        
        CubeFacePanel wFace = new CubeFacePanel(Color.WHITE);
        CubeFacePanel gFace = new CubeFacePanel(Color.GREEN);
        CubeFacePanel rFace = new CubeFacePanel(Color.RED);
        CubeFacePanel bFace = new CubeFacePanel(Color.BLUE);
        CubeFacePanel oFace = new CubeFacePanel(Color.ORANGE);
        CubeFacePanel yFace = new CubeFacePanel(Color.YELLOW);
        faces = new CubeFacePanel[] {wFace, gFace, rFace, bFace, oFace, yFace};
        
        cubePanel.add(new JLabel());
        cubePanel.add(wFace);
        cubePanel.add(new JLabel());
        cubePanel.add(new JLabel());
        cubePanel.add(gFace);
        cubePanel.add(rFace);
        cubePanel.add(bFace);
        cubePanel.add(oFace);
        cubePanel.add(new JLabel());
        cubePanel.add(yFace);


        // TODO: switch to use GridBagLayout because java swing suckssss :((((

        JPanel controlPanel = new JPanel();
        controlPanel.setBackground(Color.decode("#aaaaaa"));
        controlPanel.setBorder(new EmptyBorder(50, 50, 50, 50));
        controlPanel.setSize(150, 750);

        JLabel controlLabel = new JLabel("Controls:");
        controlPanel.add(controlLabel);

        setVisible(true);
        add(cubePanel);
        add(controlPanel);
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
        Color[] colours = Stream.of(cube.getColours()).map(cubeCol -> getColour(cubeCol)).toArray(Color[]::new);

        // Top face
        faces[0].cells[0].setBackground(colours[0]);
        faces[0].cells[1].setBackground(colours[1]);
        faces[0].cells[2].setBackground(colours[2]);
        faces[0].cells[3].setBackground(colours[3]);
        faces[0].cells[4].setBackground(colours[4]);
        faces[0].cells[5].setBackground(colours[5]);
        faces[0].cells[6].setBackground(colours[6]);
        faces[0].cells[7].setBackground(colours[7]);

        // Left face
        faces[1].cells[0].setBackground(colours[8]);
        faces[1].cells[1].setBackground(colours[9]);
        faces[1].cells[2].setBackground(colours[10]);
        faces[1].cells[3].setBackground(colours[20]);
        faces[1].cells[4].setBackground(colours[21]);
        faces[1].cells[5].setBackground(colours[28]);
        faces[1].cells[6].setBackground(colours[29]);
        faces[1].cells[7].setBackground(colours[30]);

        // Front face
        faces[2].cells[0].setBackground(colours[11]);
        faces[2].cells[1].setBackground(colours[12]);
        faces[2].cells[2].setBackground(colours[13]);
        faces[2].cells[3].setBackground(colours[22]);
        faces[2].cells[4].setBackground(colours[23]);
        faces[2].cells[5].setBackground(colours[31]);
        faces[2].cells[6].setBackground(colours[32]);
        faces[2].cells[7].setBackground(colours[33]);

        // Right face
        faces[3].cells[0].setBackground(colours[14]);
        faces[3].cells[1].setBackground(colours[15]);
        faces[3].cells[2].setBackground(colours[16]);
        faces[3].cells[3].setBackground(colours[24]);
        faces[3].cells[4].setBackground(colours[25]);
        faces[3].cells[5].setBackground(colours[34]);
        faces[3].cells[6].setBackground(colours[35]);
        faces[3].cells[7].setBackground(colours[36]);

        // Back face
        faces[4].cells[0].setBackground(colours[17]);
        faces[4].cells[1].setBackground(colours[18]);
        faces[4].cells[2].setBackground(colours[19]);
        faces[4].cells[3].setBackground(colours[26]);
        faces[4].cells[4].setBackground(colours[27]);
        faces[4].cells[5].setBackground(colours[37]);
        faces[4].cells[6].setBackground(colours[38]);
        faces[4].cells[7].setBackground(colours[39]);
        
        // Bottom face
        faces[5].cells[0].setBackground(colours[40]);
        faces[5].cells[1].setBackground(colours[41]);
        faces[5].cells[2].setBackground(colours[42]);
        faces[5].cells[3].setBackground(colours[43]);
        faces[5].cells[4].setBackground(colours[44]);
        faces[5].cells[5].setBackground(colours[45]);
        faces[5].cells[6].setBackground(colours[46]);
        faces[5].cells[7].setBackground(colours[47]);
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