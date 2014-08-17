package de.steven_tappert.tools;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Logger {

    protected static String defaultLogType = "INFO";
    protected static SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
    protected static SimpleDateFormat dfl = new SimpleDateFormat("yyyy_MM_dd");

    protected static String filename = "xmppbot";

    protected static String activeFile = "";

    private static BufferedWriter logfile;

    public static void log(String line) {
        output(formatLogString(line));
    }

    public static void log(String prefix, String line) {
        output(formatLogString(prefix, line));
    }

    public static void log(Object obj, String prefix, String line) {
        output(formatLogString(prefix, "[" + obj.getClass().getSimpleName() + "] " + line));
    }

    public static void log(String objName, String prefix, String line) {
        output(formatLogString(prefix, "[" + objName + "] " + line));
    }

    public static void log(String objName, String func, String prefix, String line) {
        output(formatLogString(prefix, "[" + objName + "::" + func + "()] " + line));
    }

    public static void log(Object obj, String func, String prefix, String line) {
        output(formatLogString(prefix, "[" + obj.getClass().getSimpleName() + "::" + func + "()] " + line));
    }

    protected static String formatLogString(String line) {
        return formatLogString(defaultLogType, line);
    }

    protected static String formatLogString(String prefix, String line) {
        return "[" + df.format(new Date()) + "] [" + prefix.toUpperCase(Locale.getDefault()) + "] " + line;
    }

    protected static void output(String str) {
        output(str, "logs");
    }

    public static void output(String str, String path) {
        System.out.println(str);

        try {
            String filestr = path + "/" + filename + "_" + dfl.format(new Date()) + ".log";
            if (!activeFile.equals(filestr)) {
                System.out.println("New logfile is: " + filestr);
                if (logfile != null)
                    logfile.close();

                activeFile = filestr;
                logfile = new BufferedWriter(new FileWriter(activeFile, true));
                System.out.println("Logfile opened!");
            }

            if (logfile != null) {
                logfile.write(str);
                logfile.newLine();
                logfile.flush();
            }
        } catch (IOException e) {
            System.err.println("[ERROR] Error while writing logfile: " + e);
        }
    }

}
