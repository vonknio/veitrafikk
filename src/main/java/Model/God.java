package Model;

import java.util.*;
import java.util.logging.Logger;

/**
 * Deus ex machina, one and only. Manages the runtime state of the simulation.
 * Is responsible for all runtime events (e.g. moving vehicles) and algorithms.
 */
abstract class God {
    private static Grid grid;
    private static Mode mode = Mode.SHORTEST_PATH_STATIC;
    private static PathPlanner pathPlanner = mode.getPlanner();
    private final static Logger logger = Logger.getLogger(God.class.getName());

    /**
     * Generate and handle all events for current timetick, e.g. move vehicles on the grid.
     *
     * @param model Grid to process.
     * @return Whether any vehicles moved.
     */
    static boolean processTimetick(Model model) {
        logger.config("\n\n\n-----------------------------");
        setGrid(model.getGrid());
        model.getGridState().updateForNextTimetick();

        boolean update = false;
        Set<Vehicle> processed = new HashSet<>();
        List<Vehicle> vehicles = model.getGridState().getVehiclesShuffled();

        for (Vehicle vehicle : vehicles) {
            if (processed.contains(vehicle));
            else if (moveVehicle(vehicle, processed))
                update = true;
            else {
                logger.config("Vehicle " + vehicle.getId() +
                        " in vertex " + vehicle.getCur() + vehicle.getCur().getVertexType() +
                        " couldn't move to " + vehicle.getNext() + vehicle.getNext().getVertexType());

                if (mode.getPlanner().isDynamic())
                    vehicle.setNextNext(getDestinationForNextTick(vehicle));
            }
            Statistics.process(vehicle);

        }

        for (Vertex vertex : grid.getVertices()) {
            Statistics.process(vertex);
            if (vertex.hasVehicle() &&
                    TestUtils.vehicleIsInCompressedVertex(vertex.getVehicle(), vertex.getVehicle().getDest(), grid)) {
                // celebrate this fact somehow
                logger.config("Vehicle " + vertex.getVehicle().getId() + " has reached its destination.");
                Statistics.processRemoved(vertex.getVehicle());
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
        if (vehicle == null) return true;
        if (processed.contains(vehicle))
            return false;

        processed.add(vehicle);
        Vertex vertex = vehicle.getCur();
        Vertex next = vehicle.getNext();
        Vertex nextNext = vehicle.getNextNext();

        if (next == null) {
            vehicle.setNext(getDestinationForNextTick(vehicle));
            next = vehicle.getNext();
        }

        if (nextNext == null) {
            vehicle.setNextNext(getDestinationForNextTick(vehicle));
            nextNext = vehicle.getNextNext();
        }

        if (vertex.getVertexType() == Vertex.VertexType.IN) {
            swapInAndOut(vertex);
        }

        if (!grid.getVertexOut(next).hasVehicle())
            swapInAndOut(next);

        if (!moveVehicle(grid.getVertexOut(next).getVehicle(), processed)
                && !moveVehicle(next.getVehicle(), processed))
            return false;

        if (next.hasVehicle())
            swapInAndOut(next);

        if (next.hasVehicle() || (grid.getVertexOut(next).hasVehicle() && TestUtils.compressedEquals(
                grid.getVertexOut(next).getVehicle().getNext(),
                nextNext)))
            return false;

        if (next.hasVehicle() && !moveVehicle(next.getVehicle(), processed)) {
            vehicle.setPrev(vehicle.getCur());
            return false;
        }

        if (TestUtils.compressedVertexHasVehicle(next, grid)) {
            Vehicle other = next.hasVehicle() ? next.getVehicle() : grid.getOther(next).getVehicle();
            Vertex otherPrev = other.getPrev();
            Vertex otherNext= other.getNext();
            // only allow two vehicles travelling from or in opposite directions enter a crossroad
            if (otherPrev.x != vehicle.getCur().x && otherPrev.y != vehicle.getCur().y &&
                    !(otherNext.x == next.x && Math.abs(otherNext.y - next.y) >= 1) &&
                    !(otherNext.y == next.y && Math.abs(otherNext.x - next.x) >= 1)
               )
                return false;
        }

        // one last sanity check
        if (next.hasVehicle()) return false;

        vehicle.getCur().removeVehicle();

        vehicle.setPrev(vehicle.getCur());
        vehicle.setCur(vehicle.getNext());
        vehicle.setNext(vehicle.getNextNext());
        vehicle.setNextNext(getDestinationForNextTick(vehicle));

        vehicle.getCur().setVehicle(vehicle);

        logger.config("Vehicle " + vehicle.getId() + " moved from "
                + vehicle.getPrev()+vehicle.getPrev().getVertexType()
                + " to " + vehicle.getCur()+vehicle.getCur().getVertexType());
        return true;
    }


    /**
     * Swaps vehicles of given vertex and its corresponding IN/OUT vertex.
     */
    private static void swapInAndOut(Vertex vertex) {
        Vertex other = grid.getOther(vertex);
        Vehicle temp = vertex.getVehicle();
        vertex.setVehicle(other.getVehicle());
        other.setVehicle(temp);

        if (vertex.hasVehicle())
            vertex.getVehicle().setCur(vertex);
        if (other.hasVehicle())
            other.getVehicle().setCur(other);
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
        if (availableDestinations.isEmpty())
            throw new IllegalStateException();

        do {
            vehicle.setDest(availableDestinations.get(
                    (new Random()).nextInt(availableDestinations.size())
            ));
        } while (availableDestinations.size() > 1 &&
                TestUtils.compressedEquals(vehicle.getDest(), vehicle.getCur()));
    }

    /**
     * Sets the 'next' field of given vehicles to a possible destination for next timetick.
     * The type of the 'next' vertex will always be IN.
     *  @param vehicles Vehicles to assign next tick destinations to.
     * @param model     Grid to operate on.
     */
    static void setDestinationForNextTick(Collection<? extends Vehicle> vehicles, Model model) {
        setGrid(model.getGrid());

        for (Vehicle vehicle : vehicles)
            setDestinationForNextTick(vehicle, model);
    }

    /**
     * Sets the 'next' field of given vehicle to a possible destination for next timetick.
     * The type of the 'next' vertex will always be IN.
     * @param vehicle Vehicle to assign next tick destination to.
     * @param model     Grid to operate on.
     */
    static void setDestinationForNextTick(Vehicle vehicle, Model model) {
        setGrid(model.getGrid());

        vehicle.setNext(getDestinationForNextTick(vehicle));
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
    private static void setGrid(Grid grid) { God.grid = grid; }

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
    static Mode getMode() { return mode; }

    enum Mode {
        // Each vehicle's path is a ranfdom walk generated on the go. If a vehicle gets stuck,
        // the path won't be updated.
        RANDOM_STATIC {
            PathPlanner getPlanner() { return new RandomPlannerStatic(); }
        },
        // Each vehicle's path is a random walk generated on the go. If a vehicle gets stuck,
        // the path can be updated.
        RANDOM_DYNAMIC {
            PathPlanner getPlanner() { return new RandomPlannerDynamic(); }
        },
        // Each vehicle's path is optimal but fixed.
        SHORTEST_PATH_STATIC {
            PathPlanner getPlanner() { return new BFSPlannerStatic(); }
        },
        // Each vehicle's path is optimal. If a vehicle gets stuck, the planner will suggest another
        // path that doesn't go through vehicle's current nextNext Vertex.
        SHORTEST_PATH_DYNAMIC {
            PathPlanner getPlanner() { return new BFSPlannerDynamic(); }
        };

        abstract PathPlanner getPlanner();
    }
}
