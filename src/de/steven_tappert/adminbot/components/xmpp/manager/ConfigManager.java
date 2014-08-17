package de.steven_tappert.adminbot.components.xmpp.manager;

import de.steven_tappert.tools.Logger;

import java.io.*;
import java.util.Properties;

public class ConfigManager {

    protected String configFilename;

    protected Properties properties = new Properties();
    BufferedInputStream iStream;
    BufferedOutputStream oStream;

    public ConfigManager(String configFilename) {
        this.configFilename = configFilename;

        try {
            loadFile(configFilename);
        } catch (IOException ioe) {
            Logger.log(this, "ConfigManager", "debug", ioe.getMessage());
            try {
                writeFile(configFilename);
            } catch (IOException e) {
                Logger.log(this, "ConfigManager", "error", ioe.getMessage());
            }
        }
    }

    public Properties getProperties() {
        return properties;
    }

    protected void loadFile(String filename) throws IOException {
        properties.load(new FileInputStream(filename));
    }

    protected void writeFile(String filename) throws IOException {
        properties.store(new FileOutputStream(filename), "");
    }

}
