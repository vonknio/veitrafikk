package View;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.LinkedList;

public class StatisticsWindow extends JFrame {

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
    private LinkedList<String> idStrings;
    private JButton jButton;
    private JComboBox<String> vehiclesBox;

    public StatisticsWindow() {
        create();
    }

    private void create() {
        setTitle("Statistics");
        setSize(500, 500);
        setLocationRelativeTo(null);
    }

    private void setup() {
        DecimalFormat dec = new DecimalFormat("#0.00");
        final int rows = 10;
        final int cols = 2;

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(0, cols, 1, 3));
        panel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        panel.setBackground(new Color(75, 75, 75));
        panel.setBorder(new MatteBorder(2, 2, 2, 2, new Color(75, 75, 75)));

        OpaqueLabel successLabel = new OpaqueLabel("RESULT", true);
        OpaqueLabel successValue = success ? new OpaqueLabel("Success", true)
                : new OpaqueLabel("Not completed", true);

        panel.add(successLabel);
        panel.add(successValue);

        OpaqueLabel ticksLabel = new OpaqueLabel("TOTAL TIME", true);
        OpaqueLabel ticksValue = new OpaqueLabel(Long.toString(ticksTotal), true);

        panel.add(ticksLabel);
        panel.add(ticksValue);

        OpaqueLabel vehiclesLabel = new OpaqueLabel("VEHICLES", true);
        panel.add(vehiclesLabel);
        panel.add(new VarPanel("total:", Long.toString(total), "finished:", Long.toString(finished)));

        OpaqueLabel velocityLabel = new OpaqueLabel("VELOCITY", true);

        panel.add(velocityLabel);
        panel.add(new VarPanel("max:", dec.format(velocityMax), "average:", dec.format(velocity)));

        OpaqueLabel pathLabel = new OpaqueLabel("PATH", true);
        panel.add(pathLabel);
        panel.add(new VarPanel("max length:", dec.format(pathMax), "average length:", dec.format(path)));

        OpaqueLabel ticksAliveLabel = new OpaqueLabel("TICKS ALIVE", true);
        panel.add(ticksAliveLabel);
        panel.add(new VarPanel("max:", dec.format(ticksMax), "average:", dec.format(ticks)));

        OpaqueLabel waitLabel = new OpaqueLabel("TICKS WAITING", true);
        panel.add(waitLabel);
        panel.add(new VarPanel("max:", dec.format(waitMax), "average:", dec.format(wait)));

        OpaqueLabel vertices1Label = new OpaqueLabel("VERTICES", true);
        panel.add(vertices1Label);
        panel.add(new VarPanel("no. visited:", Long.toString(vertices), "no. not visited:",
                Long.toString(verticesNot)));

        OpaqueLabel vertices2Label = new OpaqueLabel("VERTICES TIME EMPTY", true);
        panel.add(vertices2Label);
        panel.add(new VarPanel("max", dec.format(timeMax), "average:", dec.format(time)));

        OpaqueLabel vertices3Label = new OpaqueLabel("VERTICES VISITS", true);
        panel.add(vertices3Label);
        panel.add(new VarPanel("most visited:", dec.format(vehiclesMax), "average:",
                dec.format(vehicles)));

        LinkedList<String> idStringsTemp = new LinkedList<>();
        for (String s : idStrings) {
            idStringsTemp.add("Vehicle " + s);
        }
        vehiclesBox = new JComboBox<>(idStringsTemp.toArray(new String[0]));
        panel.add(vehiclesBox);

        jButton = new JButton("Show");
        panel.add(jButton);

        this.add(panel);
    }

    public void addVehicleListener(ActionListener listener) {
        jButton.addActionListener(listener);
    }

    public int getCurrentId() {
        return Integer.parseInt(vehiclesBox.getSelectedItem().toString().substring(8));
    }

    void setValues(double velocity, int vertices, double path, double time, double vehicles, double ticks,
                   double velocityMax, int verticesNot, double pathMax, double timeMax, double vehiclesMax,
                   double ticksMax, boolean success, long ticksTotal, double wait, double waitMax, int total,
                   int finished, LinkedList<String> idStrings) {
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
        this.idStrings = idStrings;
    }

    class VarPanel extends JPanel {
        VarPanel(String desc1, String val1, String desc2, String val2) {
            super(new GridLayout(0, 1));
            JPanel north = new JPanel(new GridLayout(0, 2));
            JPanel south = new JPanel(new GridLayout(0, 2));
            north.add(new OpaqueLabel(desc1, true));
            north.add(new JLabel(val1));
            south.add(new OpaqueLabel(desc2, true));
            south.add(new JLabel(val2));
            this.add(north);
            this.add(south);
            setVisible(true);
        }
    }

    class OpaqueLabel extends JLabel {
        OpaqueLabel(String s, boolean c) {
            super(s, SwingConstants.CENTER);
            setOpaque(true);
        }
    }

    void update() {
        setup();
    }
}
