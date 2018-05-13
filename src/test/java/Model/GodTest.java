package Model;

import org.junit.Test;

import java.util.*;
import static org.junit.Assert.*;

public class GodTest {
    private Grid grid = new Grid(10);

    @Test
    public void testSetMode() {
        try {
            God.setMode(null);
            fail("No exception");
        } catch (Exception e) {}
        God.setMode(God.Mode.RANDOM);
        assertEquals(God.Mode.RANDOM, God.getMode());
    }

    @Test
    public void testSetRandomDestinationForOneVehicle() {
        grid.addRoad(5, 5, 5, 6);
        grid.addRoad(5, 5, 5, 4);
        grid.addRoad(5, 5, 6, 5);
        grid.addRoad(5, 5, 4, 5);

        Vehicle vehicle = new Vehicle(grid.getVertexOut(5, 5));
        grid.getVertexOut(5, 5).setVehicle(vehicle);

        God.setRandomDestination(
                vehicle,
                grid.getNeighbours(vehicle.cur)
        );

        assertTrue(grid.getNeighbours(vehicle.cur).contains(vehicle.dest));
    }

    @Test
    public void testSetRandomDestination() {
        List<Vertex> vertices = new ArrayList<>();
        for (int i = 0; i < 10; ++i)
            vertices.add(new Vertex(i, i, Vertex.VertexType.OUT));

        Set<Vehicle> vehicleSet = new HashSet<>();
        for (int i = 0; i < 10; ++i)
            vehicleSet.add(new Vehicle(null, null));

        God.setRandomDestination(vehicleSet, vertices);

        for (Vehicle vehicle : vehicleSet) {
            assertTrue(vertices.contains(vehicle.dest));
            assertNotSame(vehicle.dest, vehicle.cur);
        }
    }

    @Test
    public void testSetDestinationForNextTick() {
        grid.addRoad(5, 5, 5, 6);
        grid.addRoad(5, 5, 5, 4);
        grid.addRoad(5, 5, 6, 5);
        grid.addRoad(5, 5, 4, 5);

        List<Vehicle> vehicles = new ArrayList<>();
        List<Vertex> vertices = new ArrayList<>();
        vertices.add(grid.getVertex(5, 6));
        vertices.add(grid.getVertex(5, 4));
        vertices.add(grid.getVertex(6, 5));
        vertices.add(grid.getVertex(4, 5));

        for(Vertex vertex : vertices) {
            vertex.setVehicle(new Vehicle(vertex));
            vehicles.add(vertex.getVehicle());
        }

        grid.addSource(1, 1, 10, 1);
        Vehicle lonelyVehicle = new Vehicle(grid.getVertex(1, 1));
        grid.getVertex(1, 1).setVehicle(lonelyVehicle);
        vehicles.add(lonelyVehicle);

        God.setDestinationForNextTick(vehicles, grid);

        for (Vehicle vehicle : vehicles) {
            if (vehicle == lonelyVehicle) {
                assertEquals(vehicle.cur, vehicle.next);
            } else
                assertEquals(grid.getVertexIn(5, 5), vehicle.next);
        }

    }

    @Test
    public void testMoveOneVehicle() {
        grid.addRoad(0, 0, 0, 1);
        grid.getVertex(0, 0).setVehicle(new Vehicle(
                grid.getVertex(0, 0), null)
        );

        grid.getVertex(0, 0).getVehicle().next = grid.getVertex(0, 1);

        assertTrue(grid.getVertex(0, 0).hasVehicle());
        assertFalse(grid.getVertex(0, 1).hasVehicle());

        assertTrue(God.processGrid(grid));

        assertFalse(grid.getVertex(0, 0).hasVehicle());
        assertTrue(grid.getVertex(0, 1).hasVehicle());
    }

    @Test
    public void testYouHaveReachedYourDestination() {
        grid.addSink(0, 1);
        grid.addRoad(0, 0, 0, 1);
        Vehicle vehicle = new Vehicle(grid.getVertex(0, 0));
        grid.getVertex(0, 0).setVehicle(vehicle);
        vehicle.dest = grid.getVertex(0, 1);
        vehicle.next = grid.getVertex(0, 1);

        assertTrue(God.processGrid(grid));
        assertFalse(grid.getVertex(0, 0).hasVehicle());
        assertFalse(grid.getVertex(0, 1).hasVehicle());
    }

    @Test
    public void testMoveVehicleChainOnAStraightLine() {
        grid.addRoad(0, 0, 0, grid.getSize() - 1);
        for (int i = 0; i < grid.getSize() - 1; ++i) {
            grid.getVertex(0, i).setVehicle(new Vehicle(
                    grid.getVertex(0, i), null)
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
