package de.steven_tappert.tools;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class SimpleFileReader {

    protected String f = "";

    public SimpleFileReader() {}
    public SimpleFileReader(String f) {
        this.f = f;
    }

    public ArrayList<String> read() {
        return read(f);
    }

    public ArrayList<String> read(String f) {
        BufferedReader in = null;
        ArrayList<String> read = new ArrayList<String>();
        try {
            in = new BufferedReader(new FileReader(f));
            String line;
            while ((line = in.readLine()) != null)
                read.add(line);
        } catch (FileNotFoundException e) {
            // Nothing
            Logger.log(this, "read", "info", "File not found: " + e);
        } catch (IOException e) {
            Logger.log(this, "read", "error", "Failed to read from list file: " + e);
        } finally {
            try {
                if (in != null)
                    in.close();
            } catch (IOException e) {
                // Nothing
            }
        }
        return read;
    }

}
