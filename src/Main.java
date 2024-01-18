import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Main {

    public static void main(String[] args) {
        // Parse the data file and provide it to DataVisualizationApp
        try (BufferedReader reader = new BufferedReader(new FileReader("src/data/data.txt"))) {
            // Parse headers
            String line;
            boolean headersFound = false;
            while ((line = reader.readLine()) != null) {
                if (line.equals("!!Headers!!")) {
                    System.out.println("Headers section found.");
                    headersFound = true;
                    break;
                }
            }

            if (headersFound) {
                line = reader.readLine(); // Move to the next line
                String[] headers = line.split(",");
                int numYears = headers.length;

                // Parse types
                while ((line = reader.readLine()) != null) {
                    if (line.equals("!!Types!!")) {
                        System.out.println("Types section found.");
                        line = reader.readLine(); // Move to the next line
                        String[] types = line.split(",");

                        // Initialize data structures
                        int[] xValues = new int[numYears];
                        int[][] yValues = new int[types.length][numYears];
                        String[] valueNames = types;

                        // Parse data for each type
                        for (int i = 0; i < types.length; i++) {
                            while ((line = reader.readLine()) != null) {
                                if (line.equals("!!" + types[i] + "!!")) {
                                    System.out.println(types[i] + " section found.");
                                    for (int j = 0; j < numYears; j++) {
                                        line = reader.readLine();
                                        yValues[i][j] = Integer.parseInt(line);
                                    }
                                    break;
                                }
                            }
                        }

                        // Example color mapping: replace this with your actual color mapping logic
                        Map<String, Color> colorMap = new HashMap<>();
                        for (int i = 0; i < types.length; i++) {
                            colorMap.put(types[i], generateRainbowColor(i, types.length));
                        }

                        // Create and display the GUI
                        SwingUtilities.invokeLater(() -> {
                            JFrame frame = new JFrame("Data Visualization");
                            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                            frame.setSize(800, 600);

                            DataVisualizationApp dataVisualizationApp = new DataVisualizationApp(xValues, yValues, valueNames, colorMap);
                            frame.getContentPane().add(dataVisualizationApp);

                            frame.setVisible(true);
                        });

                        break;
                    }
                }
            } else {
                System.err.println("Invalid file format: Headers section is missing.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static Color generateRainbowColor(int index, int totalEntries) {
        float hue = (float) index / (float) totalEntries;
        return Color.getHSBColor(hue, 1.0f, 1.0f);
    }
}
