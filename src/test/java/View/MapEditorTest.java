package View;

import Controller.Controller;
import Model.Model;
import org.junit.Test;

import java.awt.*;
import java.awt.event.InputEvent;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MapEditorTest {

    @Test
    public void SimpleGridTest() throws AWTException, InterruptedException {

        Model model = new Model();
        View view = new View();
        Controller controller = new Controller(view, model);
        view.setVisible(true);

        Robot robot = new Robot();

        while (view.menu == null) {}
        int x = view.menu.getX();
        int y = view.menu.getY();

        Thread.sleep(500);
        robot.mouseMove(x+80,y+80);
        robot.mousePress(InputEvent.BUTTON1_MASK);
        Thread.sleep(500);
        robot.mouseRelease(InputEvent.BUTTON1_MASK);

        while (view.mapEditor == null) {}
        while (view.mapEditor.frame == null) {}

        x = view.mapEditor.frame.getX();
        y = view.mapEditor.frame.getY();
        Thread.sleep(500);
        robot.mouseMove(x+50,y+50);
        robot.mousePress(InputEvent.BUTTON1_MASK);
        Thread.sleep(500);
        robot.mouseMove(x+50,y+200);
        robot.mousePress(InputEvent.BUTTON1_MASK);
        Thread.sleep(500);
        robot.mouseMove(x+50,y+100);
        robot.mousePress(InputEvent.BUTTON1_MASK);
        Thread.sleep(500);
        robot.mouseMove(x+200,y+100);
        robot.mousePress(InputEvent.BUTTON1_MASK);
        Thread.sleep(500);
        robot.mouseMove(x+200,y+50);
        robot.mousePress(InputEvent.BUTTON1_MASK);
        Thread.sleep(500);
        robot.mouseMove(x+200,y+200);
        robot.mousePress(InputEvent.BUTTON1_MASK);
        Thread.sleep(500);
        robot.mouseMove(x+50,y+200);
        robot.mousePress(InputEvent.BUTTON1_MASK);
        Thread.sleep(500);
        robot.mouseMove(x+200,y+200);
        robot.mousePress(InputEvent.BUTTON1_MASK);

        Thread.sleep(1000);

        assertTrue(model.hasRoad(0, 1, 3, 1));
        assertFalse(model.hasRoad(1, 0, 1, 3));

    }

}