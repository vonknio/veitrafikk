package Model;

import org.junit.Test;

import static org.junit.Assert.*;

public class BFSPlannerDynamicTest {
    private BFSPlannerDynamic planner = new BFSPlannerDynamic();
    private Grid grid = new Grid(10);

/*
D-------+
|       |
|       |
|       |
V-------+
*/
    @Test
    public void testDynamicPath() {
        grid.addRoad(0, 0, 0, 9);
        grid.addRoad(0, 0, 9, 0);
        grid.addRoad(0, 9, 9, 9);
        grid.addRoad(9, 0, 9, 9);

        Vehicle vehicle = new Vehicle(grid.getVertex(0, 0), grid.getVertexIn(0, 9));
        grid.getVertex(0, 0).setVehicle(vehicle);

        vehicle.setDest(grid.getVertex(0, 9));
        vehicle.setNextSafe(planner.getDestinationForNextTick(vehicle, grid), grid);
        assertTrue(TestUtils.compressedEquals(vehicle.getNext(),
               grid.getVertex(0, 1)));
        vehicle.setNextNextSafe(planner.getDestinationForNextTick(vehicle, grid), grid);
        assertTrue(TestUtils.compressedEquals(vehicle.getNextNext(), grid.getVertex(0, 2)));

        // no move here
        assertTrue(TestUtils.compressedEquals(planner.getDestinationForNextTick(vehicle, grid),
                grid.getVertex(1, 0)));

    }

/*
D       +
|       |
|       |
|       |
V-------+
*/

    @Test
    public void testNoAlternativePath() {
        grid.addRoad(0, 0, 0, 9);
        grid.addRoad(0, 0, 9, 0);
        grid.addRoad(9, 0, 9, 9);

        Vehicle vehicle = new Vehicle(grid.getVertex(0, 0), grid.getVertexIn(0, 9));
        grid.getVertex(0, 0).setVehicle(vehicle);

        vehicle.setDest(grid.getVertex(0, 9));
        vehicle.setNextSafe(planner.getDestinationForNextTick(vehicle, grid), grid);
        assertTrue(TestUtils.compressedEquals(vehicle.getNext(),
                grid.getVertex(0, 1)));
        vehicle.setNextNextSafe(planner.getDestinationForNextTick(vehicle, grid), grid);
        assertTrue(TestUtils.compressedEquals(vehicle.getNextNext(), grid.getVertex(0, 2)));

        // no move here
        assertTrue(TestUtils.compressedEquals(planner.getDestinationForNextTick(vehicle, grid),
                grid.getVertex(0, 3)));
    }
}