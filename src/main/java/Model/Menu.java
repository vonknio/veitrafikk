package Model;

import javax.swing.*;
import java.awt.*;

public class Menu extends JFrame {

    private JPanel menu;
    private JTextField sizeField;
    private JTextField distField;
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

        menu.setVisible(true);
        this.setVisible(true);

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

        JButton start = new JButton("Start");
        start.addActionListener(e -> openEditor());

        JButton quit = new JButton("Quit");
        quit.addActionListener(e -> System.exit(0));

        textFields.add(sizePanel, BorderLayout.NORTH);
        textFields.add(distPanel, BorderLayout.SOUTH);

        buttons.add(textFields, BorderLayout.NORTH);
        buttons.add(quit, BorderLayout.SOUTH);

        menu.add(start, BorderLayout.CENTER);
        menu.add(buttons, BorderLayout.SOUTH);

        this.add(menu);

    }

    public void openEditor () {

        setVisible(false);
        Integer size = Integer.parseInt(sizeField.getText());
        Integer dist = Integer.parseInt(distField.getText());
        if (size <= 0) size = 10;
        if (dist <= 14) dist = 15;
        mapEditor = new MapEditor(size, dist);

    }

}