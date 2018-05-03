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

        assertTrue(God.processGrid(grid));

        assertFalse(grid.getVertex(0, 0).hasVehicle());
        assertTrue(grid.getVertex(0, 1).hasVehicle());
    }

    @Test
    public void testMoveVehicleChainOnAStraightLine() {
        grid.addRoad(0, 0, 0, grid.getSize() - 1);
        for (int i = 0; i < grid.getSize() - 1; ++i) {
            grid.getVertex(0, i).setVehicle(new Vehicle(
                    grid.getVertex(0, i), null, null)
            );
            grid.getVertex(0, i).getVehicle().next = grid.getVertex(0, i+1);
            assertTrue(grid.getVertex(0, i).hasVehicle());
        }

        assertFalse(grid.getVertex(0, grid.getSize() - 1).hasVehicle());

        assertTrue(God.processGrid(grid));

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
        grid.addRoad(0, 0, 0, 1);
        grid.addRoad(0, 1, 1, 1);
        grid.addRoad(1, 1, 1, 2);
        grid.addRoad(1, 2, 2, 2);

        grid.getVertex(0, 0).setVehicle(new Vehicle(grid.getVertex(0, 0)));
        grid.getVertex(0, 1).setVehicle(new Vehicle(grid.getVertex(0, 1)));
        grid.getVertex(1, 1).setVehicle(new Vehicle(grid.getVertex(1, 1)));
        grid.getVertex(1, 2).setVehicle(new Vehicle(grid.getVertex(1, 2)));

        grid.getVertex(0, 0).getVehicle().next = grid.getVertex(0, 1);
        grid.getVertex(0, 1).getVehicle().next = grid.getVertex(1, 1);
        grid.getVertex(1, 1).getVehicle().next = grid.getVertex(1, 2);
        grid.getVertex(1, 2).getVehicle().next = grid.getVertex(2, 2);

        assertTrue(God.processGrid(grid));

        assertFalse(grid.getVertex(0, 0).hasVehicle());
        assertTrue(grid.getVertex(0, 1).hasVehicle());
        assertTrue(grid.getVertex(1, 1).hasVehicle());
        assertTrue(grid.getVertex(1, 2).hasVehicle());
        assertTrue(grid.getVertex(2, 2).hasVehicle());
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
        grid.addRoad(0, 0, 0, 1);
        grid.addRoad(0, 1, 1, 1);
        grid.addRoad(1, 1, 1, 2);
        grid.addRoad(1, 2, 0, 2);
        grid.addRoad(0, 2, 0, 1);

        grid.getVertex(0, 0).setVehicle(new Vehicle(grid.getVertex(0, 0)));
        grid.getVertex(0, 1).setVehicle(new Vehicle(grid.getVertex(0, 1)));
        grid.getVertex(1, 1).setVehicle(new Vehicle(grid.getVertex(1, 1)));
        grid.getVertex(1, 2).setVehicle(new Vehicle(grid.getVertex(1, 2)));
        grid.getVertex(0, 2).setVehicle(new Vehicle(grid.getVertex(0, 2)));

        grid.getVertex(0, 0).getVehicle().next = grid.getVertex(0, 1);
        grid.getVertex(0, 1).getVehicle().next = grid.getVertex(1, 1);
        grid.getVertex(1, 1).getVehicle().next = grid.getVertex(1, 2);
        grid.getVertex(1, 2).getVehicle().next = grid.getVertex(0, 2);
        grid.getVertex(0, 2).getVehicle().next = grid.getVertex(0, 1);

        assertFalse(God.processGrid(grid));
    }
}
