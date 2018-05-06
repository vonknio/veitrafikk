package Model;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ModelTest {
    @Test
    public void test1() throws Exception {
        Model model = new Model();
        model.createGrid(5);
        model.addRoad(0, 0, 0, 1);
        model.addRoad(0, 1, 0, 4);
        assertTrue(model.areNeighbours(0, 0, 0, 1));
        assertTrue(model.areNeighbours(0, 3, 0, 4));
        assertFalse(model.areNeighbours(0, 0, 1, 0));
    }

    @Test
    public void test2() throws Exception {
        Model model = new Model();
        model.createGrid(5);
        model.addRoad(0, 0, 0, 1);
        model.addRoad(0, 1, 0, 4);
        model.addSink(0, 0);
        model.addSource(0, 3);
        assertTrue(model.areNeighbours(0, 0, 0, 1));
        assertTrue(model.areNeighbours(0, 3, 0, 4));
        assertFalse(model.areNeighbours(0, 0, 1, 0));

        assertTrue(model.hasRoad(0, 1, 0, 0));
        assertTrue(model.hasRoad(0, 4, 0, 0));
        assertFalse(model.hasRoad(0, 0, 0, 0));
        assertFalse(model.hasRoad(1, 0, 1, 4));
    }

    @Test
    public void test3() throws Exception {
        Model model = new Model();
        model.createGrid(5);
        model.addRoad(0, 0, 0, 1);
        model.addRoad(0, 1, 0, 4);
        model.addSink(0, 0);
        model.addSource(0, 3);

        assertTrue(model.isSink(0, 0));
        assertFalse(model.isSource(0, 0));
        assertFalse(model.isSink(0, 3));
        assertTrue(model.isSource(0, 3));
        assertFalse(model.isSource(0, 4));
        assertFalse(model.isSink(0, 4));

        model.removeVertexClassifiers(0, 0);
        model.removeVertexClassifiers(0, 4);
        assertTrue(model.areNeighbours(0, 0, 0, 1));
        assertTrue(model.areNeighbours(0, 3, 0, 4));
        assertFalse(model.areNeighbours(0, 0, 1, 0));

        assertFalse(model.isSink(0, 0));
        assertFalse(model.isSource(0, 0));
        assertFalse(model.isSink(0, 3));
        assertTrue(model.isSource(0, 3));
        assertFalse(model.isSource(0, 4));
        assertFalse(model.isSink(0, 4));
    }

    @Test
    public void test4() {
        Model model = new Model();
        model.createGrid(4);
        model.addRoad(0, 0, 3, 0);
        model.removeRoad(1, 0, 2, 0);
        assertFalse(model.areNeighbours(1, 0, 2, 0));
        assertTrue(model.areNeighbours(0, 0, 1, 0));
        assertFalse(model.hasRoad(1, 0, 2, 0));
    }

    @Test
    public void test5() {
        Model model = new Model();
        model.createGrid(4);
        model.addRoad(0, 0, 3, 0);
        model.removeRoad(1, 0, 2, 0);
        model.nextTick();
        assertTrue(model.areNeighbours(0, 0, 1, 0));
        model.nextTick();
        assertTrue(model.areNeighbours(0, 0, 1, 0));
    }
}