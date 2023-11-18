
package ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

public class MenuBar extends JMenuBar {

    private final MonstersCaveGui gui;

    public MenuBar(MonstersCaveGui gui) {
        this.gui = gui;
        initComponents();
    }

    private void initComponents() {

        JMenu files = new JMenu("Archivos");

        JMenuItem save = new JMenuItem("Guardar tablero");
        files.add(save);
        save.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gui.saveMap();
            }

        });

        JMenuItem load = new JMenuItem("Cargar tablero");
        files.add(load);
        load.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gui.loadMap();
            }

        });

        this.add(files);

        JMenu clean = new JMenu("Tablero");

        JMenuItem cleanItem = new JMenuItem("Limpiar tablero");
        clean.add(cleanItem);
        cleanItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gui.resetCave();
            }

        });

        this.add(clean);

    }

}
