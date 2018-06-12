package Model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class StatisticsTest {
    @Test
    public void testSimple() {
        Model model = new Model();
        model.changeMode("SHORTEST_PATH_STATIC");
        model.createGrid(4);
        model.addRoad(0, 0, 0, 3);
        model.addSource(0, 0, 2, 1);
        model.addSink(0, 3);
        model.nextTick();
        assertEquals(1, model.getStatistics().averagePathLength(), 0.1);
        assertEquals(2, model.getStatistics().verticesVisited());
        assertEquals(1, model.getStatistics().averageTicksAlive(), 0.1);
        model.nextTick();
        assertEquals(1.5, model.getStatistics().averagePathLength(), 0.1);
        assertEquals(3, model.getStatistics().verticesVisited());
        assertEquals(1.5, model.getStatistics().averageTicksAlive(), 0.1);
        model.nextTick();
        assertEquals(4, model.getStatistics().verticesVisited());
        assertEquals(1, model.getStatistics().averageVelocity(), 0.1);
        model.nextTick();
        model.nextTick();
        assertEquals(3, model.getStatistics().averagePathLength(), 0.01);
        assertEquals(3, model.getStatistics().averageTicksAlive(), 0.1);

    }

    @Test
    public void testSimple2() {
        Model model = new Model();
        model.changeMode("SHORTEST_PATH_STATIC");
        model.createGrid(3);
        model.addRoad(0, 0, 0, 2);
        model.addRoad(0, 1, 1, 1);
        model.addSource(0, 0, 1, 1);
        model.addSource(0, 2, 1, 1);
        model.addSink(1, 1);

        model.nextTick();
        model.nextTick();
        model.nextTick();
        model.nextTick();
        model.nextTick();
        model.nextTick();

        assertEquals(4, model.verticesVisited());
        assertEquals(2, model.averagePathLength(), 0.1);
        assertEquals(2.5, model.averageTicksAlive(), 0.1);
        assertEquals(.83, model.averageVelocity(), 0.1);

    }

    @Test
    public void test3() {
        Model model = new Model();
        model.changeMode("SHORTEST_PATH_STATIC");
        model.createGrid(10);
        model.addRoad(0, 0, 0, 9);
        model.addSink(0, 9);
        model.addSource(0, 0, 5, 1);
        model.nextTick();
        model.nextTick();
        assertEquals(2, model.totalVehicles());
        assertEquals(2, model.maxPathLength(), 0.1);
        assertEquals(1.5, model.averagePathLength(), 0.1);
        for (int i = 0; i < 14; i++) {
            model.nextTick();
        }
        assertEquals(5, model.totalVehicles());
        assertEquals(9, model.maxPathLength(), 0.1);
        assertEquals(9, model.averagePathLength(), 0.1);
        assertEquals(1, model.maxVelocity(), 0.1);
        assertEquals(1, model.averageVelocity(), 0.1);
    }
}
