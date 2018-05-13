package Model;

import static java.lang.Integer.max;
import static java.lang.Integer.min;

public class Model {
    private Grid grid;

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *  Configure grid
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /**
     * Create an empty grid of given size.
     * @param size Size of grid's side in vertices.
     */
    public void createGrid(int size) {
        grid = new Grid(size);
    }

    /**
     * Add a road to the grid.
     * @param x1 X coordinate of first vertex.
     * @param y1 Y coordinate of first vertex.
     * @param x2 X coordinate of second vertex.
     * @param y2 Y coordinate of second vertex.
     */
    public void addRoad(int x1, int y1, int x2, int y2) { grid.addRoad(x1, y1, x2, y2); }

    /**
     * Remove given road from the grid.
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
     * @param x1 X coordinate.
     * @param y1 Y coordinate.
     */
    public void addSource(int x1, int y1, long limit, float probability) {
        grid.addSource(x1, y1, limit, probability);
    }

    public void addSource(int x1, int y1) {
        grid.addSource(x1, y1, 10, 1);
    }


    /**
     * Add a sink vertex to the grid.
     * @param x1 X coordinate.
     * @param y1 Y coordinate.
     */
    public void addSink(int x1, int y1) { grid.addSink(x1, y1); }

    /**
     * Set vertex type to the default type.
     * @param x1 X coordinate.
     * @param y1 X coordinate.
     */
    public void removeVertexClassifiers(int x1, int y1) { grid.removeVertexClassifiers(x1, y1); }

    public boolean isLastRoad(int x1, int y1) {
        return grid.getNeighbours(x1, y1) == null || grid.getNeighbours(x1, y1).size() == 1;
    }

    /**
     * Get coordinates of the longest road that expands given road without crossing any crossroads.
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
            while (p > 0 && hasRoad(x1, p, x1, p-1) ) {
                if (grid.getNeighbours(x1, p).size() > 2)
                    break;
                p--;
            }
            a1 = p;
            p = max(y1, y2);
            while (p < getGridSize() - 1 && hasRoad(x1, p, x1, p+1)) {
                if (grid.getNeighbours(x1, p).size() > 2)
                    break;
                p++;
            }
            a2 = p;
            res = new int[]{x1, a1, x1, a2};

        } else  {  // horizontal
            p = min(x1, x2);
            while (p > 0 && hasRoad(p, y1, p-1, y1)) {
                if (grid.getNeighbours(p, y1).size() > 2)
                    break;
                p--;
            }
            a1 = p;
            p = max(x1, x2);
            while (p < getGridSize() - 1 && hasRoad(p, y1, p+1, y1)) {
                if (grid.getNeighbours(p, y1).size() > 2)
                    break;
                p++;
            }
            a2 = p;
            res = new int[]{a1, y1, a2, y1};
        }
        
        return res;
    }

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
        return grid.getNeighbours(x1, y1).contains(grid.getVertexIn(x2, y2));
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
            return true;
        } else if (y1 == y2) {
            for (int i = min(x1, x2); i < max(x1, x2); i++) {
                if (!areNeighbours(i, y1, i+1, y2))
                    return false;
            }
            return true;
        }
        return false;
    }

    /**
     * @param x1 X coordinate.
     * @param y1 Y coordinate.
     * @return Whether given vertex is a source.
     */
    public boolean isSource(int x1, int y1) {
        return grid.hasVertex(x1, y1) &&
                grid.getVertexOut(x1, y1) instanceof Source;
    }

    /**
     * @param x1 X coordinate.
     * @param y1 Y coordinate.
     * @return Whether given vertex is a sink.
     */
    public boolean isSink(int x1, int y1) {
        return grid.hasVertex(x1, y1) &&
                grid.getVertexIn(x1, y1) instanceof Sink;
    }

    /**
     * @return Size of the grid's side in vertices.
     */
    public int getGridSize() {
        return grid.getSize();
    }
}
