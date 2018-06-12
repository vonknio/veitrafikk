package Controller;
import Model.Model;
import View.View;

import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import static java.lang.Integer.max;
import static java.lang.Thread.interrupted;

public class Controller {
    private final static Logger logger = Logger.getLogger(Controller.class.getName());
    private final View view;
    private final Model model;
    private final MapLoader mapLoader;
    private final GridPlanner gridPlanner;
    private Thread playingThread;

    public Controller(View view, Model model) {
        this.view = view;
        this.model = model;
        this.mapLoader = new MapLoader(model, view, this);
        this.gridPlanner = new GridPlanner(model, view, this);

        view.addOpenEditorListener(this::switchToEditMode);
        view.addContinueListener(this::continueToEditMode);
        view.addLoadListener(mapLoader::load);
        view.addQuitListener(e -> System.exit(0));
    }

    private void goBackToMenu(ActionEvent e) {
        pause(null);
        view.goBackToMenu();
    }

    private void continueToEditMode(ActionEvent e) {
        pause(null);
        view.continueToEditor();
    }

    void switchToEditMode(ActionEvent e) {
        int size = view.getGridSize();
        if (size < 2) size = 2;
        else if (size > 100) size = 100;
        int dist = max(view.getDistanceInPx(), 5);
        boolean fixedDistance = view.getIsDistanceFixed();

       configureEditMode(size, dist, fixedDistance);
    }

    void configureEditMode(int size, int dist, boolean fixedDistance) {
        model.createGrid(size);

        pause(null);
        view.openEditor(size, dist, fixedDistance);
        view.addModeChangeListener(this::changeMode);
        view.addFirstTickListener(this::firstTick);
        view.addBackToMenuListener(this::goBackToMenu);
        view.addStatsListener(this::showStatistics);

        view.addNewRoadListener(gridPlanner::newRoad);
        view.addRemoveListener(gridPlanner::remove);
        view.addNewSourceListener(gridPlanner::newSource);
        view.addNewSinkListener(gridPlanner::newSink);

      //  view.addSaveListener(mapLoader::save);
        view.addSaveListener(this::showSettings);
    }

    private void showStatistics(ActionEvent event) {
        view.showStatistics(
                model.averageVelocity(), model.verticesVisited(), model.averagePathLength(),
                model.averageTimeEmpty(), model.averageVehicleCount(), model.averageTicksAlive(),
                model.maxVelocity(), model.notVisitedVertices(), model.maxPathLength(),
                model.maxTimeEmpty(), model.maxVehicleCount(), model.maxTicksAlive(),
                model.endedSuccessfully(), model.getTime(), model.averageWaitingTime(),
                model.maxWaitingTime(), model.totalVehicles(), model.finishedVehicles()
        );
    }

    private void showSettings(ActionEvent event) {
        pause(null);
        view.showSettings(gridPlanner.getSourceProbability(), gridPlanner.getSourceLimit());
        view.addSettingsApplyListener(this::applySettings);
        view.addSettingsQuitListener(e -> System.exit(0));
        view.addSettingsLoadListener(mapLoader::load);
        view.addSettingsSaveListener(mapLoader::save);
    }

    private void applySettings(ActionEvent e) {
        gridPlanner.setSourceProbability(view.getSourceProbability());
        gridPlanner.setSourceLimit(view.getSourceLimit());
        int animationTime = view.getAnimationTime();
        view.setAnimationTime(animationTime < 10 ? 10 : animationTime);
        model.applySettingsToSources(view.getSourceLimit(), view.getSourceProbability());
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
        if (playingThread != null)
            if (playingThread.isAlive())
                return;
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

    private void nextTick(ActionEvent e) {
        if (playingThread != null)
            if (playingThread.isAlive())
                return;
        playingThread = playOnce();
        playingThread.start();
    }

    private boolean nextTick() throws InterruptedException {
        boolean update = model.nextTick();
        Collection<int[]> vehicleCoordinates = model.getAllVehicleCoordinates();
        view.updateVehicles(vehicleCoordinates);
        view.animate();
        return update;
    }

    private void changeMode(ActionEvent e) {
        String mode = view.getMode();
        logger.config("SWITCHING TO " + mode);
        model.changeMode(mode);
    }

    private void gameEnd() {
        showStatistics(null);
        pause(null);
    }

    private Thread spawnPlayingThread() {
        return new Thread(() -> {
            boolean updated = true;
            while (!interrupted()) {
                if (!updated && model.hasVehiclesOnGrid()  // fail
                        || (!model.hasVehiclesOnGrid() && !model.hasUnspawnedVehicles())  // success
                        ) {
                    gameEnd();
                    break;
                }
                try {
                    updated = nextTick();
                } catch (Exception ex) {
                    return;
                }
            }
        });
    }

    private Thread playOnce() {
        return new Thread(() -> {
            boolean updated;
            try {
                updated = nextTick();
                if (!updated && model.hasVehiclesOnGrid()  // fail
                        || (!model.hasVehiclesOnGrid() && !model.hasUnspawnedVehicles())  // success
                        ) {
                    gameEnd();
                }
            } catch (Exception ex) {
                return;
            }
        });
    }

}
