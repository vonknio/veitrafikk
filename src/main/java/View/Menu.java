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

        start = new JButton("Start");
        start.setBounds(50, 50, 100, 25);
        quit = new JButton("Quit");
        quit.setBounds(50, 125, 100, 25);

        textFields.add(sizePanel, BorderLayout.NORTH);
        textFields.add(distPanel, BorderLayout.SOUTH);
        textFields.setBounds(50, 75, 100, 50);

        menu.add(nameLabel);
        menu.add(start);
        menu.add(quit);
        menu.add(textFields);

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