package de.steven_tappert.adminbot.components.xmpp;

import de.steven_tappert.adminbot.BotComponent;
import de.steven_tappert.adminbot.components.xmpp.manager.ConfigManager;
import de.steven_tappert.tools.SingletonHelper;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.XMPPConnection;

import java.util.Properties;

public class XmppComponent implements BotComponent {

    private String XmppServer = "chat.eu.lol.riotgames.com";
    private int XmppPort;
    private String XmppUser = "sammy9S3";
    private String XmppUsername = "sammy9S3";
    private String XmppPassword = "AIR_";
    private String XmppServiceName = "pvp.net";

    private XmppBotCore core;

    public String getComponentName() {
        return this.getClass().getName();
    }

    public boolean loadComponent() {
        ConfigManager configManager = new ConfigManager("login.properties");
        Properties properties = configManager.getProperties();

        XmppServer = properties.getProperty("server");
        XmppPort = Integer.parseInt(properties.getProperty("port", "5223"));
        XmppUser = properties.getProperty("username");
        XmppUsername = properties.getProperty("showName");
        XmppPassword = properties.getProperty("password");
        XmppServiceName = properties.getProperty("resource");

        // properties.list(System.out);

        // Logger.log(this, "loadComponent", "debug",);

        ConnectionConfiguration conf = new ConnectionConfiguration(XmppServer, XmppPort, XmppServiceName);

        // Folgende Zeilen regeln die Loginmethode (SSL-Legacy, SASL)
        conf.setSecurityMode(ConnectionConfiguration.SecurityMode.legacy);
        conf.setSASLAuthenticationEnabled(true);
        conf.setReconnectionAllowed(true);

        conf.setDebuggerEnabled(false);
        XMPPConnection.DEBUG_ENABLED = false;

        return startComponent(conf);
    }

    public boolean unloadComponent() {
        core.shutdown();
        return true;
    }

    public boolean startComponent(ConnectionConfiguration conf) {
        try {
            SingletonHelper.registerInstance(new XmppBotCore(conf));

            core = (XmppBotCore) SingletonHelper.getInstance("XmppBotCore");
            core.setXmppUser(XmppUser);
            core.setXmppPassword(XmppPassword);
            core.setXmppUsername(XmppUsername);

            core.connect();

        } catch (AbstractMethodError e) {
            unloadComponent();
            startComponent(conf);
        }

        return false;
    }
}



