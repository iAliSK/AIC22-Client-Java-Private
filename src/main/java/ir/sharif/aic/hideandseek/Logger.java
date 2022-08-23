package ir.sharif.aic.hideandseek;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class Logger {
    boolean enableLogging;
    private PrintWriter out;

    public Logger(String fileName) {
        try {
            out = new PrintWriter(new BufferedWriter(
                    new FileWriter(fileName, true)
            ));
        } catch (IOException ignored) {
        }
    }

    public void log(String format, Object... args) {
        if (enableLogging) {
            out.printf(format, args);
            out.flush();
        }
    }

    public void enableLogging(boolean enableLogging) {
        this.enableLogging = enableLogging;
    }
}
