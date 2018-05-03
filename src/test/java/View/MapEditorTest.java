package View;

import org.junit.Test;

import java.awt.*;
import java.awt.event.InputEvent;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class MapEditorTest {

    @Test
    public void SimpleGrid() throws AWTException, InterruptedException {

        Robot robot = new Robot();
        MapEditor mapEditor = new MapEditor(4, 100);

        int x = mapEditor.frame.getX();
        int y = mapEditor.frame.getY();

        robot.mouseMove(x+100,y+100);
        robot.mousePress(InputEvent.BUTTON1_MASK);
        Thread.sleep(100);
        robot.mouseRelease(InputEvent.BUTTON1_MASK);

        robot.mouseMove(x+100,y+200);
        robot.mousePress(InputEvent.BUTTON1_MASK);
        Thread.sleep(100);
        robot.mouseRelease(InputEvent.BUTTON1_MASK);
        robot.mousePress(InputEvent.BUTTON1_MASK);
        Thread.sleep(100);
        robot.mouseRelease(InputEvent.BUTTON1_MASK);

        robot.mouseMove(x+300,y+200);
        robot.mousePress(InputEvent.BUTTON1_MASK);
        Thread.sleep(100);
        robot.mouseRelease(InputEvent.BUTTON1_MASK);

        Thread.sleep(500);

//        java.util.List<Vertex> vertexNeighbours = new ArrayList<>();
//        java.util.List<Vertex> vertexNeighbours2 = new ArrayList<>();
//
//        vertexNeighbours = mapEditor.grid.getNeighbours(0,0);
//        vertexNeighbours2 = mapEditor.grid.getNeighbours(2,1);
//
//        assertEquals(mapEditor.grid.getVertex(0,1), vertexNeighbours.get(0));
//        assertEquals(mapEditor.grid.getVertex(1,1), vertexNeighbours2.get(0));

    }

}
