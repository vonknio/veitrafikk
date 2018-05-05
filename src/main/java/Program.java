import View.View;
import Model.Model;
import Controller.Controller;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class Program {
    public static void main(String[] args) {
        Logger.getLogger("").setLevel(Level.CONFIG);
        Logger.getLogger("").getHandlers()[0].setLevel(Level.CONFIG);

        View view = new View();
        Model model = new Model();
        Controller controller = new Controller(view, model);
        view.setVisible(true);
    }

}
