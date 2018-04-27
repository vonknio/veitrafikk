package Model;

import java.util.ArrayList;
import java.util.List;

public class Vehicle {
    public Vertex prev;
    public Vertex cur;
    public Vertex next;
    public Vertex dest;

    public final ObjectStatistics stats;

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

    public class VehicleStatistics implements ObjectStatistics {
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
            if (prev == cur) {
                idleTicks++;
            }
        }

        private void updatePath() {
            if (prev != cur) {
                path.add(cur);
            }
        }

        private void updateAuxiliaryVariables() {

        }
    }
}
