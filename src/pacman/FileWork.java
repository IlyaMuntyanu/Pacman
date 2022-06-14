package pacman;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * file work
 * */
public class FileWork {

    static ArrayList<Integer> list = new ArrayList<Integer>();
    static short[] screenData;

    private static int[][] numArray;
    private static final int ROWS = 15;
    private static final int COLUMNS = 15;


    short[] ArrayFromFile() throws FileNotFoundException {
        readFile();
        to1DArray(numArray);
        return screenData;
    }

    public static void readFile() throws FileNotFoundException {
        numArray = new int[ROWS][COLUMNS];
        Scanner sc = new Scanner(new File("src" + File.separator + "level"));
        while (sc.hasNextLine()) {
            for (int i = 0; i < numArray.length; i++) {
                String[] line = sc.nextLine().trim().split("," + " ");
                for (int j = 0; j < line.length; j++) {
                    numArray[i][j] = Integer.parseInt(line[j]);
                }
            }
        }
    }

    public static void to1DArray(int[][] numArray) {

        for(int i = 0; i < numArray.length; i++) {
            for(int j = 0; j < numArray[i].length; j++){
                list.add(numArray[i][j]);
            }
        }

        screenData = new short[list.size()];

        for (int i = 0; i < screenData.length; i++){
            screenData[i] = list.get(i).shortValue();
        }
    }


    int recordFrFile;

    void writeRecord() throws IOException {
        Writer wr = new FileWriter("src" + File.separator + "record.txt");
        wr.write(String.valueOf(Model.maxScore));
        wr.close();
    }

    void readRecord(){
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("src" + File.separator + "record.txt"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        String Int_line = null;

        while (true) {
            try {
                if ((Int_line = reader.readLine()) == null) break;
            } catch (IOException e) {
                e.printStackTrace();
            }
            recordFrFile = Integer.parseInt(Int_line);
        }
    }

}

