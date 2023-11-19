package ui;

import org.smm.betterswing.Window;

import utils.Config;
import utils.FileLogger;

public class View {

    private Window window;

    public View() {
        this.window = new Window(Config.VIEW_MAIN_WIN_CONFIG_PATH);
        this.window.initConfig();
    }

    public void start() {
        FileLogger.info("[VIEW] Starting view...");
        // this.window.addMenuBar(this.creatMenuBar());
        // this.map.initMapTiles();
        // this.initSplitPane();
        // Section body = new Section();
        // body.createFreeSection(this.createInitPage());
        // this.window.addSection(body, DirectionAndPosition.POSITION_CENTER, "Main");
        this.window.start();
        FileLogger.info("[VIEW] View started");
    }
}
