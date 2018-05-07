package View;

import Controller.Controller;
import Model.Model;
import org.junit.Test;

import java.awt.*;
import java.awt.event.InputEvent;

import static org.junit.Assert.*;

public class MapEditorTest {

    @Test
    public void SimpleGrid() throws AWTException, InterruptedException {

        View view = new View();
        Model model = new Model();
        Controller controller = new Controller(view, model);
        view.setVisible(true);

        Robot robot = new Robot();

        int x = view.menu.getX();
        int y = view.menu.getY();

        robot.mouseMove(x+80,y+80);
        robot.mousePress(InputEvent.BUTTON1_MASK);
        Thread.sleep(100);
        robot.mouseRelease(InputEvent.BUTTON1_MASK);
        Thread.sleep(500);

        x = view.mapEditor.frame.getX();
        y = view.mapEditor.frame.getY();

        robot.mouseMove(x+50,y+50);
        robot.mousePress(InputEvent.BUTTON1_MASK);
        Thread.sleep(100);
        robot.mouseMove(x+50,y+200);
        robot.mousePress(InputEvent.BUTTON1_MASK);

        robot.mouseMove(x+50,y+100);
        robot.mousePress(InputEvent.BUTTON1_MASK);
        Thread.sleep(100);
        robot.mouseMove(x+200,y+100);
        robot.mousePress(InputEvent.BUTTON1_MASK);

        robot.mouseMove(x+200,y+50);
        robot.mousePress(InputEvent.BUTTON1_MASK);
        Thread.sleep(100);
        robot.mouseMove(x+200,y+200);
        robot.mousePress(InputEvent.BUTTON1_MASK);

        robot.mouseMove(x+50,y+200);
        robot.mousePress(InputEvent.BUTTON1_MASK);
        Thread.sleep(100);
        robot.mouseMove(x+200,y+200);
        robot.mousePress(InputEvent.BUTTON1_MASK);

        Thread.sleep(500);

        assertTrue(model.hasRoad(0, 1, 3, 1));
        assertFalse(model.hasRoad(1, 0, 1, 3));

    }

}
