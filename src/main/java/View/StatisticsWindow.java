package View;

import javax.swing.*;
import java.text.DecimalFormat;

public class StatisticsWindow extends JFrame {

    private JPanel panel;
    private long ticksTotal;
    private int verticesNot;
    private int vertices;
    private int total;
    private int finished;
    private double velocity;
    private double path;
    private double time;
    private double vehicles;
    private double ticks;
    private double velocityMax;
    private double pathMax;
    private double timeMax;
    private double vehiclesMax;
    private double ticksMax;
    private double wait;
    private double waitMax;
    private boolean success;


    public StatisticsWindow() {
        create();
    }

    private void create() {
        setup();
        setTitle("Statistics");
        setSize(500, 500);
        setLocationRelativeTo(null);
    }

    private void setup() {
        DecimalFormat dec = new DecimalFormat("#0.00");
        panel = new JPanel();
        panel.setLayout(null);
        JLabel successLabel = new JLabel("Result: " + (success ? "Success" : "Failure"));
        successLabel.setBounds(5, 5, 400, 40);
        JLabel ticksLabel = new JLabel("Time: " + ticksTotal);
        ticksLabel.setBounds(5, 45, 400, 40);
        JLabel vehiclesLabel = new JLabel("Vehicles:   total: " + total + " finished: " + finished);
        vehiclesLabel.setBounds(5, 85, 400, 40);
        JLabel velocityPanel = new JLabel("Velocity:   max: " + dec.format(velocityMax) + " average: " + dec.format(velocity));
        velocityPanel.setBounds(5, 125, 400, 40);
        JLabel pathPanel = new JLabel("Path length:   max: " + dec.format(pathMax) + " average: " + dec.format(path));
        pathPanel.setBounds(5, 165, 400, 40);
        JLabel ticksPanel = new JLabel("Ticks alive:   max: " + dec.format(ticksMax) + " average: " + dec.format(ticks));
        ticksPanel.setBounds(5, 205, 400, 40);
        JLabel waitPanel = new JLabel("Waiting time:   max: " + dec.format(waitMax) + " average: " + dec.format(wait));
        waitPanel.setBounds(5, 245, 400, 40);
        JLabel verticesLabel = new JLabel("Vertices: ");
        verticesLabel.setBounds(5, 285, 400, 40);
        JLabel verticesPanel = new JLabel("Visited: " + dec.format(vertices) + "   Not visited: " + dec.format(verticesNot));
        verticesPanel.setBounds(5, 325, 400, 40);
        JLabel timePanel = new JLabel("Time empty:   max: " + dec.format(timeMax) + " average: " + dec.format(time));
        timePanel.setBounds(5, 365, 400, 40);
        JLabel vehiclesPanel = new JLabel("Number of visiting vehicles:   max: " + dec.format(vehiclesMax) + " average: " + dec.format(vehicles));
        vehiclesPanel.setBounds(5, 405, 400, 40);

        panel.add(successLabel);
        panel.add(ticksLabel);
        panel.add(vehiclesLabel);
        panel.add(waitPanel);
        panel.add(velocityPanel);
        panel.add(vehiclesPanel);
        panel.add(verticesPanel);
        panel.add(verticesLabel);
        panel.add(timePanel);
        panel.add(pathPanel);
        panel.add(ticksPanel);
        this.add(panel);
    }

    void setValues(double velocity, int vertices, double path, double time, double vehicles, double ticks, double velocityMax, int verticesNot, double pathMax, double timeMax,
                   double vehiclesMax, double ticksMax, boolean success, long ticksTotal, double wait, double waitMax, int total, int finished) {
        this.velocity = velocity;
        this.vertices = vertices;
        this.path = path;
        this.time = time;
        this.vehicles = vehicles;
        this.ticks = ticks;
        this.velocityMax = velocityMax;
        this.verticesNot = verticesNot;
        this.pathMax = pathMax;
        this.timeMax = timeMax;
        this.vehiclesMax = vehiclesMax;
        this.ticksMax = ticksMax;
        this.success = success;
        this.ticksTotal = ticksTotal;
        this.wait = wait;
        this.waitMax = waitMax;
        this.total = total;
        this.finished = finished;
    }

    void update() {
        setup();
    }
}
