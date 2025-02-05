package rubikscube.interactive;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import rubikscube.Cube;
import rubikscube.Cube.Colour;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.stream.Stream;

public class CubeGUI extends JFrame {

    private Cube cube;
    private CubeFacePanel[] faces;

    public static void main(String[] args) {
        new CubeGUI();
    }

    public CubeGUI() {
        super("Rubik's Cube GUI");
        cube = new Cube();
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1250, 750);
        setLayout(new BorderLayout());
        
        CubePanel cubePanel = new CubePanel();
        ConsolePanel consolePanel = new ConsolePanel();

        add(cubePanel, BorderLayout.WEST);
        add(consolePanel, BorderLayout.CENTER);

        cubePanel.addKeyListener(new CubeKeyListener(consolePanel.consoleText));

        setVisible(true);
        cubePanel.grabFocus();        
    }

    private class CubePanel extends JPanel {
        public CubePanel() {
            super(new GridLayout(3, 4, 10, 10));
            setBackground(Color.decode("#cccccc"));
            setBorder(new EmptyBorder(50, 50, 50, 50));
            setPreferredSize(new Dimension(1000, 750));
    
            CubeFacePanel wFace = new CubeFacePanel(Color.WHITE);
            CubeFacePanel gFace = new CubeFacePanel(Color.GREEN);
            CubeFacePanel rFace = new CubeFacePanel(Color.RED);
            CubeFacePanel bFace = new CubeFacePanel(Color.BLUE);
            CubeFacePanel oFace = new CubeFacePanel(Color.ORANGE);
            CubeFacePanel yFace = new CubeFacePanel(Color.YELLOW);
            faces = new CubeFacePanel[] {wFace, gFace, rFace, bFace, oFace, yFace};
            
            add(new JLabel());
            add(wFace);
            add(new JLabel());
            add(new JLabel());
            add(gFace);
            add(rFace);
            add(bFace);
            add(oFace);
            add(new JLabel());
            add(yFace);

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    super.mouseReleased(e);
                    CubePanel.this.grabFocus();
                }
            });
        }
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

    private class ConsolePanel extends JPanel {
        CubeTerminal terminal = new CubeTerminal(cube, true);
        JTextArea consoleText;

        public ConsolePanel() {
            JPanel textPanel = new JPanel();
            textPanel.setLayout(new GridBagLayout());

            setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.weightx = 1;
            gbc.weighty = 1;
            gbc.fill = GridBagConstraints.BOTH;

            JPanel fillerPanel = new JPanel();
            fillerPanel.setBackground(Color.decode("#333333"));
            textPanel.add(fillerPanel, gbc);

            gbc.gridy = 1;
            gbc.weighty = 0;

            consoleText = new JTextArea(1, 18);
            consoleText.setBackground(Color.decode("#333333"));
            consoleText.setForeground(Color.WHITE);
            consoleText.setEditable(false);
            consoleText.setFocusable(false);
            consoleText.setLineWrap(true);
            consoleText.setFont(new Font(Font.MONOSPACED, Font.BOLD, 18));
            textPanel.add(consoleText, gbc);

            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.weightx = 1;
            gbc.weighty = 1;
            gbc.fill = GridBagConstraints.BOTH;

            add(new JScrollPane(textPanel, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER), gbc);

            gbc.gridy = 1;
            gbc.weighty = 0;

            JTextField consoleInput = new JTextField(18);
            consoleInput.setBackground(Color.decode("#555555"));
            consoleInput.setForeground(Color.WHITE);
            consoleInput.setCaretColor(Color.WHITE);
            consoleInput.setFont(new Font(Font.MONOSPACED, Font.BOLD, 18));

            consoleInput.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (consoleText.getText().length() > 0) {
                        consoleText.append("\n");
                    }

                    consoleText.append(consoleInput.getText());

                    String out = terminal.handleInput(consoleInput.getText().toUpperCase());
                    if (out.length() > 0) {
                        consoleText.append("\n" + out);
                    }
                    
                    consoleInput.setText("");
                    renderCube();
                }
            });
            add(consoleInput, gbc);
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

        private JTextArea consoleText;

        public CubeKeyListener(JTextArea consoleText) {
            this.consoleText = consoleText;
        }

        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_U) {
                if (e.isShiftDown()) {
                    cube.moves.moveU2();
                }
                else if (e.isControlDown()) {
                    cube.moves.moveUPrime();
                }
                else {
                    cube.moves.moveU();
                }
            }
            else if (e.getKeyCode() == KeyEvent.VK_L) {
                if (e.isShiftDown()) {
                    cube.moves.moveL2();
                }
                else if (e.isControlDown()) {
                    cube.moves.moveLPrime();
                }
                else {
                    cube.moves.moveL();
                }
            }
            else if (e.getKeyCode() == KeyEvent.VK_F) {
                if (e.isShiftDown()) {
                    cube.moves.moveF2();
                }
                else if (e.isControlDown()) {
                    cube.moves.moveFPrime();
                }
                else {
                    cube.moves.moveF();
                }
            }
            else if (e.getKeyCode() == KeyEvent.VK_R) {
                if (e.isShiftDown()) {
                    cube.moves.moveR2();
                }
                else if (e.isControlDown()) {
                    cube.moves.moveRPrime();
                }
                else {
                    cube.moves.moveR();
                }
            }
            else if (e.getKeyCode() == KeyEvent.VK_B) {
                if (e.isShiftDown()) {
                    cube.moves.moveB2();
                }
                else if (e.isControlDown()) {
                    cube.moves.moveBPrime();
                }
                else {
                    cube.moves.moveB();
                }
            }
            else if (e.getKeyCode() == KeyEvent.VK_D) {
                if (e.isShiftDown()) {
                    cube.moves.moveD2();
                }
                else if (e.isControlDown()) {
                    cube.moves.moveDPrime();
                }
                else {
                    cube.moves.moveD();
                }
            }
            else {
                return;
            }

            renderCube();

            if (consoleText.getText().length() > 0) {
                consoleText.append("\n");
            }
            consoleText.append(KeyEvent.getKeyText(e.getKeyCode()).toUpperCase());
            
            if (e.isShiftDown()) {
                consoleText.append("2");
            }
            else if (e.isControlDown()) {
                consoleText.append("'");
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {}

        @Override
        public void keyTyped(KeyEvent e) {}
    }
}