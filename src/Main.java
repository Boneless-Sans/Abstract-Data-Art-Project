import javax.swing.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException {
        String dataPath = "src/data/";

        int[] years = readDataFromFile(dataPath + "years.txt");
        String[] types = readTypesFromFile(dataPath + "types.txt");
        int[][] prices = readPricesFromFile(dataPath + "prices.txt", types, years);

        DataGrapher dataGrapher = new DataGrapher(years, prices, types);

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Data Visualization");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600);
            frame.getContentPane().add(dataGrapher);
            frame.setVisible(true);
        });
    }
    //this shit is so annoying
    private static int[] readDataFromFile(String fileName) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            return reader.lines().mapToInt(Integer::parseInt).toArray();
        } catch (IOException e) {
            throw new IOException(e);
        }
    }

    private static String[] readTypesFromFile(String fileName) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            return reader.lines().toArray(String[]::new);
        } catch (IOException e) {
            throw new IOException(e);
        }
    }

    private static int[][] readPricesFromFile(String fileName, String[] types, int[] years) throws IOException {
        List<int[]> pricesList = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;

            for (String type : types) {
                //read lines and skip makers
                do {
                    line = reader.readLine();
                } while (line != null && (line.startsWith("//") || line.trim().isEmpty()));

                if (line == null) {
                    System.err.println("Unexpected end of file. Expected more data for type: " + type);
                    return null;
                }

                //more debugging :)
                System.out.println("Type: " + type);

                //read prices for current type
                int[] pricesArray = new int[years.length];
                try {
                    for (int i = 0; i < years.length; i++) {
                        while (line != null && line.startsWith("//")) {
                            line = reader.readLine();
                        }

                        if (line == null) {
                            System.err.println("Unexpected end of file. Expected more data for type: " + type);
                            break;
                        }

                        pricesArray[i] = Integer.parseInt(line.replace(",", ""));
                        System.out.println(years[i] + ": " + pricesArray[i]);

                        line = reader.readLine();
                    }

                    pricesList.add(pricesArray);
                } catch (NumberFormatException e) {
                    System.err.println("Error parsing line: " + line);
                    throw new NumberFormatException();
                }
            }
        } catch (IOException e) {
            throw new IOException(e);
        }

        return pricesList.toArray(new int[0][]);
    }

}
