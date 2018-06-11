package Model;

import org.jetbrains.annotations.NotNull;
import java.util.*;
import java.util.logging.Logger;

/**
 * Planner that uses BFS to plan paths for vertices.
 */
class BFSPlannerStatic implements PathPlanner {
    // Maps vehicles to their remaining paths in the grid.
    // At any given moment such a path should connect vehicle's current position
    // and its destination.
    private Map<Vehicle, Stack<Vertex>> paths = new HashMap<>();
    private Map<Vehicle, Vertex> dests = new HashMap<>();
    private final static Logger logger = Logger.getLogger(BFSPlannerStatic.class.getName());

    @Override @NotNull
    public Vertex getDestinationForNextTick(Vehicle vehicle, Grid grid) {
        if (TestUtils.compressedEquals(vehicle.getCur(), vehicle.getDest()))
           return vehicle.getCur().getVertexType() == Vertex.VertexType.IN ?
                   vehicle.getCur() : grid.getOther(vehicle.getCur());

        if (!dests.containsKey(vehicle) ||
                !TestUtils.compressedEquals(dests.get(vehicle), vehicle.getDest()) ||
                paths.get(vehicle).isEmpty())
            planPath(vehicle, grid);

        Stack<Vertex> path = paths.get(vehicle);
        return paths.get(vehicle).pop();
    }

    @Override
    public boolean isDynamic() { return false; }

    /**
     * Find the shortest path from vehicle's current position to its destination.
     * @param vehicle Vehicle that needs a path.
     * @param grid Grid to operate on.
     */
    protected void planPath(Vehicle vehicle, Grid grid) {
        Stack<Vertex> path = planPath(vehicle, grid, null);
        if (path == null)
            throw new IllegalStateException("Disconnected graph");
        applyPath(vehicle, path);
    }

    protected Stack<Vertex> planPath(Vehicle vehicle, Grid grid, Vertex vertexToAvoid) {
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
            for (Vertex neighbour : grid.getNeighbours(cur)) {
                if (TestUtils.compressedEquals(neighbour, vertexToAvoid)) {
                    continue;
                }

                if (!visited.contains(neighbour)) {
                    queue.add(neighbour);
                    parents.put(neighbour, cur);
                }
            }
        }

        Stack<Vertex> path = new Stack<>();
        cur = dest;
        while (cur != vehicle.getCur()) {
            if (cur == null)
                return null;
            //throw new IllegalStateException("Disconnected graph");
            if (cur.getVertexType() == Vertex.VertexType.IN)
                path.push(cur);
            cur = parents.get(cur);
        }
        return path;
    }

    protected void applyPath(Vehicle vehicle, Stack<Vertex> path) {
        logger.config("Path for vehicle " + vehicle.getId() + " is " + path);
        paths.put(vehicle, path);
        dests.put(vehicle, vehicle.getDest());
    }
}