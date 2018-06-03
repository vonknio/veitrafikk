package Model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class StatisticsTest {
    @Test
    public void testSimple() {
        Model model = new Model();
        model.changeMode("SHORTEST_PATH");
        model.createGrid(4);
        model.addRoad(0, 0, 0, 3);
        model.addSource(0, 0, 2, 1);
        model.addSink(0, 3);
        model.nextTick();
        assertEquals(1, model.getStatistics().averagePathLength(), 0.1);
        assertEquals(2, model.getStatistics().verticesVisited());
        model.nextTick();
        assertEquals(1.5, model.getStatistics().averagePathLength(), 0.1);
        assertEquals(3, model.getStatistics().verticesVisited());
        model.nextTick();
        assertEquals(4, model.getStatistics().verticesVisited());
        assertEquals(1, model.getStatistics().averageVelocity(), 0.1);
        model.nextTick();
        model.nextTick();
        assertEquals(3, model.getStatistics().averagePathLength(), 0.01);
    }

    @Test
    public void testComplex() {
        //TODO

    }
}
