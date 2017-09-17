package de.steven_tappert.adminbot.components.xmpp.manager;

import de.steven_tappert.adminbot.adminbot;
import de.steven_tappert.adminbot.components.AdminManager;
import de.steven_tappert.adminbot.components.xmpp.ChatCommands.XmppChatCommand;
import de.steven_tappert.adminbot.components.xmpp.XmppBotCore;
import de.steven_tappert.adminbot.components.xmpp.XmppCommandCache;
import de.steven_tappert.adminbot.components.xmpp.XmppUser;
import de.steven_tappert.tools.Logger;
import de.steven_tappert.tools.SingletonHelper;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smack.chat2.IncomingChatMessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jxmpp.jid.EntityBareJid;

import java.nio.file.AccessDeniedException;

public class IncomingMessageManager implements IncomingChatMessageListener {

    private String cmdIdent = "!";
    private XmppBotCore core = null;

    private adminbot adminbot;

    public IncomingMessageManager() {
        if (adminbot == null) {
            adminbot = (adminbot) SingletonHelper.getInstance("adminbot");
        }
    }

    @Override
    public void newIncomingMessage(EntityBareJid from, Message message, Chat chat) {
        // Logger.log("debug", "[Message Manager::processMessage()] \n" + message.toXML());
        if (message.getBody() != null) {
            Logger.log(this, "processMessage", "debug", "From: " +
                    message.getFrom().getLocalpartOrNull() + " Body: " + message.getBody());

            try {
                if (message.getBody().startsWith(cmdIdent) && message.getBody().length() > 1) {
                    String[] commandArgs = message.getBody().replaceFirst(cmdIdent, "").split("\\s");
                    String commandString = commandArgs[0];

                    /*
                    if (commandArgs[1].equalsIgnoreCase("command")) {
                        if (commandArgs[1].equalsIgnoreCase("unload") || commandArgs[1].equalsIgnoreCase("reload")) {
                            XmppCommandCache.removeCommand(commandArgs[2]);
                            Logger.log(this, "processMessage", "debug", "Unloaded: " + commandString);
                        }
                    } */

                    loadCommand(commandString, chat, message);

                } else {
                    if (chat != null) {
                        loadCommand("chat", chat, message);
                        // chat.send("Not a valid command! [" + message.getBody() + "]");
                    }
                }

                // chat.sendMessage("Your Text was: " + message.getBody());
                // Logger.log(this, "processMessage", "debug", "Your Text was: " + message.getBody());
            } catch (XMPPException e) {
                Logger.log(this, "processMessage", "debug", e.getMessage());
            }
        }
    }

    private void loadCommand(String command, Chat chat, Message message) throws XMPPException {
        try {
            Logger.log(this, "getCommand", "debug", "Beforce Call [" + command + "]");
            XmppChatCommand chatCmd = null;

            Integer adminLevel = adminbot.adminManager.getUserLevelWithJid(message.getFrom().asBareJid());

            if (adminLevel == -1) {
                adminLevel = 0;
                command = "info";
            }

            chatCmd = XmppCommandCache.getCommand(command);

            if (adminLevel < chatCmd.getCommandAuthLevel()) {
                chat.send(String.format("Command level too low! %d (Required: %d)", adminLevel, chatCmd.getCommandAuthLevel()));
                Logger.log(this, "getCommand", "info", String.format(
                        "Denied execution of command '%s' for '%s'", command, message.getFrom().asBareJid()
                ));

            } else {
                chatCmd.runCommand(XmppBotCore.botXmppCore, chat, message);
            }

            Logger.log(this, "getCommand", "debug", "After Call");
        } catch (ClassNotFoundException e) {
            try {
                chat.send("Command \"" + message.getBody().substring(1) + "\" not found!");
            } catch (SmackException.NotConnectedException | InterruptedException e1) {
                e1.printStackTrace();
            }
        } catch (SmackException.NotConnectedException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public IncomingMessageManager setCore(XmppBotCore core) {
        this.core = core;
        return this;
    }
}
