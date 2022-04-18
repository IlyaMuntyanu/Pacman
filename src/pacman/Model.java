/*



    Осталось сделать передвижение призраков и счетчики.
    Также попробую записать несколько уровней в разные файлы и менять их по мере набора некоторого кол-ва очков.
    Ближе к концу сделаю джавадок с описанием всего-всего.



 */

package pacman;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;


public class Model extends JPanel implements ActionListener {

    private Dimension d;
    private final Font smallFont = new Font("Arial", Font.BOLD, 14);
    private boolean inGame = false;


    private final int BLOCK_SIZE = 24;
    private final int N_BLOCKS = 15;
    private final int SCREEN_SIZE = N_BLOCKS * BLOCK_SIZE;

    private final int PACMAN_SPEED = 6;

    private int N_GHOSTS = 6;
    private int lives, score;
    private int[] dx, dy;
    private int[] ghost_x, ghost_y, ghost_dx, ghost_dy;

    private Image heart, ghost;
    private Image up,down, left, right;

    private int pacman_x, pacman_y, pacmand_x, pacmand_y;
    private int req_dx, req_dy;


    /*
      0 - синий блок
      1 - левая граница
      2 - верхняя
      4 - правая
      8 - нижняя
      16 - белая точка
       */
    private final short levelData[] =
            {
                    19, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 22,
                    17, 24, 24, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 20,
                    21,  0,  0, 17, 16, 16, 24, 16, 24, 16, 16, 16, 16, 16, 20,
                    21,  0,  0, 17, 16, 20,  0, 29,  0, 17, 16, 16, 16, 16, 20,
                    21,  0,  0, 17, 16, 20,  0,  0,  0, 17, 16, 24, 24, 16, 20,
                    21,  0,  0, 17, 16, 16, 18, 18, 18, 16, 20,  0,  0, 17, 20,
                    21,  0,  0, 17, 16, 16, 24, 24, 16, 16, 20,  0, 19, 16, 20,
                    21,  0,  0, 17, 16, 20,  0,  0, 17, 16, 20,  0, 17, 16, 20,
                    21,  0,  0, 17, 16, 20,  0,  0, 17, 16, 20,  0, 17, 16, 20,
                    21,  0,  0, 17, 16, 20,  0,  0, 17, 16, 20,  0, 25, 16, 20,
                    21,  0,  0, 17, 16, 16, 18, 18, 16, 16, 20,  0,  0, 17, 20,
                    17, 18, 18, 16, 16, 16, 16, 24, 24, 24, 16, 18, 18, 16, 20,
                    17, 16, 16, 16, 16, 16, 20,  0,  0,  0, 17, 16, 16, 16, 20,
                    17, 16, 16, 16, 16, 16, 20,  0,  0,  0, 17, 16, 16, 16, 20,
                    25, 24, 24, 24, 24, 24, 24, 26, 26, 26, 24, 24, 24, 24, 28,
            };



    private short[] screenData;
    private Timer timer;

    public Model() {
        loadImages();
        initVariables();
        addKeyListener(new TAdapter());
        setFocusable(true);
        initGame();
    }

    private void loadImages() {
        down = new ImageIcon("src" + File.separator + "images" + File.separator +"down.gif").getImage();
        up = (new ImageIcon("src" + File.separator + "images" + File.separator + "up.gif")).getImage();
        left = new ImageIcon("src" + File.separator + "images" + File.separator +"left.gif").getImage();
        right = new ImageIcon("src" + File.separator + "images" + File.separator +"right.gif").getImage();
        ghost = new ImageIcon("src" + File.separator + "images" + File.separator +"ghost.gif").getImage();
        heart = new ImageIcon("src" + File.separator + "images" + File.separator + "heart.png").getImage();
    }
    private void initVariables() {

        screenData = new short[N_BLOCKS * N_BLOCKS];
        d = new Dimension(400, 400);
        ghost_x = new int[N_GHOSTS];
        ghost_dx = new int[N_GHOSTS];
        ghost_y = new int[N_GHOSTS];
        ghost_dy = new int[N_GHOSTS];
        dx = new int[4];
        dy = new int[4];

        timer = new Timer(60, this);
        timer.start();
    }

    private void playGame(Graphics2D g2d) {
        movePacman();
        drawPacman(g2d);
        drawGhosts(g2d);
    }

    private void showIntroScreen(Graphics2D g2d) {
        g2d.setFont(smallFont);
        String start = "Press SPACE to start";
        g2d.setColor(Color.yellow);
        g2d.drawString(start, (SCREEN_SIZE)/4, 150);
    }

