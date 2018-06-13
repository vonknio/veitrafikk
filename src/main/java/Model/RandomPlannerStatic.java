package Model;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Random;

class RandomPlannerStatic implements PathPlanner {
    @Override
    @NotNull
    public Vertex getDestinationForNextTick(Vehicle vehicle, Grid grid) {
        Vertex vertex;
        if (vehicle.getNext() == null)
            vertex = vehicle.getCur().getVertexType() == Vertex.VertexType.OUT ?
                    vehicle.getCur() : grid.getVertexOut(vehicle.getCur());
        else vertex = vehicle.getNext().getVertexType() == Vertex.VertexType.OUT ?
                vehicle.getNext() : grid.getVertexOut(vehicle.getNext());

        List<Vertex> candidates = grid.getNeighbours(vertex);
        if (candidates.isEmpty())
            throw new IllegalStateException("Disconnected graph");
        return candidates.get((new Random()).nextInt(candidates.size()));
    }

    @Override
    public boolean isDynamic() { return false; }
}
