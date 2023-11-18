
package gui;

import agent.Explorer;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.image.BufferedImage;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Result extends JDialog {

    private final Font fontTitle = new Font("Calibri", Font.BOLD, 18);
    private final Font fontResult = new Font("Calibri", Font.PLAIN, 30);
    private final Color winnerColor = new Color(0, 163, 33);

    public Result(MonstersCaveGui gui, Explorer[] explorers, BufferedImage[] imgs) {
        super(gui, true);

        this.setTitle("Resultados");

        this.setLayout(new BorderLayout());

        JPanel content = new JPanel();
        content.setBorder(BorderFactory.createEmptyBorder(15, 15, 30, 15));
        content.setLayout(new BoxLayout(content, BoxLayout.X_AXIS));

        int maxTreasures = this.maxValue(explorers);

        for (int i = 0; i < explorers.length; i++) {

            Explorer explorer = explorers[i];

            JPanel explorerResult = new JPanel();
            explorerResult.setLayout(new BoxLayout(explorerResult, BoxLayout.Y_AXIS));

            int id = explorer.getId();
            JLabel label = new JLabel("Explorador " + (id + 1));
            label.setIcon(new ImageIcon(imgs[id]));
            label.setFont(fontTitle);

            explorerResult.add(label);

            JPanel aux = new JPanel();
            aux.setLayout(new BoxLayout(aux, BoxLayout.X_AXIS));

            final String treasuresTaken = explorer.getTreasuresTaken();
            JLabel treasures = new JLabel(treasuresTaken);
            treasures.setFont(fontResult);

            if (Integer.parseInt(treasuresTaken) == maxTreasures) {
                treasures.setForeground(winnerColor);
            }

            aux.add(Box.createHorizontalGlue());
            aux.add(treasures);
            aux.add(Box.createHorizontalGlue());

            explorerResult.add(Box.createRigidArea(new Dimension(1, 10)));
            explorerResult.add(aux);

            content.add(explorerResult);

            if (i < explorers.length - 1) {
                content.add(Box.createRigidArea(new Dimension(25, 1)));
            }

        }

        this.add(content);

        this.pack();

        this.setLocationRelativeTo(gui);

    }

    public void showResults() {
        this.setVisible(true);
    }

    private int maxValue(Explorer[] explorers) {
        int max = -1;

        for (Explorer explorer : explorers) {
            int treasures = Integer.parseInt(explorer.getTreasuresTaken());
            if (treasures > max) {
                max = treasures;
            }
        }

        return max;
    }

}
