package Model;

import java.util.*;

import static java.lang.Integer.max;
import static java.lang.Integer.min;
import static java.lang.Math.abs;

/**
 * Class representing square grid containing vertices and straight roads between them.
 */
class Grid {
    private int size;
    private Vertex[][] vertices;
    private boolean[][] roads;

    /**
     * @param size       Size of grid's side.
     */
    Grid(int size) {
        this.size = size;
        vertices = new Vertex[size * size][2];
        roads = new boolean[size * size][4];
    }

    /**
     * Add new vertex of given coordinates and type.
     *
     * @param x X coordinate.
     * @param y Y coordinate.
     */
    private void addVertex(int x, int y) {
        vertices[x * size + y][0] = new Vertex(x, y, Vertex.VertexType.IN);
        vertices[x * size + y][1] = new Vertex(x, y, Vertex.VertexType.OUT);
    }

    private void removeVertex(int x, int y) {
        vertices[x * size + y][0] = null;
        vertices[x * size + y][1] = null;
    }

    private int getNeighbourSize(int x, int y) {
        int result = 0;
        for (int i = 0; i < 4; i++) result += !roads[x * size + y][i] ? 0 : 1;
        return result;
    }

    /**
     * @return Size of the grid's side.
     */
    int getSize() { return size; }

