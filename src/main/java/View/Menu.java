package View;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

class Menu extends JFrame {

    private JPanel menu;
    private JTextField sizeField;
    private JTextField distField;
    private JButton start;
    private JButton quit;

    MapEditor mapEditor;

    public Menu (){
        create();
    }

    private void create (){

        setup();

        setTitle("Veitrafikk");
        setSize(200, 200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }

    private void setup () {

        menu = new JPanel(new BorderLayout());

        JPanel buttons = new JPanel(new BorderLayout());
        JPanel textFields = new JPanel(new BorderLayout());

        JPanel sizePanel = new JPanel(new BorderLayout());
        JLabel sizeDescription = new JLabel("Set grid size:");

        JPanel distPanel = new JPanel(new BorderLayout());
        JLabel distDescription = new JLabel("Set distance in px:");

        sizeField = new JTextField("10", 5);
        distField = new JTextField("50", 5);

        sizePanel.add(sizeField, BorderLayout.EAST);
        sizePanel.add(sizeDescription, BorderLayout.WEST);

        distPanel.add(distField, BorderLayout.EAST);
        distPanel.add(distDescription, BorderLayout.WEST);

        start = new JButton("Start");
        quit = new JButton("Quit");

        textFields.add(sizePanel, BorderLayout.NORTH);
        textFields.add(distPanel, BorderLayout.SOUTH);

        buttons.add(textFields, BorderLayout.NORTH);
        buttons.add(quit, BorderLayout.SOUTH);

        menu.add(start, BorderLayout.CENTER);
        menu.add(buttons, BorderLayout.SOUTH);

        this.add(menu);
    }

    public int getGridSize() {
        return Integer.parseInt(sizeField.getText());
    }

    public int getDistanceInPx() {
        return Integer.parseInt(distField.getText());
    }

    public void addOpenEditorListener(ActionListener listener) {
        start.addActionListener(listener);
    }

    public void addQuitListener(ActionListener listener) {
        quit.addActionListener(listener);
    }

}