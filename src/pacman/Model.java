
package pacman;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.*;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;

/**
 * Game logic description
 * */
public class Model<string> extends JPanel implements ActionListener {

    private Dimension d;
    private final Font smallFont = new Font("Arial", Font.BOLD, 14);
    private boolean inGame = false;
    private boolean dying = false;


    private final int BLOCK_SIZE = 24;
    private final int N_BLOCKS = 15;
    private final int SCREEN_SIZE = N_BLOCKS * BLOCK_SIZE;

    private final int PACMAN_SPEED = 6;

    private int lives, score;
    static int maxScore;


    private int[] dx, dy;
    private int N_GHOSTS = 6;
    private int[] ghost_x, ghost_y, ghost_dx, ghost_dy, ghostSpeed;

    private Image heart, ghost;
    private Image up,down, left, right;

    private int pacman_x, pacman_y, pacmand_x, pacmand_y;
    private int req_dx, req_dy;

    FileWork fwork = new FileWork();


    /*
      0 - синий блок
      1 - левая граница
      2 - верхняя
      4 - правая
      8 - нижняя
      16 - белая точка
       */



    private Timer timer;

    public Model() throws FileNotFoundException {
        loadImages();
        initVariables();
        addKeyListener(new TAdapter());
        setFocusable(true);
        initGame();
        fwork.readRecord();
        fwork.ArrayFromFile();
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

        fwork.screenData = new short[N_BLOCKS * N_BLOCKS];
        d = new Dimension(400, 400);
        ghost_x = new int[N_GHOSTS];
        ghost_dx = new int[N_GHOSTS];
        ghost_y = new int[N_GHOSTS];
        ghost_dy = new int[N_GHOSTS];
        ghostSpeed = new int[N_GHOSTS];
        dx = new int[4];
        dy = new int[4];

        timer = new Timer(60, this);
        timer.start();
    }

    /**
     * Responsible for continuing and ending the game.
     * @param g2d отрисовка
     * @see Model#death()
     * @see Model#movePacman()
     * @see Model#moveGhosts(Graphics2D) ()
     * @see Model#checkMaze()
     * @see Model#drawGhost(Graphics2D, int, int)
     */
    private void playGame(Graphics2D g2d) throws IOException {
        if (dying) {

            death();

        } else {
            movePacman();
            drawPacman(g2d);
            moveGhosts(g2d);
            checkMaze();
        }
    }

    /**
     * Responsible for rendering the startup screen.
     * */
    private void showIntroScreen(Graphics2D g2d) {
        g2d.setFont(smallFont);
        String maxscore = String.valueOf(fwork.recordFrFile);
        String record = "Record is: ";
        String start = "Press SPACE to start";
        g2d.setColor(Color.yellow);
        g2d.drawString(start, (SCREEN_SIZE)/3, 150);
        g2d.drawString(record, 150, 170);
        g2d.drawString(maxscore, 225, 170);
    }


    /**
     * Responsible for drawing the score.
     * Specifies the color, layout, and font.
     * @param g method takes an object of the "Graphics2D" class to draw 2D graphics
     * */
    private void drawScore(Graphics2D g) {
        g.setFont(smallFont);
        g.setColor(new Color(5, 181, 79));
        String s = "Score: " + score;
        g.drawString(s, SCREEN_SIZE / 2 + 96, SCREEN_SIZE + 16);

        for (int i = 0; i < lives; i++) {
            g.drawImage(heart, i * 28 + 8, SCREEN_SIZE + 1, this);
        }
    }


    /**
     *Responsible for drawing the level.
     * A bitwise comparison of the numbers from the level matrix and the numbers that set this or that boundary.
     * Depending on this, the method draws blue rectangles and all 4 borders of these rectangles, as well as white points.
     * @param g2d method takes an object of the "Graphics2D" class to draw 2D graphics
     * */
    private void drawMaze(Graphics2D g2d) {

        short i = 0;
        int x, y;

        for (y = 0; y < SCREEN_SIZE; y += BLOCK_SIZE) {
            for (x = 0; x < SCREEN_SIZE; x += BLOCK_SIZE) {

                g2d.setColor(new Color(0,72,251));
                g2d.setStroke(new BasicStroke(5));

                if ((fwork.list.get(i) == 0)) {
                    g2d.fillRect(x, y, BLOCK_SIZE, BLOCK_SIZE);
                }

                if ((fwork.list.get(i) & 1) != 0) {
                    g2d.drawLine(x, y, x, y + BLOCK_SIZE - 1);
                }

                if ((fwork.list.get(i) & 2) != 0) {
                    g2d.drawLine(x, y, x + BLOCK_SIZE - 1, y);
                }

                if ((fwork.list.get(i) & 4) != 0) {
                    g2d.drawLine(x + BLOCK_SIZE - 1, y, x + BLOCK_SIZE - 1,
                            y + BLOCK_SIZE - 1);
                }

                if ((fwork.list.get(i) & 8) != 0) {
                    g2d.drawLine(x, y + BLOCK_SIZE - 1, x + BLOCK_SIZE - 1,
                            y + BLOCK_SIZE - 1);
                }

                if ((fwork.screenData[i] & 16) != 0) {
                    g2d.setColor(new Color(255,255,255));
                    g2d.fillOval(x + 10, y + 10, 6, 6);
                }

                i++;
            }
        }
    }

