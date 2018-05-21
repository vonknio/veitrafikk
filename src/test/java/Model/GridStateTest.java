package Model;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class GridStateTest {

    /* Tests will be using a simple grid:
        |-----
        |
        |
        |
        |
        |
        |-----
     */

    Model model;

    @Before
    public void prepareGrid(){
        model = new Model();
        model.createGrid(10);
        model.addRoad(0,0, 0, 6);
        model.addRoad(0,0, 5, 0);
        model.addRoad(0,6, 5, 6);
    }

    /*
        s-----
        |
        |
        |
        |
        |
        |----t
     */

    @Test
    public void wrongAttemptsTest(){
        model.addSource(0, 0, 1, 1);
        try {
            model.addSource(0, 0, 1, 0);
            fail("No exception");
        } catch (IllegalArgumentException e) {}
        model.getGridState().updateForNextTimetick();
        assertEquals(0, model.getVehicles().size());
        model.addSink(5, 6);
        model.getGridState().updateForNextTimetick();
        assertEquals(1, model.getVehicles().size());
    }

    /*
        s-----     s-----
        |          v
        |          v
        |      ->  v
        |          v
        |          v
        |----t     |----t
     */

    @Test
    public void sourceCapacityTest(){
        model.addSink(5, 6);
        model.addSource(0, 0, 5, 1);

        model.getGridState().updateForNextTimetick();
        int i = 1;
        for (Vehicle v : model.getVehicles())
             v.setNextSafe(model.getGrid().getVertexIn(0, i--), model.getGrid());
        God.processTimetick(model);

        model.getGridState().updateForNextTimetick();
        i = 2;
        for (Vehicle v : model.getVehicles())
            v.setNextSafe(model.getGrid().getVertexIn(0, i--), model.getGrid());
        God.processTimetick(model);

        model.getGridState().updateForNextTimetick();
        i = 3;
        for (Vehicle v : model.getVehicles())
            v.setNextSafe(model.getGrid().getVertexIn(0, i--), model.getGrid());
        God.processTimetick(model);

        model.getGridState().updateForNextTimetick();
        i = 4;
        for (Vehicle v : model.getVehicles())
            v.setNextSafe(model.getGrid().getVertexIn(0, i--), model.getGrid());
        God.processTimetick(model);

        model.getGridState().updateForNextTimetick();
        i = 5;
        for (Vehicle v : model.getVehicles())
            v.setNextSafe(model.getGrid().getVertexIn(0, i--), model.getGrid());
        God.processTimetick(model);

        assertEquals(5, model.getVehicles().size());
        model.getGridState().updateForNextTimetick();

        assertEquals(5, model.getVehicles().size());
    }

}