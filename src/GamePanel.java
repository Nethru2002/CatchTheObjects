import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener {
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    private static final int BASKET_WIDTH = 100;
    private static final int BASKET_HEIGHT = 30;

    private Timer timer;
    private int basketX;
    private int score;
    private int gameSpeed;
    private boolean gameOver;

    private ArrayList<GameObject> fallingObjects;
    private Random random;

    public GamePanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    basketX -= 20;
                    if (basketX < 0) basketX = 0;
                } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    basketX += 20;
                    if (basketX > WIDTH - BASKET_WIDTH) basketX = WIDTH - BASKET_WIDTH;
                } else if (e.getKeyCode() == KeyEvent.VK_SPACE && gameOver) {
                    resetGame();
                }
            }
        });

        random = new Random();
        fallingObjects = new ArrayList<>();
        basketX = WIDTH / 2 - BASKET_WIDTH / 2;
        gameSpeed = 5;
    }

    public void startGame() {
        timer = new Timer(16, this); // ~60 FPS
        timer.start();
    }

    private void resetGame() {
        score = 0;
        gameSpeed = 5;
        gameOver = false;
        fallingObjects.clear();
        basketX = WIDTH / 2 - BASKET_WIDTH / 2;
    }

    private void spawnObject() {
        if (random.nextInt(100) < 3) { // 3% chance each frame
            int x = random.nextInt(WIDTH - 30);
            int type = random.nextInt(3);
            Color color;

            switch (type) {
                case 0: color = Color.RED; break;
                case 1: color = Color.GREEN; break;
                default: color = Color.BLUE;
            }

            fallingObjects.add(new GameObject(x, 0, 30, 30, color));
        }
    }

    private void update() {
        if (gameOver) return;

        spawnObject();

        // Move falling objects
        for (int i = 0; i < fallingObjects.size(); i++) {
            GameObject obj = fallingObjects.get(i);
            obj.setY(obj.getY() + gameSpeed);

            // Check if caught by basket
            if (obj.getY() + obj.getHeight() >= HEIGHT - BASKET_HEIGHT &&
                    obj.getX() + obj.getWidth() >= basketX &&
                    obj.getX() <= basketX + BASKET_WIDTH) {
                score += 10;
                fallingObjects.remove(i);
                i--;
                continue;
            }

            // Check if missed
            if (obj.getY() > HEIGHT) {
                gameOver = true;
                break;
            }
        }

        // Increase difficulty
        if (score > 0 && score % 100 == 0) {
            gameSpeed = 5 + score / 100;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw basket
        g.setColor(Color.WHITE);
        g.fillRect(basketX, HEIGHT - BASKET_HEIGHT, BASKET_WIDTH, BASKET_HEIGHT);

        // Draw falling objects
        for (GameObject obj : fallingObjects) {
            g.setColor(obj.getColor());
            g.fillRect(obj.getX(), obj.getY(), obj.getWidth(), obj.getHeight());
        }

        // Draw score
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 24));
        g.drawString("Score: " + score, 20, 30);

        // Draw game over message
        if (gameOver) {
            g.setFont(new Font("Arial", Font.BOLD, 48));
            g.setColor(Color.RED);
            g.drawString("GAME OVER", WIDTH/2 - 150, HEIGHT/2 - 50);
            g.setFont(new Font("Arial", Font.PLAIN, 24));
            g.drawString("Press SPACE to restart", WIDTH/2 - 120, HEIGHT/2 + 20);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        update();
        repaint();
    }
}