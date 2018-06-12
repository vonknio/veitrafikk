package View;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

class SettingsWindow extends JFrame {
    private JPanel settingsPanel;
    private JTextField probabilityField;
    private JTextField limitField;
    private JButton apply;
    private JButton save;
    private JButton load;
    private JButton quit;
    private float probability;
    private int limit;

    SettingsWindow(float probability, int limit) {
        this.probability = probability;
        this.limit = limit;
        create();
    }

    private void create (){
        setup();

        setTitle("Settings");
        setSize(500, 275);
        setLocationRelativeTo(null);
    }

    private void setup () {
        settingsPanel = new JPanel();
        settingsPanel.setLayout(null);

        JLabel nameLabel = new JLabel("VEITRAFIKK", SwingConstants.CENTER);
        nameLabel.setFont(new Font("Monaco", Font.PLAIN, 25));
        nameLabel.setBounds(250-190/2,20, 190, 40);

        JPanel textFields = new JPanel(new BorderLayout());

        JLabel probabilityDescription = new JLabel("Probability of spawning a new vehicle in each tick:",
                SwingConstants.CENTER);
        probabilityDescription.setVisible(true);
        probabilityField = new JTextField(Float.toString(probability), 2);
        probabilityField.setVisible(true);

        JPanel probabilityPanel = new JPanel(new BorderLayout());
        probabilityPanel.add(probabilityDescription, BorderLayout.NORTH);
        probabilityPanel.add(probabilityField, BorderLayout.SOUTH);

        JLabel limitDescription = new JLabel("Total number of vehicles to spawn (0 for unlimited):",
                SwingConstants.CENTER);
        limitField = new JTextField(Integer.toString(limit), 2);
        limitField.setVisible(true);

        JPanel limitPanel = new JPanel(new BorderLayout());
        limitPanel.add(limitDescription, BorderLayout.NORTH);
        limitPanel.add(limitField, BorderLayout.SOUTH);

        load = new JButton("Load");
        load.setBounds(90, 195, 100, 30);
        save = new JButton("Save");
        save.setBounds(190, 195, 100, 30);

        apply = new JButton("Apply");
        apply.setBounds(320, 195, 100, 30);
        quit = new JButton("Quit");
        quit.setBounds(310, 195, 100, 25);

        textFields.add(probabilityPanel, BorderLayout.NORTH);
        textFields.add(limitPanel, BorderLayout.SOUTH);
        textFields.setBounds(50, 75, 400, 100);

        textFields.setVisible(true);

        settingsPanel.add(nameLabel);
        settingsPanel.add(apply);
        settingsPanel.add(load);
        settingsPanel.add(save);
        //settingsPanel.add(quit);
        settingsPanel.add(textFields);

        this.add(settingsPanel);
    }


    public void addLoadListener(ActionListener listener) {
        load.addActionListener(e -> {
            listener.actionPerformed(e);
            this.setVisible(false);
        });
    }

    public void addQuitListener(ActionListener listener) {
        quit.addActionListener(e -> {
            listener.actionPerformed(e);
            this.setVisible(false);
        });
    }

    public void addSaveListener(ActionListener listener) {
        save.addActionListener(e -> {
            listener.actionPerformed(e);
            this.setVisible(false);
        });
    }

    public void addApplyListener(ActionListener listener) {
        apply.addActionListener(e -> {
            listener.actionPerformed(e);
            this.setVisible(false);
        });
    }

    float getSourceProbability() {
        return Float.parseFloat(probabilityField.getText());
    }

    int getSourceLimit() {
        return Integer.parseInt(limitField.getText());
    }

    void update() { setup(); }
}
