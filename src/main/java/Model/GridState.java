package Model;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

class GridState {
    private long timeTick = 0;
    private List<Vehicle> vehicles = new LinkedList<>();
    private List<Sink> sinks = new LinkedList<>();
    private List<Source> sources = new LinkedList<>();
    private final Grid grid;
    private final static Logger logger = Logger.getLogger(GridState.class.getName());

    GridState(Grid grid) {
        this.grid = grid;
    }

    long getTime() {
        return timeTick;
    }

    void updateForNextTimetick() {
        for (Source source : sources) {
            if (source.canSpawnVehicle()) {
                Sink randomSink = getRandomSink();
                if (randomSink != null) {
                    Vehicle vehicle = source.spawnVehicle(randomSink);
                    addVehicle(vehicle);
                    logger.config("Source " + source + " has spawned vehicle " + vehicle.getId());
                }
            }
        }
        timeTick++;
    }

    List<Vehicle> getVehicles() {
        return vehicles;
    }

    List<Vehicle> getVehiclesSorted() {
        LinkedList<Vehicle> vehicles = new LinkedList<>();
        for (Vertex v : grid.getVerticesSorted()) {
            if (v.hasVehicle())
                vehicles.add(v.getVehicle());
        }
        return vehicles;
    }

    Collection<Sink> getSinks() {
        return sinks;
    }

    Collection<Source> getSources() {
        return sources;
    }

    void addVehicle(Vehicle vehicle) {
        vehicles.add(vehicle);
    }

    void addSink(int x, int y) {
        Sink sink = grid.addSink(x, y);
        sinks.removeIf((z) -> TestUtils.compressedEquals(z, sink));
        sinks.add(sink);
    }

    void addSource(int x, int y, long limit, float probability) {
        Source source = grid.addSource(x, y, limit, probability);
        sources.removeIf((z) -> TestUtils.compressedEquals(z, source));
        sources.add(source);
    }

    void removeVehicle(Vehicle vehicle) {
        vehicle.getCur().removeVehicle();
        vehicles.remove(vehicle);
    }

    private Sink getRandomSink() {
        if (sinks.isEmpty()) return null;
        return sinks.get(new Random().nextInt(sinks.size()));
    }
}
