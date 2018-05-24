package Model;

import org.junit.Test;

import java.util.*;
import static org.junit.Assert.*;

public class GodTest {
   private Model model = new Model();
   { model.createGrid(10); }
   private Grid grid = model.getGrid();
   private GridState gridState = model.getGridState();

   private void turnEveryVertexIntoSink() {
       for (Vertex vertex : grid.getVertices()) {
         model.addSink(vertex.x, vertex.y);
       }
   }

    @Test
    public void testSetRandomDestinationForOneVehicle() {
        grid.addRoad(5, 5, 5, 6);
        grid.addRoad(5, 5, 5, 4);
        grid.addRoad(5, 5, 6, 5);
        grid.addRoad(5, 5, 4, 5);

       // Vehicle vehicle = new Vehicle(grid.getVertexOut(5, 5));
       // grid.getVertexOut(5, 5).setVehicle(vehicle);
        turnEveryVertexIntoSink();
        Vehicle vehicle = TestUtils.createVehicleOnGrid(5, 5, model);

        God.setRandomDestination(
                vehicle,
                grid.getNeighbours(vehicle.getCur())
        );

        assertTrue(TestUtils.areNeighboursCompressed(vehicle.getCur(), vehicle.getDest(), grid));
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
            assertTrue(vertices.contains(vehicle.getDest()));
            assertNotSame(vehicle.getDest(), vehicle.getCur());
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

        turnEveryVertexIntoSink();

        for(Vertex vertex : vertices)
            TestUtils.createVehicleOnGrid(vertex, model);

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
            assertSame(vehicle.getNext().getVertexType(), Vertex.VertexType.IN);
            assertEquals(grid.getVertexIn(5, 5), vehicle.getNext());
        }

    }

    @Test
    public void testMoveOneVehicle() {
        model.addRoad(0, 0, 0, 2);
        turnEveryVertexIntoSink();
        Vehicle vehicle = TestUtils.createVehicleOnGrid(0, 0, model);
        vehicle.setDest(grid.getVertexIn(0, 2));

        assertTrue(TestUtils.compressedVertexHasVehicle(0, 0, grid));
        assertFalse(TestUtils.compressedVertexHasVehicle(0, 1, grid));

        assertTrue(God.processTimetick(model));

        assertFalse(TestUtils.compressedVertexHasVehicle(0, 0, grid));
        assertTrue(TestUtils.compressedVertexHasVehicle(0, 1, grid));
    }

    @Test
    public void testYouHaveReachedYourDestination() {
        model.addSink(0, 1);
        model.addRoad(0, 0, 0, 1);
        Vehicle vehicle = TestUtils.createVehicleOnGrid(0, 0, model);

        assertTrue(God.processTimetick(model));
        assertFalse(TestUtils.compressedVertexHasVehicle(0, 0, grid));
        assertFalse(TestUtils.compressedVertexHasVehicle(0, 1, grid));
        assertFalse(TestUtils.vehicleIsOnGrid(vehicle, model));
    }

    @Test
    public void testMoveVehicleChainOnAStraightLine() {
        model.addRoad(0, 0, 0, grid.getSize() - 1);
        turnEveryVertexIntoSink();
        for (int i = 0; i < grid.getSize() - 1; ++i) {
            Vehicle vehicle = TestUtils.createVehicleOnGrid(0, i, model);
            vehicle.setNextSafe(grid.getVertexIn(0, i+1), grid);
            vehicle.setDest(grid.getVertexIn(0, (i + 9)%9));
            assertTrue(TestUtils.compressedVertexHasVehicle(0, i, grid));
        }

        assertFalse(TestUtils.compressedVertexHasVehicle(0, grid.getSize() - 1, grid));

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
        turnEveryVertexIntoSink();

        Vehicle vehicle1 = TestUtils.createVehicleOnGrid(0, 0, model);
        Vehicle vehicle2 = TestUtils.createVehicleOnGrid(1, 1, model);
        Vehicle vehicle3 = TestUtils.createVehicleOnGrid(1, 2, model);
        Vehicle vehicle4 = TestUtils.createVehicleOnGrid(2, 2, model);

        vehicle1.setNextSafe(grid.getVertex(0, 1), grid);
        vehicle2.setNextSafe(grid.getVertex(1, 1), grid);
        vehicle3.setNextSafe(grid.getVertex(1, 2), grid);
        vehicle4.setNextSafe(grid.getVertex(2, 2), grid);

        vehicle1.setDest(grid.getVertex(2, 2));
        vehicle2.setDest(grid.getVertex(2, 2));
        vehicle3.setDest(grid.getVertex(0, 0));
        vehicle4.setDest(grid.getVertex(0, 0));

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
        turnEveryVertexIntoSink();

        Vehicle vehicle1 = TestUtils.createVehicleOnGrid(4, 5, model);
        Vehicle vehicle2 = TestUtils.createVehicleOnGrid(6, 5, model);


        vehicle1.setDest(grid.getVertex(6, 5));
        vehicle2.setDest(grid.getVertex(4, 5));

        vehicle1.setNextSafe(grid.getVertexIn(5, 5) , grid);
        vehicle2.setNextSafe(grid.getVertexIn(5, 5), grid);

        God.processTimetick(model);

        assertFalse(TestUtils.compressedVertexHasVehicle(4, 5, grid));
        assertFalse(TestUtils.compressedVertexHasVehicle(6, 5, grid));

        assertTrue(grid.getVertexIn(5, 5).hasVehicle() &&
                grid.getVertexOut(5, 5).hasVehicle() &&
                grid.getVertexIn(5, 5).getVehicle() != grid.getVertexOut(5, 5).getVehicle());

        vehicle1.setNextSafe(grid.getVertexIn(5, 4), grid);
        vehicle2.setNextSafe(grid.getVertexIn(5, 6), grid);

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
        turnEveryVertexIntoSink();

        God.setMode(God.Mode.SHORTEST_PATH);

        Vehicle vehicle = TestUtils.createVehicleOnGrid(0, 0, model);
        vehicle.setDest(grid.getVertexOut(0, 9));

        God.setDestinationForNextTick(gridState.getVehicles(), model);
        God.processTimetick(model);

        assertTrue(TestUtils.compressedEquals(grid.getVertex(0, 1), vehicle.getCur()));
    }

    @Test
    public void testShortestPathSeveralVehicles() {
        grid.addRoad(0, 0, 0, 9);
        grid.addRoad(0, 0, 9, 0);
        grid.addRoad(0, 9, 9, 9);
        grid.addRoad(9, 0, 9, 9);
        turnEveryVertexIntoSink();

        God.setMode(God.Mode.SHORTEST_PATH);

        Vehicle vehicle = TestUtils.createVehicleOnGrid(0, 0, model);
        vehicle.setDest(grid.getVertex(0, 9));

        Vehicle vehicle2 = TestUtils.createVehicleOnGrid(0, 0, model);
        vehicle2.setDest(grid.getVertex(9, 0));

        Vehicle vehicle3 = TestUtils.createVehicleOnGrid(0, 1, model);
        vehicle3.setDest(grid.getVertex(9, 0));

        List<Vehicle> vehicles = new ArrayList<>();
        vehicles.add(vehicle);
        vehicles.add(vehicle2);
        vehicles.add(vehicle3);
        God.setDestinationForNextTick(vehicles, model);
        God.processTimetick(model);

        assertTrue(TestUtils.compressedEquals(grid.getVertex(0, 1), vehicle.getCur()));
        assertTrue(TestUtils.compressedEquals(grid.getVertex(1, 0), vehicle2.getCur()));
        assertTrue(TestUtils.compressedEquals(grid.getVertex(0, 0), vehicle3.getCur()));
    }
}
