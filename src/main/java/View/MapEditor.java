package View;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

class MapEditor {

    private int size;
    private int dist;

    public JFrame frame;
    Container container;
    MapPlanner mapPlanner;

    private JButton start;
    private JPanel drawingButtons;

    public MapEditor (int size, int dist){

        this.size = size;
        this.dist = dist;

        mapPlanner = new MapPlanner(size, dist);

        setupFrame();
        setupContainer();
        setupMapPlanner();

    }

    public void setVisible(boolean b) {
        frame.setVisible(b);
    }

    private void setupFrame (){

        frame = new JFrame();
        frame.setSize(size * dist + dist, size * dist + 100);
        frame.setTitle("Veitrafikk - Map Editor");
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }

    private void setupContainer (){

        container = frame.getContentPane();
        container.setLayout(new BorderLayout());

        drawingButtons = new JPanel(new BorderLayout());
        JPanel simButtons = new JPanel(new BorderLayout());
        JPanel menu = new JPanel(new BorderLayout());

        JButton addSource = new JButton("Add source");
        addSource.addActionListener(e -> {
            mapPlanner.drawSink = false;
            mapPlanner.drawSource = true;
            mapPlanner.startedDrawing = false;
        });

        JButton addSink = new JButton("Add sink");
        addSink.addActionListener(e -> {
            mapPlanner.drawSource = false;
            mapPlanner.drawSink = true;
            mapPlanner.startedDrawing = false;
        });

        start = new JButton("Start");

        drawingButtons.add(addSource, BorderLayout.EAST);
        drawingButtons.add(addSink, BorderLayout.WEST);

        simButtons.add(start, BorderLayout.WEST);

        menu.add(drawingButtons, BorderLayout.EAST);
        menu.add(simButtons, BorderLayout.WEST);

        container.add(menu, BorderLayout.SOUTH);

    }

    private void setupMapPlanner (){

        container.add(mapPlanner, BorderLayout.CENTER);

    }

    public int[] getCoordinates() {
        return mapPlanner.getCoordinates();
    }


    public void drawRoad(int x1, int y1, int x2, int y2) { mapPlanner.drawRoad(x1, y1, x2, y2); }

    public void drawSource(int x1, int y1) { mapPlanner.drawSource(x1, y1); }

    public void drawSink(int x1, int y1) { mapPlanner.drawSink(x1, y1); }


    public void addNextTickListener(ActionListener listener) {
        start.addActionListener(e -> {
            mapPlanner.blockDrawing = true;
            drawingButtons.setVisible(false);
            start.setText("Next tick");
            listener.actionPerformed(e);
        });}

    public void addNewRoadListener(ActionListener listener) { mapPlanner.addNewRoadListener(listener); }

    public void addNewSourceListener(ActionListener listener) { mapPlanner.addNewSourceListener(listener); }

    public void addNewSinkListener(ActionListener listener) { mapPlanner.addNewSinkListener(listener); }

}
