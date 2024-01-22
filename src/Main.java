import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Data Visualization");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600);

            DataVisualizationApp dataVisualizationApp = new DataVisualizationApp();
            frame.getContentPane().add(dataVisualizationApp);

            frame.setVisible(true);
        });
    }
}
