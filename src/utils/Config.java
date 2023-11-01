package utils;

public class Config {
    private Config() {
        throw new IllegalStateException("Utility class");
    }

    public static final boolean DEBUG = true;
    public static final boolean RUN_TESTS = false;
    public static final String VIEW_MAIN_WIN_CONFIG_PATH = "./src/ui/settings/main.json";
    public static final String OBSTACLE_IMG_PATH = "./assets/obstacle.png";
    public static final String ROBOT_IMG_PATH = "./assets/robot.png";
    public static final String PAUSE_IMG_PATH = "./assets/pause.png";
    public static final String PLAY_IMG_PATH = "./assets/play.png";
    public static final String APP_UI_ICON_PATH = "./assets/icon.png";
    public static final String PATH_TO_LOGS = "./logs/";
    public static final String LOG_FILE_NAME = "application.log";
    public static final String LOG_FILE_PATH = PATH_TO_LOGS + LOG_FILE_NAME;
}
