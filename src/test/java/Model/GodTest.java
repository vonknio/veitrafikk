package Model;

import org.junit.Test;

import java.util.*;
import static org.junit.Assert.*;

public class GodTest {
    private Grid grid = new Grid(10);

    @Test
    public void testSetRandomDestination() {
        List<Vertex> vertexSet = new ArrayList<>();
        for (int i = 0; i < 10; ++i)
            vertexSet.add(new Vertex(i, i, Vertex.VertexType.ROAD, null));

        Set<Vehicle> vehicleSet = new HashSet<>();
        for (int i = 0; i < 10; ++i)
            vehicleSet.add(new Vehicle(null, null, null));

        God.setRandomDestination(vehicleSet, vertexSet);

        for (Vehicle vehicle : vehicleSet) {
            assertTrue(vertexSet.contains(vehicle.dest));
            assertNotSame(vehicle.dest, vehicle.cur);
        }
    }

    @Test
    public void testMoveOneVehicle() {
        grid.addRoad(0, 0, 0, 1);
        grid.getVertex(0, 0).setVehicle(new Vehicle(
                grid.getVertex(0, 0), null, null)
        );

        grid.getVertex(0, 0).getVehicle().next = grid.getVertex(0, 1);

        assertTrue(grid.getVertex(0, 0).hasVehicle());
        assertFalse(grid.getVertex(0, 1).hasVehicle());

        God.processGrid(grid);

        assertFalse(grid.getVertex(0, 0).hasVehicle());
        assertTrue(grid.getVertex(0, 1).hasVehicle());
    }

    @Test
    public void testMoveVehicleChainOnAStraightLine() {
        for (int i = 0; i < grid.getSize() - 1; ++i) {
            // TODO: move this out of loop
            grid.addRoad(0, i, 0, i+1);
            grid.getVertex(0, i).setVehicle(new Vehicle(
                    grid.getVertex(0, i), null, null)
            );
            grid.getVertex(0, i).getVehicle().next = grid.getVertex(0, i+1);
            assertTrue(grid.getVertex(0, i).hasVehicle());
        }

        assertFalse(grid.getVertex(0, grid.getSize() - 1).hasVehicle());

        God.processGrid(grid);

        assertFalse(grid.getVertex(0, 0).hasVehicle());
        for (int i = 1; i < grid.getSize(); ++i) {
            assertTrue(grid.getVertex(0, i).hasVehicle());
        }
    }

    /*
      +----->
      ^
      |
 ---->+
^
|
+
 */
    @Test
    public void testMoveVehicleChain() {

    }

/*
<-------+
|       ^
|       |
v       |
 ------>+
^
|
|
+
 */
    @Test
    public void testVehicleChainCantMove() {

    }
}
