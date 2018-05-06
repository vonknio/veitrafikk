package Controller;

import View.View;
import Model.Model;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

public class ControllerTest {
    View view = spy(new View());
    Model model = spy(new Model());
    Controller controller = new Controller(view, model);

    @Test
    public void testSwitchToEditMode() {
        when(view.getGridSize()).thenReturn(4);
        when(view.getDistanceInPx()).thenReturn(100);

        controller.switchToEditMode(null);
        assertEquals(4, model.getGridSize());
    }
}