    private void drawMaze(Graphics2D g2d) {

        short i = 0;
        int x, y;

        for (y = 0; y < SCREEN_SIZE; y += BLOCK_SIZE) {
            for (x = 0; x < SCREEN_SIZE; x += BLOCK_SIZE) {

                g2d.setColor(new Color(0,72,251));
                g2d.setStroke(new BasicStroke(5));

                if ((levelData[i] == 0)) {
                    g2d.fillRect(x, y, BLOCK_SIZE, BLOCK_SIZE);
                }

                if ((screenData[i] & 1) != 0) {
                    g2d.drawLine(x, y, x, y + BLOCK_SIZE - 1);
                }

                if ((screenData[i] & 2) != 0) {
                    g2d.drawLine(x, y, x + BLOCK_SIZE - 1, y);
                }

                if ((screenData[i] & 4) != 0) {
                    g2d.drawLine(x + BLOCK_SIZE - 1, y, x + BLOCK_SIZE - 1,
                            y + BLOCK_SIZE - 1);
                }

                if ((screenData[i] & 8) != 0) {
                    g2d.drawLine(x, y + BLOCK_SIZE - 1, x + BLOCK_SIZE - 1,
                            y + BLOCK_SIZE - 1);
                }

                if ((screenData[i] & 16) != 0) {
                    g2d.setColor(new Color(255,255,255));
                    g2d.fillOval(x + 10, y + 10, 6, 6);
                }

                i++;
            }
        }
    }

    private void initGame() {

        lives = 3;
        score = 0;
        initLevel();
    }

    private void initLevel() {

        int i;
        for (i = 0; i < N_BLOCKS * N_BLOCKS; i++) {
            screenData[i] = levelData[i];
        }

        continueLevel();
    }

    private void continueLevel() {

        for (int i = 0; i < N_GHOSTS; i++) {

            ghost_y[i] = 3 * BLOCK_SIZE;
            ghost_x[i] = 7 * BLOCK_SIZE;
        }

        pacman_x = 0 * BLOCK_SIZE;
        pacman_y = 14 * BLOCK_SIZE;
    }

    private void movePacman() {

        int PacPos;
        short screenPos;

        if (pacman_x % BLOCK_SIZE == 0 && pacman_y % BLOCK_SIZE == 0) //нужно чтобы пакман ходил четко по блокам и "не съезжал" с линии
        {
            PacPos = pacman_x / BLOCK_SIZE + N_BLOCKS * (pacman_y / BLOCK_SIZE); //
            screenPos = screenData[PacPos];

            if ((screenPos & 16) != 0) //проверка на белые точки
            {
                screenData[PacPos] = (short) (screenPos & 15);
                score++;
            }

            if (req_dx != 0 || req_dy != 0) {
                if (!((req_dx == -1 && req_dy == 0 && (screenPos & 1) != 0)
                        || (req_dx == 1 && req_dy == 0 && (screenPos & 4) != 0)
                        || (req_dx == 0 && req_dy == -1 && (screenPos & 2) != 0)
                        || (req_dx == 0 && req_dy == 1 && (screenPos & 8) != 0))) {
                    pacmand_x = req_dx;
                    pacmand_y = req_dy;
                }
            }

            if ((pacmand_x == -1 && pacmand_y == 0 && (screenPos & 1) != 0)
                    || (pacmand_x == 1 && pacmand_y == 0 && (screenPos & 4) != 0)
                    || (pacmand_x == 0 && pacmand_y == -1 && (screenPos & 2) != 0)
                    || (pacmand_x == 0 && pacmand_y == 1 && (screenPos & 8) != 0)) {
                pacmand_x = 0;
                pacmand_y = 0;
            }
        }
        pacman_x = pacman_x + PACMAN_SPEED * pacmand_x;
        pacman_y = pacman_y + PACMAN_SPEED * pacmand_y;
    }

    private void drawPacman(Graphics2D g2d) {

        if (req_dx == -1) {
            g2d.drawImage(left, pacman_x + 1, pacman_y + 1, this);
        } else if (req_dx == 1) {
            g2d.drawImage(right, pacman_x + 1, pacman_y + 1, this);
        } else if (req_dy == -1) {
            g2d.drawImage(up, pacman_x + 1, pacman_y + 1, this);
        } else {
            g2d.drawImage(down, pacman_x + 1, pacman_y + 1, this);
        }
    }

    private void drawGhosts(Graphics2D g2d) {

        for (int i = 0; i < N_GHOSTS; i++) {

            g2d.drawImage(ghost, ghost_x[i], ghost_y[i], this);

        }
    }


    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;

        g2d.setColor(Color.black);
        g2d.fillRect(0, 0, d.width, d.height);

        drawMaze(g2d);


        if (inGame) {
            playGame(g2d);
        } else {
            showIntroScreen(g2d);
        }

        Toolkit.getDefaultToolkit().sync();
        g2d.dispose();
    }

    class TAdapter extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent e) {

            int key = e.getKeyCode();

            if (inGame) {
                if (key == KeyEvent.VK_LEFT) {
                    req_dx = -1;
                    req_dy = 0;
                } else if (key == KeyEvent.VK_RIGHT) {
                    req_dx = 1;
                    req_dy = 0;
                } else if (key == KeyEvent.VK_UP) {
                    req_dx = 0;
                    req_dy = -1;
                } else if (key == KeyEvent.VK_DOWN) {
                    req_dx = 0;
                    req_dy = 1;
                } else if (key == KeyEvent.VK_ESCAPE && timer.isRunning()) {
                    inGame = false;
                }
            } else {
                if (key == KeyEvent.VK_SPACE) {
                    inGame = true;
                    initGame();
                }
            }
        }
    }



    @Override
    public void actionPerformed(ActionEvent e) {
        repaint();
    }

}

