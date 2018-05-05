package Model;

/**
 * Class representing an existing vertex of the grid.
 */
class Vertex {
    static final VertexType defaultType = VertexType.CROSSROAD;
    final ObjectStatistics stats;
    final int x, y;

    private VertexType type;
    private Vehicle vehicle;

    Vertex(int x, int y) {
        this(x, y, defaultType);
    }

    Vertex(int x, int y, VertexType vertexType) {
        this.x = x;
        this.y = y;
        this.type = vertexType;
        this.stats = new VertexStatistics();
    }

    Vertex(int x, int y, VertexType vertexType, ObjectStatistics stats) {
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

    Vehicle getVehicle() { return vehicle; }

    void setVehicle(Vehicle vehicle) { this.vehicle = vehicle; }

    void removeVehicle() { this.vehicle = null; }

    boolean isCrossroad() { return type != VertexType.ROAD; }

    enum VertexType {
        SOURCE,     // spawns vehicles
        SINK,       // swallows vehicles
        ROAD,       // no turn possible
        CROSSROAD   // turn possible
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
            if (vehicleInPrevTick == vehicle)
                vehicleCount++;

            if (vehicle == null)
                noVehicleTicks++;
        }

        private void updateAuxiliaryVariables() {
            vehicleInPrevTick = vehicle;
        }
    }
}
