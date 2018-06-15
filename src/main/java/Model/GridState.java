package Model;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.logging.Logger;

class GridState {
    private long timeTick = 0;
    private List<Vehicle> vehicles = new LinkedList<>();
    private List<Vehicle> ghostVehicles = new LinkedList<>();
    private final List<Sink> sinks = new LinkedList<>();
    private final List<Source> sources = new LinkedList<>();
    private final Grid grid;
    private final static Logger logger = Logger.getLogger(GridState.class.getName());

    GridState(Grid grid) { this.grid = grid; }

    long getTime() { return timeTick; }

    /**
     * Generate all changes for next timetick, e.g. spawn vehicles.
     */
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

    /**
     * @return List of vehicles currently present on the grid.
     */
    List<Vehicle> getVehicles() { return vehicles; }

    /**
     * @return List of ghost vehicles - vehicles that died within one tick.
     */
    List<Vehicle> getGhostVehicles() { return ghostVehicles; }

    /**
     * @return List of vehicles sorted by id.
     */
    List<Vehicle> getVehiclesSorted() {
       List<Vehicle> vehiclesSorted = new ArrayList<>(vehicles);
       vehiclesSorted.sort(Comparator.comparingInt(Vehicle::getId));
       return vehiclesSorted;
    }

    /**
     * @return Randomly shuffled list of vehicles.
     */
    List<Vehicle> getVehiclesShuffled() {
        LinkedList<Vehicle> shuffled = new LinkedList<>(vehicles);
        Collections.shuffle(shuffled);
        return shuffled;
    }

    Collection<Sink> getSinks() { return sinks; }

    Collection<Source> getSources() { return sources; }

    void addVehicle(Vehicle vehicle) { vehicles.add(vehicle); }

    Color addSink(int x, int y) {
        Sink sink = grid.addSink(x, y);
        sink.setColor();
        sinks.removeIf(z -> TestUtils.compressedEquals(z, sink));
        sinks.add(sink);
        return sink.getColor();
    }

    void addSource(int x, int y, long limit, float probability) {
        Source source = grid.addSource(x, y, limit, probability);
        sources.removeIf(z -> TestUtils.compressedEquals(z, source));
        sources.add(source);
    }

    void removeVehicle(Vehicle vehicle) {
        vehicle.getCur().removeVehicle();
        vehicles.remove(vehicle);
        if (vehicle.getPathLength() <= 2.0)
            ghostVehicles.add(vehicle);
    }

    void removeGhostVehicle(Vehicle vehicle) {
        ghostVehicles.remove(vehicle);
    }

    /**
     * Remove source or sink under given coordinates.
     * @param x1 X coordinate.
     * @param y1 Y coordinate.
     */
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

    public void applySettingsToSources(int limit, float probability) {
        for (Source s : sources) {
            s.setLimit(limit);
            s.setProbability(probability);
        }
    }

}
