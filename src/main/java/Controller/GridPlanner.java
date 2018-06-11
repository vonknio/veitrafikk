package Controller;

import Model.Model;
import View.View;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.logging.Logger;

class GridPlanner {
    private final static Logger logger = Logger.getLogger(GridPlanner.class.getName());
    private final Model model;
    private final View view;
    private final Controller controller;

    private float sourceProbability = 1;
    private int sourceLimit = 10;

    GridPlanner(Model model, View view, Controller controller) {
        this.model = model;
        this.view = view;
        this.controller = controller;
    }

    void newRoad(ActionEvent e) {
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

    void newSource(ActionEvent e) {
        int[] coordinates = view.getCoordinates();
        if (coordinates.length != 2) {
            throw new IllegalStateException();
        }
        int x1 = coordinates[0], y1 = coordinates[1];

        logger.config("Adding source (" + x1 + ", " + y1 + ") with probability " + sourceProbability + " and limit " + sourceLimit);
        model.removeVertexClassifiers(x1, y1);
        model.addSource(x1, y1, sourceLimit, sourceProbability);
        view.removeSpecialVertex(x1, y1);
        view.drawSource(x1, y1);
    }

    void newSink(ActionEvent e) {
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

    /**
     *  Remove sink, source, or a road. What is to be removed depends where the user has clicked.
     */
    void remove(ActionEvent e) {
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

    float getSourceProbability() { return sourceProbability; }

    void setSourceProbability(float probability) {
        if (probability <= 0 || probability > 1)
            probability = 1;
        sourceProbability = probability;
    }

    int getSourceLimit() { return sourceLimit; }

    void setSourceLimit(int limit) {
        if (limit == 0)
            sourceLimit = -1;
        sourceLimit = limit;
    }
}
