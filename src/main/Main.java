package main;

import ui.View;
import utils.Helpers;

public class Main {

    public static void main(String[] args) {
        Helpers.createLogFileAndPathIfNotExists();
        View gui = new View();
        gui.showGui();
    }

}
