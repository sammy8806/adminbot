package de.steven_tappert.adminbot.components.xmpp.ChatCommands;

import de.steven_tappert.adminbot.adminbot;
import de.steven_tappert.adminbot.components.AdminUser;
import de.steven_tappert.adminbot.components.ts3.Ts3BotCore;
import de.steven_tappert.tools.Logger;
import de.steven_tappert.tools.SingletonHelper;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smack.chat2.ChatManager;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;

import java.util.*;

public class admin extends XmppChatCmd {

    private Ts3BotCore ts3BotCore = null;
    private adminbot adminbot = null;

    public admin() {
        setCommandName("admin");
        setCommandDescription("Get Admin Controls");
        setCommandAuthLevel(4);

        setCommandSyntax("!admin <username> get|set (level|name|jid) <level>");
        setCommandSyntax("!admin <username> add|del ts3 <ts3-uid>");
        setCommandSyntax("!admin <username> enable|disable config-option");
        setCommandSyntax("!admin list users [regex]");
        setCommandSyntax("");
        setCommandSyntax("Config-Options:");
        this.getConfigOptions().forEach((name, desc) -> {
            setCommandSyntax(String.format("- %s (%s)", name, desc));
        });

        if (ts3BotCore == null) {
            ts3BotCore = (Ts3BotCore) SingletonHelper.getInstance("Ts3BotCore");
        }

        if (adminbot == null) {
            adminbot = (adminbot) SingletonHelper.getInstance("adminbot");
        }
    }

    public HashMap<String, String> getConfigOptions() {
        HashMap<String, String> options = new HashMap<>();
        options.put("ts3_redirect_disable", "Disables the TS3->XMPP redirect receive");
        options.put("xmpp_chat_disable", "Disables the XMPP->XMPP Chat receive");
        return options;
    }

    @Override
    public void runCommand(XMPPConnection conn, Chat chat, Message message) {
        super.getArgs(message);

        boolean shouldSendSyntax = true;
        boolean rewriteUsers = false;
        AdminUser user = null;

        try {
            if (args.length > 2 && args[1].equalsIgnoreCase("list")) {
                if (args[2].equalsIgnoreCase("users")) {
                    shouldSendSyntax = false;

                    HashSet<AdminUser> users = new HashSet<>();
                    if (args.length > 3) {
                        for (AdminUser adminUser : adminbot.adminManager.getAdmins()) {
                            LinkedList<String> fields = new LinkedList<>(Arrays.asList(adminUser.name, adminUser.jid));
                            fields.addAll(adminUser.ts3uid);

                            for (String value : fields) {
                                if (value.matches(args[3])) {
                                    users.add(adminUser);
                                }
                                if (value.matches(".*" + args[3] + ".*")) {
                                    users.add(adminUser);
                                }
                            }
                        }
                    } else {
                        users.addAll(adminbot.adminManager.getAdmins());
                    }

                    for (AdminUser tmpUser : users) {
                        String msg = "";
                        msg += String.format("  Name:  %s\n", tmpUser.name);
                        msg += String.format("  JID:   %s\n", tmpUser.jid);
                        msg += String.format("  Level: %s\n", tmpUser.level);
                        msg += "  TS3:\n";
                        for (String uid : tmpUser.ts3uid) {
                            msg += String.format("  - %s\n", uid);
                        }
                        if (tmpUser.config != null) {
                            msg += "  Config options:\n";
                            for (Map.Entry<String, Boolean> config : tmpUser.config.entrySet()) {
                                msg += String.format("  - %s: %b\n", config.getKey(), config.getValue());
                            }
                        }
                        chat.send(msg);
                    }
                }
            }

            if (args.length > 2 && args[2].matches("(get|set|add|del|enable|disable)")) {
                user = getAdminUser(chat);
                if (user == null) return;
            }

            if (args.length > 3 && args[2].matches("(get|set|add|del)") && user != null) {
                if (args[3].equalsIgnoreCase("level")) {
                    if (args[2].equalsIgnoreCase("set") && args.length > 4) {
                        user.level = Integer.valueOf(args[4]);
                        chat.send(String.format("Set level of %s to %d", user.jid, user.level));
                        Message msg = new Message();
                        msg.setBody(String.format("Your security level has been set to %d", user.level));
                        Chat newChat = ChatManager.getInstanceFor(conn).chatWith(JidCreate.entityBareFrom(user.jid));
                        newChat.send(msg);
                        shouldSendSyntax = false;
                        rewriteUsers = true;
                    } else if (args[2].equalsIgnoreCase("get")) {
                        chat.send(String.format("Level of %s: %d", user.jid, user.level));
                        shouldSendSyntax = false;
                    }
                }

                if (args[3].equalsIgnoreCase("name")) {
                    if (args[2].equalsIgnoreCase("set") && args.length > 4) {
                        user.name = args[4];
                        chat.send(String.format("Set name of %s to %s", user.jid, user.name));
                        Message msg = new Message();
                        msg.setBody(String.format("Your name has been set to %s", user.name));
                        Chat newChat = ChatManager.getInstanceFor(conn).chatWith(JidCreate.entityBareFrom(user.jid));
                        newChat.send(msg);
                        shouldSendSyntax = false;
                        rewriteUsers = true;
                    } else if (args[2].equalsIgnoreCase("get")) {
                        chat.send(String.format("Name of %s: %s", user.jid, user.name));
                        shouldSendSyntax = false;
                    }
                }
            }

            if (args.length > 3 && args[2].matches("(enable|disable)") && user != null) {
                if (user.config == null) {
                    user.config = new HashMap<>();
                }
                if (args[3].equalsIgnoreCase("ts3_redirect_disable")) {
                    if (args[2].equalsIgnoreCase("enable")) {
                        user.config.putIfAbsent("ts3_redirect_disable", true);
                        chat.send(String.format("Enabled TS3 Redirect for %s", user.name));
                        shouldSendSyntax = false;
                        rewriteUsers = true;
                    } else if (args[2].equalsIgnoreCase("disable")) {
                        user.config.putIfAbsent("ts3_redirect_disable", false);
                        chat.send(String.format("Enabled TS3 Redirect for %s", user.name));
                        shouldSendSyntax = false;
                        rewriteUsers = true;
                    }
                }
            }

            if (rewriteUsers) {
                adminbot.adminManager.writeUsers();
            }

            if (shouldSendSyntax) {
                sendSyntax(chat);
            }
        } catch (SmackException.NotConnectedException | InterruptedException | NullPointerException | XmppStringprepException e) {
            e.printStackTrace();
        }
    }

    private AdminUser getAdminUser(Chat chat) throws XmppStringprepException, SmackException.NotConnectedException, InterruptedException {
        AdminUser user;
        Logger.log(this, "runCommand", "debug", String.format(
                "Searching User with Name '%s'", args[1]
        ));
        user = adminbot.adminManager.getAdmin(args[1]);

        if (user == null) {
            Logger.log(this, "runCommand", "debug", String.format(
                    "Searching User with JID '%s'", JidCreate.bareFrom(args[1]).toString()
            ));
            user = adminbot.adminManager.getAdmin(JidCreate.bareFrom(args[1]));
        }
        if (user == null) {
            Logger.log(this, "runCommand", "debug", String.format(
                    "Searching User with TS3 '%s'", args[1]
            ));
            user = adminbot.adminManager.getAdminByTs3uid(args[1]);
        }
        if (user == null) {
            chat.send("User not found");
            return null;
        }
        return user;
    }

    private void sendSyntax(Chat chat) {
        try {
            chat.send(getCommandSyntax());
        } catch (SmackException.NotConnectedException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
