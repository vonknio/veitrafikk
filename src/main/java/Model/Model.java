package Model;

import static java.lang.Integer.max;
import static java.lang.Integer.min;

public class Model {
    private Grid grid;

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *  Configure grid
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    public void createGrid(int size) {
        grid = new Grid(size);
    }

    public void addRoad(int x1, int y1, int x2, int y2) { grid.addRoad(x1, y1, x2, y2); }

    public void removeRoad(int x1, int y1, int x2, int y2) {
        grid.removeRoad(x1, y1, x2, y2);
    }

    public void addSource(int x1, int y1) { grid.addSource(x1, y1); }

    public void addSink(int x1, int y1) { grid.addSink(x1, y1); }

    public void removeVertexClassifiers(int x1, int y1) { grid.removeVertexClassifiers(x1, y1); }


    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *  Simulation
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    public void nextTick() { God.processGrid(grid); }


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
        return grid.getNeighbours(x1, y1).contains(grid.getVertex(x2, y2));
    }

    /**
     * @param x1 X coordinate of the first vertex.
     * @param y1 Y coordinate of the first vertex.
     * @param x2 X coordinate of the second vertex.
     * @param y2 X coordinate of the second vertex.
     * @return Whether there is a road connecting given vertices in the grid.
     */
    public boolean hasRoad(int x1, int y1, int x2, int y2) {
        if (x1 == x2 && y1 == y2) return false;
        if (x1 == x2) {
            for (int i = min(y1, y2); i < max(y1, y2); i++) {
                if (!areNeighbours(x1, i, x1, i+1))
                    return false;
            }
        } else if (y1 == y2) {
            for (int i = min(x1, x2); i < max(x1, x2); i++) {
                if (!areNeighbours(i, y1, i+1, y2))
                    return false;
            }
        }
        return true;
    }

    /**
     * @param x1 X coordinate.
     * @param y1 Y coordinate.
     * @return Whether given vertex is a source.
     */
    public boolean isSource(int x1, int y1) {
        return grid.hasVertex(x1, y1) &&
                grid.getVertex(x1, y1).getVertexType() == Vertex.VertexType.SOURCE;
    }

    /**
     * @param x1 X coordinate.
     * @param y1 Y coordinate.
     * @return Whether given vertex is a sink.
     */
    public boolean isSink(int x1, int y1) {
        return grid.hasVertex(x1, y1) &&
                grid.getVertex(x1, y1).getVertexType() == Vertex.VertexType.SINK;
    }

    /**
     * @return Size of the grid's side in vertices.
     */
    public int getGridSize() {
        return grid.getSize();
    }
}
