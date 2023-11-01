package pl.edu.wat.sr.ricart_agrawala.core.log;

import javafx.scene.control.TextArea;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LogController {
    private static LogController instance;
    private TextArea outLogTextArea;
    private LogLevel minLogLevel;

    private LogController() {
        outLogTextArea = null;
        minLogLevel = LogLevel.INFO;
    }

    public static LogController getInstance() {
        if (instance == null) {
            instance = new LogController();
        }
        return instance;
    }

    public void setOutLogTextArea(TextArea textArea) {
        outLogTextArea = textArea;
    }
    public void setMinLogLevel(LogLevel logLevel) {
        minLogLevel = logLevel;
        logInfo(this.getClass().getName(), String.format("Min log level switched to : %s", logLevel.name()));
    }

    public void log(LogLevel logLevel, String classname, String message) {
        if (logLevel.ordinal() < minLogLevel.ordinal()) {
            return;
        }

        String[] classnameParts = classname.split("\\.", 6);
        if (classnameParts.length == 6) {
            classname = classnameParts[5];
        }
        LocalDateTime systemTime = LocalDateTime.now();
        String logMsg = String.format("[%-18s] : [%-7s] : [%-20s] : %s\n",
                systemTime.format(DateTimeFormatter.ISO_TIME),
                logLevel.name(),
                classname,
                message);

        System.out.print(logMsg);
        if (outLogTextArea == null) {
            System.out.printf("[%-18s] : [%-7s] : [%-20s] : `outLogTextArea` is null!\n",
                    systemTime.format(DateTimeFormatter.ISO_TIME),
                    LogLevel.ERROR.name(),
                    this.getClass().getName().split("\\.", 6)[5]);
        } else {
            outLogTextArea.appendText(logMsg);

            int currentRow = outLogTextArea.getParagraphs().size();
            // TODO: LOG_TEXT_AREA_MAX_ROW_COUNT FX control to allow user to change this value. Temporary const value
            if (currentRow > 600) {
                int removeCount = currentRow - 600;
                int endIndex = 0;
                for(CharSequence sequence : outLogTextArea.getParagraphs()) {
                    if(removeCount == 0) {
                        break;
                    }
                    endIndex += sequence.length();
                    removeCount--;
                }
                outLogTextArea.deleteText(0, endIndex + 1);
            }
        }
    }

    public void logDebug(String classname, String message) {
        log(LogLevel.DEBUG, classname, message);
    }
    public void logInfo(String classname, String message) {
        log(LogLevel.INFO, classname, message);
    }
    public void logWarning(String classname, String message) {
        log(LogLevel.WARNING, classname, message);
    }
    public void logError(String classname, String message) {
        log(LogLevel.ERROR, classname, message);
    }
    public void logFatal(String classname, String message) {
        log(LogLevel.FATAL, classname, message);
    }
}
