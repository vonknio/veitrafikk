package View;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.Collection;

public class View {
    Menu menu;
    MapEditor mapEditor;
    StatisticsWindow statisticsWindow;
    private Object active;
    private boolean isVisible = false;

    public View() {
        menu = new Menu();
        active = menu;
    }

    public void setVisible(boolean b) {
        isVisible = b;
        if (b && active != null) {
            if (active == menu)
                menu.setVisible(true);
            else if (active == mapEditor)
                mapEditor.setVisible(true);
            else if (active == statisticsWindow)
                statisticsWindow.setVisible(true);
        }
        else {
            if (menu != null)
                menu.setVisible(false);
            if (mapEditor != null)
                mapEditor.setVisible(false);
        }
    }

    public void openEditor(int size, int dist, boolean fixed) {
        menu.setVisible(false);
        mapEditor = new MapEditor(size, dist, fixed);
        active = mapEditor;
        if (isVisible)
            mapEditor.setVisible(true);
    }

    public void goBackToMenu() {
        if (mapEditor != null)
            mapEditor.setVisible(false);
        active = menu;
        menu.showContinue();
        if (isVisible)
            menu.setVisible(true);
    }

    public void continueToEditor() {
        if (menu != null)
            menu.setVisible(false);
        active = mapEditor;
        if (isVisible)
            mapEditor.setVisible(true);
    }

    public void showStatistics(double path, double ticks, double time, double vehicles, double velocity, int vertices) {
        statisticsWindow = new StatisticsWindow();
        statisticsWindow.setPath(path);
        statisticsWindow.setTicks(ticks);
        statisticsWindow.setTime(time);
        statisticsWindow.setVehicles(vehicles);
        statisticsWindow.setVelocity(velocity);
        statisticsWindow.setVertices(vertices);
        statisticsWindow.update();
        statisticsWindow.setVisible(true);
    }

    public void nextTick(){
        //TODO
    }

    /** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *  Getters and setters
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    public int getGridSize() { return menu.getGridSize(); }

    public int getDistanceInPx() { return menu.getDistanceInPx(); }

    public boolean getIsDistanceFixed() { return menu.getIsDistanceFixed(); }

    public int[] getCoordinates() { return mapEditor.getCoordinates(); }

    public String getMode() { return mapEditor.getMode(); }

    public int getXOriginCoordinate() {
        if (mapEditor != null)
            return mapEditor.frame.getX();
        throw new IllegalStateException();
    }

    public int getYOriginCoordinate() {
        if (mapEditor != null)
            return mapEditor.frame.getY();
        throw new IllegalStateException();
    }

    public void updateVehicles(Collection<int[]> coordinates){ mapEditor.updateVehicles(coordinates); }

    /** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *  Draw
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    public void drawRoad(int x1, int y1, int x2, int y2) { mapEditor.drawRoad(x1, y1, x2, y2); }

    public void drawSource(int x1, int y1) { mapEditor.drawSource(x1, y1); }

    public void drawSink(int x1, int y1, Color color) { mapEditor.drawSink(x1, y1, color); }

    public void removeRoad(int x1, int y1, int x2, int y2) { mapEditor.removeRoad(x1, y1, x2, y2); }

    public void removeSpecialVertex(int x1, int y1) { mapEditor.removeSpecialVertex(x1, y1); }

    /** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *  Add MapEditor listeners
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    public void addStatsListener(ActionListener listener) {
        mapEditor.addStatsListener(listener);
    }

    public void addModeChangeListener(ActionListener listener) { mapEditor.addModeChangeListener(listener); }

    public void addFirstTickListener(ActionListener listener) { mapEditor.addFirstTickListener(listener); }

    public void addPlayListener(ActionListener listener) { mapEditor.addPlayListener(listener); }

    public void addPauseListener(ActionListener listener) { mapEditor.addPauseListener(listener); }

    public void addNextTickListener(ActionListener listener) { mapEditor.addNextTickListener(listener); }

    public void addBackToMenuListener(ActionListener listener) { mapEditor.addBackToMenuListener(listener); }

    public void addSaveListener(ActionListener listener) {
        mapEditor.addSaveListener(listener);
    }

    public void addNewRoadListener(ActionListener listener) { mapEditor.addNewRoadListener(listener); }

    public void addNewSourceListener(ActionListener listener) { mapEditor.addNewSourceListener(listener); }

    public void addNewSinkListener(ActionListener listener) { mapEditor.addNewSinkListener(listener); }

    public void addRemoveListener(ActionListener listener) { mapEditor.addRemoveListener(listener); }

    /** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *  Add Menu listeners
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    public void addQuitListener(ActionListener listener) {
        menu.addQuitListener(listener);
    }

    public void addOpenEditorListener(ActionListener listener) { menu.addOpenEditorListener(listener); }

    public void addLoadListener(ActionListener listener) { menu.addLoadListener(listener); }

    public void addContinueListener(ActionListener listener) { menu.addContinueListener(listener); }

    /** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *  Helper methods for communicating with the user
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    public void removeFirstTickListener() { mapEditor.removeFirstTickListener(); }

    public JComponent getMapPlanner() { return mapEditor.mapPlanner; }

    public JFrame getMenu() { return menu; }

    public void showDisconnectedGraphError() {
        JOptionPane.showMessageDialog(null,
                "All roads have to be connected and contain at least one sink and one source!",
                "Warning", JOptionPane.PLAIN_MESSAGE);
    }

}

