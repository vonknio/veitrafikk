package Model;

public class Vertex {
    private VertexType type;
    private Vehicle vehicle;

    public final ObjectStatistics stats;
    public final int x, y;


    public Vertex(int x, int y, VertexType vertexType, ObjectStatistics stats) {
        this.x = x;
        this.y = y;
        this.type = vertexType;
        this.stats = stats;
    }

    public VertexType getVertexType() {
        return type;
    }

    public void setVertexType(VertexType vertexType) {
        this.type = vertexType;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public boolean isCrossroad() {
        return type != VertexType.ROAD;
    }

    public enum VertexType {
        SOURCE, SINK, ROAD, CROSSROAD
    }

    public class VertexStatistics implements ObjectStatistics {
        @Override
        public void process() {

        }
    }
}
