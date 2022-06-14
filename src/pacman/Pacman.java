package pacman;

import javax.swing.JFrame;
import java.io.FileNotFoundException;

public class Pacman extends JFrame{

    public Pacman() throws FileNotFoundException {
        add(new Model());
    }


    public static void main(String[] args) throws FileNotFoundException {
        Pacman pac = new Pacman();
        pac.setVisible(true);
        pac.setTitle("Pacman");
        pac.setSize(380,420);
        pac.setDefaultCloseOperation(EXIT_ON_CLOSE);
        pac.setLocationRelativeTo(null);

    }

}
