package ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import utils.Helpers;

public class FeaturesPanel extends JPanel {

    private final int iconSize = 30;
    private final Font font = new Font("Calibri", Font.BOLD, 16);

    JCheckBox arrowsCheck;

    JCheckBox bridgeCheck;

    public FeaturesPanel(View gui) {

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Exploradores");
        title.setFont(new Font("Calibri", Font.BOLD, 24));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setVerticalAlignment(SwingConstants.CENTER);

        contentPanel.add(title);

        JPanel arrowsPanel = new JPanel();
        arrowsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        arrowsPanel.setLayout(new BoxLayout(arrowsPanel, BoxLayout.X_AXIS));

        this.arrowsCheck = new JCheckBox("Pistola", true);
        this.arrowsCheck.setFont(font);
        this.arrowsCheck.setVerticalTextPosition(SwingConstants.TOP);

        this.arrowsCheck.addActionListener(e -> {
            JCheckBox check = (JCheckBox) e.getSource();
            gui.setExplorersCanShoot(check.isSelected());
        });
        JLabel arrowsIcon = new JLabel("", Helpers.escalateImageIcon("./assets/images/gun.png", iconSize, iconSize),
                SwingConstants.LEFT);

        JPanel bridgePanel = new JPanel();
        bridgePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        bridgePanel.setLayout(new BoxLayout(bridgePanel, BoxLayout.X_AXIS));

        this.bridgeCheck = new JCheckBox("Tablones", true);

        this.bridgeCheck.addActionListener(e -> {
            JCheckBox check = (JCheckBox) e.getSource();
            gui.setExplorersCanPutBridge(check.isSelected());
        });

        JLabel bridgeIcon = new JLabel("", Helpers.escalateImageIcon("./assets/images/planks.png", iconSize, iconSize),
                SwingConstants.LEFT);

        // Arrows
        arrowsPanel.add(arrowsCheck);
        arrowsPanel.add(arrowsIcon);
        // Bridge
        bridgePanel.add(bridgeCheck);
        bridgePanel.add(bridgeIcon);

        JPanel contentPanel2 = new JPanel();
        contentPanel2.add(arrowsPanel);
        contentPanel2.add(Box.createRigidArea(new Dimension(15, 1)));
        contentPanel2.add(bridgePanel);

        this.add(contentPanel);
        this.add(contentPanel2);

    }

    public boolean canShoot() {
        return this.arrowsCheck.isSelected();
    }

    public boolean canPutBridge() {
        return this.bridgeCheck.isSelected();
    }
}
