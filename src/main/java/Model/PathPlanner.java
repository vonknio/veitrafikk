package Model;

import org.jetbrains.annotations.NotNull;

/**
 * Class responsible for planning vehicles' routes on the Grid.
 */
interface PathPlanner {
    /**
     * Return a vertex that can serve as vehicle's destination for next timetick.
     * No objects are modified. The type of the 'next' vertex will always be IN.
     *
     * @param vehicle Vehicle to assign a new destination to.
     * @param grid Grid to operate on.
     * @return Possible destination for next timetick.
     */
    @NotNull
    Vertex getDestinationForNextTick(Vehicle vehicle, Grid grid);
}
