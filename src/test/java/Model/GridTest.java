package Model;

import org.junit.Test;

import static org.junit.Assert.*;

public class GridTest {
    @Test
    public void test0() throws Exception {
        Grid grid = new Grid(4);
        assertEquals(4, grid.getSize());
    }

    @Test
    public void test1() {
        Grid grid = new Grid(4);
        grid.addRoad(0, 0, 1, 0);
        assertEquals(2, grid.getVertices().size());
        grid.addRoad(1, 0, 2, 0);
        assertEquals(3, grid.getVertices().size());
        grid.addRoad(2, 0, 1, 0);
        assertEquals(3, grid.getVertices().size());
    }

    @Test
    public void test2() {
        Grid grid = new Grid(3);
        grid.addRoad(0, 0, 1, 0);
        grid.addRoad(1, 0, 2, 0);
        assertNotNull(grid.getVertex(1, 0));
        assertNotNull(grid.getVertex(0, 0));
        assertNotNull(grid.getVertex(2, 0));
        assertNull(grid.getVertex(0, 1));
    }

    @Test
    public void test3() {
        Grid grid = new Grid(3);
        grid.addRoad(0, 0, 1, 0);
        grid.addRoad(1, 0, 2, 0);
        grid.removeRoad(1, 0, 2, 0);
        assertNotNull(grid.getVertex(1, 0));
        assertNotNull(grid.getVertex(0, 0));
        assertNull(grid.getVertex(2, 0));
    }

    @Test
    public void test4() {
        Grid grid = new Grid(3);
        grid.addRoad(0, 0, 0, 1);
        grid.addRoad(0, 1, 0, 2);
        grid.removeRoad(0, 1, 0, 2);
        assertNotNull(grid.getVertex(0, 1));
        assertNotNull(grid.getVertex(0, 0));
        assertNull(grid.getVertex(0, 2));
    }

    @Test
    public void test5() {
        Grid grid = new Grid(3);
        grid.addSink(0, 0);
        assertEquals(Vertex.VertexType.SINK, grid.getVertex(0, 0).getVertexType());
        grid.addRoad(0, 0, 0, 1);
        assertEquals(Vertex.VertexType.SINK, grid.getVertex(0, 0).getVertexType());
    }

    @Test
    public void test6() {
        Grid grid = new Grid(3);
        grid.addSource(0, 0);
        assertEquals(Vertex.VertexType.SOURCE, grid.getVertex(0, 0).getVertexType());
        grid.addRoad(0, 0, 0, 1);
        assertEquals(Vertex.VertexType.SOURCE, grid.getVertex(0, 0).getVertexType());
    }

    @Test()
    public void test7() {
        Grid grid = new Grid(4);
        grid.addRoad(0, 0, 1, 0);
        grid.addRoad(1, 0, 2, 0);
        assertEquals(2, grid.getNeighbours(1, 0).size());
        assertEquals(1, grid.getNeighbours(0, 0).size());
        assertEquals(grid.getNeighbours(0, 0), grid.getNeighbours(grid.getVertex(0, 0)));
    }

    @Test
    public void test8() {
        Grid grid = new Grid(5);
        grid.addRoad(1, 1, 1, 0);
        grid.addRoad(2, 1, 1, 1);
        grid.addRoad(1, 1, 1, 2);
        grid.addRoad(1, 1, 0, 1);
        assertTrue(grid.getNeighbours(1, 1).contains(grid.getVertex(1, 0)));
        assertTrue(grid.getNeighbours(1, 1).contains(grid.getVertex(0, 1)));
        assertTrue(grid.getNeighbours(1, 1).contains(grid.getVertex(1, 2)));
        assertTrue(grid.getNeighbours(1, 1).contains(grid.getVertex(2, 1)));

    }
    @Test
    public void test9() {
        Grid grid = new Grid(5);
        grid.addRoad(1, 1, 1, 0);
        grid.addRoad(2, 1, 1, 1);
        grid.addRoad(1, 1, 1, 2);
        grid.addRoad(1, 1, 0, 1);
        assertEquals(5, grid.getVertices().size());
        grid.removeRoad(1, 1, 1, 0);
        grid.removeRoad(1, 1, 2, 1);
        grid.removeRoad(1, 1, 1, 2);
        assertEquals(2,grid.getVertices().size());
        grid.removeRoad(0, 1, 1, 1);
        assertEquals(0,grid.getVertices().size());
    }

    @Test
    public void testAddLongRoad() {
        Grid grid = new Grid(5);
        grid.addRoad(0, 0, 0, grid.getSize() - 1);
        for (int i = 0; i < grid.getSize() - 1; ++i) {
            assertTrue(grid.getNeighbours(0, i).contains(grid.getVertex(0, i+1)));
        }
    }
}