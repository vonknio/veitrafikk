package Model;

import java.util.*;

/**
 * Deus ex machina, one and only. Manages the runtime state of the simulation.
 * Is responsible for all runtime events (e.g. moving vehicles) and algorithms.
 */
abstract class God {
    private static Grid grid;
    private static Mode mode = Mode.RANDOM;
    private static PathPlanner pathPlanner = mode.getPlanner();

    /**
     * Generate and handle all events for current timetick, e.g. move vehicles on the grid.
     *
     * @param model Grid to process.
     * @return Whether any vehicles moved.
     */
    static boolean processTimetick(Model model) {
        setGrid(model.getGrid());
        model.getGridState().updateForNextTimetick();

        boolean update = false;
        Set<Vehicle> processed = new HashSet<>();
        List<Vehicle> vehicles = model.getGridState().getVehicles();

        vehicles.sort((x, y) -> {
            if (x.cur.getVertexType() == y.cur.getVertexType())
                return 0;
            if (x.cur.getVertexType() == Vertex.VertexType.IN)
                return 1;
            return -1;
        });

        for (Vehicle vehicle : vehicles) {
            if (moveVehicle(vehicle, processed))
                update = true;
        }

        for (Vertex vertex : grid.getVertices()) {
            Statistics.process(vertex);
            if (vertex.hasVehicle() &&
                    TestUtils.vehicleIsInCompressedVertex(vertex.getVehicle(), vertex.getVehicle().dest, grid)) {
                // celebrate this fact somehow
                model.getGridState().removeVehicle(vertex.getVehicle());
            }
        }
        return update;
    }

    /**
     * Try to move vehicle to its 'next' field. If the field is blocked,
     * try to move the blocking vehicle recursively.
     *
     * @param vehicle   Vehicle to move.
     * @param processed Collection of vehicles already processed in this timetick.
     * @return Whether the vehicle moved.
     */
    private static boolean moveVehicle(Vehicle vehicle, Set<Vehicle> processed) {
        if (vehicle == null || processed.contains(vehicle))
            return false;

        processed.add(vehicle);
        Vertex vertex = vehicle.cur;
        Vertex next = vehicle.next;

        if (vertex.getVertexType() == Vertex.VertexType.IN) {
            swapInAndOut(vertex);
            vertex = grid.getVertexOut(vertex);
        }

        if (!grid.getVertexOut(next).hasVehicle())
            swapInAndOut(next);

        if (next.hasVehicle() && !moveVehicle(next.getVehicle(), processed))
            return false;

        vehicle.prev = vehicle.cur;
        vehicle.cur = vehicle.next;
        vehicle.next = getDestinationForNextTick(vehicle);

        vertex.removeVehicle();
        vehicle.cur.setVehicle(vehicle);

        return true;
    }

    private static void swapInAndOut(Vertex vertex) {
        Vertex other = grid.getOther(vertex);
        Vehicle temp = vertex.getVehicle();
        vertex.setVehicle(other.getVehicle());
        other.setVehicle(temp);

        if (vertex.hasVehicle())
            vertex.getVehicle().cur = vertex;
        if (other.hasVehicle())
            other.getVehicle().cur = other;
    }


    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *  Manage destinations
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /**
     * Choose random destinations for given vehicles. If there is more than one available destination,
     * the new destination will be different from the vehicle's current position.
     *
     * @param vehicles              Vehicles to assign random destinations to.
     * @param availableDestinations List of destinations to choose from.
     */
    static void setRandomDestination(Collection<? extends Vehicle> vehicles, List<Vertex> availableDestinations) {
        for (Vehicle vehicle : vehicles)
            setRandomDestination(vehicle, availableDestinations);
    }

    /**
     * Choose a random destination for a vehicle. If there is more than one available destination,
     * the new destination will be different from the vehicle's current position.
     *
     * @param vehicle               Vehicles to assign a random destination to.
     * @param availableDestinations List of destinations to choose from.
     */
    static void setRandomDestination(Vehicle vehicle, List<Vertex> availableDestinations) {
        do {
            vehicle.dest = availableDestinations.get(
                    (new Random()).nextInt(availableDestinations.size())
            );
        } while (availableDestinations.size() > 1 && vehicle.dest == vehicle.cur);
    }

    /**
     * Sets the 'next' field of given vehicles to a possible destination for next timetick.
     * The type of the 'next' vertex will always be IN.
     * The type of the 'next' vertex will always be IN.
     *  @param vehicles Vehicles to assign next tick destinations to.
     * @param model     Grid to operate on.
     */
    static void setDestinationForNextTick(Collection<? extends Vehicle> vehicles, Model model) {
        setGrid(model.getGrid());

        for (Vehicle vehicle : vehicles)
            vehicle.next = getDestinationForNextTick(vehicle);
    }

    /**
     * Return a vertex that can serve as vehicle's destination for next timetick.
     * No objects are modified. The type of the 'next' vertex will always be IN.
     *
     * @param vehicle Vehicle to assign a new destination to.
     * @return Possible destination for next timetick.
     */
    private static Vertex getDestinationForNextTick(Vehicle vehicle) {
        return pathPlanner.getDestinationForNextTick(vehicle, grid);
    }


    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *  Getters and setters
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /**
     * Set a grid that God will operate on until a new grid is passed to Him.
     * Note that all non-private methods can modify this field.
     *
     * @param grid Grid to set.
     */
    private static void setGrid(Grid grid) {
        God.grid = grid;
    }

    /**
     * Set God to operate in one of available modes. Mode determines path planning algorithms.
     *
     * @param mode Mode to set.
     * @see God.Mode
     */
    static void setMode(Mode mode) {
        if (mode == null)
            throw new IllegalArgumentException();
        God.mode = mode;
        God.pathPlanner = mode.getPlanner();
    }

    /**
     * Get current mode.
     *
     * @see God.Mode
     */
    static Mode getMode() {
        return mode;
    }

    enum Mode {
        // Each vehicle's path is a random walk generated on the go.
        RANDOM {
            PathPlanner getPlanner() {
                return new RandomPlanner();
            }
        },
        // Each vehicle's path is optimal but fixed.
        SHORTEST_PATH {
            PathPlanner getPlanner() {
                return new BFSPlanner();
            }
        };

        abstract PathPlanner getPlanner();
    }
}

