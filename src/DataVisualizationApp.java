import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class DataVisualizationApp extends JPanel {

    private int[] xValues;
    private int[][] yValues;  // Updated to handle multiple data sets
    private String[] valueNames;
    private Map<String, Color> colorMap;

    public DataVisualizationApp() {
        loadDataFromFile("src/data/data.txt");
    }

    private int getMinValue() {
        int min = Integer.MAX_VALUE;

        for (int[] dataSet : yValues) {
            for (int value : dataSet) {
                min = Math.min(min, value);
            }
        }

        return roundToNearest1000(min);
    }

    private int getMaxValue() {
        int max = Integer.MIN_VALUE;

        for (int[] dataSet : yValues) {
            for (int value : dataSet) {
                max = Math.max(max, value);
            }
        }

        return roundToNearest1000(max);
    }
    private void loadDataFromFile(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
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

                // Initialize xValues array with parsed years
                xValues = new int[numYears];
                for (int i = 0; i < numYears; i++) {
                    xValues[i] = Integer.parseInt(headers[i]);
                }

                // Parse types
                while ((line = reader.readLine()) != null) {
                    if (line.equals("!!Types!!")) {
                        System.out.println("Types section found.");
                        line = reader.readLine(); // Move to the next line
                        String[] types = line.split(",");

                        // Initialize data structures
                        yValues = new int[types.length][numYears];
                        valueNames = types;

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
                        colorMap = new HashMap<>();
                        for (int i = 0; i < types.length; i++) {
                            colorMap.put(types[i], generateRainbowColor(i, types.length));
                        }

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

    private int roundToNearest1000(int value) {
        return Math.round(value / 10000.0f) * 10000;
    }
    //this shit is so fucking annoying to deal with, getting the data to transfer is not working out too well
    //todo: kill myself after this
    /*
    FIXME
        Headers section found.
        Types section found.
        Printing data for debugging:
        xValues: [0]
        yValues: [[0]]
        valueNames: [City Houses]
        minValue: 0
        maxValue: 0
        Printing data for debugging:
        xValues: [0]
        yValues: [[0]]
        valueNames: [City Houses]
        minValue: 0
        maxValue: 0
      WHY IS IT 0???
     */
    private void drawLineGraph(Graphics2D g2d, int width, int height) {
        int numDataSets = yValues.length;
        int MARGIN = 10;

        // Check if there is at least one dataset
        if (numDataSets != 0) {
            int numPoints = xValues.length;

            // Find the min and max values in the y dataset
            double minValue = yValues[0][0];
            double maxValue = yValues[0][0];

            for (int i = 0; i < numDataSets; i++) {
                for (int j = 0; j < numPoints; j++) {
                    double value = yValues[i][j];
                    minValue = Math.min(minValue, value);
                    maxValue = Math.max(maxValue, value);
                }
            }

            // Handle the case where minValue equals maxValue
            double yRange = Math.max(1, maxValue - minValue);

            // Check if yRange is not zero
            if (yRange != 0) {
                System.out.println("Debugging Data:");
                System.out.println("numDataSets: " + numDataSets);
                System.out.println("numPoints: " + numPoints);
                System.out.println("minValue: " + minValue);
                System.out.println("maxValue: " + maxValue);
                System.out.println("yRange: " + yRange);

                // Implement the rest of the drawing logic here...

                // Example: Calculate scaling factor for y-axis
                double scaleY = (height - 2 * MARGIN) / yRange;

                // Example: Draw lines using the calculated scaleY
                for (int i = 0; i < numDataSets; i++) {
                    for (int j = 0; j < numPoints - 1; j++) {
                        int x1 = (int) (MARGIN + j * (width - 2 * MARGIN) / (numPoints - 1));
                        int y1 = (int) (height - MARGIN - (yValues[i][j] - minValue) * scaleY);
                        int x2 = (int) (MARGIN + (j + 1) * (width - 2 * MARGIN) / (numPoints - 1));
                        int y2 = (int) (height - MARGIN - (yValues[i][j + 1] - minValue) * scaleY);

                        // Example: Draw a line segment
                        g2d.drawLine(x1, y1, x2, y2);
                    }
                }
            } else {
                System.out.println("Error: yRange is zero.");
            }
        } else {
            System.out.println("Error: No datasets available.");
        }
    }

    private int calculateYCoordinate(int minValue, int maxValue, int graphHeight, int numDataSets, int dataSetIndex, int dataIndex, int lineY) {
        int denominator = maxValue - minValue;
        int y;

        if (denominator != 0 && numDataSets != 0) {
            y = lineY - (yValues[dataSetIndex][dataIndex] * (graphHeight / numDataSets) / denominator);
        } else {
            // If either denominator or numDataSets is zero, set y to lineY
            y = lineY;
        }

        return y;
    }
    //int keyWidth = 150;
    private void drawKey(Graphics g) {
        int keyX = 20;
        int keyY = 20;
        int keyWidth = 150;
        int colorBoxSize = 20;
        int labelOffset = 5;

        int keyHeight = (valueNames.length * (colorBoxSize + labelOffset)) + labelOffset;

        g.setColor(Color.WHITE);
        g.fillRect(keyX, keyY, keyWidth, keyHeight);

        g.setColor(Color.BLACK);
        g.drawRect(keyX, keyY, keyWidth, keyHeight);

        for (int i = 0; i < valueNames.length; i++) {
            Color rainbowColor = generateRainbowColor(i, valueNames.length);
            g.setColor(rainbowColor);
            int y = keyY + labelOffset + i * (colorBoxSize + labelOffset);
            g.fillRect(keyX + labelOffset, y,
                    colorBoxSize, colorBoxSize);

            g.setColor(Color.BLACK);
            g.drawRect(keyX + labelOffset, y,
                    colorBoxSize, colorBoxSize);

            g.drawString(valueNames[i], keyX + colorBoxSize + 2 * labelOffset,
                    keyY + labelOffset + i * (colorBoxSize + labelOffset) + colorBoxSize);
        }
    }

    private Color generateRainbowColor(int index, int totalEntries) {
        float hue = (float) index / (float) totalEntries;
        return Color.getHSBColor(hue, 1.0f, 1.0f);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Check if xValues, yValues, and valueNames are not null
        if (xValues != null && yValues != null && valueNames != null) {
            Graphics2D g2d = (Graphics2D) g;
            // Implement the drawing logic here
            drawLineGraph(g2d, getWidth(), getHeight());
            drawKey(g2d);
        }
    }
}