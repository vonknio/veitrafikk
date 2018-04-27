package Model;

import java.util.*;

public class Grid {
    private int size;
    private Vertex[] vertices;
    private boolean[][] roads;
    private int roadLength;

    private static final int defaultRoadLength = 1;  // intervals

    Grid(int size) {
       this(size, defaultRoadLength);
    }

    Grid(int size, int roadLength) {
        if ((size - 1) % roadLength != 0)
            throw new IllegalArgumentException();

        this.size = size;
        vertices = new Vertex[size * size];
        roads = new boolean[size * size][4];

        if (roadLength < 1)
            throw new IllegalArgumentException();

        this.roadLength = roadLength;
    }

    private void addVertex(int x, int y, Vertex.VertexType type) {
        vertices[x * size + y] = new Vertex(x, y, type, null);
    }

    private int getNeighbourSize(int x, int y) {
        int result = 0;
        for (int i = 0; i < 4; i++) result += !roads[x * size + y][i] ? 0 : 1;
        return result;
    }

    public int getSize() {
        return size;
    }

    // TODO: rename to addRoad
    public void addLongRoad(int x1, int y1, int x2, int y2) {
        if (x1 >= size || y1 >= size || x2 >= size || y2 >= size
                || x1 < 0 || y1 < 0 || x2 < 0 || y2 < 0)
            throw new IllegalArgumentException();

        if (!(x1 % roadLength == 0 && y1 % roadLength == 0)
                || !(x2 % roadLength == 0 && y2 % roadLength == 0))
            throw new IllegalArgumentException();

        if (x1 == x2) {
            for (int i = y1; i < y2; ++i)
                addRoad(x1, i, x1, i+1);
        } else if (y1 == y2) {
            for (int i = x1; i < x2; ++i)
                addRoad(i, y1, i+1, y1);
        } else {
            throw new IllegalArgumentException();
        }
    }

    // TODO: make private, rename to addUnitRoad
    public void addRoad(int x1, int y1, int x2, int y2) {
        if (vertices[x1 * size + y1] == null) addVertex(x1, y1, Vertex.VertexType.ROAD);
        if (vertices[x2 * size + y2] == null) addVertex(x2, y2, Vertex.VertexType.ROAD);
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
        if (getNeighbourSize(x1, y1) > 2 && !getVertex(x1, y1).isCrossroad())
            vertices[x1 * size + y1].setVertexType(Vertex.VertexType.CROSSROAD);
        if (getNeighbourSize(x2, y2) > 2 && !getVertex(x2, y2).isCrossroad())
            vertices[x2 * size + y2].setVertexType(Vertex.VertexType.CROSSROAD);
    }

    public void removeRoad(int x1, int y1, int x2, int y2) {
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
            vertices[x1 * size + y1] = null;
        else {
            if (getNeighbourSize(x1, y1) <= 2 && getVertex(x1, y1).getVertexType() == Vertex.VertexType.CROSSROAD)
                vertices[x1 * size + y1].setVertexType(Vertex.VertexType.ROAD);
        }
        if (getNeighbourSize(x2, y2) == 0)
            vertices[x2 * size + y2] = null;
        else {
            if (getNeighbourSize(x2, y2) <= 2 && getVertex(x1, y1).getVertexType() == Vertex.VertexType.CROSSROAD)
                vertices[x2 * size + y2].setVertexType(Vertex.VertexType.ROAD);

        }
    }

    public void addSink(int x, int y) {
        if (vertices[x * size + y] == null) addVertex(x, y, Vertex.VertexType.SINK);
        else vertices[x * size + y].setVertexType(Vertex.VertexType.SINK);
    }

    public void addSource(int x, int y) {
        if (vertices[x * size + y] == null) addVertex(x, y, Vertex.VertexType.SOURCE);
        else vertices[x * size + y].setVertexType(Vertex.VertexType.SOURCE);
    }

    Vertex getVertex(int x, int y) {
        return vertices[x * size + y];
    }

    public List<Vertex> getNeighbours(int x, int y) {
        List<Vertex> result = new LinkedList<>();
        for (int i = 0; i < 4; i++) {
            if (roads[x * size + y][i]) result.add(getNeighbour(x, y, i));
        }
        return result;
    }

    public List<Vertex> getNeighbours(Vertex vertex) {
        return getNeighbours(vertex.x, vertex.y);
    }

    private Vertex getNeighbour(int x, int y, int i1) {
        switch (i1) {
            case 0:
                return vertices[x * size + (y - 1)];
            case 1:
                return vertices[(x + 1) * size + y];
            case 2:
                return vertices[x * size + (y + 1)];
        }
        return vertices[(x - 1) * size + y];
    }

    public Collection<Vertex> getVertices() {
        HashSet<Vertex> result = new HashSet<>(Arrays.asList(vertices));
        result.remove(null);
        return result;
    }
}
