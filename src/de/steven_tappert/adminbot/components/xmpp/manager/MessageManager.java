package de.steven_tappert.adminbot.components.xmpp.manager;

import de.steven_tappert.adminbot.components.xmpp.ChatCommands.XmppChatCommand;
import de.steven_tappert.adminbot.components.xmpp.XmppBotCore;
import de.steven_tappert.adminbot.components.xmpp.XmppCommandCache;
import de.steven_tappert.adminbot.components.xmpp.XmppUser;
import de.steven_tappert.tools.Logger;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;

public class MessageManager implements MessageListener {

    private String cmdIdent = "!";

    @Override
    public void processMessage(Chat chat, Message message) {
        // Logger.log("debug", "[Message Manager::processMessage()] \n" + message.toXML());
        if (message.getBody() != null) {
            Logger.log(this, "processMessage", "debug", "From: " +
                    XmppUser.getNameFromJID(message.getFrom()) + " Body: " + message.getBody());

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
                    if (chat != null)
                        chat.sendMessage("Not a valid command! [" + message.getBody() + "]");
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
            chatCmd = XmppCommandCache.getCommand(command);
            chatCmd.runCommand(XmppBotCore.botXmppCore, chat, message);
            Logger.log(this, "getCommand", "debug", "After Call");
        } catch (ClassNotFoundException e) {
            chat.sendMessage("Command \"" + message.getBody().substring(1) + "\" not found!");
        }
    }
}
