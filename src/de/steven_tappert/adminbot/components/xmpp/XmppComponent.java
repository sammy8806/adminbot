package de.steven_tappert.adminbot.components.xmpp;

import de.steven_tappert.adminbot.BotComponent;
import de.steven_tappert.adminbot.components.xmpp.manager.ConfigManager;
import de.steven_tappert.tools.SingletonHelper;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jxmpp.stringprep.XmppStringprepException;

import java.util.Properties;

public class XmppComponent implements BotComponent {

    private String XmppServer = "";
    private int XmppPort;
    private String XmppResource = "JavaBot";
    private String XmppUsername = "";
    private String XmppPassword = "";
    private String XmppServiceName = "";

    private XmppBotCore core;

    public String getComponentName() {
        return this.getClass().getName();
    }

    public boolean loadComponent() {
        ConfigManager configManager = new ConfigManager("login.properties");
        Properties properties = configManager.getProperties();

        XmppServer = properties.getProperty("server");
        XmppPort = Integer.parseInt(properties.getProperty("port", "5222"));
        XmppResource = properties.getProperty("username");
        XmppUsername = properties.getProperty("showName");
        XmppPassword = properties.getProperty("password");
        XmppServiceName = properties.getProperty("resource");

        // properties.list(System.out);

        // Logger.log(this, "loadComponent", "debug",);

        //  (XmppServer, XmppPort, XmppServiceName)
        try {
            XMPPTCPConnectionConfiguration conf = XMPPTCPConnectionConfiguration.builder()
                    .setUsernameAndPassword(XmppUsername, XmppPassword)
                    .setXmppDomain(XmppServiceName)
                    .setHost(XmppServer)
                    .setPort(XmppPort)
                    .setResource(XmppResource)
                    .setSecurityMode(XMPPTCPConnectionConfiguration.SecurityMode.required)
                    .setDebuggerEnabled(false) // GUI-Debugger
                    .build();

            return startComponent(conf);

        } catch (XmppStringprepException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean unloadComponent() {
        core.botShutdown();
        return true;
    }

    public boolean startComponent(XMPPTCPConnectionConfiguration conf) {
        try {
            SingletonHelper.registerInstance(new XmppBotCore(conf));

            core = (XmppBotCore) SingletonHelper.getInstance("XmppBotCore");
            core.setXmppUser(XmppResource);
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
