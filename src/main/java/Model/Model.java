package Model;

import java.util.Collection;
import java.util.LinkedList;

import static java.lang.Integer.max;
import static java.lang.Integer.min;

public class Model {
    private Grid grid;
    private GridState gridState;
    private Statistics statistics;

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *  Configure grid
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /**
     * Create an empty grid of given size and accompanying GridState.
     *
     * @param size Size of grid's side in vertices.
     */
    public void createGrid(int size) {
        grid = new Grid(size);
        gridState = new GridState(grid);
        statistics = new Statistics(grid, gridState);
    }

    /**
     * Add a road to the grid.
     *
     * @param x1 X coordinate of first vertex.
     * @param y1 Y coordinate of first vertex.
     * @param x2 X coordinate of second vertex.
     * @param y2 Y coordinate of second vertex.
     */
    public void addRoad(int x1, int y1, int x2, int y2) {
        grid.addRoad(x1, y1, x2, y2);
    }

    /**
     * Remove given road from the grid.
     *
     * @param x1 X coordinate of first vertex.
     * @param y1 Y coordinate of first vertex.
     * @param x2 X coordinate of second vertex.
     * @param y2 Y coordinate of second vertex.
     */
    public void removeRoad(int x1, int y1, int x2, int y2) {
        grid.removeRoad(x1, y1, x2, y2);
    }

    /**
     * Add a source vertex to the grid.
     *
     * @param x1          X coordinate.
     * @param y1          Y coordinate.
     * @param limit       limit of vehicles spawned from source during whole simulation.
     * @param probability probability of spawning a new vehicle in each tick.
     */
    public void addSource(int x1, int y1, long limit, float probability) {
        gridState.addSource(x1, y1, limit, probability);
    }

    /**
     * Add a sink vertex to the grid.
     *
     * @param x1 X coordinate.
     * @param y1 Y coordinate.
     */
    public void addSink(int x1, int y1) {
        gridState.addSink(x1, y1);
    }

    public void addSource(int x1, int y1) {
        addSource(x1, y1, 10, 1);
    }

    /**
     * Set vertex type to the default type.
     *
     * @param x1 X coordinate.
     * @param y1 X coordinate.
     */
    public void removeVertexClassifiers(int x1, int y1) {
        grid.removeVertexClassifiers(x1, y1);
    }

