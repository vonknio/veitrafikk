package Controller;
import Model.Model;
import View.View;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.logging.Logger;

import static java.lang.Integer.compareUnsigned;
import static java.lang.Integer.max;

public class MapLoader {
    private final static Logger logger = Logger.getLogger(MapLoader.class.getName());
    private final Model model;
    private final View view;
    private final Controller controller;

    MapLoader(Model model, View view, Controller controller) {
        this.model = model;
        this.view = view;
        this.controller = controller;
    }

    void save(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Choose/create file to save the grid");
        if (fileChooser.showSaveDialog(view.getMapPlanner()) != JFileChooser.APPROVE_OPTION)
            return;
        File file = fileChooser.getSelectedFile();

        int minLine = 23;
        int size = model.getGridSize();

        StringBuilder grid = new StringBuilder();
        StringBuilder top = new StringBuilder();
        for (int i = 0; i < max(minLine, size*2 + 3); ++i)
            top.append("@");
        StringBuilder wall = new StringBuilder("@@");
        for (int i = 0; i < minLine - (size*2) - 3; ++i)
            wall.append("@");

        grid.append(top.substring(0,2)).append("0000").append(top.substring(6))
                .append(System.lineSeparator())
                .append(top).append(System.lineSeparator());
        for (int j = 0; j < size; ++j){
            StringBuilder current = new StringBuilder("@@");
            StringBuilder next = new StringBuilder();
            if (j < size-1)
                next.append("@@");
            for (int i = 0; i < size; ++i){
                if (model.isVertex(i, j)){
                    if (model.isSource(i, j))
                        current.append('s');
                    else if (model.isSink(i, j))
                        current.append('t');
                    else
                        current.append('.');

                    if (i < size-1){
                        if (model.areNeighbours(i, j, i+1, j))
                            current.append('-');
                        else
                            current.append('#');
                    }

                    if (j < size-1){
                        if (model.areNeighbours(i, j, i, j+1))
                            next.append("|");
                        else
                            next.append("#");
                        if (i < size - 1)
                            next.append("#");
                    }
                }
                else {
                    if (i < size - 1)
                        current.append("#");
                    current.append("#");
                    if (j < size - 1) {
                        if (i < size - 1)
                            next.append("#");
                        next.append("#");
                    }
                }
            }
            current.append(wall);
            if (j < size - 1) next.append(wall);
            grid.append(current.toString()).append(System.lineSeparator());
            if (j < size - 1) grid.append(next.toString()).append(System.lineSeparator());
        }

        grid.append(top).append(System.lineSeparator())
                .append(top).append(System.lineSeparator());

        String[] car = {" ","   _______     "  ,"   "  ,
                " ","  / /||  \\\\    ","   ",
                " ","_/ /_||___\\\\___","___",
                "/","  _VEITRAFIKK_ "  ,"  ("  ,
                "|","_/ \\________/ \\","__|",
                " "," \\_/        \\_/","   "};

        String[] whl = {"    " ,
                "    " ,
                "____" ,
                "  _ " ,
                "_/ \\",
                " \\_/"};

        int carLen = 19;
        ArrayList<StringBuilder> carBuilder = new ArrayList<>();
        for (int i = 0; i < 6; ++i) {
            carBuilder.add(new StringBuilder());
            carBuilder.get(i).append(car[3 * i]);
        }

        ArrayList<StringBuilder> backBuilder = new ArrayList<>();
        ArrayList<StringBuilder> frontBuilder = new ArrayList<>();
        for (int i = 0; i < 6; ++i) {
            backBuilder.add(new StringBuilder());
            frontBuilder.add(new StringBuilder());
        }

        while (carLen + 8 <= max(minLine-4, size*2-1)) {
            for (int i = 0; i < 6; ++i) {
                backBuilder.get(i).append(whl[i]);
                frontBuilder.get(i).append(whl[i]);
            }
            carLen += 8;
        }

        for (int i = 0; i < 6; ++i)
            carBuilder.get(i).append(backBuilder.get(i))
                    .append(car[3 * i + 1])
                    .append(frontBuilder.get(i))
                    .append(car[3 * i + 2]);

        StringBuilder diff = new StringBuilder();
        for (int i = 0; i < (max(size*2+3, minLine) - carLen)/2; ++i)
            diff.append("@");

        for (int i = 0; i < 6; ++i)
            grid.append(diff).append(carBuilder.get(i)).append(diff).append(System.lineSeparator());

        grid.append(top).append(System.lineSeparator())
                .append(top).append(System.lineSeparator());

        try {
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(grid.toString());
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        logger.config("Saving into: " + file.getAbsolutePath()
                + System.lineSeparator() + grid.toString());

    }

    void load(ActionEvent e) {
        logger.config("LOAD");
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Choose save file");
        if (fileChooser.showOpenDialog(view.getMenu()) != JFileChooser.APPROVE_OPTION)
            return;
        File file = fileChooser.getSelectedFile();

        BufferedReader reader;
        int lineSize, size, distance = 0;
        boolean fixed = false;
        try {
            reader = new BufferedReader(new FileReader(file));
            String line = reader.readLine();
            lineSize = line.length();
            size = (lineSize - 3)/2;
            if (line.charAt(3) != '@'){
                distance = Integer.parseInt(line.substring(2,6));
                if (distance > 0)
                    fixed = true;
            }

            controller.configureEditMode(size, distance, fixed);

            line = reader.readLine(); //wall

            for (int i = 0; i < size*2-1; ++i){
                line = reader.readLine();
                if (i%2 == 0){
                    for (int j = 2; j <= size*2; ++j){
                        if (j%2 == 0){
                            if (line.charAt(j) == 't'){
                                loadSink((j-2)/2, i/2);
                            } else if (line.charAt(j) == 's'){
                                loadSource((j-2)/2, i/2);
                            }
                        }
                        else {
                            if (line.charAt(j) == '-'){
                                loadRoad((j-2)/2, i/2, (j-2)/2+1, i/2);
                            }
                        }
                    }
                }
                else {
                    for (int j = 2; j <= size*2; ++j) {
                        if (j%2 == 0){
                            if (line.charAt(j) == '|'){
                                loadRoad((j-2)/2, i/2, (j-2)/2, i/2+1);
                            }
                        }
                    }
                }
            }

        } catch (Exception exception) {
            exception.printStackTrace();
            return;
        }

    }

    private void loadSource(int x1, int y1) {
        logger.config("Loading source (" + x1 + ", " + y1 + ")");
        model.removeVertexClassifiers(x1, y1);
        model.addSource(x1, y1);
        view.removeSpecialVertex(x1, y1);
        view.drawSource(x1, y1);
    }

    private void loadSink(int x1, int y1) {
        logger.config("Loading sink (" + x1 + ", " + y1 + ")");
        model.removeVertexClassifiers(x1, y1);
        Color color = model.addSink(x1, y1);
        view.removeSpecialVertex(x1, y1);
        view.drawSink(x1, y1, color);
    }

    private void loadRoad(int x1, int y1, int x2, int y2) {
        if (x1 != x2 && y1 != y2) { return; }

        logger.config("Loading road (" + x1 + ", " + y1 + ") <-> (" + x2 + ", " + y2 + ")");
        model.addRoad(x1, y1, x2, y2);
        model.addRoad(x2, y2, x1, y1);
        view.drawRoad(x1, y1, x2, y2);
    }


}
