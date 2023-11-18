package gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import utils.ImageLoader;

public class FeaturesPanel extends JPanel {

    private final int iconSize = 30;
    private final Font font = new Font("Calibri", Font.BOLD, 16);

    JCheckBox arrowsCheck;

    JCheckBox bridgeCheck;

    public FeaturesPanel(MonstersCaveGui gui) {

        this.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(2, 0, 0, 0, Color.black),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));

        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        JLabel title = new JLabel("Habilidades");
        title.setFont(new Font("Calibri", Font.BOLD, 24));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        title.setVerticalAlignment(JLabel.CENTER);
        this.add(title);

        this.add(Box.createRigidArea(new Dimension(25, 1)));

        JPanel arrowsPanel = new JPanel();
        arrowsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        arrowsPanel.setLayout(new BoxLayout(arrowsPanel, BoxLayout.X_AXIS));

        this.arrowsCheck = new JCheckBox("Flechas", true);
        this.arrowsCheck.setFont(font);
        this.arrowsCheck.setVerticalTextPosition(SwingConstants.TOP);

        this.arrowsCheck.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JCheckBox check = (JCheckBox) e.getSource();
                gui.setExplorersCanShoot(check.isSelected());
            }
        });

        // this.arrowsCheck.setIcon(new
        // ImageIcon(ImageLoader.loadImageScaled("src/main/java/images/arrow.png",
        // iconSize, iconSize)));
        arrowsPanel.add(arrowsCheck);

        arrowsPanel.add(new JLabel("",
                new ImageIcon(ImageLoader.loadImageScaled("./assets/images/arrow.png", iconSize, iconSize)),
                SwingConstants.LEFT));

        JPanel bridgePanel = new JPanel();
        bridgePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        bridgePanel.setLayout(new BoxLayout(bridgePanel, BoxLayout.X_AXIS));

        this.bridgeCheck = new JCheckBox("Tablones", true);

        this.bridgeCheck.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JCheckBox check = (JCheckBox) e.getSource();
                gui.setExplorersCanPutBridge(check.isSelected());
            }
        });

        bridgePanel.add(bridgeCheck);

        bridgePanel.add(new JLabel("",
                new ImageIcon(ImageLoader.loadImageScaled("./assets/images/planks.png", iconSize, iconSize)),
                SwingConstants.LEFT));

        this.add(arrowsPanel);

        this.add(Box.createRigidArea(new Dimension(15, 1)));

        this.add(bridgePanel);

    }

    public boolean canShoot() {
        return this.arrowsCheck.isSelected();
    }

    public boolean canPutBridge() {
        return this.bridgeCheck.isSelected();
    }
}
