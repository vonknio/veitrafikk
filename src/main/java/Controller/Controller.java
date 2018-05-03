package Controller;
import Model.Model;
import View.View;
import java.awt.event.ActionEvent;
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

    private void newSource(ActionEvent e) {
        int[] coordinates = view.getCoordinates();
        if (coordinates.length != 2) {
            throw new IllegalStateException();
        }
        int x1 = coordinates[0], y1 = coordinates[1];

        logger.config("Adding source (" + x1 + ", " + y1 + ")");
        model.removeVertexClassifiers(x1, y1);
        model.addSource(x1, y1);
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
        view.drawSink(x1, y1);
    }

    public void switchToEditMode(ActionEvent e) {
        int size = view.getGridSize();
        if (size < 1) size = 10;
        int dist = max(view.getDistanceInPx(), 15);

        model.createGrid(size);
        view.openEditor(size, dist);
        view.addNewRoadListener(this::newRoad);
        view.addNewSourceListener(this::newSource);
        view.addNewSinkListener(this::newSink);
        view.addNextTickListener(this::nextTick);
    }

    private void nextTick(ActionEvent e) {
        model.nextTick();
    }

}
