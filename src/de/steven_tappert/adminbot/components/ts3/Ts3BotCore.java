package de.steven_tappert.adminbot.components.ts3;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.TS3Config;
import com.github.theholywaffle.teamspeak3.TS3Query;
import com.github.theholywaffle.teamspeak3.api.ClientProperty;
import com.github.theholywaffle.teamspeak3.api.event.*;
import com.github.theholywaffle.teamspeak3.api.wrapper.Channel;
import com.github.theholywaffle.teamspeak3.api.wrapper.Client;
import de.steven_tappert.adminbot.adminbot;
import de.steven_tappert.adminbot.components.AdminManager;
import de.steven_tappert.adminbot.components.AdminUser;
import de.steven_tappert.adminbot.components.ts3.listener.TS3ChatListener;
import de.steven_tappert.adminbot.components.xmpp.XmppBotCore;
import de.steven_tappert.tools.SingletonHelper;
import org.jivesoftware.smack.chat2.ChatManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static de.steven_tappert.tools.Logger.log;

public class Ts3BotCore {
    private String username;
    private String password;
    private Integer defaultServer;

    private TS3Config config;
    private TS3Query query;
    private TS3Api api;

    public ChatManager chatManager;
    public AdminManager adminManager;

    private UserManager userManager;
    private ChannelManager channelManager;

    private boolean _connected = false;
    private String clientName;
    int clientId;

    private List<String> masterUsers;

    Ts3BotCore(TS3Config config) {
        this.config = config;
        this.userManager = new UserManager();
        this.channelManager = new ChannelManager();
        this.masterUsers = new ArrayList<>();
        this.chatManager = ChatManager.getInstanceFor((XmppBotCore) SingletonHelper.getInstance("XmppBotCore"));
        this.adminManager = ((adminbot) SingletonHelper.getInstance("adminbot")).adminManager;
    }

    public void addMasterUser(String uid) {
        masterUsers.add(uid);
        scanMasterUsers();
    }

    public void removeMasterUser(String uid) {
        masterUsers.remove(uid);
        scanMasterUsers();
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
        this.api = query.getApi();

        login();

        // api.getServerIdByPort(9987)
        selectDefaultServer(defaultServer);
        setClientName("TS3Bot");
        getClientData();

        api.registerEvent(TS3EventType.TEXT_CHANNEL, 0);
        api.registerEvent(TS3EventType.SERVER);
        api.registerEvent(TS3EventType.CHANNEL);

        scanMasterUsers();

        api.sendChannelMessage("At your service master!");

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
                Client client = api.getClientInfo(clid);
                userManager.registerUser(clid, client);
                log(this, "onClientJoin", "Debug", "Client join (" + client.getNickname() + ") #" + clid);
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

                userManager.updateUser(user.getId(), api.getClientInfo(user.getId()));
                log(this, "onClientJoin", "Debug", "Updated client: " + user.getNickname() + " " + user.getChannelId());

                if(e.getClientId() == getClientId()) return;
                scanMasterUsers();
            }
        });
    }

    private void scanMasterUsers() {
        List<Integer> masterClients = new ArrayList<>();
        if (masterUsers == null || masterUsers.size() < 1) {
            log(this, "connect", "info", "No master users defined ... skipping registering");
        } else {
            userManager.getUsers().forEach((integer, client) -> {
                try {
                    if (masterUsers.contains(client.getUniqueIdentifier()) &&
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
        this.clientId = api.whoAmI().getId();
        log(this, "getClientData", "Debug", "ClientId: " + this.clientId);

        log(this, "getClientData", "Debug", "Getting active user list");
        List<Client> clients = api.getClients();
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
        if (api.selectVirtualServerById(defaultServer)) {

            log(this, "selectDefaultServer", "info", "Selected defaultServer " + defaultServer);
            return true;
        }

        log(this, "selectDefaultServer", "info", "Failed selecting defaultServer " + defaultServer);
        return false;
    }

    private boolean login() {
        log(this, "login", "info", "Login");
        if (this.api.login(username, password)) {
            this._connected = true;
            log(this, "login", "info", "Login seems successful");
            return true;
        }

        return false;
    }

    public boolean setClientName(String clientName) {
        if (api.setNickname(clientName)) {
            log(this, "setClientName", "info", "Set Nickname to " + clientName);
            this.clientName = clientName;
            return true;
        }

        log(this, "setClientName", "info", "Could not set nickname");
        return false;
    }

    public UserManager getUserManager() {
        return userManager;
    }

    public List<Channel> getChannels() {
        return api.getChannels();
    }

    public boolean goChannel(int cid) {
        log(this, "goChannel", "info", "Going into channel " + cid);
        api.moveQuery(cid);

        return true;
    }

    public void sendMessageToChannel(String msg) {
        api.sendChannelMessage(msg);
    }
}
