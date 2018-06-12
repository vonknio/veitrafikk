package View;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

class SettingsWindow extends JFrame {
    private JPanel settingsPanel;
    private JTextField probabilityField;
    private JTextField limitField;
    private JTextField animationTimeField;
    private JButton apply;
    private JButton save;
    private JButton load;
    private JButton quit;

    private float probability;
    private int limit;
    private int animationTime;

    SettingsWindow(float probability, int limit, int animationTime) {
        this.probability = probability;
        this.limit = limit;
        this.animationTime = animationTime;
        create();
    }

    private void create (){
        setup();

        setTitle("Settings");
        setSize(500, 400);
        setLocationRelativeTo(null);
    }

    private void setup () {
        settingsPanel = new JPanel();
        settingsPanel.setLayout(null);

        JLabel nameLabel = new JLabel("VEITRAFIKK", SwingConstants.CENTER);
        nameLabel.setFont(new Font("Monaco", Font.PLAIN, 25));
        nameLabel.setBounds(250-190/2,20, 190, 40);

        JPanel textFields = new JPanel(new BorderLayout());
        JLabel sourceSetDescription = new JLabel("Add sources with...");
        sourceSetDescription.setFont(new Font("Monaco", Font.PLAIN, 15));
        sourceSetDescription.setVisible(true);

        JLabel probabilityDescription = new JLabel("...probability of spawning a new vehicle in each tick:",
                SwingConstants.CENTER);
        probabilityDescription.setVisible(true);
        probabilityField = new JTextField(Float.toString(probability), 2);
        probabilityField.setVisible(true);

        JPanel probabilityPanel = new JPanel(new BorderLayout());
        probabilityPanel.add(probabilityDescription, BorderLayout.NORTH);
        probabilityPanel.add(probabilityField, BorderLayout.SOUTH);

        JLabel limitDescription = new JLabel("...total number of vehicles to spawn (0 for unlimited):",
                SwingConstants.CENTER);
        limitField = new JTextField(Integer.toString(limit), 2);
        limitField.setVisible(true);

        JPanel limitPanel = new JPanel(new BorderLayout());
        limitPanel.add(limitDescription, BorderLayout.NORTH);
        limitPanel.add(limitField, BorderLayout.SOUTH);

        load = new JButton("Load");
        load.setBounds(90, 300, 100, 30);
        save = new JButton("Save");
        save.setBounds(190, 300, 100, 30);

        apply = new JButton("Apply");
        apply.setBounds(320, 300, 100, 30);
        quit = new JButton("Quit");
        quit.setBounds(310, 300, 100, 25);

        textFields.add(sourceSetDescription, BorderLayout.NORTH);
        textFields.add(probabilityPanel, BorderLayout.CENTER);
        textFields.add(limitPanel, BorderLayout.SOUTH);
        textFields.setBounds(50, 75, 400, 100);

        textFields.setVisible(true);

        // Animation settings

        JPanel animationFields = new JPanel(new BorderLayout());

        JLabel animationFieldsDescription = new JLabel("Visuals");
        animationFieldsDescription.setFont(new Font("Monaco", Font.PLAIN, 15));
        animationFieldsDescription.setVisible(true);

        JLabel animationTimeDescription = new JLabel("Animation duration:",
                SwingConstants.CENTER);
        animationTimeDescription.setVisible(true);

        animationTimeField = new JTextField(Integer.toString(animationTime), 2);
        animationTimeField.setVisible(true);

        JPanel animationTimePanel = new JPanel(new BorderLayout());
        animationTimePanel.add(animationTimeDescription, BorderLayout.NORTH);
        animationTimePanel.add(animationTimeField, BorderLayout.SOUTH);

        animationFields.add(animationFieldsDescription, BorderLayout.NORTH);
        animationFields.add(animationTimePanel, BorderLayout.CENTER);
        animationFields.setBounds(50, 185, 400, 60);

        settingsPanel.add(nameLabel);
        settingsPanel.add(apply);
        settingsPanel.add(load);
        settingsPanel.add(save);
        settingsPanel.add(textFields);
        settingsPanel.add(animationFields);

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

    int getAnimationTime() { return Integer.parseInt(animationTimeField.getText()); }

    void update() { setup(); }
}
