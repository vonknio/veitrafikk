package View;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

class Menu extends JFrame {

    private JPanel menu;
    private JTextField sizeField;
    private JTextField distField;
    private JButton create;
    private JButton start;
    private JButton back;
    private JButton load;
    private JButton quit;

    Menu() {
        create();
    }

    private void create (){
        setup();

        setTitle("Veitrafikk");
        setSize(200, 225);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    private void setup () {
        menu = new JPanel();
        menu.setLayout(null);

        JLabel nameLabel = new JLabel("VEITRAFIKK", SwingConstants.CENTER);
        nameLabel.setFont(new Font("Monaco", Font.PLAIN, 25));
        nameLabel.setBounds(5,5, 190, 40);

        JPanel textFields = new JPanel(new BorderLayout());

        JPanel sizePanel = new JPanel(new BorderLayout());
        JLabel sizeDescription = new JLabel("Grid size:");

        JPanel distPanel = new JPanel(new BorderLayout());
        JLabel distDescription = new JLabel("Distance:");

        sizeField = new JTextField("10", 2);
        distField = new JTextField("50", 2);

        sizePanel.add(sizeField, BorderLayout.EAST);
        sizePanel.add(sizeDescription, BorderLayout.WEST);

        distPanel.add(distField, BorderLayout.EAST);
        distPanel.add(distDescription, BorderLayout.WEST);

        textFields.add(sizePanel, BorderLayout.NORTH);
        textFields.add(distPanel, BorderLayout.SOUTH);
        textFields.setBounds(50, 75, 100, 50);
        textFields.setVisible(false);

        start = new JButton("Start");
        start.setBounds(50, 50, 100, 25);

        create = new JButton("Create");
        create.setBounds(50, 50, 100, 25);
        create.addActionListener(e -> {
            start.setVisible(true);
            back.setVisible(true);
            textFields.setVisible(true);
            create.setVisible(false);
            load.setVisible(false);
            quit.setVisible(false);
        });

        load = new JButton("Load");
        load.setBounds(50, 75, 100, 25);
        quit = new JButton("Quit");
        quit.setBounds(50, 150, 100, 25);

        back = new JButton("Back");
        back.setBounds(50, 150, 100, 25);
        back.addActionListener(e -> {
            start.setVisible(false);
            back.setVisible(false);
            textFields.setVisible(false);
            create.setVisible(true);
            load.setVisible(true);
            quit.setVisible(true);
        });
        back.setVisible(false);

        menu.add(nameLabel);
        menu.add(create);
        menu.add(load);
        menu.add(start);
        menu.add(back);
        menu.add(quit);
        menu.add(textFields);

        this.add(menu);
    }

    int getGridSize() {
        return Integer.parseInt(sizeField.getText());
    }

    int getDistanceInPx() {
        return Integer.parseInt(distField.getText());
    }

    void addOpenEditorListener(ActionListener listener) {
        start.addActionListener(listener);
    }

    void addQuitListener(ActionListener listener) {
        quit.addActionListener(listener);
    }

}