package utils;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class FileLogger {
    private static Logger logger;

    private FileLogger() {
        throw new IllegalStateException("Utility class");
    }

    static {
        logger = Logger.getLogger(FileLogger.class.getName());
        try {
            FileHandler fileHandler = new FileHandler(Config.LOG_FILE_PATH);
            fileHandler.setFormatter(new LogFormatter());
            logger.addHandler(fileHandler);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void info(String message) {
        logger.log(Level.INFO, message);
    }

    public static void warning(String message) {
        logger.log(Level.WARNING, message);
    }

    public static void error(String message) {
        logger.log(Level.SEVERE, message);
    }

    private static class LogFormatter extends SimpleFormatter {
        @Override
        public synchronized String format(LogRecord rec) {
            // [DATE] [LEVEL] [MESSAGE]
            return String.format("[%s] [%s] %s%n", rec.getInstant(), rec.getLevel(), rec.getMessage());
        }
    }
}
