/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gui;

import agent.Explorer;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

/**
 *
 * @author ccf20
 */
public class MapsPanel extends JPanel {

    private MapDisplayer[] maps = null;
    private JPanel panel;
    private final Font font = new Font("Calibri", Font.BOLD, 16);
    private final Font fontTitle = new Font("Calibri", Font.BOLD, 24);
    private JLabel title;
    private JLabel[] labels;
    private Explorer[] explorers;

    public MapsPanel() {
        super();

        this.setLayout(new BorderLayout());

        panel = new JPanel();
        panel.setAutoscrolls(true);

        JScrollPane scroll = new JScrollPane(panel);
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        this.add(scroll);

        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 2, 0, 0, Color.black),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));

        title = new JLabel("MAPAS");
        title.setFont(fontTitle);
    }

    public void setMapDisplayers(Explorer[] explorers, Point[] initialPos, BufferedImage[] imgs) {

        panel.removeAll();

        panel.add(title);

        this.explorers = explorers;
        this.maps = new MapDisplayer[explorers.length];
        this.labels = new JLabel[explorers.length];
        for (int i = 0; i < explorers.length; i++) {

            this.maps[i] = new MapDisplayer(explorers[i].getMap(), initialPos[i]);

            panel.add(Box.createRigidArea(new Dimension(1, 50)));

            JPanel aux = new JPanel();
            aux.setLayout(new BoxLayout(aux, BoxLayout.X_AXIS));

            this.labels[i] = new JLabel(
                    "  Explorador " + (i + 1) + "       Tesoros: " + explorers[i].getTreasuresTaken());
            this.labels[i].setIcon(new ImageIcon(imgs[i]));
            this.labels[i].setFont(font);
            aux.add(this.labels[i]);

            aux.add(Box.createRigidArea(new Dimension(10, 1)));

            JCheckBox checkBox = new JCheckBox("Mostrar veces visitado");
            checkBox.setSelected(false);
            checkBox.setActionCommand(Integer.toString(i));
            checkBox.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    JCheckBox checkBox = (JCheckBox) e.getSource();
                    maps[Integer.parseInt(e.getActionCommand())].setShowTimesVisited(checkBox.isSelected());
                    repaint();
                }
            });
            aux.add(checkBox);

            panel.add(aux);

            panel.add(Box.createRigidArea(new Dimension(1, 5)));

            panel.add(this.maps[i]);

        }

        this.revalidate();
        this.repaint();
    }

    public void updateMaps(BufferedImage img) {
        // BufferedImage resized = resizeImage(img, MapDisplayer.dimension.width,
        // MapDisplayer.dimension.height);
        // for (MapDisplayer map : maps) {
        // map.setCaveImage(resized);
        // }
        for (MapDisplayer map : maps) {
            map.setCaveImage(img);
        }
        this.repaint();
    }

    public BufferedImage resizeImage(BufferedImage original, int width, int height) {
        BufferedImage resized = new BufferedImage(width, height, original.getType());
        Graphics2D g = resized.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(original, 0, 0, width, height, 0, 0, original.getWidth(),
                original.getHeight(), null);
        g.dispose();
        return resized;
    }

    void setSizeMaps(int total, int tile, int frame, int nTiles) {
        for (MapDisplayer map : maps) {
            map.setSizeMaps(total, tile, frame, nTiles);
        }
    }

    void updateCounters() {
        for (int i = 0; i < this.labels.length; i++) {
            this.labels[i].setText("  Explorador " + (i + 1) + "       Tesoros: " + explorers[i].getTreasuresTaken());
        }
    }

}
