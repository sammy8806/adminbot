package de.steven_tappert.adminbot.handler;

import java.io.*;

public class BotErrorHandler extends PrintStream {

    public BotErrorHandler(OutputStream out) {
        super(out);
    }

    public BotErrorHandler(OutputStream out, boolean autoFlush) {
        super(out, autoFlush);
    }

    public BotErrorHandler(OutputStream out, boolean autoFlush, String encoding) throws UnsupportedEncodingException {
        super(out, autoFlush, encoding);
    }

    public BotErrorHandler(String fileName) throws FileNotFoundException {
        super(fileName);
    }

    public BotErrorHandler(String fileName, String csn) throws FileNotFoundException, UnsupportedEncodingException {
        super(fileName, csn);
    }

    public BotErrorHandler(File file) throws FileNotFoundException {
        super(file);
    }

    public BotErrorHandler(File file, String csn) throws FileNotFoundException, UnsupportedEncodingException {
        super(file, csn);
    }
}
