package Controller;
import Model.Model;
import View.View;
import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.LinkedList;
import java.util.logging.Logger;
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
    }

    private void firstTick(ActionEvent e) {
        if (!model.isReadyToStart())
            view.showDisconnectedGraphError();
        else {
            view.removeFirstTickListener();
            view.addNextTickListener(this::nextTick);
        }
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

}
