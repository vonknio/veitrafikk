package View;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.lang.reflect.Field;

class Menu extends JFrame {

    private JPanel menu;
    private JTextField sizeField;
    private JTextField distField;
    private boolean distanceFixed;
    private boolean canContinue;
    private JButton create;
    private JButton cont;
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
        JButton distTick = new JButton("Fix distance");
        JLabel distDescription = new JLabel("Distance:");

        sizeField = new JTextField("10", 2);
        distField = new JTextField("60", 2);

        distDescription.setVisible(false);
        distField.setVisible(false);

        distTick.addActionListener(e -> {
            distanceFixed = true;
            distTick.setVisible(false);
            distDescription.setVisible(true);
            distField.setVisible(true);
        });

        sizePanel.add(sizeField, BorderLayout.EAST);
        sizePanel.add(sizeDescription, BorderLayout.WEST);

        distPanel.add(distField, BorderLayout.EAST);
        distPanel.add(distTick, BorderLayout.CENTER);
        distPanel.add(distDescription, BorderLayout.WEST);

        textFields.add(sizePanel, BorderLayout.NORTH);
        textFields.add(distPanel, BorderLayout.SOUTH);
        textFields.setBounds(50, 75, 100, 55);
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
            cont.setVisible(false);
            load.setVisible(false);
            quit.setVisible(false);
        });

        cont = new JButton("Continue");
        cont.setBounds(50, 50, 100, 25);
        cont.setVisible(false);

        load = new JButton("Load");
        load.setBounds(50, 75, 100, 25);
        quit = new JButton("Quit");
        quit.setBounds(50, 150, 100, 25);

        back = new JButton("Back");
        back.setBounds(50, 150, 100, 25);
        back.addActionListener(e -> {
            distanceFixed = false;
            distTick.setVisible(true);
            distDescription.setVisible(false);
            distField.setVisible(false);

            start.setVisible(false);
            back.setVisible(false);
            textFields.setVisible(false);
            create.setVisible(true);
            load.setVisible(true);
            quit.setVisible(true);
            if (canContinue) cont.setVisible(true);
        });
        back.setVisible(false);

        menu.add(nameLabel);
        menu.add(create);
        menu.add(load);
        menu.add(cont);
        menu.add(start);
        menu.add(back);
        menu.add(quit);
        menu.add(textFields);

        this.add(menu);
    }

    void showContinue() {
        canContinue = true;
        create.setBounds(50, 75, 100, 25);
        load.setBounds(50, 100, 100, 25);
        back.doClick();
    }

    int getGridSize() {
        return Integer.parseInt(sizeField.getText());
    }

    int getDistanceInPx() { return Integer.parseInt(distField.getText()); }

    boolean getIsDistanceFixed() { return distanceFixed; }

    void addOpenEditorListener(ActionListener listener) {
        start.addActionListener(listener);
    }

    public void addLoadListener(ActionListener listener) { load.addActionListener(listener); }

    public void addQuitListener(ActionListener listener) { quit.addActionListener(listener); }

    public void addContinueListener(ActionListener listener) { cont.addActionListener(listener); }

}