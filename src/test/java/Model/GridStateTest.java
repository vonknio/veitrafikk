package Model;

import org.junit.Assert;
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
        model.addSource(0, 0, 1, 0);
        model.gridStateTick();
        assertTrue(model.getVehicles().size() == 0);
        model.addSink(5, 6);
        model.gridStateTick();
        assertTrue(model.getVehicles().size() == 1);
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

        model.gridStateTick();
        int i = 1;
        for (Vehicle v : model.getVehicles())
            v.next = model.getVertex(0, i--);
        God.processGrid(model.getGrid());

        model.gridStateTick();
        i = 2;
        for (Vehicle v : model.getVehicles())
            v.next = model.getVertex(0, i--);
        God.processGrid(model.getGrid());

        model.gridStateTick();
        i = 3;
        for (Vehicle v : model.getVehicles())
            v.next = model.getVertex(0, i--);
        God.processGrid(model.getGrid());

        model.gridStateTick();
        i = 4;
        for (Vehicle v : model.getVehicles())
            v.next = model.getVertex(0, i--);
        God.processGrid(model.getGrid());

        model.gridStateTick();
        i = 5;
        for (Vehicle v : model.getVehicles())
            v.next = model.getVertex(0, i--);
        God.processGrid(model.getGrid());

        assertTrue(model.getVehicles().size() == 5);
        model.gridStateTick();

        assertTrue(model.getVehicles().size() == 5);
    }

}