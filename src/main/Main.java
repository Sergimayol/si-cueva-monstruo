package main;

import ui.ViewContent;

public class Main {

    public static void main(String[] args) {
        (new Main()).start();
    }

    public void start() {
        ViewContent gui = new ViewContent();
        gui.showGui();
    }
}
