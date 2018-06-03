package Model;

import java.util.HashSet;
import java.util.LinkedList;

class Statistics {
    private static LinkedList<Vehicle.VehicleStatistics> removedVehiclesStatistics = new LinkedList<>();
    /**
     * Update all statistics related to given vertex for current timetick.
     */
    private GridState gridState;
    private Grid grid;

    public Statistics(Grid grid, GridState gridState) {
        setGrid(grid);
        setGridState(gridState);
        removedVehiclesStatistics = new LinkedList<>();
    }

    static void process(Vehicle vehicle) {
        if (vehicle.stats != null)
            vehicle.stats.process();
    }

    static void processRemoved(Vehicle vehicle) {
        removedVehiclesStatistics.add(vehicle.stats);
    }

    static void process(Vertex vertex) {
        if (vertex.stats != null)
            vertex.stats.process();
    }

    void setGridState(GridState gridState) {
        this.gridState = gridState;
    }

    void setGrid(Grid grid) {
        this.grid = grid;
    }

    LinkedList<Vehicle.VehicleStatistics> vehiclesStatistics() {
        LinkedList<Vehicle.VehicleStatistics> statistics = new LinkedList<>();
        for (Vehicle vehicle : gridState.getVehicles()) {
            statistics.add(vehicle.stats);
        }
        return statistics;
    }

    LinkedList<Vertex.VertexStatistics> verticesStatistics() {
        LinkedList<Vertex.VertexStatistics> statistics = new LinkedList<>();
        for (Vertex vertex : grid.getVertices()) {
            statistics.add(vertex.stats);
        }
        return statistics;
    }

    double averageVelocity() {
        double result = 0;
        for (Vehicle vehicle : gridState.getVehicles()) {
            result += vehicle.stats.velocity();
        }
        return result / gridState.getVehicles().size();
    }

    int verticesVisited() {
        HashSet<Vertex> set = new HashSet<>();
        for (Vehicle vehicle : gridState.getVehicles()) {
            set.addAll(vehicle.stats.verticesVisited());
        }
        for (Vehicle.VehicleStatistics statistics : removedVehiclesStatistics) {
            set.addAll(statistics.verticesVisited());
        }
        return set.size();
    }

    double averagePathLength() {
        double result = 0;
        for (Vehicle vehicle : gridState.getVehicles()) {
            result += (vehicle.stats.pathLength() - 1);
        }
        return result / gridState.getVehicles().size();
    }

    double averageTimeEmpty() {
        double result = 0;
        for (Vertex vertex : grid.getVertices()) {
            result += vertex.stats.timeEmpty();
        }
        return result / grid.getVertices().size();
    }

    double averageVehicleCount() {
        double result = 0;
        for (Vertex vertex : grid.getVertices()) {
            result += vertex.stats.vehicleCount();
        }
        return result / grid.getVertices().size();
    }

}
