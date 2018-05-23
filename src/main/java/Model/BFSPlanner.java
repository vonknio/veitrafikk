package Model;

import org.jetbrains.annotations.NotNull;
import java.util.*;

/**
 * Planner that uses BFS to plan paths for vertices.
 */
class BFSPlanner implements PathPlanner {
    // Maps vehicles to their remaining paths in the grid.
    // At any given moment such a path should connect vehicle's current position
    // and its destination.
    private Map<Vehicle, Stack<Vertex>> paths = new HashMap<>();

    @Override @NotNull
    public Vertex getDestinationForNextTick(Vehicle vehicle, Grid grid) {
        if (TestUtils.compressedEquals(vehicle.getCur(), vehicle.getDest()))
           return vehicle.getCur().getVertexType() == Vertex.VertexType.IN ?
                   vehicle.getCur() : grid.getOther(vehicle.getCur());

        if (!paths.containsKey(vehicle) || paths.get(vehicle).isEmpty())
            planPath(vehicle, grid);

        return paths.get(vehicle).pop();
    }

    /**
     * Find the shortest path from vehicle's current position to its destination.
     * @param vehicle Vehicle that needs a path.
     * @param grid Grid to operate on.
     */
    private void planPath(Vehicle vehicle, Grid grid) {
        Vertex cur = vehicle.getCur();
        Vertex dest = vehicle.getDest();

        if (cur == null || dest == null)
            throw new IllegalStateException();

        Map<Vertex, Vertex> parents = new HashMap<>();
        Set<Vertex> visited = new HashSet<>();
        List<Vertex> queue = new LinkedList<>();

        queue.add(cur);

        while (!queue.isEmpty()) {
            cur = queue.get(0);
            queue.remove(0);
            if (cur == dest) break;

            visited.add(cur);
            for (Vertex neighbour : grid.getNeighbours(cur))
                if (!visited.contains(neighbour)) {
                    queue.add(neighbour);
                    parents.put(neighbour, cur);
                }
        }

        Stack<Vertex> path = new Stack<>();
        cur = dest;
        while (cur != vehicle.getCur()) {
            if (cur == null)
                throw new IllegalStateException("Disconnected graph");
            if (cur.getVertexType() == Vertex.VertexType.IN)
                path.push(cur);
            cur = parents.get(cur);
        }
        paths.put(vehicle, path);
    }
}