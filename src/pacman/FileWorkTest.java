package pacman;

import java.io.FileNotFoundException;

import static org.junit.jupiter.api.Assertions.*;

class FileWorkTest {

    private final short levelData[] = {
            19, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 22,
            17, 24, 24, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 20,
            21, 0, 0, 17, 16, 16, 24, 16, 24, 16, 16, 16, 16, 16, 20,
            21, 0, 0, 17, 16, 20, 0, 29, 0, 17, 16, 16, 16, 16, 20,
            21, 0, 0, 17, 16, 20, 0, 0, 0, 17, 16, 24, 24, 16, 20,
            21, 0, 0, 17, 16, 16, 18, 18, 18, 16, 20, 0, 0, 17, 20,
            21, 0, 0, 17, 16, 16, 24, 24, 16, 16, 20, 0, 19, 16, 20,
            21, 0, 0, 17, 16, 20, 0, 0, 17, 16, 20, 0, 17, 16, 20,
            21, 0, 0, 17, 16, 20, 0, 0, 17, 16, 20, 0, 17, 16, 20,
            21, 0, 0, 17, 16, 20, 0, 0, 17, 16, 20, 0, 25, 16, 20,
            21, 0, 0, 17, 16, 16, 18, 18, 16, 16, 20, 0, 0, 17, 20,
            17, 18, 18, 16, 16, 16, 16, 24, 24, 24, 16, 18, 18, 16, 20,
            17, 16, 16, 16, 16, 16, 20, 0, 0, 0, 17, 16, 16, 16, 20,
            17, 16, 16, 16, 16, 16, 20, 0, 0, 0, 17, 16, 16, 16, 20,
            25, 24, 24, 24, 24, 24, 24, 26, 26, 26, 24, 24, 24, 24, 28
    };

    FileWork fw = new FileWork();

    FileWorkTest() throws FileNotFoundException {
    }


    @org.junit.jupiter.api.Test
    void arrayFromFile() throws FileNotFoundException {

        assertArrayEquals(levelData, fw.ArrayFromFile());
    }
}