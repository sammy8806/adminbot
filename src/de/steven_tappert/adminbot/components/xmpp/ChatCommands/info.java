package de.steven_tappert.adminbot.components.xmpp.ChatCommands;

import com.sun.org.apache.xpath.internal.operations.Bool;
import de.steven_tappert.adminbot.adminbot;
import de.steven_tappert.adminbot.components.AdminUser;
import de.steven_tappert.tools.SingletonHelper;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smack.packet.Message;

import java.util.HashMap;
import java.util.Map;

public class info extends XmppChatCmd {

    private adminbot adminbot;

    public info() {
        setCommandName("info");
        setCommandDescription("Show your User-Info");
        setCommandSyntax("!info");
        setCommandAuthLevel(0);

        if (adminbot == null) {
            adminbot = (adminbot) SingletonHelper.getInstance("adminbot");
        }
    }

    @Override
    public void runCommand(XMPPConnection conn, Chat chat, Message message) {
        AdminUser user = adminbot.adminManager.getAdmin(message.getFrom().asBareJid());

        try {
            if (user == null) {
                String msg = "Welcome to the living hell, write !info for more ...";
                chat.send(msg);

                user = new AdminUser();
                user.level = 0;
                user.jid = message.getFrom().asBareJid().toString();
                user.name = message.getFrom().getLocalpartOrNull().toString();
                adminbot.adminManager.addAdmin(user);
            }

            String msg = String.format("Hey %s,\n", message.getFrom().toString());
            msg += "here are your current infos: \n";
            msg += String.format("  Name:  %s\n", user.name);
            msg += String.format("  JID:   %s\n", user.jid);
            msg += String.format("  Level: %s\n", user.level);
            msg += "  TS3:\n";
            for (String uid : user.ts3uid) {
                msg += String.format("  - %s\n", uid);
            }

            HashMap<String, Boolean> configMap = new HashMap<>();

            if (user.config != null) {
                configMap.putAll(user.config);
            }

            for (String key : admin.getConfigOptions().keySet()) {
                configMap.putIfAbsent(key, false);
            }

            msg += "  Config options:\n";
            for (Map.Entry<String, Boolean> config : configMap.entrySet()) {
                msg += String.format("  - %s: %s\n", config.getKey(), config.getValue());
            }
            msg += "\nWrite !help for a list of usable commands";

            chat.send(msg);
        } catch (SmackException.NotConnectedException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
