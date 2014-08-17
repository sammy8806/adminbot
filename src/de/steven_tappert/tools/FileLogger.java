package de.steven_tappert.tools;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class FileLogger extends Logger {

    protected String filename = "";
    protected String path;

    protected BufferedWriter logFile;
    protected boolean fileOpened = false;

    public FileLogger(String filename) {
        this(filename, ".");
    }

    public FileLogger(String filename, String path) {
        this.filename = filename;
        this.path = path;

        if(!fileOpened)
            openFile();
    }

    public boolean openFile() {
        try {
            logFile = new BufferedWriter(new FileWriter(filename+".log", true));
            fileOpened = true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    protected void log(String line, Boolean logToFile) {
        log("debug",line);
    }

    protected String logLine(String line) {
        try {
            logFile.write(line);
            logFile.newLine();
            flush();
        } catch (NullPointerException npe) {
            log("FileStream cannot be written! File open? "+filename, false);

            log("Try to open File ...", false);
            openFile();
            // log(line);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return line;
    }

    public void flush() throws IOException {
        logFile.flush();
    }

}
