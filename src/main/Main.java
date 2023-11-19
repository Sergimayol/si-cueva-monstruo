package main;

import ui.ViewContent;
import utils.Helpers;

public class Main {

    public static void main(String[] args) {
        Helpers.createLogFileAndPathIfNotExists();
        (new Main()).start();
    }

    public void start() {
        ViewContent gui = new ViewContent();
        gui.showGui();
    }
}
