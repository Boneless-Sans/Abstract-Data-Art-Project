import javax.swing.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Main {

    public static void main(String[] args) {
        String dataPath = "src/data/";

        int[] years = readDataFromFile(dataPath + "years.txt");
        String[] types = readTypesFromFile(dataPath + "types.txt");
        int[][] prices = readPricesFromFile(dataPath + "prices.txt", types, years);

        // Print all the data
        System.out.println("Debugging all data:");
        System.out.println("Years: " + Arrays.toString(years));
        System.out.println("Types: " + Arrays.toString(types));
        System.out.println("Prices:");
        for (int i = 0; i < Objects.requireNonNull(prices).length; i++) {
            System.out.println("Type: " + types[i] + ", Prices: " + Arrays.toString(prices[i]));
        }

        DataVisualizationApp dataVisualizationApp = new DataVisualizationApp(years, prices, types);

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Data Visualization");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600);
            frame.getContentPane().add(dataVisualizationApp);
            frame.setVisible(true);
        });
    }

    private static int[] readDataFromFile(String fileName) {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            return reader.lines().mapToInt(Integer::parseInt).toArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String[] readTypesFromFile(String fileName) {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            return reader.lines().toArray(String[]::new);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static int[][] readPricesFromFile(String fileName, String[] types, int[] years) {
        List<int[]> pricesList = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;

            for (String type : types) {
                // Read the next line and skip comments and empty lines
                do {
                    line = reader.readLine();
                } while (line != null && (line.startsWith("//") || line.trim().isEmpty()));

                if (line == null) {
                    System.err.println("Unexpected end of file. Expected more data for type: " + type);
                    return null;
                }

                // Debug print
                System.out.println("Type: " + type);

                // Read prices for the current type
                int[] pricesArray = new int[years.length];
                try {
                    for (int i = 0; i < years.length; i++) {
                        // Skip lines starting with "//" inside the loop
                        while (line != null && line.startsWith("//")) {
                            line = reader.readLine();
                        }

                        // Handle the end of the file for the current type
                        if (line == null) {
                            System.err.println("Unexpected end of file. Expected more data for type: " + type);
                            break;
                        }

                        pricesArray[i] = Integer.parseInt(line.replace(",", ""));
                        System.out.println(years[i] + ": " + pricesArray[i]);

                        // Read the next line for the next iteration
                        line = reader.readLine();
                    }

                    pricesList.add(pricesArray);
                } catch (NumberFormatException e) {
                    System.err.println("Error parsing line: " + line);
                    e.printStackTrace();
                    return null;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return pricesList.toArray(new int[0][]);
    }

}
