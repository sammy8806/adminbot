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

    public static void logShort(String prefix, String line, Object... args) {
        logIntern(prefix, line, args);
    }

    public static void log(Object obj, String func, String prefix, String line, Object... args) {
        logIntern(prefix, line, args);
    }

    private static void logIntern(String prefix, String line, Object... args) {
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        int element = (stackTraceElements.length <= 3 ? 2 : 3); // Go 3 levels up to the non Logger calling function
        String[] classNameArray = stackTraceElements[element].getClassName().split("\\.");
        String className = classNameArray.length > 0 ? classNameArray[classNameArray.length - 1] : "";
        output(formatLogString(prefix, "[%s:%d][%s::%s()] %s",
                stackTraceElements[element].getFileName(),
                stackTraceElements[element].getLineNumber(),
                className,
                stackTraceElements[element].getMethodName(),
                String.format(line, args)
        ));
    }

    public static void log(String objName, String func, String prefix, String line, Object... args) {
        output(formatLogString(prefix, "[%s::%s()] %s", objName, func, String.format(line, args)));
    }

    public static void debug(String line, Object... args) {
        logIntern("debug", line, args);
    }

    public static void info(String line, Object... args) {
        logIntern("info", line, args);
    }

    public static void warn(String line, Object... args) {
        logIntern("warn", line, args);
    }

    public static void error(String line, Object... args) {
        logIntern("error", line, args);
    }

    protected static String formatLogString(String line) {
        return formatLogString(defaultLogType, line);
    }

    protected static String formatLogString(String prefix, String line, Object... args) {
        return String.format(
                "[%s][%-5s] %s",
                df.format(new Date()),
                prefix.toUpperCase(Locale.getDefault()),
                String.format(line, args)
        );
        // return "[" + df.format(new Date()) + "] [" + prefix.toUpperCase(Locale.getDefault()) + "] " + line;
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
