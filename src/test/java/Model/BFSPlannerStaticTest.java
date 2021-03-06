package Model;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class BFSPlannerStaticTest {
    private BFSPlannerStatic planner  = new BFSPlannerStatic();
    private Grid grid = new Grid(10);

    @Test
    public void testNoPath() {
        grid.addRoad(0, 0, 0, 1);
        Vehicle vehicle = new Vehicle(grid.getVertex(0, 0), grid.getVertexOut(0, 0));
        grid.getVertex(0, 0).setVehicle(vehicle);

        assertTrue(TestUtils.compressedEquals(vehicle.getCur(),
                planner.getDestinationForNextTick(vehicle, grid)));
    }

    @Test
    public void testUnitPath() {
        grid.addRoad(0, 0, 0, 1);
        Vehicle vehicle = new Vehicle(grid.getVertex(0, 0), grid.getVertexOut(0, 1));
        grid.getVertex(0, 0).setVehicle(vehicle);

        assertTrue(TestUtils.compressedEquals(grid.getVertexIn(0, 1),
                planner.getDestinationForNextTick(vehicle, grid)));
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

        Vehicle vehicle = new Vehicle(grid.getVertex(0, 0), grid.getVertexIn(0, 9));
        grid.getVertex(0, 0).setVehicle(vehicle);

        for (int i = 1; i <= 9; ++i) {
            vehicle.setNext(planner.getDestinationForNextTick(vehicle, grid));
            assertTrue(TestUtils.compressedEquals(grid.getVertex(0, i),
                    vehicle.getNext()));
            vehicle.setCur(vehicle.getNext());
        }
    }
}