    public void nextTick() {
        God.processTimetick(this);
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *  Simulation
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    public void changeMode(String string) {
        God.setMode(God.Mode.valueOf(string));
    }

    /**
     * @return Size of the grid's side in vertices.
     */
    public int getGridSize() {
        return grid.getSize();
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *  Getters
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /**
     * Get coordinates of the longest road that expands given road without crossing any crossroads.
     *
     * @param x1 X coordinate of the first vertex.
     * @param y1 Y coordinate of the first vertex.
     * @param x2 X coordinate of the second vertex.
     * @param y2 X coordinate of the second vertex.
     * @return (x1, y2, x2, y2) - coordinates of enclosing road
     */
    @SuppressWarnings("Duplicates")
    public int[] getEnclosingRoad(int x1, int y1, int x2, int y2) {
        if (!hasRoad(x1, y1, x2, y2))
            throw new IllegalArgumentException();

        int[] res;
        int a1, a2, p;
        if (x1 == x2) {  // vertical
            p = min(y1, y2);
            while (p > 0 && hasRoad(x1, p, x1, p - 1)) {
                if (grid.getNeighbours(x1, p).size() > 2)
                    break;
                p--;
            }
            a1 = p;
            p = max(y1, y2);
            while (p < getGridSize() - 1 && hasRoad(x1, p, x1, p + 1)) {
                if (grid.getNeighbours(x1, p).size() > 2)
                    break;
                p++;
            }
            a2 = p;
            res = new int[]{x1, a1, x1, a2};

        } else {  // horizontal
            p = min(x1, x2);
            while (p > 0 && hasRoad(p, y1, p - 1, y1)) {
                if (grid.getNeighbours(p, y1).size() > 2)
                    break;
                p--;
            }
            a1 = p;
            p = max(x1, x2);
            while (p < getGridSize() - 1 && hasRoad(p, y1, p + 1, y1)) {
                if (grid.getNeighbours(p, y1).size() > 2)
                    break;
                p++;
            }
            a2 = p;
            res = new int[]{a1, y1, a2, y1};
        }

        return res;
    }

    /**
     * @return Grid on which the model is operating.
     */
    Grid getGrid() {
        return grid;
    }

    public void setGrid(Grid grid) {
        this.grid = grid;
    }

    /**
     * @return GridState assotiated with the model.
     */
    GridState getGridState() {
        return gridState;
    }

    /**
     * @param x1 X coordinate of the vertex.
     * @param y1 Y coordinate of the vertex.
     * @return Vertex at the given point of the grid or null if it does not exist.
     */
    Vertex getVertex(int x1, int y1) {
        return grid.getVertex(x1, y1);
    }

    /**
     * @return Collection of all vehicles currently in the grid.
     */
    Collection<Vehicle> getVehicles() {
        return gridState.getVehicles();
    }

    /**
     * @return Size of the underlying grid.
     */
    int getSize() {
        return grid.getSize();
    }

    /**
     * @return Linked list of coordinates of previous vertex, current vertex and id for each Vehicle
     */
    public Collection<int[]> getAllVehicleCoordinates() {
        LinkedList<int[]> result = new LinkedList<>();
        Collection<Vehicle> vehicles = getVehicles();
        for (Vehicle v : vehicles) {
            int[] coords = new int[7];
            coords[0] = v.getPrev().getXCoordinate();
            coords[1] = v.getPrev().getYCoordinate();
            coords[2] = v.getCur().getXCoordinate();
            coords[3] = v.getCur().getYCoordinate();
            coords[4] = v.getNext().getXCoordinate();
            coords[5] = v.getNext().getYCoordinate();
            coords[6] = v.getId();
            result.add(coords);
        }
        return result;
    }


    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *  Test
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /**
     * @param x1 X coordinate of the first vertex.
     * @param y1 Y coordinate of the first vertex.
     * @param x2 X coordinate of the second vertex.
     * @param y2 X coordinate of the second vertex.
     * @return Whether given vertices are connected by an edge in the grid.
     */
    public boolean areNeighbours(int x1, int y1, int x2, int y2) {
        return TestUtils.areNeighbours(x1, y1, x2, y2, grid);
    }

    /**
     * @param x1 X coordinate of the first vertex.
     * @param y1 Y coordinate of the first vertex.
     * @param x2 X coordinate of the second vertex.
     * @param y2 X coordinate of the second vertex.
     * @return Whether there is a road connecting given vertices in the grid.
     */
    public boolean hasRoad(int x1, int y1, int x2, int y2) {
        return TestUtils.hasRoad(x1, y1, x2, y2, grid);
    }

    /**
     * @param x1 X coordinate of the vertex
     * @param y1 Y coordinate of the vertex
     * @return Whether there exists a vertex at given position.
     */
    public boolean isVertex(int x1, int y1) {
        return getVertex(x1, y1) != null;
    }

    /**
     * @param x1 X coordinate of the vertex
     * @param y1 Y coordinate of the vertex
     * @return Whether given vertex has no more than one road - a dead end.
     */
    public boolean isLastRoad(int x1, int y1) {
        return grid.getNeighbours(x1, y1) == null || grid.getNeighbours(x1, y1).size() == 1;
    }

    /**
     * @param x1 X coordinate.
     * @param y1 Y coordinate.
     * @return Whether given vertex is a source.
     */
    public boolean isSource(int x1, int y1) {
        return TestUtils.isSource(x1, y1, grid);
    }

    /**
     * @param x1 X coordinate.
     * @param y1 Y coordinate.
     * @return Whether given vertex is a sink.
     */
    public boolean isSink(int x1, int y1) {
        return TestUtils.isSink(x1, y1, grid);
    }

    /**
     * @return Whether simulation can be started.
     */
    public boolean isReadyToStart() {
        return grid.isConnected() && !gridState.getSinks().isEmpty() && !gridState.getSources().isEmpty();
    }


    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *  Statistics
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    public LinkedList<Vehicle.VehicleStatistics> vehiclesStatistics() {
        return statistics.vehiclesStatistics();
    }

    public LinkedList<Vertex.VertexStatistics> verticesStatistics() {
        return statistics.verticesStatistics();
    }

    public double averageVelocity() {
        return statistics.averageVelocity();
    }

    public int verticesVisited() {
        return statistics.verticesVisited();
    }

    public double averagePathLength() {
        return statistics.averagePathLength();
    }

    public double averageTimeEmpty() {
        return statistics.averageTimeEmpty();
    }

    public double averageVehicleCount() {
        return statistics.averageVehicleCount();
    }

    public double averageTicksAlive() {
        return statistics.averageTicksAlive();
    }

    Statistics getStatistics() {
        return statistics;
    }
}
