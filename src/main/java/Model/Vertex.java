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

    public boolean hasVehicle() { return getVehicle() != null; }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public void removeVehicle() { this.vehicle = null; }

    public boolean isCrossroad() {
        return type != VertexType.ROAD;
    }

    public enum VertexType {
        SOURCE, SINK, ROAD, CROSSROAD
    }

    public class VertexStatistics implements ObjectStatistics {
        private long vehicleCount = 0;
        private long noVehicleTicks = 0;

        private Vehicle vehicleInPrevTick;

        @Override
        public void process() {
            updateVehicleCount();

            updateAuxiliaryVariables();
        }

        private void updateVehicleCount() {
            if (vehicleInPrevTick == vehicle) {
                vehicleCount++;
            }
            if (vehicle == null) {
                noVehicleTicks++;
            }
        }

        private void updateAuxiliaryVariables() {
            vehicleInPrevTick = vehicle;
        }
    }
}
