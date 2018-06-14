package View;

import javax.swing.*;
import javax.swing.border.MatteBorder;
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

    public VehicleWindow(int[] previous, boolean hasFinished, int[] currentPosition, int id, double velocity,
                         long ticksAlive, Color color) {
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

        panel.setLayout(new GridLayout(0, 2, 1, 2));
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        panel.setBackground(color);
        panel.setBorder(new MatteBorder(2, 2, 2, 2, color));

        panel.add(new OpaqueLabel("VEHICLE ID: "));
        panel.add(new OpaqueLabel(Integer.toString(id)));

        panel.add(new OpaqueLabel("FINISHED: "));
        panel.add(new OpaqueLabel(hasFinished ? "True" : "False"));

        panel.add(new OpaqueLabel("CURRENT POSITION: "));
        panel.add(new OpaqueLabel("(" + currentPosition[0] + "," + currentPosition[1] + ")"));

        panel.add(new OpaqueLabel("PREVIOUS POSITION: "));
        panel.add(new OpaqueLabel("(" + previous[0] + "," + previous[1] + ")"));

        panel.add(new OpaqueLabel("VELOCITY: "));
        panel.add(new OpaqueLabel(new DecimalFormat("#0.00").format(velocity)));

        panel.add(new OpaqueLabel("TICKS ALIVE: "));
        panel.add(new OpaqueLabel(Long.toString(ticksAlive)));

        this.add(panel);
    }

    class OpaqueLabel extends JLabel {
        OpaqueLabel(String s) {
            super(s, SwingConstants.CENTER);
            setOpaque(true);
        }
    }

    public void update() {
        setup();
    }
}
