import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class DataGrapher_Pre_Animation extends JPanel {

    private final int[] xValues;
    private final int[][] yValues;
    private final String[] valueNames;

    public DataGrapher_Pre_Animation(int[] xValues, int[][] yValues, String[] valueNames) {
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

        //draw graph frame
        g2d.drawLine(50, getHeight() - 50, getWidth() - 50, getHeight() - 50);
        g2d.drawLine(50, getHeight() - 50, 50, 50);

        //draw x axis labels
        int xLabelStep = (getWidth() - 100) / (xValues.length - 1);
        for (int i = 0; i < xValues.length; i++) {
            int xLabel = 50 + i * xLabelStep;
            g2d.drawString(Integer.toString(xValues[i]), xLabel, getHeight() - 30);
        }

        //draw y axis labels
        int yLabelStep = (getHeight() - 100) / 10;
        for (int i = 0; i <= 10; i++) {
            int yLabel = minValue + i * (maxValue - minValue) / 10;
            g2d.drawString(Integer.toString(yLabel), 30, getHeight() - 50 - i * yLabelStep);
        }

        for (int dataSetIndex = 0; dataSetIndex < yValues.length; dataSetIndex++) {
            if (dataSetIndex >= valueNames.length) {
                //prevents ArrayIndexOutOfBoundsException >:(
                break;
            }

            int[] xPoints = new int[xValues.length];
            int[] yPoints = new int[xValues.length];

            Color lineColor = generateRainbowColor(dataSetIndex);
            g2d.setColor(lineColor);

            for (int i = 0; i < xValues.length; i++) {
                if (i >= yValues[dataSetIndex].length) {
                    //prevents ArrayIndexOutOfBoundsException >:(
                    break;
                }

                int x = calculateXCoordinate(i, xValues.length, getWidth());
                int y = calculateYCoordinate(yValues[dataSetIndex][i], maxValue, minValue, getHeight() - 50);

                xPoints[i] = x;
                yPoints[i] = y;

                //insert dots for data points
                g2d.fillOval(x - 2, y - 2, 4, 4);

                //draw price above each data point dot
                String priceText = "$" + yValues[dataSetIndex][i];
                int textWidth = g2d.getFontMetrics().stringWidth(priceText);
                g2d.drawString(priceText, x - textWidth / 2, y - 8);
            }

            g2d.drawPolyline(xPoints, yPoints, xValues.length);
        }
    }

    private int calculateXCoordinate(int index, int totalPoints, int width) {
        double xLabelStep = (double) (width - 100) / (totalPoints - 1);
        return (int) (50 + index * xLabelStep);
    }

    private int calculateYCoordinate(int value, int maxValue, int minValue, int height) {
        double yRange = maxValue - minValue;
        double yScale = (double) (height - 100) / yRange;

        int y = height - 50 - (int) (yScale * (value - minValue));

        return Math.min(Math.max(y, 50), height - 50);
    }

    private void drawKey(Graphics g) {
        int keyX = getWidth() - 170;
        int keyY = 20;
        int keyWidth = 150;
        int colorBoxSize = 20;
        int labelOffset = 5;

        int keyHeight = (valueNames.length * (colorBoxSize + labelOffset)) + labelOffset;

        g.setColor(Color.WHITE); //controls background color
        g.fillRect(keyX, keyY, keyWidth, keyHeight);

        g.setColor(Color.BLACK); //controls border color
        g.drawRect(keyX, keyY, keyWidth, keyHeight);

        for (int i = 0; i < valueNames.length; i++) {
            Color rainbowColor = generateRainbowColor(i);
            g.setColor(rainbowColor);
            int y = keyY + labelOffset + i * (colorBoxSize + labelOffset);
            g.fillRect(keyX + labelOffset, y,
                    colorBoxSize, colorBoxSize);

            g.setColor(Color.BLACK); //controls font color
            g.drawRect(keyX + labelOffset, y,
                    colorBoxSize, colorBoxSize);

            g.drawString(valueNames[i], keyX + colorBoxSize + 2 * labelOffset,
                    keyY + labelOffset + i * (colorBoxSize + labelOffset) + colorBoxSize);
        }
    }
    private Color generateRainbowColor(int index) { //formally made random colors, now just does the rainbow, looks way better
       Color[] colors = {Color.red,
               new Color(255,85,0),
               new Color(200,200,0), //how do I make the yellow not look like piss without breaking the rainbow
               new Color(0,200,0),
               Color.blue,
               Color.magenta};
       return colors[index];
    }
}