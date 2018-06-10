package Controller;
import Model.Model;
import View.View;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import static java.lang.Integer.max;
import static java.lang.Thread.interrupted;

public class Controller {
    private final static Logger logger = Logger.getLogger(Controller.class.getName());
    private View view;
    private Model model;
    private Thread playingThread;
    private MapLoader mapLoader;

    public Controller(View view, Model model) {
        this.view = view;
        this.model = model;
        this.mapLoader = new MapLoader(model, view, this);

        view.addOpenEditorListener(this::switchToEditMode);
        view.addLoadListener(mapLoader::load);
        view.addQuitListener(e -> System.exit(0));
        view.addContinueListener(this::continueToEditMode);
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
        Color color = model.addSink(x1, y1);
        view.removeSpecialVertex(x1, y1);
        view.drawSink(x1, y1, color);
    }

    private void goBackToMenu(ActionEvent e) { view.goBackToMenu(); }

    public void continueToEditMode(ActionEvent e) {
        view.continueToEditor();
    }

    public void switchToEditMode(ActionEvent e) {
        int size = view.getGridSize();
        if (size < 1) size = 10;
        int dist = max(view.getDistanceInPx(), 5);
        boolean fixedDistance = view.getIsDistanceFixed();

       configureEditMode(size, dist, fixedDistance);
    }

    void configureEditMode(int size, int dist, boolean fixedDistance) {
        model.createGrid(size);

        view.openEditor(size, dist, fixedDistance);
        view.addModeChangeListener(this::changeMode);
        view.addNewRoadListener(this::newRoad);
        view.addRemoveListener(this::remove);
        view.addNewSourceListener(this::newSource);
        view.addNewSinkListener(this::newSink);
        view.addFirstTickListener(this::firstTick);
        view.addBackToMenuListener(this::goBackToMenu);
        view.addSaveListener(mapLoader::save);
        view.addStatsListener(this::showStatistics);
    }

    private void showStatistics(ActionEvent event) {
        view.showStatistics(model.averagePathLength(), model.averageTicksAlive(), model.averageTimeEmpty(),
                model.averageVehicleCount(), model.averageVelocity(), model.verticesVisited());
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
        playingThread = spawnPlayingThread();
        playingThread.start();
    }

    private void pause(ActionEvent e) {
        logger.config("PAUSE");
        if (playingThread != null) {
            playingThread.interrupt();
            try {
               if (playingThread.isAlive())
                    playingThread.join();
            } catch (Exception ex) {}
        }
    }

    private boolean nextTick(ActionEvent e) {
        boolean update = model.nextTick();
        Collection<int[]> vehicleCoordinates = model.getAllVehicleCoordinates();
        view.updateVehicles(vehicleCoordinates);
        view.nextTick();
        return update;
    }

    private void changeMode(ActionEvent e) {
        String mode = view.getMode();
        logger.config("SWITCHING TO " + mode);
        model.changeMode(mode);
    }

    private Thread spawnPlayingThread() {
        return new Thread(() -> {
            while (!interrupted() && nextTick(null)) {
                try {
                    TimeUnit.SECONDS.sleep(4);
                } catch (Exception ex) {
                    return;
                }
            }
        });
    }
}
