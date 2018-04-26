package Model;

import java.util.Collection;
import java.util.LinkedList;

public class GridState {
    private long timeTick=0;
    private Collection<Vehicle> vehicles = new LinkedList<>();

    public long getTime() {
        return timeTick;
    }

    public void play() {
        timeTick++;
        //God.processGrid();
    }

    public Collection<Vehicle> getVehicles() {
        return vehicles;
    }

    public void addVehicle(Vehicle vehicle) {
        vehicles.add(vehicle);
    }
}
