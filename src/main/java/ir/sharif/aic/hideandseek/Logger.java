package ir.sharif.aic.hideandseek;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class Logger {
    private boolean enableLogging;
    private PrintWriter out;
    private final String fileName;

    public Logger(String fileName) {
        this.fileName = fileName;
    }

    private void createFile() {
        try {
            out = new PrintWriter(new BufferedWriter(
                    new FileWriter(fileName, true)
            ));
        } catch (IOException ignored) {
        }
    }

    public void log(String format, Object... args) {
        if (enableLogging) {
            if (out == null) {
                createFile();
            }
            out.printf(format, args);
            out.flush();
        }
    }

    public void enableLogging(boolean enableLogging) {
        this.enableLogging = enableLogging;
    }
}
