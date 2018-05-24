package View;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.Collection;

class MapEditor {

    private int size;
    private int dist;

    public JFrame frame;
    Container container;
    MapPlanner mapPlanner;

    private JButton quit;
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
        JPanel gameButtons = new JPanel(new BorderLayout());
        JPanel otherButtons = new JPanel(new BorderLayout());
        JPanel simButtons = new JPanel(new BorderLayout());
        JPanel menu = new JPanel(new BorderLayout());

        JButton remove = new JButton("Delete");
        remove.addActionListener(e -> {
            mapPlanner.drawSink = false;
            mapPlanner.drawSource = false;
            mapPlanner.startedDrawing = false;
            if (mapPlanner.deleteMode) {
                mapPlanner.deleteMode = false;
                remove.setText("Delete");
            }
            else {
                mapPlanner.deleteMode = true;
                remove.setText("Add");
            }
        });

        JButton addSource = new JButton("Add source");
        addSource.addActionListener(e -> {
            mapPlanner.drawSink = false;
            mapPlanner.drawSource = true;
            mapPlanner.startedDrawing = false;
            mapPlanner.deleteMode = false;
            remove.setText("Delete");
        });

        JButton addSink = new JButton("Add sink");
        addSink.addActionListener(e -> {
            mapPlanner.drawSource = false;
            mapPlanner.drawSink = true;
            mapPlanner.startedDrawing = false;
            mapPlanner.deleteMode = false;
            remove.setText("Delete");
        });

        quit = new JButton("Quit");
        quit.addActionListener(e -> System.exit(0));

        start = new JButton("Start");

        drawingButtons.add(addSource, BorderLayout.EAST);
        drawingButtons.add(addSink, BorderLayout.CENTER);
        drawingButtons.add(remove, BorderLayout.WEST);

        simButtons.add(start, BorderLayout.WEST);

        gameButtons.add(simButtons, BorderLayout.WEST);
        gameButtons.add(drawingButtons, BorderLayout.EAST);

        otherButtons.add(quit, BorderLayout.WEST);

        menu.add(gameButtons, BorderLayout.EAST);
        menu.add(otherButtons, BorderLayout.WEST);

        container.add(menu, BorderLayout.SOUTH);
    }

    private void setupMapPlanner (){
        container.add(mapPlanner, BorderLayout.CENTER);
    }

    public int[] getCoordinates() {
        return mapPlanner.getCoordinates();
    }


    public void drawRoad(int x1, int y1, int x2, int y2) { mapPlanner.drawRoad(x1, y1, x2, y2); }

    public void drawSource(int x1, int y1) { mapPlanner.drawSpecialVertex(x1, y1, new Color(0,255,0)); }

    public void drawSink(int x1, int y1) { mapPlanner.drawSpecialVertex(x1, y1, new Color(255,150,0)); }

    public void removeRoad(int x1, int y1, int x2, int y2) { mapPlanner.removeRoad(x1, y1, x2, y2); }

    public void removeSpecialVertex(int x1, int y1) { mapPlanner.removeSpecialVertex(x1, y1); }

    public void updateVehicles(Collection<int[]> coordinates) { mapPlanner.updateVehicles(coordinates); }

    public void addNextTickListener(ActionListener listener) {
        start.addActionListener(e -> {
            mapPlanner.blockDrawing = true;
            drawingButtons.setVisible(false);
            start.setText("Next tick");
            listener.actionPerformed(e);
        });}

    public void addNewRoadListener(ActionListener listener) { mapPlanner.addNewRoadListener(listener); }

    public void addRemoveListener(ActionListener listener) { mapPlanner.addRemoveListener(listener); }

    public void addNewSourceListener(ActionListener listener) { mapPlanner.addNewSourceListener(listener); }

    public void addNewSinkListener(ActionListener listener) { mapPlanner.addNewSinkListener(listener); }

}
