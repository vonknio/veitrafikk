package Model;

public class Model {
    private Grid grid;

    /** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *  Configure grid
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    public void createGrid(int size) {
        grid = new Grid(size);
    }

    public void addRoad(int x1, int y1, int x2, int y2) { grid.addRoad(x1, y1, x2, y2); }

    public void addSource(int x1, int y1) { grid.addSource(x1, y1); }

    public void addSink(int x1, int y1) { grid.addSink(x1, y1); }

    public void removeVertexClassifiers(int x1, int y1) { grid.removeVertexClassifiers(x1, y1); }


    /** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *  Simulation
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    public void nextTick() { God.processGrid(grid); }


    /** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *  Test
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    public boolean areNeighbours(int x1, int y1, int x2, int y2) {
        return grid.getNeighbours(x1, y1).contains(grid.getVertex(x2, y2));
    }
}
