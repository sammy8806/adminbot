package de.steven_tappert.adminbot.components.xmpp.ChatCommands;

import de.steven_tappert.adminbot.components.xmpp.XmppCommandCache;
import de.steven_tappert.tools.Logger;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;

import java.util.HashSet;
import java.util.Map;

public class help extends XmppChatCmd {

    public help() {
        setCommandName("help");
        setCommandDescription("Displays the Help");
        setCommandSyntax("!help [cmdname]");
        setCommandAuthLevel(0);
    }

    public synchronized void runCommand(XMPPConnection conn, Chat chat, Message message) {
        super.getArgs(message);

        try {
            if (args.length == 1) {
                Logger.log(this, "runCommand", "debug", "Begin Execute");
                String msg = "";
                // msg = "Commands: ";
                msg += getHelpLevel(4);
                msg += getHelpLevel(3);
                msg += getHelpLevel(2);
                msg += getHelpLevel(1);
                msg += getHelpLevel(0);
                Logger.log(this, "runCommand", "debug", "MSG: " + msg);
                chat.sendMessage(msg);
            } else if (args.length == 2) {
                chat.sendMessage(
                        XmppCommandCache.
                        getCommand(
                            args[2].replaceFirst("^!","").trim()
                        ).getCommandSyntax()
                );
            }
        } catch (XMPPException e) {
            e.printStackTrace();
            Logger.log(this, "runCommand", "error", e.getMessage());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        Logger.log(this, "runCommand", "debug", "After Execute!");
    }

    public String getHelpLevel(Integer level) {
        String out = "";
        HashSet<String> cmds = new HashSet<String>();

        for (Map.Entry<String, Integer> entry : XmppCommandCache.getCommandsWithLevels().entrySet()) {
            if (entry.getValue().equals(level))
                cmds.add(entry.getKey());
        }

        Logger.log(this, "getHelpLevel", "debug", "CMD Size: " + cmds.size());

        if (cmds.size() > 0)
            out += "Level: " + level + "\n";

        for (String entry : cmds) {
            out += entry + ", ";
        }

        out += "\n";
        cmds.clear();

        if (out.length() > 2)
            return out.substring(0, out.length() - 2);
        else
            return "";
    }
}
