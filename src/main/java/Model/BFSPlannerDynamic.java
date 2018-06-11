package Model;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class BFSPlannerDynamic extends BFSPlannerStatic {
    private Map<Vehicle, Vertex> latestCur = new HashMap<>();

    @NotNull
    @Override
    public Vertex getDestinationForNextTick(Vehicle vehicle, Grid grid) {
        if (TestUtils.compressedEquals(vehicle.getCur(), latestCur.get(vehicle)) &&
            vehicle.getNextNext() != null) {
            // Vehicle hasn't moved since last function call – find a new route
            Vertex curBackup = vehicle.getCur();
            vehicle.setCur(vehicle.getNext());
            Stack<Vertex> path = planPath(vehicle, grid,
                    (vehicle.getNextNext() == null) ?
                            vehicle.getNext() :
                            vehicle.getNextNext());
            vehicle.setCur(curBackup);
            if (path != null)
                applyPath(vehicle, path);
        }

        Vertex next = super.getDestinationForNextTick(vehicle, grid);
        latestCur.put(vehicle, vehicle.getCur());
        return next;
    }

    @Override
    public boolean isDynamic() { return true; }
}
