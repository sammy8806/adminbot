package de.steven_tappert.adminbot.components.ts3;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.TS3ApiAsync;
import com.github.theholywaffle.teamspeak3.TS3Config;
import com.github.theholywaffle.teamspeak3.TS3Query;
import com.github.theholywaffle.teamspeak3.api.ClientProperty;
import com.github.theholywaffle.teamspeak3.api.CommandFuture;
import com.github.theholywaffle.teamspeak3.api.event.*;
import com.github.theholywaffle.teamspeak3.api.wrapper.Channel;
import com.github.theholywaffle.teamspeak3.api.wrapper.Client;
import com.github.theholywaffle.teamspeak3.api.wrapper.ClientInfo;
import com.github.theholywaffle.teamspeak3.commands.Command;
import de.steven_tappert.adminbot.adminbot;
import de.steven_tappert.adminbot.components.AdminManager;
import de.steven_tappert.adminbot.components.AdminUser;
import de.steven_tappert.adminbot.components.ts3.listener.TS3ChatListener;
import de.steven_tappert.adminbot.components.xmpp.XmppBotCore;
import de.steven_tappert.tools.Logger;
import de.steven_tappert.tools.SingletonHelper;
import org.jivesoftware.smack.chat2.ChatManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static de.steven_tappert.tools.Logger.log;
import static de.steven_tappert.tools.Logger.logShort;

public class Ts3BotCore {
    private String username;
    private String password;
    private Integer defaultServer;
    private String defaultClientName;

    private TS3Config config;
    private TS3Query query;
    private TS3ApiAsync api;

    public ChatManager chatManager;
    public AdminManager adminManager;

    private UserManager userManager;
    private ChannelManager channelManager;

    private boolean _connected = false;
    private String clientName;
    int clientId;

    Ts3BotCore(TS3Config config) {
        this.config = config;
        this.userManager = new UserManager();
        this.channelManager = new ChannelManager();
        this.chatManager = ChatManager.getInstanceFor((XmppBotCore) SingletonHelper.getInstance("XmppBotCore"));
        this.adminManager = ((adminbot) SingletonHelper.getInstance("adminbot")).adminManager;

        this.defaultClientName = "";
    }

    void setUsername(String username) {
        this.username = username;
    }

    void setPassword(String password) {
        this.password = password;
    }

    public void setDefaultServer(Integer defaultServer) {
        this.defaultServer = defaultServer;
    }

    public int getClientId() {
        return clientId;
    }

    void connect() {
        log(this, "connect", "info", "Connect");
        this.query = new TS3Query(config);
        this.query.connect();
        this.api = query.getAsyncApi();

        login();

        // api.getServerIdByPort(9987)
        selectDefaultServer(defaultServer);
        setClientName(defaultClientName);
        getClientData();

        api.registerEvent(TS3EventType.TEXT_CHANNEL, 0);
        api.registerEvent(TS3EventType.SERVER);
        api.registerEvent(TS3EventType.CHANNEL);

        scanMasterUsers();

        // api.sendChannelMessage("At your service master!");

        api.addTS3Listeners(new TS3ChatListener(this));
        api.addTS3Listeners(new TS3EventAdapter() {
            @Override
            public void onChannelCreate(ChannelCreateEvent e) {
                super.onChannelCreate(e);

            }
        });
        api.addTS3Listeners(new TS3EventAdapter() {
            @Override
            public void onClientJoin(ClientJoinEvent e) {
                super.onClientJoin(e);
                log(this, "onClientJoin", "Debug", "Client join: " + e.getClientNickname());
                Integer clid = e.getClientId();
                CommandFuture<ClientInfo> client = api.getClientInfo(clid);
                try {
                    userManager.registerUser(clid, client.get());
                    log(this, "onClientJoin", "Debug", "Client join (" + client.get().getNickname() + ") #" + clid);
                } catch (InterruptedException e1) {
                    logShort("error", "Could not join channel #%d (No TS3 response)", clid);
                }
                scanMasterUsers();
                log(this, "onClientJoin", "Debug", "Got " + userManager.getUserCount() + " users");
            }

            @Override
            public void onClientLeave(ClientLeaveEvent e) {
                super.onClientLeave(e);
                log(this, "onClientJoin", "Debug", "Client leave: " + e.getClientId() + " (" + userManager.getUser(e.getClientId()).getNickname() + ")");
                userManager.removeUser(e.getClientId());
                log(this, "onClientJoin", "Debug", "Got " + userManager.getUserCount() + " users");
                scanMasterUsers();
            }

            @Override
            public void onClientMoved(ClientMovedEvent e) {
                super.onClientMoved(e);
                int clientId = e.getClientId();
                Client user = userManager.getUser(clientId);
                Integer oldCh = user.getChannelId();
                Integer newCh = e.getTargetChannelId();
                log(this, "onClientJoin", "Debug", "Client moved: " + user.getNickname() + " " + oldCh + "->" + newCh);

                CommandFuture<ClientInfo> clientInfo = api.getClientInfo(user.getId());
                try {
                    userManager.updateUser(user.getId(), clientInfo.get());
                } catch (InterruptedException e1) {
                    logShort("error", "Could not get ClientInfo for %s (no TS3 response)", user.getId());
                }
                log(this, "onClientJoin", "Debug", "Updated client: " + user.getNickname() + " " + user.getChannelId());

                if (e.getClientId() == getClientId()) return;
                scanMasterUsers();
            }
        });
    }

