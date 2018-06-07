package Model;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

/**
 * Class representing a vehicle.
 */
class Vehicle {
    private Color color;
    private Vertex prev;
    private Vertex cur;
    private Vertex next;
    private Vertex nextNext;
    private Vertex dest;
    private int id;
    private static int idCounter = 0;

    final VehicleStatistics stats;

    Vehicle(Vertex cur) {
        this(cur, null);
    }

    Vehicle(Vertex cur, Vertex dest) {
        this(cur, dest, new Color(0,255,255));
    }

    Vehicle(Vertex cur, Vertex dest, Color color) {
        this.prev = cur;
        this.cur = cur;
        this.dest = dest;
        this.stats = new VehicleStatistics();
        this.color = color;
        this.id = idCounter++;
    }

    Vehicle(Vertex cur, Vertex dest, VehicleStatistics stats) {
        this(cur, dest, stats, new Color(0,255,255));
    }

    Vehicle(Vertex cur, Vertex dest, VehicleStatistics stats, Color color) {
        this.prev = cur;
        this.cur = cur;
        this.dest = dest;
        this.stats = stats;
        this.color = color;
        this.id = idCounter++;
    }


    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *  Getters and setters
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    Vertex getCur() {
        return cur;
    }

    void setCur(Vertex vertex) {
        cur = vertex;
    }

    Vertex getNext() {
        return next;
    }

    Vertex getNextNext() {
        return nextNext;
    }

    Vertex getPrev() {
        return prev;
    }

    void setPrev(Vertex vertex) {
        prev = vertex;
    }

    Vertex getDest() {
        return dest;
    }

    void setDest(Vertex vertex) {
        dest = vertex;
    }

    void setNext(Vertex vertex) {
        if (vertex.getVertexType() != Vertex.VertexType.IN)
            throw new IllegalArgumentException("Next field should always have IN type.");
        next = vertex;
    }

    void setNextSafe(Vertex vertex, Grid grid) {
        if (vertex.getVertexType() != Vertex.VertexType.IN)
            next = grid.getOther(vertex);
        else next = vertex;
    }

    void setNextNext(Vertex vertex) {
        if (vertex.getVertexType() != Vertex.VertexType.IN)
            throw new IllegalArgumentException("Next field should always have IN type.");
        this.nextNext = vertex;
    }

    void setNextNextSafe(Vertex vertex, Grid grid) {
        if (vertex.getVertexType() != Vertex.VertexType.IN)
            nextNext = grid.getOther(vertex);
        else nextNext = vertex;
    }

    Color getColor() {
        return color;
    }

    int getId() {
        return id;
    }

    //TODO idleTicks
    class VehicleStatistics implements ObjectStatistics {
        private long idleTicks = 0;
        private long ticksAlive = 0;
        private List<Vertex> path = new ArrayList<>();

        @Override
        public void process() {
            updateTime();
            updatePath();

            updateAuxiliaryVariables();
        }

        private void updateTime() {
            ticksAlive++;
            if (TestUtils.compressedEquals(prev, cur) && !path.isEmpty())
                idleTicks++;
        }

        private void updatePath() {
            if (path.isEmpty()) {
                if (cur instanceof Source)
                    path.add(cur);
            }
            if (!TestUtils.compressedEquals(prev, cur))
                path.add(cur);
        }

        private void updateAuxiliaryVariables() {
        }

        long ticksAlive() {
            return ticksAlive - 1;
        }

        double pathLength() {
            Vertex prev = null;
            int result = 0;
            for (Vertex vertex : path) {
                if (!TestUtils.compressedEquals(prev, vertex))
                    result++;
                prev = vertex;
            }
            return result;
        }

        Collection<Vertex> verticesVisited() {
            return new HashSet<>(path);
        }

        double velocity() {
            return pathLength() / (ticksAlive() + 1);
        }
    }
}
