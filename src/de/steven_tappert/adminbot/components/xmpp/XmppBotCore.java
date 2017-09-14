package de.steven_tappert.adminbot.components.xmpp;

import de.steven_tappert.adminbot.components.xmpp.listener.XmppRosterListener;
import de.steven_tappert.adminbot.components.xmpp.manager.ConfigManager;
import de.steven_tappert.adminbot.components.xmpp.manager.IncomingMessageManager;
import de.steven_tappert.adminbot.components.xmpp.manager.PresenceManager;
import de.steven_tappert.tools.Logger;
import de.steven_tappert.tools.SingletonHelper;
import org.jivesoftware.smack.*;
import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smack.chat2.ChatManager;
import org.jivesoftware.smack.chat2.OutgoingChatMessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jxmpp.jid.EntityBareJid;

import java.io.IOException;
import java.util.Properties;

import static de.steven_tappert.tools.Logger.log;

public class XmppBotCore extends XMPPTCPConnection {

    private String xmppPassword;
    private String xmppUser;
    private String xmppUsername;

    private boolean exit = false;

    public static boolean shutdown = false;

    private RosterManager rM;

    private PresenceManager pM;
    private IncomingMessageManager mM;
    private Integer watchdogTimer = 7 * 1000;

    private boolean watchdogEnable = true;
    private boolean watchdogReconnect = true;
    private Thread watchdog;
    public static XMPPTCPConnection botXmppCore;

    private boolean shouldBeConnected = true;

    public XmppBotCore(XMPPTCPConnectionConfiguration conf) {
        super(conf);

        if (watchdogEnable) {
            Thread watchdog = new Thread(new XmppWatchdog());
            watchdog.start();
        }

        log("debug", "Construct");
        Thread holdOpen = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!exit) {
                    // log(this, "Thread[HoldOpen::Run()]", "debug", "XmppBotCore Heartbeat!");
                    try {
                        Thread.sleep(5 * 1000);
                    } catch (InterruptedException e) {
                        log(this, "Thread[HoldOpen::Run()]", "error", "XmppBotCore died [Thread.sleep died]");
                        break;
                    }
                }
            }
        });
        holdOpen.start();

        shouldBeConnected = true;

        XmppBotCore.botXmppCore = this;

        // Do some init stuff
        rM = new RosterManager(this);
        // rM.getRoster().setSubscriptionMode(Roster.SubscriptionMode.accept_all);
        rM.getRoster().setSubscriptionMode(Roster.SubscriptionMode.manual);

        pM = new PresenceManager();

        // Register some Handlers
        rM.getRoster().addRosterListener(new XmppRosterListener());
        ChatManager chatManager = ChatManager.getInstanceFor(botXmppCore);
        chatManager.addIncomingListener(new IncomingMessageManager());
        chatManager.addOutgoingListener((to, message, chat) -> {
            message.setType(Message.Type.chat);
        });
    }

    public XMPPConnection getInstance() {
        return this;
    }

    public String getXmppUsername() {
        return xmppUsername;
    }

    public void setXmppUsername(String xmppUsername) {
        this.xmppUsername = xmppUsername;
    }

    public AbstractXMPPConnection connect() {
        try {
            if (!isConnected()) {
                log(this, "connect", "info", "Connect");
                try {
                    super.connect();
                } catch (NullPointerException npe) {
                    System.exit(1);
                } catch (InterruptedException | IOException | SmackException e) {
                    e.printStackTrace();
                }
            }

            if (!isConnected()) {
                Thread wait = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(10 * 1000);
                            log(this, "run", "debug", "Wait 10sec for reconnect!");
                            connect();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });
                wait.start();
            }

            if (!isAuthenticated()) {
                log(this, "connect", "info", "Login");
                super.login(xmppUser, xmppPassword);
            }

            if (!isAuthenticated()) {
                Thread wait = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(10 * 1000);
                            log(this, "run", "debug", "Wait 10sec for relogin!");
                            connect();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });
                wait.start();
            }

        } catch (XMPPException e) {
            e.printStackTrace();
            log(this, "connect", "error", "Message: " + e.getMessage());
            log(this, "connect", "error", "Try to reconnect in 5sec ...");
            try {
                Thread.sleep(5 * 1000);
            } catch (InterruptedException ignored) {
            }
            log(this, "connect", "error", "Try to reconnect ...");
            connect();
        } catch (InterruptedException | IOException | SmackException e) {
            e.printStackTrace();
        }

        afterConnect();

        return this;
    }

    public void afterConnect() {
        ConfigManager configManager = new ConfigManager("XmppBotCore.properties");
        Properties properties = configManager.getProperties();

        String[] autoloadCommands = properties.getProperty("autoloadCommands").split(",");

        for (String cmd : autoloadCommands) {
            try {
                XmppCommandCache.getCommand(cmd.trim());
            } catch (ClassNotFoundException e) {
                log(this, "afterConnect", "error", "Command \"" + cmd + "\" cannot be loaded!");
            }
        }

        String subscriptionMode = properties.getProperty("subscriptionMode");
        if (subscriptionMode.equals("accept_all"))
            rM.getRoster().setSubscriptionMode(Roster.SubscriptionMode.accept_all);
        else if (subscriptionMode.equals("manual"))
            rM.getRoster().setSubscriptionMode(Roster.SubscriptionMode.manual);
        else if (subscriptionMode.equals("reject_all"))
            rM.getRoster().setSubscriptionMode(Roster.SubscriptionMode.reject_all);

    }

    public void setXmppPassword(String xmppPassword) {
        this.xmppPassword = xmppPassword;
    }

    public void setXmppUser(String xmppUser) {
        this.xmppUser = xmppUser;
    }

    public void shutdown() {
        log(this, "shutdown", "info", "XmppBotCore is shutting down!");
        shouldBeConnected = false;
        watchdogEnable = false;
        unloadAllCommands();
        disconnect();
        exit = true;
        log(this, "shutdown", "info", "XmppBotCore shutted down!");
    }

    private void unloadAllCommands() {
        XmppCommandCache.reloadAllCommands();
        // ((XmppMucManager) SingletonHelper.getInstance("xmppmucmanager")).partAllRooms();
    }


    protected class XmppWatchdog implements Runnable {
        public void run() {
            while (watchdogEnable) {
                if (shutdown) {
                    shutdown();
                    break;
                }

                try {
                    Thread.sleep(watchdogTimer);

                    if (!isConnected()) {
                        Logger.log(this, "run", "error", "Watchdog Alert! Try to restart!");
                        disconnect();
                        connect();
                    }

                } catch (InterruptedException e) {
                    // Nothing
                }
            }
        }
    }
}
