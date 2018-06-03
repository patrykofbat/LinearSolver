import org.ejml.simple.SimpleMatrix;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Main {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        //intro
        System.out.println(ANSI_PURPLE+"Specify path to the file");
        ArrayList<double[]> inputData = new ArrayList<>();
//        String path = br.readLine();

        //C:\Users\Patryk\Desktop\programInput.txt
        //obtain data from file
        Files.lines(Paths.get("C:\\Users\\Patryk\\Desktop\\programInput.txt"))
                .forEach(string -> {
                    String[] splited = string.split(" ");
                    double[] tmpTable = new double[splited.length];
                    for(int i = 0;i<splited.length;i++){
                        tmpTable[i] =Double.parseDouble(splited[i]);
                    }
                    inputData.add(tmpTable);
                });


        double[][] inequalities =  {inputData.get(0), inputData.get(1)};
        SimpleMatrix matrix = new SimpleMatrix(inequalities);

        LinearProgram linearProgram = new LinearProgram(matrix.transpose(), inputData.get(2));
        linearProgram.run();



    }
}