package de.steven_tappert.adminbot.components.xmpp.ChatCommands;

import de.steven_tappert.adminbot.BotComponent;
import de.steven_tappert.adminbot.adminbot;
import de.steven_tappert.adminbot.components.AdminUser;
import de.steven_tappert.adminbot.components.ts3.Ts3BotCore;
import de.steven_tappert.tools.SingletonHelper;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smack.packet.Message;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.jid.parts.Domainpart;
import org.jxmpp.jid.parts.Localpart;
import org.jxmpp.stringprep.XmppStringprepException;

import java.util.Arrays;
import java.util.HashSet;

public class ts3admin extends XmppChatCmd {

    private Ts3BotCore ts3BotCore = null;
    private adminbot adminbot = null;

    public ts3admin() {
        setCommandName("admin");
        setCommandDescription("Get Admin Controls");
        setCommandSyntax("!ts3admin [add|del|list|go] <uid|chan> [<jid>] [<name>]");
        setCommandAuthLevel(4);
    }

    @Override
    public void runCommand(XMPPConnection conn, Chat chat, Message message) {
        if (ts3BotCore == null) {
            ts3BotCore = (Ts3BotCore) SingletonHelper.getInstance("Ts3BotCore");
        }

        if (adminbot == null) {
            adminbot = (adminbot) SingletonHelper.getInstance("adminbot");
        }

        String[] args = message.getBody().split("\\s");

        try {
            if (args[1].equalsIgnoreCase("add")) {
                String uid = args[2];
                AdminUser admin = new AdminUser();
                admin.ts3uid.add(uid);
                if (args.length > 3) {
                    admin.jid = args[3];
                }
                if (args.length > 4) {
                    admin.name = args[4];
                }
                if (args.length > 5) {
                    admin.level = Integer.valueOf(args[5]);
                }
                adminbot.adminManager.addAdmin(admin);
            } else if (args[1].equalsIgnoreCase("del")) {
                String uid = args[2];
                AdminUser admin = adminbot.adminManager.getAdminByTs3uid(uid);
                adminbot.adminManager.removeAdmin(admin);
            } else if (args[1].equalsIgnoreCase("list")) {
                StringBuilder buf = new StringBuilder();
                if(adminbot.adminManager.users.isEmpty()) {
                    buf.append("No users found!");
                }
                adminbot.adminManager.getAdmins().forEach(adminUser -> {
                    buf.append(adminUser.name).append(": \n");
                    buf.append("TS3: ").append(adminUser.ts3uid).append("\n");
                    buf.append("JID: ").append(adminUser.jid).append("\n");
                });
                chat.send(buf.toString().trim());
            } else if (args[1].equalsIgnoreCase("goChan")) {
                ts3BotCore.goChannel(Integer.parseInt(args[2]));
            } else
                sendSyntax(chat);
        } catch (ArrayIndexOutOfBoundsException e) {
            sendSyntax(chat);
        } catch (SmackException.NotConnectedException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void sendSyntax(Chat chat) {
        try {
            chat.send(getCommandSyntax());
        } catch (SmackException.NotConnectedException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
