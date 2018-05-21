package Model;

import org.junit.Test;

import java.util.*;
import static org.junit.Assert.*;

public class GodTest {
   private Model model = new Model();
   { model.createGrid(10); }
   private Grid grid = model.getGrid();
   private GridState gridState = model.getGridState();

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

        assertTrue(TestUtils.areNeighboursCompressed(vehicle.cur, vehicle.dest, grid));
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

        try {
            God.setRandomDestination(vehicles, vertices);
            God.setDestinationForNextTick(vehicles, model);
            fail("Successful operation on a disconnected graph.");
        } catch (IllegalStateException e) {}

        vehicles.remove(lonelyVehicle);
        God.setRandomDestination(vehicles, vertices);

        for (Vehicle vehicle : vehicles) {
            assertTrue(vehicle.next.getVertexType() == Vertex.VertexType.IN);
            assertEquals(grid.getVertexIn(5, 5), vehicle.next);
        }

    }

    @Test
    public void testMoveOneVehicle() {
        model.addRoad(0, 0, 0, 2);
        Vehicle vehicle = new Vehicle(grid.getVertexIn(0, 0));
        grid.getVertexIn(0, 0).setVehicle(vehicle);
        gridState.addVehicle(vehicle);

        grid.getVertexIn(0, 0).getVehicle().dest = grid.getVertexIn(0, 2);
        grid.getVertexIn(0, 0).getVehicle().next = grid.getVertexIn(0, 1);

        assertTrue(grid.getVertexIn(0, 0).hasVehicle() ||
                grid.getVertexOut(0, 0).hasVehicle());
        assertFalse(grid.getVertexIn(0, 1).hasVehicle() ||
                grid.getVertexOut(0, 1).hasVehicle());

        assertTrue(God.processTimetick(model));

        assertFalse(TestUtils.compressedVertexHasVehicle(0, 0, grid));
        assertTrue(TestUtils.compressedVertexHasVehicle(0, 1, grid));
    }

    @Test
    public void testYouHaveReachedYourDestination() {
        model.addSink(0, 1);
        model.addRoad(0, 0, 0, 1);
        Vehicle vehicle = new Vehicle(grid.getVertex(0, 0));
        gridState.addVehicle(vehicle);
        grid.getVertex(0, 0).setVehicle(vehicle);
        vehicle.dest = grid.getVertexOut(0, 1);
        vehicle.next = grid.getVertexIn(0, 1);

        assertTrue(God.processTimetick(model));
        assertFalse(TestUtils.compressedVertexHasVehicle(0, 0, grid));
        assertFalse(TestUtils.compressedVertexHasVehicle(0, 1, grid));
        assertFalse(TestUtils.vehicleIsOnGrid(vehicle, model));
    }

    @Test
    public void testMoveVehicleChainOnAStraightLine() {
        model.addRoad(0, 0, 0, grid.getSize() - 1);
        for (int i = 0; i < grid.getSize() - 1; ++i) {
            Vehicle vehicle = new Vehicle(grid.getVertexIn(0, i));
            grid.getVertexIn(0, i).setVehicle(vehicle);
            gridState.addVehicle(vehicle);
            grid.getVertexIn(0, i).getVehicle().next = grid.getVertexIn(0, i+1);
            assertTrue(grid.getVertexIn(0, i).hasVehicle());
        }

        assertFalse(grid.getVertexIn(0, grid.getSize() - 1).hasVehicle());

        assertTrue(God.processTimetick(model));

        assertFalse(TestUtils.compressedVertexHasVehicle(0, 0, grid));
        for (int i = 1; i < grid.getSize(); ++i) {
            assertTrue(TestUtils.compressedVertexHasVehicle(0, i, grid));
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
        model.addRoad(0, 0, 0, 1);
        model.addRoad(0, 1, 1, 1);
        model.addRoad(1, 1, 1, 2);
        model.addRoad(1, 2, 2, 2);

        Vehicle vehicle1 = new Vehicle(grid.getVertexIn(0, 0));
        Vehicle vehicle2 = new Vehicle(grid.getVertexIn(0, 1));
        Vehicle vehicle3 = new Vehicle(grid.getVertexIn(1, 1));
        Vehicle vehicle4 = new Vehicle(grid.getVertexIn(1, 2));

        grid.getVertexIn(0, 0).setVehicle(vehicle1);
        grid.getVertexIn(0, 1).setVehicle(vehicle2);
        grid.getVertexIn(1, 1).setVehicle(vehicle3);
        grid.getVertexIn(1, 2).setVehicle(vehicle4);

        gridState.addVehicle(vehicle1);
        gridState.addVehicle(vehicle2);
        gridState.addVehicle(vehicle3);
        gridState.addVehicle(vehicle4);

        grid.getVertexIn(0, 0).getVehicle().next =
                grid.getVertexIn(0, 1);
        grid.getVertexIn(0, 1).getVehicle().next =
                grid.getVertexIn(1, 1);
        grid.getVertexIn(1, 1).getVehicle().next =
                grid.getVertexIn(1, 2);
        grid.getVertexIn(1, 2).getVehicle().next =
                grid.getVertexIn(2, 2);

        assertTrue(God.processTimetick(model));

        assertFalse(TestUtils.compressedVertexHasVehicle(0, 0, grid));
        assertTrue(TestUtils.compressedVertexHasVehicle(0, 1, grid));
        assertTrue(TestUtils.compressedVertexHasVehicle(1, 1, grid));
        assertTrue(TestUtils.compressedVertexHasVehicle(1, 2, grid));
        assertTrue(TestUtils.compressedVertexHasVehicle(2, 2, grid));
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
//    @Test
//    public void testVehicleChainCantMove() {
//        grid.addRoad(0, 0, 0, 1);
//        grid.addRoad(0, 1, 1, 1);
//        grid.addRoad(1, 1, 1, 2);
//        grid.addRoad(1, 2, 0, 2);
//        grid.addRoad(0, 2, 0, 1);
//
//        grid.getVertexIn(0, 0).setVehicle(new Vehicle(grid.getVertexIn(0, 0)));
//        grid.getVertexIn(0, 1).setVehicle(new Vehicle(grid.getVertexIn(0, 1)));
//        grid.getVertexIn(1, 1).setVehicle(new Vehicle(grid.getVertexIn(1, 1)));
//        grid.getVertexIn(1, 2).setVehicle(new Vehicle(grid.getVertexIn(1, 2)));
//        grid.getVertexIn(0, 2).setVehicle(new Vehicle(grid.getVertexIn(0, 2)));
//
//        grid.getVertexIn(0, 0).getVehicle().next = grid.getVertexIn(0, 1);
//        grid.getVertexIn(0, 1).getVehicle().next = grid.getVertexIn(1, 1);
//        grid.getVertexIn(1, 1).getVehicle().next = grid.getVertexIn(1, 2);
//        grid.getVertexIn(1, 2).getVehicle().next = grid.getVertexIn(0, 2);
//        grid.getVertexIn(0, 2).getVehicle().next = grid.getVertexIn(0, 1);
//
//        assertFalse(God.processTimetick(grid));
//    }

/*
     V
     |
+<------->+
     |
     V
*/
    @Test
    public void testTwoVehiclesCrossSimultaneously() {
        model.addRoad(4, 5, 6, 5);
        model.addRoad(5, 4, 5, 6);

        Vehicle vehicle1 = new Vehicle(grid.getVertexIn(4, 5));
        Vehicle vehicle2 = new Vehicle(grid.getVertexIn(6, 5));

        grid.getVertexIn(4, 5).setVehicle(vehicle1);
        grid.getVertexIn(6, 5).setVehicle(vehicle2);

        gridState.addVehicle(vehicle1);
        gridState.addVehicle(vehicle2);

        vehicle1.dest = grid.getVertexIn(6, 5);
        vehicle2.dest = grid.getVertexIn(4, 5);

        vehicle1.next = grid.getVertexIn(5, 5);
        vehicle2.next = grid.getVertexIn(5, 5);

        God.processTimetick(model);

        assertFalse(TestUtils.compressedVertexHasVehicle(4, 5, grid));
        assertFalse(TestUtils.compressedVertexHasVehicle(6, 5, grid));

        assertTrue(grid.getVertexIn(5, 5).hasVehicle() &&
                grid.getVertexOut(5, 5).hasVehicle() &&
                grid.getVertexIn(5, 5).getVehicle() != grid.getVertexOut(5, 5).getVehicle());

        vehicle1.next = grid.getVertexIn(5, 4);
        vehicle2.next = grid.getVertexIn(5, 6);

        God.processTimetick(model);

        assertFalse(TestUtils.compressedVertexHasVehicle(5,5, grid));
        assertTrue(TestUtils.compressedVertexHasVehicle(5, 4, grid));
        assertTrue(TestUtils.compressedVertexHasVehicle(5, 6, grid));
    }

    @Test
    public void testSetMode() {
        try {
            God.setMode(null);
            fail("No exception");
        } catch (Exception e) {}

        God.setMode(God.Mode.SHORTEST_PATH);
        assertEquals(God.Mode.SHORTEST_PATH, God.getMode());

        God.setMode(God.Mode.RANDOM);
        assertEquals(God.Mode.RANDOM, God.getMode());
    }

    /*
   D-------+
   |       |
   |       |
   |       |
   V-------+
   */
    @Test
    public void testShortestPath() {
        grid.addRoad(0, 0, 0, 9);
        grid.addRoad(0, 0, 9, 0);
        grid.addRoad(0, 9, 9, 9);
        grid.addRoad(9, 0, 9, 9);

        God.setMode(God.Mode.SHORTEST_PATH);

        Vehicle vehicle = new Vehicle(grid.getVertex(0, 0), grid.getVertexOut(0, 9));
        grid.getVertex(0, 0).setVehicle(vehicle);
        gridState.addVehicle(vehicle);

        God.setDestinationForNextTick(gridState.getVehicles(), model);
        God.processTimetick(model);

        assertTrue(TestUtils.compressedEquals(grid.getVertex(0, 1), vehicle.cur));
    }

    @Test
    public void testShortestPathSeveralVehicles() {
        grid.addRoad(0, 0, 0, 9);
        grid.addRoad(0, 0, 9, 0);
        grid.addRoad(0, 9, 9, 9);
        grid.addRoad(9, 0, 9, 9);

        God.setMode(God.Mode.SHORTEST_PATH);

        Vehicle vehicle = new Vehicle(grid.getVertexIn(0, 0), grid.getVertexOut(0, 9));
        grid.getVertexIn(0, 0).setVehicle(vehicle);
        gridState.addVehicle(vehicle);

        Vehicle vehicle2 = new Vehicle(grid.getVertex(0, 0), grid.getVertexOut(9, 0));
        grid.getVertexOut(0, 0).setVehicle(vehicle2);
        gridState.addVehicle(vehicle2);

        Vehicle vehicle3 = new Vehicle(grid.getVertex(0, 1), grid.getVertexOut(9, 0));
        grid.getVertex(0, 1).setVehicle(vehicle3);
        gridState.addVehicle(vehicle3);

        List<Vehicle> vehicles = new ArrayList<Vehicle>();
        vehicles.add(vehicle);
        vehicles.add(vehicle2);
        vehicles.add(vehicle3);
        God.setDestinationForNextTick(vehicles, model);
        God.processTimetick(model);

        assertTrue(TestUtils.compressedEquals(grid.getVertex(0, 1), vehicle.cur));
        assertTrue(TestUtils.compressedEquals(grid.getVertex(1, 0), vehicle2.cur));
        assertTrue(TestUtils.compressedEquals(grid.getVertex(0, 0), vehicle3.cur));
    }
}