    private void scanMasterUsers() {
        List<Integer> masterClients = new ArrayList<>();
        if (adminManager.users.size() < 1) {
            log(this, "connect", "info", "No master users defined ... skipping registering");
        } else {
            userManager.getUsers().forEach((integer, client) -> {
                try {
                    if (adminManager.getAdminByTs3uid(client.getUniqueIdentifier()).level > 0 &&
                            !client.getPlatform().equalsIgnoreCase("ServerQuery")) {
                        log(this, "connect", "info", "Found MasterClient for " + client.getNickname());
                        masterClients.add(client.getId());
                    }
                } catch (NullPointerException npe) {
                    log(this, "connect", "info", "Skipping faulty Client #" + integer);
                }
            });

            if (masterClients.size() > 0) {
                log(this, "connect", "info", "Moving to first master client");
                Client client = userManager.getUser(masterClients.get(0));
                api.moveQuery(client.getChannelId());
            } else {
                log(this, "connect", "info", "Found no master client");
            }
        }
    }

    private void getClientData() {
        log(this, "getClientData", "Debug", "Getting local client id");
        try {
            this.clientId = api.whoAmI().get().getId();
        } catch (InterruptedException e) {
            Logger.error("Could not get clientId (no TS3 response) ... retrying");
            getClientData();
            return;
        }
        log(this, "getClientData", "Debug", "ClientId: " + this.clientId);

        log(this, "getClientData", "Debug", "Getting active user list");
        List<Client> clients = null;
        try {
            clients = api.getClients().get();
        } catch (InterruptedException e) {
            Logger.error("Could not get client list (no TS3 response) ... retrying");
            getClientData();
            return;
        }
        for (Client client : clients) {
            userManager.registerUser(client.getId(), client);
        }
        log(this, "getClientData", "Debug", "Got " + userManager.getUserCount() + " users");

        /*
        // Does not work properly! :(
        log(this, "getClientData", "Debug", "Registering for every channel-chat");
        api.getChannels().forEach(channel -> {
            log(this, "getClientData", "Debug", "Registering for channel-chat #" + channel.getId());
            api.registerEvent(TS3EventType.TEXT_CHANNEL, channel.getId());
        });
        */
    }

    private boolean selectDefaultServer(Integer defaultServer) {
        log(this, "selectDefaultServer", "info", "Selecting");
        try {
            if (api.selectVirtualServerById(defaultServer).get()) {

                log(this, "selectDefaultServer", "info", "Selected defaultServer " + defaultServer);
                return true;
            }
        } catch (InterruptedException e) {
            Logger.error("Could not select server (No TS3 response) ... retrying");
            return selectDefaultServer(defaultServer);
        }

        log(this, "selectDefaultServer", "info", "Failed selecting defaultServer " + defaultServer);
        return false;
    }

    private boolean login() {
        log(this, "login", "info", "Login");
        try {
            if (this.api.login(username, password).get()) {
                this._connected = true;
                log(this, "login", "info", "Login seems successful");
                return true;
            }
        } catch (InterruptedException e) {
            Logger.error("Could not login (No TS3 response) ... retrying");
            return login();
        }

        return false;
    }

    public boolean setClientName(String clientName) {
        try {
            if (api.setNickname(clientName).get()) {
                log(this, "setClientName", "info", "Set Nickname to " + clientName);
                this.clientName = clientName;
                return true;
            }
        } catch (InterruptedException e) {
            Logger.error("Could net set the clientName (No TS3 response) ... retrying");
            return setClientName(clientName);
        }

        log(this, "setClientName", "info", "Could not set nickname");
        return false;
    }

    public UserManager getUserManager() {
        return userManager;
    }

    public List<Channel> getChannels() {
        try {
            return api.getChannels().get();
        } catch (InterruptedException e) {
            Logger.error("Could not get channel list (No TS3 response) ... retrying");
            return getChannels();
        }
    }

    public boolean goChannel(int cid) {
        log(this, "goChannel", "info", "Going into channel " + cid);
        api.moveQuery(cid);

        return true;
    }

    public void sendMessageToChannel(String msg) {
        api.sendChannelMessage(msg);
    }

    public void setDefaultClientName(String defaultClientName) {
        this.defaultClientName = defaultClientName;
    }
}
