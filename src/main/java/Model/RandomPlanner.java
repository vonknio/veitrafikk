package Model;

import org.jetbrains.annotations.NotNull;
import java.util.List;
import java.util.Random;

/**
 * Planner that chooses a new destination uniformly at random from vehicles' neighbours.
 */
class RandomPlanner implements PathPlanner {
    @Override @NotNull
    public Vertex getDestinationForNextTick(Vehicle vehicle, Grid grid) {
        Vertex cur = vehicle.getCur().getVertexType() == Vertex.VertexType.OUT ?
                vehicle.getCur() : grid.getVertexOut(vehicle.getCur());

        List<Vertex> candidates = grid.getNeighbours(cur);
        if (candidates.isEmpty())
            throw new IllegalStateException("Disconnected graph");
        return candidates.get((new Random()).nextInt(candidates.size()));
    }
}