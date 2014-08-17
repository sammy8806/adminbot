package de.steven_tappert.tools;

import java.io.IOException;
import java.io.OutputStream;

public class LogStream extends OutputStream {

    private StringBuilder error = new StringBuilder();

    @Override
    public void write(int b) throws IOException {
        error.append((byte)b);
        System.out.print((byte)b);
    }

    public void write(byte[] b) throws IOException {
        for(byte b1 : b)
            write(b1);
    }


    public void flush() {
        Logger.log("fatal-error", error.toString());
        error.delete(0,error.length());
    }

}
