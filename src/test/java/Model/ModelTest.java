package Model;

import org.junit.Test;

import java.util.Arrays;

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
        model.addSource(0, 3, 10, 0.5f);
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
        model.addSource(0, 3, 10, .4f);

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

    @Test
    public void testGetEnclosingRoad() {
        Model model = new Model();
        model.createGrid(10);
        model.addRoad(0, 0, 0, 9);
        model.addRoad(9, 9, 0, 9);
        assertTrue(model.hasRoad(0, 0, 0, 9));

        int[] r1 = model.getEnclosingRoad(0, 5, 0, 4);
        model.removeRoad(r1[0], r1[1], r1[2], r1[3]);

        r1 = model.getEnclosingRoad(9, 9, 8, 9);
        model.removeRoad(r1[0], r1[1], r1[2], r1[3]);

        assertFalse(model.hasRoad(0, 0, 0, 9));
        assertFalse(model.hasRoad(0, 9, 9, 9));
        for (int i = 0; i < 9; i++) {
            assertFalse(model.hasRoad(0, i, 0, i + 1));
            assertFalse(model.hasRoad(9, i, 9, i + 1));
        }

    }

    @Test
    public void testEnclosingRoadWithCrossroad() {
        Model model = new Model();
        model.createGrid(10);
        model.addRoad(0, 0, 0, 9);
        model.addRoad(0, 5, 9, 5);

        int[] r1 = model.getEnclosingRoad(0, 0, 0, 2);
        model.removeRoad(r1[0], r1[1], r1[2], r1[3]);
        assertFalse(model.hasRoad(0, 0, 0, 9));
        assertFalse(model.hasRoad(0, 0, 0, 5));
        assertTrue(model.hasRoad(0, 5, 0, 9));
    }

    @Test
    public void hasRoadTestWithInvalidArgs() {
        Model model = new Model();
        model.createGrid(10);
        assertFalse(model.hasRoad(0, 0, 0, 0));
        assertFalse(model.hasRoad(0, 1, 5, 6));
        assertFalse(model.hasRoad(-1, -3, 0, 5));
        assertFalse(model.hasRoad(666, 666, 3, 4));
    }
}