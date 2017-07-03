import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Random;

public class Game2048 extends JFrame implements KeyListener {

    public enum State {INIT, CONTINUE, END}

    private State state = State.INIT;

    public static final int BOXES_WIDTH = 4;

    private JPanel gamePanel = new JPanel();

    private DigitBox[][] boxes = new DigitBox[BOXES_WIDTH][BOXES_WIDTH];

    private class DigitBox extends JButton {
        private int value;

        public DigitBox(int value) {
            Random r = new Random();
            int red;
            int green;
            int blue;

            do {
                red = r.nextInt(255);
                green = r.nextInt(255);
                blue = r.nextInt(255);
            } while (red < 30 && green < 30 && blue < 30);
            setBackground(new Color(red, green, blue));
            setFont(new Font("Verdana", Font.BOLD, 22));
            this.value = value;
            setText("" + value);
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
            setText("" + (value == 0 ? "" : value));
            int red = (value * 73) % 226 + 30;
            int green = (value * 110) % 226 + 30;
            int blue = (value * 40) % 226 + 30;
            if(value == 0) {
                red = green = blue = 250;
            }
            setBackground(new Color(red, green, blue));
        }
    }

    public void generateState() {
        Random rand = new Random();
        switch (state) {
            case INIT: {
                for (int i = 0; i < BOXES_WIDTH; i++) {
                    for (int j = 0; j < BOXES_WIDTH; j++) {
                        boxes[i][j].setValue(0);
                    }
                }

                int row1 = rand.nextInt(BOXES_WIDTH);
                int col1 = rand.nextInt(BOXES_WIDTH);
                int row2;
                int col2;
                do {
                    row2 = rand.nextInt(BOXES_WIDTH);
                    col2 = rand.nextInt(BOXES_WIDTH);
                } while (row2 == row1 && col2 == col1);

                System.out.println("row1 = " + row1);
                System.out.println("col1 = " + col1);
                System.out.println("row2 = " + row2);
                System.out.println("col2 = " + col2);

                boxes[row1][col1].setValue(2);
                boxes[row2][col2].setValue(2);
                state = State.CONTINUE;
                break;
            }
            case CONTINUE: {
                int row1;
                int col1;

                boolean doSpawn = false;
                checkEmptySpace:
                for (int i = 0; i < BOXES_WIDTH; i++) {
                    for (int j = 0; j < BOXES_WIDTH; j++) {
                        if (boxes[i][j].getValue() == 0){
                            doSpawn = true;
                            break checkEmptySpace;
                        }
                    }
                }
                if (doSpawn) {
                    do {
                        row1 = rand.nextInt(BOXES_WIDTH);
                        col1 = rand.nextInt(BOXES_WIDTH);
                    } while (boxes[row1][col1].getValue() != 0);

                    System.out.println("row1 = " + row1);
                    System.out.println("col1 = " + col1);

                    boxes[row1][col1].setValue(2);
                }

                State s = analyseEndState();
                if (s == State.END) {
                    JOptionPane.showMessageDialog(this, "Game over!");
                    System.exit(0);
                }
                break;
            }
        }

    }

    private State analyseEndState() {
        for (int i = 0; i < BOXES_WIDTH; i++) {
            for (int j = 0; j < BOXES_WIDTH; j++) {
                if (boxes[i][j].getValue() == 0) return State.CONTINUE;
                if (i > 0 && boxes[i][j].getValue() == boxes[i - 1][j].getValue()) return State.CONTINUE;
                if (i < BOXES_WIDTH - 1 && boxes[i][j].getValue() == boxes[i + 1][j].getValue()) return State.CONTINUE;
                if (j > 0 && boxes[i][j].getValue() == boxes[i][j - 1].getValue()) return State.CONTINUE;
                if (j < BOXES_WIDTH - 1 && boxes[i][j].getValue() == boxes[i][j + 1].getValue()) return State.CONTINUE;
            }
        }
        return State.END;
    }

    public Game2048() {
        setTitle("2048");
        add(gamePanel, BorderLayout.CENTER);
        gamePanel.setPreferredSize(new Dimension(400, 400));
        gamePanel.setLayout(new GridLayout(BOXES_WIDTH, BOXES_WIDTH));

        for (int i = 0; i < BOXES_WIDTH; i++) {
            for (int j = 0; j < BOXES_WIDTH; j++) {
                boxes[i][j] = new DigitBox(0);
                gamePanel.add(boxes[i][j]);
            }
        }

        addKeyListener(this);
        setFocusable(true);
        generateState();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new Game2048();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setVisible(true);
            frame.setLocationRelativeTo(null);
        });
    }

    public void keyPressed(KeyEvent e) {
        System.out.println("keyPressed");
    }

    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            for (int i = 0; i < BOXES_WIDTH; i++) {
                for (int j = BOXES_WIDTH - 2; j >= 0; j--) {
                    if (boxes[i][j + 1].getValue() == 0){
                        boxes[i][j + 1].setValue(boxes[i][j].getValue());
                        boxes[i][j].setValue(0);
                    } else if (boxes[i][j + 1].getValue() == boxes[i][j].getValue()){
                        boxes[i][j + 1].setValue(boxes[i][j].getValue() * 2);
                        boxes[i][j].setValue(0);
                        System.out.println("right merge");
                    }
                }
            }
        } else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            for (int i = 0; i < BOXES_WIDTH; i++) {
                for (int j = 1; j < BOXES_WIDTH; j++) {
                    if (boxes[i][j - 1].getValue() == 0){
                        boxes[i][j - 1].setValue(boxes[i][j].getValue());
                        boxes[i][j].setValue(0);
                    } else if (boxes[i][j - 1].getValue() == boxes[i][j].getValue()){
                        boxes[i][j - 1].setValue(boxes[i][j].getValue() * 2);
                        boxes[i][j].setValue(0);
                        System.out.println("left merge");
                    }
                }
            }
        } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            for (int i = BOXES_WIDTH - 2; i >= 0; i--) {
                for (int j = 0; j < BOXES_WIDTH; j++) {
                    if (boxes[i + 1][j].getValue() == 0){
                        boxes[i + 1][j].setValue(boxes[i][j].getValue());
                        boxes[i][j].setValue(0);
                    } else if (boxes[i + 1][j].getValue() == boxes[i][j].getValue()){
                        boxes[i + 1][j].setValue(boxes[i][j].getValue() * 2);
                        boxes[i][j].setValue(0);
                        System.out.println("down merge");

                    }
                }
            }
        } else if (e.getKeyCode() == KeyEvent.VK_UP) {
            for (int i = 1; i < BOXES_WIDTH; i++) {
                for (int j = 0; j < BOXES_WIDTH; j++) {
                    if (boxes[i - 1][j].getValue() == 0){
                        boxes[i - 1][j].setValue(boxes[i][j].getValue());
                        boxes[i][j].setValue(0);
                    } else if (boxes[i - 1][j].getValue() == boxes[i][j].getValue()){
                        boxes[i - 1][j].setValue(boxes[i][j].getValue() * 2);
                        boxes[i][j].setValue(0);
                        System.out.println("up merge");

                    }
                }
            }
        }
        generateState();
    }

    public void keyTyped(KeyEvent e) {
    }

}

