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
    private final Color color;
    private Vertex prev;
    private Vertex cur;
    private Vertex next;
    private Vertex nextNext;
    private Vertex dest;
    private final int id;
    private static int idCounter = 0;

    final VehicleStatistics stats;

    Vehicle(Vertex cur) {
        this(cur, null);
    }

    Vehicle(Vertex cur, Vertex dest) {
        this(cur, dest, new Color(0, 255, 255));
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
        this(cur, dest, stats, new Color(0, 255, 255));
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

    Vertex getCur() { return cur; }

    void setCur(Vertex vertex) { cur = vertex; }

    Vertex getNext() { return next; }

    Vertex getNextNext() { return nextNext; }

    Vertex getPrev() { return prev; }

    void setPrev(Vertex vertex) { prev = vertex; }

    Vertex getDest() { return dest; }

    void setDest(Vertex vertex) { dest = vertex; }

    double getPathLength() { return stats.pathLength(); }

    List<Vertex> getPath(){
        return stats.path;
    }

    /**
     * @param vertex Vertex of IN type.
     */
    void setNext(Vertex vertex) {
        if (vertex.getVertexType() != Vertex.VertexType.IN)
            throw new IllegalArgumentException("Next field should always have IN type.");
        next = vertex;
    }

    /**
     * If given vertex has IN type, 'next' field will be set to it. Otherwise
     * it will be set to the twin IN vertex of the given one.
     */
    void setNextSafe(Vertex vertex, Grid grid) {
        if (vertex.getVertexType() != Vertex.VertexType.IN) next = grid.getOther(vertex);
        else next = vertex;
    }

    /**
     * @param vertex Vertex of IN type.
     */
    void setNextNext(Vertex vertex) {
        if (vertex.getVertexType() != Vertex.VertexType.IN)
            throw new IllegalArgumentException("Next field should always have IN type.");
        this.nextNext = vertex;
    }

    /**
     * If given vertex has IN type, 'nextNext' field will be set to it. Otherwise
     * it will be set to the twin IN vertex of the given one.
     */
    void setNextNextSafe(Vertex vertex, Grid grid) {
        if (vertex.getVertexType() != Vertex.VertexType.IN) nextNext = grid.getOther(vertex);
        else nextNext = vertex;
    }

    Color getColor() { return color; }

    int getId() { return id; }

    static void resetIdCounter() {
        idCounter = 0;
    }


    class VehicleStatistics implements ObjectStatistics {
        private long idleTicks = 0;
        private long ticksAlive = 0;
        private List<Vertex> path = new ArrayList<>();
        private boolean finished = false;

        @Override
        public void process() {
            updateTime();
            updatePath();

            updateAuxiliaryVariables();
        }

        private void updateTime() {
            ticksAlive++;
            if (TestUtils.compressedEquals(prev, cur) && !path.isEmpty()) idleTicks++;
        }

        private void updatePath() {
            if (path.isEmpty()) {
                if (cur instanceof Source) path.add(cur);
            }
            if (!TestUtils.compressedEquals(prev, cur)) path.add(cur);
        }

        private void updateAuxiliaryVariables() { }

        long ticksAlive() { return ticksAlive - 1; }

        long getIdleTicks() {
            return idleTicks;
        }

        double pathLength() {
            Vertex prev = null;
            int result = 0;
            for (Vertex vertex : path) {
                if (!TestUtils.compressedEquals(prev, vertex)) result++;
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

        int getId() {
            return id;
        }

        int[] getPosition() {
            int[] po = new int[2];
            po[0] = cur.x;
            po[1] = cur.y;
            return po;
        }

        void setFinished() {
            finished = true;
        }

        boolean hasFinished() {
            return finished;
        }

        int[] previous() {
            int[] po = new int[2];
            po[0] = prev.x;
            po[1] = prev.y;
            return po;
        }

        public Color color() {
            return color;
        }
    }
}
