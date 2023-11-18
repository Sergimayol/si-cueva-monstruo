package main;

import ui.MonstersCaveGui;

public class Main {

    public static void main(String[] args) {
        (new Main()).start();
    }

    public void start() {
        MonstersCaveGui gui = new MonstersCaveGui();
        gui.showGui();
    }
}
