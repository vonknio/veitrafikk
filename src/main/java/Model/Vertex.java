package Model;

/**
 * Class representing an existing vertex of the grid.
 */
class Vertex {
    final VertexStatistics stats;
    final int x, y;

    private VertexType type;
    private Vehicle vehicle;

    Vertex(int x, int y, VertexType vertexType) {
        this.x = x;
        this.y = y;
        this.type = vertexType;
        this.stats = new VertexStatistics();
    }

    @Deprecated
    Vertex(int x, int y, VertexType vertexType, VertexStatistics stats) {
        this.x = x;
        this.y = y;
        this.type = vertexType;
        this.stats = stats;
    }

    int[] getCoordinates () {
        int[] coordinates = new int[2];
        coordinates[0] = this.x;
        coordinates[1] = this.y;
        return coordinates;
    }

    int getXCoordinate () { return this.x; }

    int getYCoordinate () { return this.y; }

    VertexType getVertexType() { return type; }

    void setVertexType(VertexType vertexType) {
        this.type = vertexType;
    }

    boolean hasVehicle() { return getVehicle() != null; }

    Vehicle getVehicle() { return vehicle; }

    void setVehicle(Vehicle vehicle) { this.vehicle = vehicle; }

    void removeVehicle() { this.vehicle = null; }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }

    enum VertexType {
        IN,
        OUT
    }

    class VertexStatistics implements ObjectStatistics {
        private long vehicleCount = 0;
        private long noVehicleTicks = 0;
        private long ticks = 0;
        private Vehicle vehicleInPrevTick;

        @Override
        public void process() {
            updateTime();
            updateVehicleCount();
            updateAuxiliaryVariables();
        }

        private void updateTime() {
            if (vehicle == null)
                noVehicleTicks++;
            ticks++;
        }


        private void updateVehicleCount() {
            if (vehicleInPrevTick == vehicle)
                vehicleCount++;
        }

        private void updateAuxiliaryVariables() {
            vehicleInPrevTick = vehicle;
        }

        long vehicleCount() {
            return vehicleCount;
        }

        double timeEmpty() {
            if (ticks == 0)
                return 0;
            return noVehicleTicks / ticks;
        }

    }
}
