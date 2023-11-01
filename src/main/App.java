package main;

import ui.View;
import utils.Helpers;

public class App {
    public static void main(String[] args) {
        Helpers.createLogFileAndPathIfNotExists();
        View view = new View();
        view.start();
    }
}
