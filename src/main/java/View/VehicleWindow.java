package View;

import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;

public class VehicleWindow extends JFrame {
    private int[] previous;
    private boolean hasFinished;
    private int[] currentPosition;
    private int id;
    private double velocity;
    private long ticksAlive;
    private Color color;

    public VehicleWindow(int[] previous, boolean hasFinished, int[] currentPosition, int id, double velocity, long ticksAlive, Color color) {
        this.previous = previous;
        this.hasFinished = hasFinished;
        this.currentPosition = currentPosition;
        this.id = id;
        this.velocity = velocity;
        this.ticksAlive = ticksAlive;
        this.color = color;
        create();
    }

    public VehicleWindow(int id) {
        this.id = id;
        create();
    }

    private void create() {
        setTitle("Vehicle " + id);
        setSize(300, 300);
        setLocationRelativeTo(null);
    }

    private void setup() {
        JPanel panel = new JPanel();
        panel.setLayout(null);

        JLabel idLabel = new JLabel();
        idLabel.setText("Vehicle id: " + id);
        idLabel.setBounds(20, 20, 250, 40);

        JLabel finishedLabel = new JLabel();
        finishedLabel.setText("Finished: " + hasFinished);
        finishedLabel.setBounds(20, 60, 250, 40);

        JLabel positionLabel = new JLabel();
        positionLabel.setText("Current position: (" + currentPosition[0] + "," + currentPosition[1] + ")");
        positionLabel.setBounds(20, 100, 250, 40);

        JLabel prevPositionLabel = new JLabel();
        prevPositionLabel.setText("Previous position: (" + previous[0] + "," + previous[1] + ")");
        prevPositionLabel.setBounds(20, 140, 250, 40);

        JLabel velocityLabel = new JLabel();
        velocityLabel.setText("Velocity: " + new DecimalFormat("#0.00").format(velocity));
        velocityLabel.setBounds(20, 180, 250, 40);

        JLabel ticksLabel = new JLabel();
        ticksLabel.setText("Ticks alive: " + ticksAlive);
        ticksLabel.setBounds(20, 220, 250, 40);

        panel.add(idLabel);
        panel.add(finishedLabel);
        panel.add(positionLabel);
        panel.add(prevPositionLabel);
        panel.add(velocityLabel);
        panel.add(ticksLabel);

        this.add(panel);
    }

    public void update() {
        setup();
    }
}