    /**
     * Initializes counters and character positions.
     * @see Model#initPositions()
     * */
    private void initGame() throws FileNotFoundException {

        lives = 3;
        score = 0;
        initPositions();
    }


    /**
     * Sets the positions and resets the direction of the ghosts and pacman.
     * */
    private void initPositions() {

        for (int i = 0; i < N_GHOSTS; i++) {

            ghost_y[i] = 4 * BLOCK_SIZE;
            ghost_x[i] = 4 * BLOCK_SIZE;

            ghostSpeed[i] = 3;
        }

        pacman_x = 7 * BLOCK_SIZE;
        pacman_y = 11 * BLOCK_SIZE;
        pacmand_x = 0;
        pacmand_y = 0;
        req_dx = 0;
        req_dy = 0;
        dying = false;
    }

    /**
     *The method is responsible for the movement of ghosts.
     * The variable "pos" contains the number of the element on which the ghost is located.
     * The "dx" and "dy" arrays record each of the 4 directions of movement.
     * After checking for an obstacle, the "ghost_dx" and "ghost_dy" arrays randomly select a direction for each of the ghosts.
     * The ghost moves along this direction with the declared speed.
     * At the end there is a check to see if pacman and ghost entered the same block.
     * If so, the "dying" variable becomes "true" and calls the "death" method.
     * @see Model#dying
     * @see Model#death()
     * */
    private void moveGhosts(Graphics2D g2d) {

        int pos;
        int count;

        for (int i = 0; i < N_GHOSTS; i++) {
            if (ghost_x[i] % BLOCK_SIZE == 0 && ghost_y[i] % BLOCK_SIZE == 0) {
                pos = ghost_x[i] / BLOCK_SIZE + N_BLOCKS * (int) (ghost_y[i] / BLOCK_SIZE);

                count = 0;

                if ((fwork.screenData[pos] & 1) == 0 && ghost_dx[i] != 1) {
                    dx[count] = -1;
                    dy[count] = 0;
                    count++;
                }

                if ((fwork.screenData[pos] & 2) == 0 && ghost_dy[i] != 1) {
                    dx[count] = 0;
                    dy[count] = -1;
                    count++;
                }

                if ((fwork.screenData[pos] & 4) == 0 && ghost_dx[i] != -1) {
                    dx[count] = 1;
                    dy[count] = 0;
                    count++;
                }

                if ((fwork.screenData[pos] & 8) == 0 && ghost_dy[i] != -1) {
                    dx[count] = 0;
                    dy[count] = 1;
                    count++;
                }

                if (count == 0) {

                    if ((fwork.screenData[pos] & 15) == 15) {
                        ghost_dx[i] = 0;
                        ghost_dy[i] = 0;
                    } else {
                        ghost_dx[i] = -ghost_dx[i];
                        ghost_dy[i] = -ghost_dy[i];
                    }

                } else {

                    count = (int) (Math.random() * count);

                    if (count > 3) {
                        count = 3;
                    }

                    ghost_dx[i] = dx[count];
                    ghost_dy[i] = dy[count];
                }

            }

            ghost_x[i] += (ghost_dx[i] * ghostSpeed[i]);
            ghost_y[i] += (ghost_dy[i] * ghostSpeed[i]);
            drawGhost(g2d, ghost_x[i] + 1, ghost_y[i] + 1);

            if (pacman_x > (ghost_x[i] - 12) && pacman_x < (ghost_x[i] + 12)
                    && pacman_y > (ghost_y[i] - 12) && pacman_y < (ghost_y[i] + 12)
                    && inGame) {

                dying = true;
            }
        }
    }


