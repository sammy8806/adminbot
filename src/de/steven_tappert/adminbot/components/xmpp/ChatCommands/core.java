package de.steven_tappert.adminbot.components.xmpp.ChatCommands;

import de.steven_tappert.adminbot.components.xmpp.XmppBotCore;
import de.steven_tappert.adminbot.components.xmpp.XmppCommandCache;
import de.steven_tappert.tools.Logger;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;

import java.util.Map;

public class core extends XmppChatCmd {

    public core() {
        setCommandName("core");
        setCommandSyntax("!core [load|unload|unloadall|reload|list|shutdown] ([command])");
        setCommandAuthLevel(4);
    }

    @Override
    public void runCommand(XMPPConnection conn, Chat chat, Message message) {
        String[] args = message.getBody().split("\\s");

        try {
            if (args[1].equalsIgnoreCase("load")) {
                loadCommand(args[2]);
                chat.sendMessage("Command \""+args[2]+"\" loaded!");
            } else if (args[1].equalsIgnoreCase("unload")) {
                unloadCommand(args[2]);
                chat.sendMessage("Command \""+args[2]+"\" unloaded!");
            } else if (args[1].equalsIgnoreCase("unloadall")) {
                XmppCommandCache.reloadAllCommands();
                chat.sendMessage("All Commands unloaded");
            } else if (args[1].equalsIgnoreCase("reload")) {
                unloadCommand(args[2]);
                loadCommand(args[2]);
                chat.sendMessage("Command \""+args[2]+"\" reloaded!");
            } else if (args[1].equalsIgnoreCase("list")) {
                for (Map.Entry<String, XmppChatCommand> entry : XmppCommandCache.getCommands().entrySet()) {
                    try {
                        chat.sendMessage("Loaded: " + entry.getKey());
                    } catch (XMPPException e) {
                        e.printStackTrace();
                    }
                }
            } else if (args[1].equalsIgnoreCase("shutdown")) {
                chat.sendMessage("Bot is shutting down!");
                XmppBotCore.shutdown = true;
            } else
            sendSyntax(chat);
        } catch (ArrayIndexOutOfBoundsException e) {
            sendSyntax(chat);
        } catch (XMPPException e) {
            e.printStackTrace();
        }

    }

    private void unloadCommand(String command) {
        XmppCommandCache.removeCommand(command);
    }

    private void loadCommand(String command) {
        try {
            XmppCommandCache.getCommand(command);
        } catch (ClassNotFoundException e) {
            Logger.log(this, "runCommand", "info", "Command \"" + command + "\" not found!");
        }
    }

    private void sendSyntax(Chat chat) {
        try {
            chat.sendMessage(getCommandSyntax());
        } catch (XMPPException e) {
            e.printStackTrace();
        }
    }
}
