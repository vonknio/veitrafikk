package Model;

class Vertex {
    private VertexType type;
    private Vehicle vehicle;

    final ObjectStatistics stats;
    final int x, y;


    public Vertex(int x, int y, VertexType vertexType, ObjectStatistics stats) {
        this.x = x;
        this.y = y;
        this.type = vertexType;
        this.stats = stats;
    }

    VertexType getVertexType() {
        return type;
    }

    void setVertexType(VertexType vertexType) {
        this.type = vertexType;
    }

    boolean hasVehicle() { return getVehicle() != null; }

    Vehicle getVehicle() {
        return vehicle;
    }

    void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    void removeVehicle() { this.vehicle = null; }

    boolean isCrossroad() {
        return type != VertexType.ROAD;
    }

    public enum VertexType {
        SOURCE, SINK, ROAD, CROSSROAD
    }

    class VertexStatistics implements ObjectStatistics {
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
