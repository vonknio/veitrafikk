package Model;

import java.util.ArrayList;

import static java.lang.Integer.max;
import static java.lang.Integer.min;
import static java.lang.Math.abs;

/**
 * Class that provides testing functions. Can be safely used by all classes within the package.
 */
abstract class TestUtils {
    /**
     * @param x1 X coordinate.
     * @param y1 Y coordinate.
     * @return Whether given vertex is a source.
     */
    static boolean isSource(int x1, int y1, Grid grid) {
        return grid.hasVertex(x1, y1) &&
                grid.getVertexOut(x1, y1) instanceof Source;
    }

    /**
     * @param x1 X coordinate.
     * @param y1 Y coordinate.
     * @return Whether given vertex is a sink.
     */
    static boolean isSink(int x1, int y1, Grid grid) {
        return grid.hasVertex(x1, y1) &&
                grid.getVertexIn(x1, y1) instanceof Sink;
    }

    /**
     * @return Grid size.
     */
    static int getGridSize(Grid grid) {
        return grid.getSize();
    }

    /**
     * @param x1 X coordinate.
     * @param y1 Y coordinate.
     * @param grid Grid.
     * @return Whether any of the corresponding in/out vertices of given coordinates has vehicle.
     */
    static boolean compressedVertexHasVehicle(int x1, int y1, Grid grid) {
        return grid.getVertexIn(x1, y1).hasVehicle() ||
                grid.getVertexOut(x1, y1).hasVehicle();
    }

    /**
     * @return Whether vertex or its twin IN/OUT vertex has vehicle.
     */
    static boolean compressedVertexHasVehicle(Vertex vertex, Grid grid) {
        return compressedVertexHasVehicle(vertex.x, vertex.y, grid);
    }

    /**
     * @return Whether given vehicle is currently in given vertex or its twin IN/OUT vertex.
     */
    static boolean vehicleIsInCompressedVertex(Vehicle vehicle, Vertex vertex, Grid grid) {
        if (vertex == null) return false;
        return vertex.getVehicle() == vehicle ||
                grid.getOther(vertex).getVehicle() == vehicle;
    }

    /**
     *
     * @param v1 First vertex.
     * @param v2 Second vertex.
     * @return Whether given vertices have the same coordinates;
     */
    static boolean compressedEquals(Vertex v1, Vertex v2) {
        if (v1 == null || v2 == null)
            return false;
        return v1.x == v2.x && v1.y == v2.y;
    }

    /**
     * @param x1 X coordinate of the first vertex.
     * @param y1 Y coordinate of the first vertex.
     * @param x2 X coordinate of the second vertex.
     * @param y2 X coordinate of the second vertex.
     * @return Whether given vertices are connected by an edge in the grid.
     */
    static boolean areNeighbours(int x1, int y1, int x2, int y2, Grid grid) {
        return grid.getNeighbours(x1, y1).contains(grid.getVertexIn(x2, y2));
    }

    static boolean areNeighbours(Vertex vertex1, Vertex vertex2, Grid grid) {
        return areNeighbours(vertex1.x, vertex1.y, vertex2.x, vertex2.y, grid);
    }

    /**
     * @param x1 X coordinate of the first vertex.
     * @param y1 Y coordinate of the first vertex.
     * @param x2 X coordinate of the second vertex.
     * @param y2 X coordinate of the second vertex.
     * @return Whether grid has an edge between compressed vertices of given coordinates.
     */
    static boolean areNeighboursCompressed(int x1, int y1, int x2, int y2, Grid grid) {
        return grid.getNeighbours(grid.getVertexIn(x1, y1)).contains(grid.getVertexIn(x2, y2)) ||
                grid.getNeighbours(grid.getVertexIn(x1, y1)).contains(grid.getVertexOut(x2, y2)) ||
                grid.getNeighbours(grid.getVertexOut(x1, y1)).contains(grid.getVertexIn(x2, y2)) ||
                grid.getNeighbours(grid.getVertexOut(x1, y1)).contains(grid.getVertexOut(x2, y2));
    }

    static boolean areNeighboursCompressed(Vertex vertex1, Vertex vertex2, Grid grid) {
        return areNeighboursCompressed(vertex1.x, vertex1.y, vertex2.x, vertex2.y, grid);
    }

    /**
     * @param x1 X coordinate of the first vertex.
     * @param y1 Y coordinate of the first vertex.
     * @param x2 X coordinate of the second vertex.
     * @param y2 Y coordinate of the second vertex.
     * @return Whether there is a road connecting given vertices in the grid.
     */
    static boolean hasRoad(int x1, int y1, int x2, int y2, Grid grid) {
        if (x1 == x2 && y1 == y2) return false;
        if (x1 == x2) {
            for (int i = min(y1, y2); i < max(y1, y2); i++) {
                if (!areNeighbours(x1, i, x1, i + 1, grid))
                    return false;
            }
            return true;
        } else if (y1 == y2) {
            for (int i = min(x1, x2); i < max(x1, x2); i++) {
                if (!areNeighbours(i, y1, i + 1, y2, grid))
                    return false;
            }
            return true;
        }
        return false;
    }

    /**
     * @return Whether given vehicle is currently on the grid.
     */
    static boolean vehicleIsOnGrid(Vehicle vehicle, Model model) {
        return model.getGridState().getVehicles().contains(vehicle);
    }

    /**
     * Create new vehicle and add it to the grid, so that the system
     * state remains consistent. If one of the corresponding IN/OUT vertices
     * is occupied, the new vehicle will be assigned to the other one.
     * @param x X coordinate
     * @param y Y coordinate
     * @param model Model to operate on.
     */
    static Vehicle createVehicleOnGrid(int x, int y, Model model) {
        Grid grid = model.getGrid();

        Vertex vertex = grid.getVertexOut(x, y);
        if (vertex.hasVehicle())
            vertex = grid.getVertexIn(x, y);
        if (vertex.hasVehicle())
            throw new IllegalArgumentException("Vertex is already occupied.");

        return createVehicleOnGrid(vertex, model);
    }

    /**
     * Create new vehicle and add it to the grid, so that the system
     * state remains consistent. Note that the vertex is NOT interpreted
     * to be compressed here.
     * @param vertex Vertex to add a vehicle to.
     * @param model Model to operate on.
     */
    static Vehicle createVehicleOnGrid(Vertex vertex, Model model) {
        Grid grid = model.getGrid();
        GridState gridState = model.getGridState();

        if (vertex.hasVehicle())
            throw new IllegalArgumentException("Vertex is already occupied.");

        Vehicle vehicle = new Vehicle(vertex);
        vertex.setVehicle(vehicle);
        gridState.addVehicle(vehicle);
        God.setRandomDestination(vehicle, new ArrayList<>(gridState.getSinks()));
        God.setDestinationForNextTick(vehicle, model);

        return vehicle;
    }

    static void assertSameOrUnitDistance(Vertex v1, Vertex v2) {
        assert(compressedEquals(v1, v2)
                || v1.x == v2.x && abs(v1.y - v2.y) == 1
                || v1.y == v2.y && abs(v1.x - v2.x) == 1);
    }
}