    /**
     * Responsible for the movement of the pacman.
     * The first if statement checks if the packman has reached the block boundary.
     * In "pacman_x / BLOCK_SIZE + N_BLOCKS * (pacman_y / BLOCK_SIZE)" the position of the pacman is set.
     * For example, with x = 11 and y = 7 the pacman is set to the 7th row of the matrix and to the 11th position in that row.
     * Then there is a check for white points by bitwise comparing the number on which the pacman stands and 16 (which means a white point)
     * and if it is greater than zero then the position is multiplied by 15, which makes it 16 and the next frame will not render the point.
     * Accordingly, 1 is added to the "scrore" Then there is a check for the maximum "score", in which it is written to the file.
     * Then there is a check for input, and after it the direction that the player entered is passed to the next if,
     * in which the check for obstacle is carried out. If there is an obstacle the movement is reset.
     * If there is no obstacle, the packman moves according to the given direction and speed.
     */
    private void movePacman() throws IOException {

        int PacPos;
        short screenPos;

        if (pacman_x % BLOCK_SIZE == 0 && pacman_y % BLOCK_SIZE == 0) //нужно чтобы пакман ходил четко по блокам и "не съезжал" с линии
        {
            PacPos = pacman_x / BLOCK_SIZE + N_BLOCKS * (pacman_y / BLOCK_SIZE); //выставляет позицию пакмена в одномерном списке (в данном случае: 172-ой элемент)
            screenPos = fwork.screenData[PacPos];

            if ((screenPos & 16) != 0) //проверка на белые точки
            {
                fwork.screenData[PacPos] = (short) (screenPos & 15);//любая точка больше по разряду чем 15, мы делаем ее >=15
                score++;

                if (score > fwork.recordFrFile){
                    maxScore = score;
                    fwork.writeRecord();
                    fwork.readRecord();
                }
            }
            //идет проверка на ввод с клавиши и есть ли в направлении движения стена
            if (req_dx != 0 || req_dy != 0) {
                if (((req_dx == -1 && req_dy == 0)
                        || (req_dx == 1 && req_dy == 0 )
                        || (req_dx == 0 && req_dy == -1 )
                        || (req_dx == 0 && req_dy == 1 )))
                {
                    pacmand_x = req_dx;
                    pacmand_y = req_dy;
                }
            }
            //перемещение пакмена до тех пор пока он "не врежится" в стенку. после переменные перемещения pacmand_x и pacmand_y сбрасываются и он перестает двигаться
            if ((pacmand_x == -1 && pacmand_y == 0 && (screenPos & 1) != 0)
                    || (pacmand_x == 1 && pacmand_y == 0 && (screenPos & 4) != 0)
                    || (pacmand_x == 0 && pacmand_y == -1 && (screenPos & 2) != 0)
                    || (pacmand_x == 0 && pacmand_y == 1 && (screenPos & 8) != 0))
            {
                pacmand_x = 0;
                pacmand_y = 0;
            }
        }
        pacman_x += PACMAN_SPEED * pacmand_x;
        pacman_y += PACMAN_SPEED * pacmand_y;

    }


    /**
     * Depending on the direction chosen by the player, draws a gif corresponding to this direction
     * */
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

    /**
     * Draws the gif of all ghosts
     * */
    private void drawGhost(Graphics2D g2d, int x, int y) {
        g2d.drawImage(ghost, x, y, this);
    }


    /**
     * Checks for the remaining number of white dots.
     * If none remain, adds 50 points to the score and resets the characters' positions.
     * */
    private void checkMaze() {

        int i = 0;
        boolean notfinished = true;

        while (i < N_BLOCKS * N_BLOCKS && notfinished) {

            if ((fwork.screenData[i]) != 0) {
                notfinished = false;
            }

            i++;
        }

        if (notfinished) {

            score += 50;
            inGame = false;
        }
    }

    /**
     * Remaining Life Meter. If there are no lives left, the game is restarted.
     * */
    private void death() throws IOException {

        lives--;

        if (lives == 0){
            inGame = false;
            fwork.ArrayFromFile();
        }

        initPositions();
    }


    /**
     * Shades the background and causes the level and counters to be drawn.
     * */
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;

        g2d.setColor(Color.black);
        g2d.fillRect(0, 0, d.width, d.height);

        drawMaze(g2d);
        drawScore(g2d);


        if (inGame) {
            try {
                playGame(g2d);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            showIntroScreen(g2d);
        }

        Toolkit.getDefaultToolkit().sync();
        g2d.dispose();
    }

    /**
     * Implementation of reading player input. Depending on the key pressed, the direction of the pacman's movement is set.
     * */
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
                    try {
                        initGame();
                    } catch (FileNotFoundException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        repaint();
    }

}

