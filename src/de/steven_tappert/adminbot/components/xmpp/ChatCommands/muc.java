package de.steven_tappert.adminbot.components.xmpp.ChatCommands;

import de.steven_tappert.adminbot.components.xmpp.manager.ConfigManager;
import de.steven_tappert.adminbot.components.xmpp.manager.XmppMucManager;
import de.steven_tappert.tools.SingletonHelper;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;

import java.util.Properties;

public class muc extends XmppChatCmd {

    XmppMucManager mucManager;

    public muc() {
        setCommandName("muc");
        setCommandSyntax("!muc [load|unload|unloadall|reload|list] ([command])");
        setCommandAuthLevel(4);

        SingletonHelper.registerInstanceOnce(new XmppMucManager());
        mucManager = (XmppMucManager) SingletonHelper.getInstance("XmppMucManager");

        parseConfig();
    }

    protected void parseConfig() {
        ConfigManager conf = new ConfigManager("muc.properties");
        Properties properties = conf.getProperties();

        String[] autoloadActions = properties.getProperty("autoloadActions").split(",");
        for (String action : autoloadActions)
            loadAction(action.trim());

        String[] autojoinRoom = properties.getProperty("autojoinRoom").split(",");
        for (String room : autojoinRoom)
            joinRoom(room.trim());
    }

    @Override
    public void runCommand(XMPPConnection conn, Chat chat, Message message) {
        String[] args = message.getBody().split("\\s");
        String room;

        try {
            if (args[1].equalsIgnoreCase("list")) {
            } else if (args[1].equalsIgnoreCase("register")) {
                mucManager.registerMucAction(args[2]);
            } else if (args[1].equalsIgnoreCase("unregister")) {
                mucManager.unregisterMucAction(args[2]);
            } else if (args[1].equalsIgnoreCase("join")) {
                String tmp = message.getBody();
                tmp = tmp.replaceFirst(args[0], "").trim();
                tmp = tmp.replaceFirst(args[1], "").trim();
                joinRoom(tmp);
            } else if (args[1].equalsIgnoreCase("part")) {
                String tmp = message.getBody();
                tmp = tmp.replaceFirst(args[0], "").trim();
                tmp = tmp.replaceFirst(args[1], "").trim();
                partRoom(tmp);
            } else
                sendSyntax(chat);
        } catch (ArrayIndexOutOfBoundsException e) {
            sendSyntax(chat);
        }

    }

    private void joinRoom(String room) {
        mucManager.joinRoom(room);
    }

    private void partRoom(String room) {
        mucManager.partRoom(room);
    }

    private void unloadAction(String action) {
        mucManager.unregisterMucAction(action);
    }

    private void loadAction(String action) {
        mucManager.registerMucAction(action);
    }

    private void sendSyntax(Chat chat) {
        try {
            chat.sendMessage(getCommandSyntax());
        } catch (XMPPException e) {
            e.printStackTrace();
        }
    }
}