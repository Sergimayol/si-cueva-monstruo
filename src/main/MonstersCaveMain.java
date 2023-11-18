package main;

import gui.MonstersCaveGui;

public class MonstersCaveMain {

    public static void main(String[] args) {
        (new MonstersCaveMain()).start();
    }

    public void start() {
        MonstersCaveGui gui = new MonstersCaveGui();
        gui.showGui();
    }
}
