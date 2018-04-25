package Model;

public class Vehicle {
    public Vertex prev;
    public Vertex cur;
    public Vertex next;
    public final Vertex dest;

    public final ObjectStatistics stats;

    Vehicle(Vertex cur, Vertex dest, ObjectStatistics stats) {
        this.cur = cur;
        this.dest = dest;
        this.stats = stats;
    }

    public class VehicleStatistics implements ObjectStatistics {
        @Override
        public void process() {
        }
    }
}
