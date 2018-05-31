package Controller;
import Model.Model;
import View.View;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Logger;

import static java.lang.Integer.compareUnsigned;
import static java.lang.Integer.max;

public class Controller {
    private final static Logger logger = Logger.getLogger(Controller.class.getName());
    private View view;
    private Model model;

    public Controller(View view, Model model) {
        this.view = view;
        this.model = model;
        view.addOpenEditorListener(this::switchToEditMode);
        view.addQuitListener(e -> System.exit(0));
    }

    private void newRoad(ActionEvent e) {
        int[] coordinates = view.getCoordinates();
        if (coordinates.length != 4) {
            throw new IllegalArgumentException();
        }

        int x1 = coordinates[0], y1 = coordinates[1],
                x2 = coordinates[2], y2 = coordinates[3];

        if (x1 != x2 && y1 != y2) {
            return;
        }

        logger.config("Adding road (" + x1 + ", " + y1 + ") <-> (" + x2 + ", " + y2 + ")");
        model.addRoad(x1, y1, x2, y2);
        model.addRoad(x2, y2, x1, y1);
        view.drawRoad(x1, y1, x2, y2);
    }

    private void remove(ActionEvent e) {
        int[] coordinates = view.getCoordinates();
        if (coordinates.length != 4) {
            throw new IllegalStateException();
        }
        int x1 = coordinates[0], y1 = coordinates[1],
                x2 = coordinates[2], y2 = coordinates[3];

        if (model.isSink(x1, y1) || model.isSource(x1, y1)){
            logger.config("Removing vertex classifier (" + x1 + ", " + y1 + ")");
            model.removeVertexClassifiers(x1, y1);
            view.removeSpecialVertex(x1, y1);
            return;
        }

        if (!model.hasRoad(x1, y1, x2, y2))
            return;

        coordinates = model.getEnclosingRoad(x1, y1, x2, y2);

        x1 = coordinates[0];
        y1 = coordinates[1];
        x2 = coordinates[2];
        y2 = coordinates[3];

        logger.config("Removing road (" + x1 + ", " + y1 + ") <-> (" + x2 + ", " + y2 + ")");

        if (x1 == x2)
            for (int i = 0; i < y2-y1; ++i)
                removeUnitRoad(x1, y1+i, x1, y1+i+1);
        else
            for (int i = 0; i < x2-x1; ++i)
                removeUnitRoad(x1+i, y1, x1+i+1, y1);

    }

    private void removeUnitRoad(int x1, int y1, int x2, int y2){

        if (model.isLastRoad(x1, y1))
            view.removeSpecialVertex(x1, y1);
        if (model.isLastRoad(x2, y2))
            view.removeSpecialVertex(x2, y2);

        model.removeRoad(x1, y1, x2, y2);
        view.removeRoad(x1, y1, x2, y2);
    }

    private void newSource(ActionEvent e) {
        int[] coordinates = view.getCoordinates();
        if (coordinates.length != 2) {
            throw new IllegalStateException();
        }
        int x1 = coordinates[0], y1 = coordinates[1];

        logger.config("Adding source (" + x1 + ", " + y1 + ")");
        model.removeVertexClassifiers(x1, y1);
        model.addSource(x1, y1);
        view.removeSpecialVertex(x1, y1);
        view.drawSource(x1, y1);
    }

    private void newSink(ActionEvent e) {
        int[] coordinates = view.getCoordinates();
        if (coordinates.length != 2) {
            throw new IllegalStateException();
        }
        int x1 = coordinates[0], y1 = coordinates[1];

        logger.config("Adding sink (" + x1 + ", " + y1 + ")");
        model.removeVertexClassifiers(x1, y1);
        model.addSink(x1, y1);
        view.removeSpecialVertex(x1, y1);
        view.drawSink(x1, y1);
    }

    public void switchToEditMode(ActionEvent e) {
        int size = view.getGridSize();
        if (size < 1) size = 10;
        int dist = max(view.getDistanceInPx(), 15);

        model.createGrid(size);
        view.openEditor(size, dist);
        view.addModeChangeListener(this::changeMode);
        view.addNewRoadListener(this::newRoad);
        view.addRemoveListener(this::remove);
        view.addNewSourceListener(this::newSource);
        view.addNewSinkListener(this::newSink);
        view.addFirstTickListener(this::firstTick);
        view.addSaveListener(this::save);
    }

    private void firstTick(ActionEvent e) {
        if (!model.isReadyToStart())
            view.showDisconnectedGraphError();
        else {
            view.removeFirstTickListener();
            view.addNextTickListener(this::nextTick);
            view.addPlayListener(this::play);
            view.addPauseListener(this::pause);
        }
    }

    private void play(ActionEvent e) {
        logger.config("PLAY");
    }

    private void pause(ActionEvent e) {
        logger.config("PAUSE");
    }

    private void nextTick(ActionEvent e) {
        model.nextTick();
        Collection<int[]> vehicleCoordinates = model.getAllVehicleCoordinates();
        view.updateVehicles(vehicleCoordinates);
        view.nextTick();
    }

    private void changeMode(ActionEvent e) {
        String mode = view.getMode();
        System.out.println(mode);
        model.changeMode(mode);
    }

    private void save(ActionEvent e) {
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

        grid.append(top).append(System.lineSeparator())
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

}
