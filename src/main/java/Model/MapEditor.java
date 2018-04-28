package Model;

import javax.swing.*;
import java.awt.*;

public class MapEditor {

    private int size;
    private int dist;

    JFrame frame;
    Container container;
    MapPlanner mapPlanner;
    Grid grid;

    MapEditor (int size, int dist){

        this.size = size;
        this.dist = dist;

        grid = new Grid(size);

        mapPlanner = new MapPlanner(grid, size, dist);

        setupFrame();
        setupContainer();
        setupMapPlanner();

        frame.setVisible(true);

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

        JPanel drawingButtons = new JPanel(new BorderLayout());
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

        JButton start = new JButton("Start");
        start.addActionListener(e -> {

            mapPlanner.blockDrawing = true;
            drawingButtons.setVisible(false);
            start.setText("Next tick");

            God.processGrid(grid);

        });

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

}
