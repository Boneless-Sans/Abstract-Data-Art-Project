import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class DataVisualizationApp extends JPanel {

    private int[] xValues;
    private int[][] yValues;
    private String[] valueNames;

    public DataVisualizationApp(int[] xValues, int[][] yValues, String[] valueNames) {
        this.xValues = xValues;
        this.yValues = yValues;
        this.valueNames = valueNames;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (xValues != null && yValues != null && valueNames != null) {
            Graphics2D g2d = (Graphics2D) g;
            drawLineGraph(g2d);
            drawKey(g2d);
        }
    }

    private void drawLineGraph(Graphics2D g2d) {
        int maxValue = Arrays.stream(yValues)
                .flatMapToInt(Arrays::stream)
                .max()
                .orElse(0);

        int minValue = Arrays.stream(yValues)
                .flatMapToInt(Arrays::stream)
                .min()
                .orElse(0);

        // Draw frame
        g2d.drawLine(50, getHeight() - 50, getWidth() - 50, getHeight() - 50);
        g2d.drawLine(50, getHeight() - 50, 50, 50);

        // Draw x-axis labels
        int xLabelStep = (getWidth() - 100) / (xValues.length - 1);
        for (int i = 0; i < xValues.length; i++) {
            int xLabel = 50 + i * xLabelStep;
            g2d.drawString(Integer.toString(xValues[i]), xLabel, getHeight() - 30);
        }

        // Draw y-axis labels
        int yLabelStep = (getHeight() - 100) / 10;
        for (int i = 0; i <= 10; i++) {
            int yLabel = minValue + i * (maxValue - minValue) / 10;
            g2d.drawString(Integer.toString(yLabel), 30, getHeight() - 50 - i * yLabelStep);
        }

        // Loop through each data set
        for (int dataSetIndex = 0; dataSetIndex < yValues.length; dataSetIndex++) {
            if (dataSetIndex >= valueNames.length) {
                // Added bounds checking to prevent ArrayIndexOutOfBoundsException
                break;
            }

            int[] xPoints = new int[xValues.length];
            int[] yPoints = new int[xValues.length];

            // Get color from the key
            Color lineColor = generateRainbowColor();
            g2d.setColor(lineColor);

            // Loop through each x-value and calculate the corresponding y-coordinate
            for (int i = 0; i < xValues.length; i++) {
                if (i >= yValues[dataSetIndex].length) {
                    // Added bounds checking to prevent ArrayIndexOutOfBoundsException
                    break;
                }

                int x = calculateXCoordinate(i, xValues.length, getWidth());
                int y = calculateYCoordinate(yValues[dataSetIndex][i], maxValue, minValue, getHeight() - 50, yLabelStep);

                xPoints[i] = x;
                yPoints[i] = y;

                // Draw dots for each data point
                g2d.fillOval(x - 2, y - 2, 4, 4);

                // Draw price text above each dot
                String priceText = String.valueOf(yValues[dataSetIndex][i]);
                int textWidth = g2d.getFontMetrics().stringWidth(priceText);
                g2d.drawString(priceText, x - textWidth / 2, y - 8);
            }

            // Draw the line for the current data set
            g2d.drawPolyline(xPoints, yPoints, xValues.length);
        }
    }

    private int calculateXCoordinate(int index, int totalPoints, int width) {
        double xLabelStep = (double) (width - 100) / (totalPoints - 1);
        int xCoordinate = (int) (50 + index * xLabelStep);
        //System.out.println("Index: " + index + ", X Coordinate: " + xCoordinate);
        return xCoordinate;
    }

    private int calculateYCoordinate(int value, int maxValue, int minValue, int height, int yLabelStep) {
        double yRange = maxValue - minValue;
        double yScale = (double) (height - 100) / yRange;

        int y = height - 50 - (int) (yScale * (value - minValue));

        // Ensure y is within the valid range
        return Math.min(Math.max(y, 50), height - 50);
    }

    private void drawKey(Graphics g) {
        int keyX = getWidth() - 170;
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
            Color rainbowColor = generateRainbowColor();
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
    private Color generateRainbowColor() {
        String filePath = "src/data/colors.txt";
        String[] colors = new String[0];
        try {
            // Read the file
            BufferedReader reader = new BufferedReader(new FileReader(filePath));

            // Count the number of lines in the file
            long lineCount = reader.lines().count();

            // Create an array to store the lines
            colors = new String[(int) lineCount];

            // Reset the reader
            reader.close();
            reader = new BufferedReader(new FileReader(filePath));

            // Read each line from the file and save it to the array
            for (int i = 0; i < lineCount; i++) {
                colors[i] = reader.readLine();
            }

            // Close the file reader
            reader.close();

            // Print the resulting array (optional)
            for (int i = 0; i < colors.length; i++) {
                System.out.println("Line " + (i + 1) + ": " + colors[i]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        int ranIndex = (int) (Math.random() * (colors.length - 1));
        String[] colorValue = colors[ranIndex].split(",");
        int[] rgbValues = new int[]{
                Integer.parseInt(colorValue[0]),
                Integer.parseInt(colorValue[1]),
                Integer.parseInt(colorValue[2])
        };
        return new Color(rgbValues[0], rgbValues[1], rgbValues[2]);
    }
}