    /**
     * Add long road. Vertices must lie on the same straight line.
     *
     * @param x1 X coordinate of first vertex.
     * @param y1 Y coordinate of first vertex.
     * @param x2 X coordinate of last vertex.
     * @param y2 Y coordinate of last vertex.
     */
    void addRoad(int x1, int y1, int x2, int y2) {
        if (x1 >= size || y1 >= size || x2 >= size || y2 >= size
                || x1 < 0 || y1 < 0 || x2 < 0 || y2 < 0)
            throw new IllegalArgumentException();

        if (x1 == x2) {
            for (int i = min(y1, y2); i < max(y1, y2); ++i)
                addUnitRoad(x1, i, x1, i + 1);
        } else if (y1 == y2) {
            for (int i = min(x1, x2); i < max(x1, x2); ++i)
                addUnitRoad(i, y1, i + 1, y1);
        } else {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Add unit road. Distance between given vertices must be one unit.
     *
     * @param x1 X coordinate of first vertex.
     * @param y1 Y coordinate of first vertex.
     * @param x2 X coordinate of second vertex.
     * @param y2 Y coordinate of second vertex.
     */
    private void addUnitRoad(int x1, int y1, int x2, int y2) {
        if (x1 >= size || y1 >= size || x2 >= size || y2 >= size
                || x1 < 0 || y1 < 0 || x2 < 0 || y2 < 0)
            throw new IllegalArgumentException();

        if (!(x1 == x2 && (abs(y1 - y2) == 1)) && !(y1 == y2 && (abs(x1 - x2) == 1)))
            throw new IllegalArgumentException();

        if (vertices[x1 * size + y1][0] == null) addVertex(x1, y1);
        if (vertices[x2 * size + y2][0] == null) addVertex(x2, y2);

        if (x1 == x2 && y1 - y2 == 1) {
            roads[x1 * size + y1][0] = true;
            roads[x2 * size + y2][2] = true;
        }

        if (x1 - x2 == -1 && y1 == y2) {
            roads[x1 * size + y1][1] = true;
            roads[x2 * size + y2][3] = true;
        }

        if (x1 == x2 && y1 - y2 == -1) {
            roads[x1 * size + y1][2] = true;
            roads[x2 * size + y2][0] = true;
        }

        if (x1 - x2 == 1 && y1 == y2) {
            roads[x1 * size + y1][3] = true;
            roads[x2 * size + y2][1] = true;
        }
    }

    /**
     * Remove long road. Vertices must lie on the same straight line.
     *
     * @param x1 X coordinate of first vertex.
     * @param y1 Y coordinate of first vertex.
     * @param x2 X coordinate of last vertex.
     * @param y2 Y coordinate of last vertex.
     */
    void removeRoad(int x1, int y1, int x2, int y2) {
        if (x1 >= size || y1 >= size || x2 >= size || y2 >= size
                || x1 < 0 || y1 < 0 || x2 < 0 || y2 < 0)
            throw new IllegalArgumentException();

        if (x1 == x2) {
            for (int i = min(y1, y2); i < max(y1, y2); ++i)
                removeUnitRoad(x1, i, x1, i + 1);
        } else if (y1 == y2) {
            for (int i = min(x1, x2); i < max(x1, x2); ++i)
                removeUnitRoad(i, y1, i + 1, y1);
        } else {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Remove unit road. Distance between given vertices must be one unit.
     *
     * @param x1 X coordinate of first vertex.
     * @param y1 Y coordinate of first vertex.
     * @param x2 X coordinate of second vertex.
     * @param y2 Y coordinate of second vertex.
     */
    private void removeUnitRoad(int x1, int y1, int x2, int y2) {
        if (x1 >= size || y1 >= size || x2 >= size || y2 >= size
                || x1 < 0 || y1 < 0 || x2 < 0 || y2 < 0)
            throw new IllegalArgumentException();

        if (!(x1 == x2 && (abs(y1 - y2) == 1)) && !(y1 == y2 && (abs(x1 - x2) == 1)))
            throw new IllegalArgumentException();

        if (x1 == x2 && y1 - y2 == 1) {
            roads[x1 * size + y1][0] = false;
            roads[x2 * size + y2][2] = false;
        }

        if (x1 - x2 == -1 && y1 == y2) {
            roads[x1 * size + y1][1] = false;
            roads[x2 * size + y2][3] = false;
        }

        if (x1 == x2 && y1 - y2 == -1) {
            roads[x1 * size + y1][2] = false;
            roads[x2 * size + y2][0] = false;
        }

        if (x1 - x2 == 1 && y1 == y2) {
            roads[x1 * size + y1][3] = false;
            roads[x2 * size + y2][1] = false;
        }

        if (getNeighbourSize(x1, y1) == 0)
            removeVertex(x1, y1);

        if (getNeighbourSize(x2, y2) == 0)
            removeVertex(x2, y2);
    }

    /**
     * Add new vertex of sink type.
     *
     * @param x X coordinate.
     * @param y Y coordinate.
     */
    Sink addSink(int x, int y) {
        Sink s = new Sink(x, y, Vertex.VertexType.IN);
        vertices[x * size + y][1] = new Vertex(x, y, Vertex.VertexType.OUT);
        vertices[x * size + y][0] = s;
        return s;
    }

    /**
     * Add new vertex of source type.
     *
     * @param x X coordinate.
     * @param y Y coordinate.
     */
    Source addSource(int x, int y, long limit, float probability) {
        vertices[x * size + y][0] = new Vertex(x, y, Vertex.VertexType.IN);
        Source s = new Source(x, y, Vertex.VertexType.OUT, limit, probability);
        vertices[x * size + y][1] = s;
        return s;
    }

    /**
     * Set vertex type to the default type.
     */
    void removeVertexClassifiers(int x, int y) {
        if (vertices[x * size + y] == null) return;
        addVertex(x, y);
    }

    /**
     * @param x X coordinate.
     * @param y Y coordinate.
     * @return Whether the vertex under given coordinates has been initialised in the grid.
     */
    boolean hasVertex(int x, int y) {
        if (x < 0 || y < 0 || x >= size || y >= size)
            throw new IllegalArgumentException();
        return (vertices[x * size + y][0] != null);
    }

    /**
     * Get vertex of given coordinates.
     *
     * @param x X coordinate.
     * @param y Y coordinate.
     */
    Vertex getVertexIn(int x, int y) {
        if (x < 0 || y < 0 || x >= size || y >= size)
            throw new IllegalArgumentException();
        return vertices[x * size + y][0];
    }

    Vertex getVertexIn(Vertex out) {
        if (out.getVertexType() != Vertex.VertexType.OUT)
            throw new IllegalArgumentException();
        return getVertexIn(out.x, out.y);
    }

    Vertex getVertexOut(int x, int y) {
        if (x < 0 || y < 0 || x >= size || y >= size)
            throw new IllegalArgumentException();
        return vertices[x * size + y][1];
    }

    Vertex getVertexOut(Vertex in) {
        if (in.getVertexType() != Vertex.VertexType.IN)
            throw new IllegalArgumentException();
        return getVertexOut(in.x, in.y);
    }

    Vertex getVertex(int x, int y) { return getVertexOut(x, y); }

    /**
     * @return Corresponding IN/OUT vertex of given vertex.
     */
    Vertex getOther(Vertex vertex) {
        if (vertex.getVertexType() == Vertex.VertexType.OUT)
            return getVertexIn(vertex);
        if (vertex.getVertexType() == Vertex.VertexType.IN)
            return getVertexOut(vertex);
        throw new IllegalStateException();
    }

    /**
     * Get vertices adjacent to the given vertex. (OUT type)
     *
     * @param x X coordinate.
     * @param y Y coordinate.
     */
    List<Vertex> getNeighbours(int x, int y) {
        List<Vertex> result = new LinkedList<>();
        for (int i = 0; i < 4; i++) {
            if (roads[x * size + y][i]) result.add(getNeighbour(x, y, i));
        }
        return result;
    }

    /**
     * Get vertices adjacent to the given vertex.
     *
     * @param vertex Vertex to get neighbours of.
     */
    List<Vertex> getNeighbours(Vertex vertex) {
        if (vertex.getVertexType() == Vertex.VertexType.IN)
            return new LinkedList<Vertex>(Collections.singletonList(vertices[vertex.x * size + vertex.y][1]));
        return getNeighbours(vertex.x, vertex.y);
    }

    /**
     * Get neighbour of vertex with given coordinates.
     *
     * @param x X coordinate.
     * @param y Y coordinate.
     * @param direction 0-up, 1-right, 2-down, 3-left neighbour.
     * @return Neighbour vertex.
     */
    private Vertex getNeighbour(int x, int y, int direction) {
        if (direction < 0 || direction > 4)
            throw new IllegalArgumentException();
        switch (direction) {
            case 0:
                return vertices[x * size + (y - 1)][0];
            case 1:
                return vertices[(x + 1) * size + y][0];
            case 2:
                return vertices[x * size + (y + 1)][0];
        }
        return vertices[(x - 1) * size + y][0];
    }

    /**
     * Get all existing vertices of the grid.
     */
    Collection<Vertex> getVertices() {
        HashSet<Vertex> result = new HashSet<>();
        for (int i = 0; i < size * size; i++) {
            result.add(vertices[i][0]);
            result.add(vertices[i][1]);
        }
        result.remove(null);
        return result;
    }

    /**
     * Get all existing vertices of the grid sorted by type.
     */
    Collection<Vertex> getVerticesSorted() {
        LinkedList<Vertex> result = new LinkedList<>();
        for (int i = 0; i < size * size; i++) {
            if (vertices[i][1] != null)
                result.add(vertices[i][1]);
        }
        for (int i = 0; i < size * size; i++) {
            if (vertices[i][0] != null)
                result.add(vertices[i][0]);
        }
        return result;
    }

    private void visitVertex(Vertex vertex, HashMap<Vertex, Boolean> visited) {
        visited.put(vertex, true);
        for (Vertex vertex1 : getNeighbours(vertex)) {
            if (!visited.get(vertex1))
                visitVertex(vertex1, visited);
        }
    }

    /**
     * @return Whether there is a path between every pair of vertices.
     */
    boolean isConnected() {
        Collection<Vertex> vertices = getVertices();
        if (vertices.isEmpty())
            return true;
        HashMap<Vertex, Boolean> visited = new HashMap<>();
        for (Vertex vertex : vertices) {
            visited.put(vertex, false);
        }
        visitVertex(vertices.iterator().next(), visited);
        for (Vertex vertex1 : vertices) {
            if (!visited.get(vertex1))
                return false;
        }
        return true;
    }
}
