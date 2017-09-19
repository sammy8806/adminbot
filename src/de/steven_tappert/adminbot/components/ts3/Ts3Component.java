package de.steven_tappert.adminbot.components.ts3;

import com.github.theholywaffle.teamspeak3.TS3Config;
import de.steven_tappert.adminbot.BotComponent;
import de.steven_tappert.adminbot.components.xmpp.manager.ConfigManager;
import de.steven_tappert.tools.SingletonHelper;

import java.util.Properties;

public class Ts3Component implements BotComponent {

    private Ts3BotCore core;
    private ConfigManager configManager;

    private String Username;
    private String Password;
    private int DefaultServer;

    @Override
    public String getComponentName() {
        return this.getClass().getName();
    }

    @Override
    public boolean loadComponent() {
        configManager = new ConfigManager("ts3.properties");
        Properties properties = configManager.getProperties();

        String Hostname = properties.getProperty("hostname");
        Integer Port = Integer.parseInt(properties.getProperty("port", "10011"));
        Username = properties.getProperty("user");
        Password = properties.getProperty("password");
        DefaultServer = Integer.parseInt(properties.getProperty("defaultserver"));

        final TS3Config config = new TS3Config();
        config.setHost(Hostname);
        config.setQueryPort(Port);

        return startComponent(config);
    }

    @Override
    public boolean unloadComponent() {
        return false;
    }

    private boolean startComponent(TS3Config config) {

        try {
            SingletonHelper.registerInstance(new Ts3BotCore(config));

            core = (Ts3BotCore) SingletonHelper.getInstance("Ts3BotCore");
            core.setUsername(Username);
            core.setPassword(Password);
            core.setDefaultServer(DefaultServer);
            core.setDefaultClientName(configManager.getProperties().getProperty("clientname", "TS3Bot"));
            core.connect();

            return true;
        } catch (AbstractMethodError e) {
            unloadComponent();
            startComponent(config);
        }

        return false;
    }
}
