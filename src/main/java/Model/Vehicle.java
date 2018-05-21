package Model;

import java.util.ArrayList;
import java.util.List;

/**
 * Class representing a vehicle.
 */
class Vehicle {
    private Vertex prev;
    private Vertex cur;
    private Vertex next;
    private Vertex dest;

    final ObjectStatistics stats;

    Vehicle(Vertex cur) {
        this(cur, null);
    }

    Vehicle(Vertex cur, Vertex dest) {
        this.cur = cur;
        this.dest = dest;
        this.stats = new VehicleStatistics();
    }

    Vehicle(Vertex cur, Vertex dest, ObjectStatistics stats) {
        this.cur = cur;
        this.dest = dest;
        this.stats = stats;
    }


    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *  Getters and setters
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    Vertex getCur() { return cur; }

    Vertex getNext() { return next; }

    Vertex getPrev() { return prev; }

    Vertex getDest() { return dest; }

    void setCur(Vertex vertex) { cur = vertex; }

    void setPrev(Vertex vertex) { prev = vertex; }

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

    void setDest(Vertex vertex) { dest = vertex; }

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
            if (prev == cur)
                idleTicks++;
        }

        private void updatePath() {
            if (prev != cur)
                path.add(cur);
        }

        private void updateAuxiliaryVariables() {

        }
    }
}
