package Model;

import java.util.Collection;
import java.util.LinkedList;

class GridState {
    private long timeTick=0;
    private Collection<Vehicle> vehicles = new LinkedList<>();

    long getTime() {
        return timeTick;
    }

    void play() {
        timeTick++;
        //God.processGrid();
    }

    Collection<Vehicle> getVehicles() {
        return vehicles;
    }

    void addVehicle(Vehicle vehicle) {
        vehicles.add(vehicle);
    }
}
