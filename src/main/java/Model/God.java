package Model;

import java.util.*;

public abstract class God {
    private static Mode mode = Mode.RANDOM;
    private static Grid grid;

    /**
     * Generate and handle all events for current timetick, e.g. move vehicles on the grid.
     * @param grid Grid to process.
     */
    public static void processGrid(Grid grid) {
        setGrid(grid);

        Set<Vertex> visited = new HashSet<>();
        for (Vertex vertex : grid.getVertices()) {
            if (vertex.hasVehicle()) {
                moveVehicle(vertex, visited);
            }
            Statistics.process(vertex);
            if (vertex.hasVehicle() && vertex.getVehicle().cur == vertex.getVehicle().dest) {
                // celebrate this fact somehow
                vertex.removeVehicle();
            }
        }
    }

    /**
     * Choose random destinations for given vehicles. If there is more than one available destination,
     * the new destination will be different from the vehicle's current position.
     * @param vehicles Vehicles to assign random destinations to.
     * @param availableDestinations List of destinations to choose from.
     */
    public static void setRandomDestination(Collection<? extends Vehicle> vehicles, List<Vertex> availableDestinations) {
        for (Vehicle vehicle : vehicles) {
            setRandomDestination(vehicle, availableDestinations);
        }
    }

    /**
     * Choose a random destination for a vehicle. If there is more than one available destination,
     * the new destination will be different from the vehicle's current position.
     * @param vehicle Vehicles to assign a random destination to.
     * @param availableDestinations List of destinations to choose from.
     */
    public static void setRandomDestination(Vehicle vehicle, List<Vertex> availableDestinations) {
            do {
            vehicle.dest = availableDestinations.get(
                    (new Random()).nextInt(availableDestinations.size())
            );
            } while (availableDestinations.size() > 1 && vehicle.dest == vehicle.cur);
    }

    /**
     * Try to move a vehicle to its 'next' field. If the field is blocked,
     * try to move the blocking vehicle recursively.
     * @param vertex Vertex containing the vehicle to move.
     * @param visited Vertices already processed in this timetick.
     * @return Whether the vehicle moved.
     */
    private static boolean moveVehicle(Vertex vertex, Set<Vertex> visited) {
        if (visited.contains(vertex)) {
            return false;
        }
        visited.add(vertex);

        Vehicle vehicle = vertex.getVehicle();
        Vertex next = vehicle.next;

        if (!next.hasVehicle())
            visited.add(next);
        else if (!moveVehicle(next, visited))
            return false;

        vehicle.prev = vehicle.cur;
        vehicle.cur = vehicle.next;
        vehicle.next = getDestinationForNextTick(vehicle);

        vertex.removeVehicle();
        vehicle.cur.setVehicle(vehicle);

        return vehicle.cur != vertex;
    }

    /**
     * Return a vertex that can serve as the vehicle's destination for the next timetick.
     * No objects are modified.
     * @param vehicle Vehicle to assign a new destination to.
     * @return Possible destination for next timetick.
     */
    private static Vertex getDestinationForNextTick(Vehicle vehicle) {
        // will this be REFACTORED later? who knows!
        switch (mode) {
            case RANDOM:
                return getRandomDestinationForNextTick(vehicle);
        }
        return null;
    }

    /**
     * Chooses a new destination uniformly at random from the vehicle's neighbours.
     * @param vehicle Vehicle to assign a new destination to.
     * @return Possible destination for next timetick.
     */
    private static Vertex getRandomDestinationForNextTick(Vehicle vehicle) {
        List<Vertex> candidates = grid.getNeighbours(vehicle.cur);
        if (candidates.isEmpty())
            return vehicle.cur;
        return candidates.get((new Random()).nextInt(candidates.size()));
    }

    /**
     * Set a grid that God will operate on until a new grid is passed to Him.
     * Note that all public methods can modify this field.
     * @param grid Grid to set.
     */
    private static void setGrid(Grid grid) {
        God.grid = grid;
    }

    /**
     * Set God to operate in one of available modes. Mode determines path planning algorithms.
     * @see God.Mode
     * @param mode Mode to set.
     */
    public static void setMode(Mode mode) {
        if (mode == null)
            throw new NullPointerException();
        God.mode = mode;
    }

    /**
     * Get current mode.
     * @see God.Mode
     * @return Current mode.
     */
    public static Mode getMode() {
        return mode;
    }

    public enum Mode {
        // Each vehicle's path is a random walk generated on the go.
        RANDOM
    }
}
