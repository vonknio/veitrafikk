package Model;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.logging.Logger;

class GridState {
    private long timeTick = 0;
    private List<Vehicle> vehicles = new LinkedList<>();
    private List<Sink> sinks = new LinkedList<>();
    private List<Source> sources = new LinkedList<>();
    private TrafficLight light = new TrafficLight();
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
                    Statistics.process(vehicle);
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

    List<Vehicle> getVehiclesShuffled() {
        LinkedList<Vehicle> shuffled = new LinkedList<>(vehicles);
        Collections.shuffle(shuffled);
        return shuffled;
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

    Color addSink(int x, int y) {
        Sink sink = grid.addSink(x, y);
        sink.setColor();
        sinks.removeIf((z) -> TestUtils.compressedEquals(z, sink));
        sinks.add(sink);
        return sink.getColor();
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

    void removeSpecialVertex(int x1, int y1){
        for (int i = 0; i < sinks.size(); ++i){
            if (sinks.get(i).x == x1 && sinks.get(i).y == y1)
                sinks.remove(i);
        }
        for (int i = 0; i < sources.size(); ++i){
            if (sources.get(i).x == x1 && sources.get(i).y == y1)
                sources.remove(i);
        }
    }

    private Sink getRandomSink() {
        if (sinks.isEmpty()) return null;
        return sinks.get(new Random().nextInt(sinks.size()));
    }
}
