package de.steven_tappert.adminbot.components.xmpp.ChatCommands;

import de.steven_tappert.adminbot.adminbot;
import de.steven_tappert.adminbot.components.AdminUser;
import de.steven_tappert.adminbot.components.xmpp.XmppCommandCache;
import de.steven_tappert.tools.Logger;
import de.steven_tappert.tools.SingletonHelper;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smack.packet.Message;

import java.util.HashSet;
import java.util.Map;

public class help extends XmppChatCmd {

    private adminbot adminbot;

    public help() {
        setCommandName("help");
        setCommandDescription("Displays the Help");
        setCommandSyntax("!help [cmdname]");
        setCommandAuthLevel(0);

        if (adminbot == null) {
            adminbot = (adminbot) SingletonHelper.getInstance("adminbot");
        }
    }

    public synchronized void runCommand(XMPPConnection conn, Chat chat, Message message) {
        super.getArgs(message);
        AdminUser user = adminbot.adminManager.getAdmin(message.getFrom().asBareJid());

        try {
            if (args.length == 1) {
                Logger.log(this, "runCommand", "debug", "Begin Execute");
                String msg = "";
                msg = "Commands: ";
                for (int i = 4; i >= 0; i--) {
                    if (user.level >= i) {
                        msg += getHelpLevel(i);
                    }
                }
                Logger.log(this, "runCommand", "debug", "MSG: %s", msg);
                chat.send(msg);
            } else if (args.length == 2) {
                chat.send(
                        XmppCommandCache.getCommand(
                                args[1].replaceFirst("^!", "").trim()
                        ).getCommandSyntax()
                );
            }
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
            Logger.log(this, "runCommand", "error", e.getMessage());
        } catch (ClassNotFoundException | InterruptedException e) {
            e.printStackTrace();
        }

        Logger.log(this, "runCommand", "debug", "After Execute!");
    }

    public String getHelpLevel(Integer level) {
        StringBuilder out = new StringBuilder();
        HashSet<String> cmds = new HashSet<String>();

        for (Map.Entry<String, Integer> entry : XmppCommandCache.getCommandsWithLevels().entrySet()) {
            if (entry.getValue().equals(level))
                cmds.add(entry.getKey());
        }

        Logger.log(this, "getHelpLevel", "debug", "CMD Size: " + cmds.size());

        if (cmds.size() > 0)
            out.append("Level: ").append(level).append("\n");

        for (String entry : cmds) {
            out.append("- ")
                    .append(entry)
                    .append(" (").append(XmppCommandCache.getCommands().get(entry).getCommandDescription()).append(")")
                    .append("\n");
        }

        if (out.length() > 2)
            out.substring(0, out.length() - 2);

        out.append("\n");
        cmds.clear();

        if (out.length() > 2)
            return out.toString();
        else
            return "";
    }
}
