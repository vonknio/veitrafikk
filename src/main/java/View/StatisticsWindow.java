package View;

import javax.swing.*;

public class StatisticsWindow extends JFrame {

    private JPanel panel;
    private double velocity = 0;
    private int vertices;
    private double path;
    private double time;
    private double vehicles;
    private double ticks;

    public StatisticsWindow() {
        create();
    }

    private void create() {
        setup();
        setTitle("Statistics");
        setSize(350, 300);
        setLocationRelativeTo(null);
    }

    private void setup() {
        panel = new JPanel();
        panel.setLayout(null);
        JLabel velocityPanel = new JLabel("Average vehicle velocity: " + velocity);
        velocityPanel.setBounds(5, 5, 400, 40);
        JLabel verticesPanel = new JLabel("Total vertices visited: " + vertices);
        verticesPanel.setBounds(5, 45, 400, 40);
        JLabel pathPanel = new JLabel("Average path length: " + path);
        pathPanel.setBounds(5, 85, 400, 40);
        JLabel timePanel = new JLabel("Average time vertex was empty: " + time);
        timePanel.setBounds(5, 125, 400, 40);
        JLabel vehiclesPanel = new JLabel("Average number of vehicles visiting vertex: " + vehicles);
        vehiclesPanel.setBounds(5, 165, 400, 40);
        JLabel ticksPanel = new JLabel("Average ticks vehicle was alive: " + ticks);
        ticksPanel.setBounds(5, 205, 400, 40);

        panel.add(velocityPanel);
        panel.add(vehiclesPanel);
        panel.add(verticesPanel);
        panel.add(timePanel);
        panel.add(pathPanel);
        panel.add(ticksPanel);
        this.add(panel);
    }

    void setVelocity(double velocity) {
        this.velocity = velocity;
    }

    void setVertices(int vertices) {
        this.vertices = vertices;
    }

    void setPath(double path) {
        this.path = path;
    }

    void setTime(double time) {
        this.time = time;
    }

    void setVehicles(double vehicles) {
        this.vehicles = vehicles;
    }

    void setTicks(double ticks) {
        this.ticks = ticks;
    }

    void update() {
        setup();
    }
}
