package Model;

import java.util.*;

class Statistics {
    private static List<Vehicle.VehicleStatistics> removedVehiclesStatistics = new LinkedList<>();
    private GridState gridState;
    private Grid grid;

    Statistics(Grid grid, GridState gridState) {
        setGrid(grid);
        setGridState(gridState);
        removedVehiclesStatistics = new LinkedList<>();
    }

    /**
     * Update all statistics related to given vertex for current timetick.
     */
    static void process(Vehicle vehicle) {
        if (vehicle.stats != null)
            vehicle.stats.process();
    }

    void setGridState(GridState gridState) {
        this.gridState = gridState;
    }

    void setGrid(Grid grid) {
        this.grid = grid;
    }

    static void processRemoved(Vehicle vehicle) {
        vehicle.stats.setFinished();
        removedVehiclesStatistics.add(vehicle.stats);
    }

    static void process(Vertex vertex) {
        if (vertex.stats != null)
            vertex.stats.process();
    }


     /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *  Results
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    LinkedList<Vehicle.VehicleStatistics> vehiclesStatistics() {
        LinkedList<Vehicle.VehicleStatistics> statistics = new LinkedList<>();
        for (Vehicle vehicle : gridState.getVehicles()) {
            statistics.add(vehicle.stats);
        }
        statistics.addAll(removedVehiclesStatistics);
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
        for (Vehicle.VehicleStatistics statistics : removedVehiclesStatistics) {
            result += (statistics.velocity());
        }
        return result /
                (gridState.getVehicles().size() + removedVehiclesStatistics.size());
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
        for (Vehicle.VehicleStatistics statistics : removedVehiclesStatistics) {
            result += (statistics.pathLength() - 1);
        }
        return result /
                (gridState.getVehicles().size() + removedVehiclesStatistics.size());
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

    double averageTicksAlive() {
        double result = 0;
        for (Vehicle vehicle : gridState.getVehicles()) {
            result += (vehicle.stats.ticksAlive());
        }
        for (Vehicle.VehicleStatistics statistics : removedVehiclesStatistics) {
            result += (statistics.ticksAlive());
        }
        return result /
                (gridState.getVehicles().size() + removedVehiclesStatistics.size());
    }

    double averageWaitingTime() {
        double result = 0;
        for (Vehicle vehicle : gridState.getVehicles()) {
            result += (vehicle.stats.getIdleTicks());
        }
        for (Vehicle.VehicleStatistics statistics : removedVehiclesStatistics) {
            result += (statistics.getIdleTicks());
        }
        return result /
                (gridState.getVehicles().size() + removedVehiclesStatistics.size());
    }

    int finishedVehicles() {
        return removedVehiclesStatistics.size();
    }

    int totalVehicles() {
        return finishedVehicles() + gridState.getVehicles().size();
    }

    double maxVelocity() {
        return Collections.max(vehiclesStatistics(),
                Comparator.comparingDouble(Vehicle.VehicleStatistics::velocity)).velocity();
    }

    double maxTicksAlive() {
        return Collections.max(vehiclesStatistics(),
                Comparator.comparingDouble(Vehicle.VehicleStatistics::ticksAlive)).ticksAlive();
    }

    double maxPathLength() {
        return Collections.max(vehiclesStatistics(),
                Comparator.comparingDouble(Vehicle.VehicleStatistics::pathLength)).pathLength() - 1;
    }

    double maxVehicleCount() {
        return Collections.max(verticesStatistics(),
                Comparator.comparingDouble(Vertex.VertexStatistics::vehicleCount)).vehicleCount();
    }

    double maxTimeEmpty() {
        return Collections.max(verticesStatistics(),
                Comparator.comparingDouble(Vertex.VertexStatistics::timeEmpty)).timeEmpty();
    }

    double maxWaitingTime() {
        return Collections.max(vehiclesStatistics(),
                Comparator.comparingDouble(Vehicle.VehicleStatistics::getIdleTicks)).getIdleTicks();
    }

    int notVisitedVertices() {
        return grid.getVertices().size() - verticesVisited();
    }

    int maxVelocityId() {
        return Collections.max(vehiclesStatistics(),
                Comparator.comparingDouble(Vehicle.VehicleStatistics::velocity)).getId();

    }

    int maxPathId() {
        return Collections.max(vehiclesStatistics(),
                Comparator.comparingDouble(Vehicle.VehicleStatistics::pathLength)).getId();
    }

    int maxTickId() {
        return Collections.max(vehiclesStatistics(),
                Comparator.comparingDouble(Vehicle.VehicleStatistics::ticksAlive)).getId();
    }

    int maxWaitId() {
        return Collections.max(vehiclesStatistics(),
                Comparator.comparingDouble(Vehicle.VehicleStatistics::getIdleTicks)).getId();
    }
}
