package View;

import java.awt.event.ActionListener;

public class View {
    Menu menu;
    MapEditor mapEditor;
    private Object active = null;
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
        }
        else {
            if (menu != null)
                menu.setVisible(false);
            if (mapEditor != null)
                mapEditor.setVisible(false);
        }
    }

    public void openEditor(int size, int dist) {
        if (menu != null)
            menu.setVisible(false);
        mapEditor = new MapEditor(size, dist);
        active = mapEditor;
        if (isVisible)
            mapEditor.setVisible(true);
    }

    /** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *  Getters and setters
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    public int getGridSize() { return menu.getGridSize(); }

    public int getDistanceInPx() { return menu.getDistanceInPx(); }

    public int[] getCoordinates() { return mapEditor.getCoordinates(); }

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


    /** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *  Draw
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    public void drawRoad(int x1, int y1, int x2, int y2) { mapEditor.drawRoad(x1, y1, x2, y2); }

    public void drawSource(int x1, int y1) { mapEditor.drawSource(x1, y1); }

    public void drawSink(int x1, int y1) { mapEditor.drawSink(x1, y1); }


    /** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *  Add listeners
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    public void addOpenEditorListener(ActionListener listener) {
        menu.addOpenEditorListener(listener);
    }

    public void addNextTickListener(ActionListener listener) { mapEditor.addNextTickListener(listener); }

    public void addQuitListener(ActionListener listener) {
        menu.addQuitListener(listener);
    }

    public void addNewRoadListener(ActionListener listener) { mapEditor.addNewRoadListener(listener); }

    public void addNewSourceListener(ActionListener listener) { mapEditor.addNewSourceListener(listener); }

    public void addNewSinkListener(ActionListener listener) { mapEditor.addNewSinkListener(listener); }

}

