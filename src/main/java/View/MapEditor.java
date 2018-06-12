package View;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.Collection;

class MapEditor {

    private int size;
    private int dist;

    private ActionListener firstTickListener;

    JFrame frame;
    Container container;
    MapPlanner mapPlanner;

    private JButton quit;
    private JButton play;
    private JButton settings;
    private JButton pause;
    private JButton start;
    private JButton stats;
    private JPanel playButtons;
    private JPanel gameButtons;
    private JPanel drawingButtons;
    private JComboBox modesMenu;
    private String[] modes = {"SHORTEST_PATH_STATIC", "SHORTEST_PATH_DYNAMIC", "RANDOM_STATIC", "RANDOM_DYNAMIC"};

    MapEditor(int size, int userDist, boolean fixed) {
        this.size = size;

        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int border = (int) Math.min(dimension.getHeight(), dimension.getWidth());
        if (!fixed)
            this.dist = (border - 150)/(size+2);
        else
            this.dist = userDist;

        int width = Math.min(dist/4, 11);
        width = Math.max(width, 1);
        if (width%2 == 0) width -= 1;

        mapPlanner = new MapPlanner(size, dist, width);

        setupFrame();
        setupContainer();
        setupMapPlanner();
    }

    void setVisible(boolean b) {
        frame.setVisible(b);
    }

    private void setupFrame (){
        frame = new JFrame();
        frame.setSize(Math.max(size * dist + dist, 550), Math.max(size * dist + Math.max(125, dist*2), 675));
        frame.setTitle("Veitrafikk - Map Editor");
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    private void setupContainer (){
        container = frame.getContentPane();
        container.setLayout(new BorderLayout());

        drawingButtons = new JPanel(new BorderLayout());
        gameButtons = new JPanel(new BorderLayout());
        JPanel otherButtons = new JPanel(new BorderLayout());
        JPanel simButtons = new JPanel(new BorderLayout());
        playButtons = new JPanel(new BorderLayout());
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

        quit = new JButton("Back");

        settings = new JButton("Settings");

        stats = new JButton("Stats");
        stats.setVisible(false);

        modesMenu = new JComboBox<>(modes);

        start = new JButton("Start");

        play = new JButton("Play");
        play.setVisible(false);

        pause = new JButton("Pause");
        pause.setVisible(false);

        playButtons.add(play, BorderLayout.WEST);
        playButtons.add(pause, BorderLayout.CENTER);

        drawingButtons.add(addSource, BorderLayout.EAST);
        drawingButtons.add(addSink, BorderLayout.CENTER);
        drawingButtons.add(remove, BorderLayout.WEST);

        simButtons.add(drawingButtons, BorderLayout.NORTH);
        simButtons.add(modesMenu, BorderLayout.CENTER);

        gameButtons.add(simButtons, BorderLayout.EAST);
        gameButtons.add(start, BorderLayout.CENTER);
        gameButtons.add(playButtons, BorderLayout.NORTH);

        otherButtons.add(settings, BorderLayout.CENTER);
        otherButtons.add(quit, BorderLayout.WEST);
        otherButtons.add(stats, BorderLayout.EAST);

        menu.add(gameButtons, BorderLayout.EAST);
        menu.add(otherButtons, BorderLayout.WEST);

        container.add(menu, BorderLayout.SOUTH);
    }

    private void setupMapPlanner (){ container.add(mapPlanner, BorderLayout.CENTER); }

    int[] getCoordinates() {
        return mapPlanner.getCoordinates();
    }

    void animate() throws InterruptedException { mapPlanner.animate(); }

    void drawRoad(int x1, int y1, int x2, int y2) {
        mapPlanner.drawRoad(x1, y1, x2, y2);
    }

    void drawSource(int x1, int y1) {
        mapPlanner.drawSpecialVertex(x1, y1, new Color(255, 255, 255));
    }

    void drawSink(int x1, int y1, Color color) { mapPlanner.drawSpecialVertex(x1, y1, color); }

    void removeRoad(int x1, int y1, int x2, int y2) {
        mapPlanner.removeRoad(x1, y1, x2, y2);
    }

    void removeSpecialVertex(int x1, int y1) {
        mapPlanner.removeSpecialVertex(x1, y1);
    }

    void updateVehicles(Collection<int[]> coordinates) {
        mapPlanner.updateVehicles(coordinates);
    }

    String getMode() {
        return (String) modesMenu.getSelectedItem();
    }

    void addModeChangeListener(ActionListener listener) {
        modesMenu.addActionListener(listener);
    }

    void addFirstTickListener(ActionListener listener) {
        start.addActionListener(listener);
        firstTickListener = listener;
    }

    void removeFirstTickListener() {
        mapPlanner.blockDrawing = true;
        drawingButtons.setVisible(false);
        gameButtons.remove(start);
        playButtons.add(start, BorderLayout.EAST);
        play.setVisible(true);
        pause.setVisible(true);
        stats.setVisible(true);
        start.setText("Next tick");
        start.removeActionListener(firstTickListener);
    }

    void addPlayListener(ActionListener listener) {
        play.addActionListener(e -> {
            start.setText("Next tick");
            listener.actionPerformed(e);
        });
    }

    void addPauseListener(ActionListener listener) {
        pause.addActionListener(listener);
    }

    void addNextTickListener(ActionListener listener) {
        start.addActionListener(e -> {
            start.setText("Next tick");
            listener.actionPerformed(e);
        });}

    void addBackToMenuListener(ActionListener listener) { quit.addActionListener(listener); }

    void addStatsListener(ActionListener listener) {
        stats.addActionListener(listener);
    }

    void addSaveListener(ActionListener listener) {
        settings.addActionListener(listener);
    }

    void addNewRoadListener(ActionListener listener) {
        mapPlanner.addNewRoadListener(listener);
    }

    void addRemoveListener(ActionListener listener) {
        mapPlanner.addRemoveListener(listener);
    }

    void addNewSourceListener(ActionListener listener) {
        mapPlanner.addNewSourceListener(listener);
    }

    void addNewSinkListener(ActionListener listener) {
        mapPlanner.addNewSinkListener(listener);
    }

}
