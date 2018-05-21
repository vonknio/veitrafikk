package Model;

import java.util.*;

class GridState {
    private long timeTick = 0;
    private List<Vehicle> vehicles = new LinkedList<>();
    private List<Sink> sinks = new LinkedList<>();
    private List<Source> sources = new LinkedList<>();

    long getTime() {
        return timeTick;
    }

    void updateForNextTimetick() {
        for (Source source : sources) {
            if (source.canSpawnVehicle()) {
                Sink randomSink = getRandomSink();
                if (randomSink != null)
                    addVehicle(source.spawnVehicle(randomSink));
            }
        }
        timeTick++;
    }

    List<Vehicle> getVehicles() { return vehicles; }

    Collection<Sink> getSinks() { return sinks; }

    Collection<Source> getSources() { return sources; }

    void addVehicle(Vehicle vehicle) { vehicles.add(vehicle); }

    void addSink(Sink sink) { sinks.add(sink); }

    void addSource(Source source) { sources.add(source); }

    void removeVehicle(Vehicle vehicle) {
        vehicle.getCur().removeVehicle();
        vehicles.remove(vehicle);
    }

    private Sink getRandomSink(){
        if (sinks.isEmpty()) return null;
        return sinks.get(new Random().nextInt(sinks.size()));
    }
}
