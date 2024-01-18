import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class DataVisualizationApp extends JPanel {

    private int[] xValues;
    private int[][] yValues;  // Updated to handle multiple data sets
    private String[] valueNames;
    private Map<String, Color> colorMap;

    public DataVisualizationApp(int[] xValues, int[][] yValues, String[] valueNames, Map<String, Color> colorMap) {
        this.xValues = xValues;
        this.yValues = yValues;
        this.valueNames = valueNames;
        this.colorMap = colorMap;
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
      WHY IS IT FUCKING 0???
     */
    private void drawLineGraph(Graphics g) {
        System.out.println("Printing data for debugging:");
        System.out.println("xValues: " + Arrays.toString(xValues));
        System.out.println("yValues: " + Arrays.deepToString(yValues));
        System.out.println("valueNames: " + Arrays.toString(valueNames));

        int minValue = getMinValue();
        int maxValue = getMaxValue();

        System.out.println("minValue: " + minValue);
        System.out.println("maxValue: " + maxValue);

        int xMargin = 50;
        int yMargin = 50;
        int graphWidth = getWidth() - 2 * xMargin;
        int graphHeight = getHeight() - 2 * yMargin;

        int numPoints = Math.min(xValues.length, yValues[0].length);
        int numDataSets = yValues.length;

        int xScale = graphWidth / (numPoints - 1);

        // Draw a line for each data set with the corresponding color
        for (int dataSetIndex = 0; dataSetIndex < numDataSets; dataSetIndex++) {
            // Calculate y position for each line
            int lineY = getHeight() - yMargin - dataSetIndex * (graphHeight / numDataSets);

            // Get the color for the current data set
            String dataSetName = valueNames[dataSetIndex];
            Color lineColor = colorMap.get(dataSetName);
            g.setColor(lineColor);

            // Draw the line using data points with dots
            for (int i = 0; i < numPoints - 1; i++) {
                int x1 = xMargin + i * xScale;
                int y1 = calculateYCoordinate(minValue, maxValue, graphHeight, numDataSets, dataSetIndex, i, lineY);
                int x2 = xMargin + (i + 1) * xScale;
                int y2 = calculateYCoordinate(minValue, maxValue, graphHeight, numDataSets, dataSetIndex, i + 1, lineY);

                // Draw the line
                g.drawLine(x1, y1, x2, y2);

                // Draw a dot at each data point
                int dotSize = 5; // Adjust the size of the dot
                g.fillOval(x1 - dotSize / 2, y1 - dotSize / 2, dotSize, dotSize);
            }
        }
    }

    private int calculateYCoordinate(int minValue, int maxValue, int graphHeight, int numDataSets, int dataSetIndex, int dataIndex, int lineY) {
        int denominator = maxValue - minValue;
        int y;

        if (denominator != 0) {
            y = lineY - (yValues[dataSetIndex][dataIndex] * (graphHeight / numDataSets) / denominator);
        } else {
            // Handle the case where denominator is zero (e.g., all values are the same)
            System.err.println("Division by zero detected. dataSetIndex: " + dataSetIndex + ", dataIndex: " + dataIndex);
            System.err.println("minValue: " + minValue + ", maxValue: " + maxValue);
            System.err.println("yValues[dataSetIndex][dataIndex]: " + yValues[dataSetIndex][dataIndex]);
            System.err.println("graphHeight: " + graphHeight + ", numDataSets: " + numDataSets);
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
            g.fillRect(keyX + labelOffset, keyY + labelOffset + i * (colorBoxSize + labelOffset),
                    colorBoxSize, colorBoxSize);

            g.setColor(Color.BLACK);
            g.drawRect(keyX + labelOffset, keyY + labelOffset + i * (colorBoxSize + labelOffset),
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
            // Implement the drawing logic here
            drawLineGraph(g);
            drawKey(g);
        }
    }
}