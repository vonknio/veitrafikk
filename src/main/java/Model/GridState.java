package Model;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

class GridState {
    private long timeTick = 0;
    private List<Vehicle> vehicles = new LinkedList<>();
    private List<Sink> sinks = new LinkedList<>();
    private List<Source> sources = new LinkedList<>();

    long getTime() {
        return timeTick;
    }

    void play() {
        for (Source source : sources) {
            if (source.canSpawnVehicle()) {
                Sink randomSink = getRandomSink();
                if (randomSink != null)
                    addVehicle(source.spawnVehicle(randomSink));
            }
        }
        timeTick++;
    }

    Collection<Vehicle> getVehicles() { return vehicles; }
    Collection<Sink> getSinks() { return sinks; }
    Collection<Source> getSources() { return sources; }

    void addVehicle(Vehicle vehicle) { vehicles.add(vehicle); }
    void addSink(Sink sink) { sinks.add(sink); }
    void addSource(Source source) { sources.add(source); }

    private Sink getRandomSink(){
        if (sinks.isEmpty()) return null;
        return sinks.get(new Random().nextInt(sinks.size()));
    }
}
