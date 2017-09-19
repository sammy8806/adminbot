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

public class chat extends XmppChatCmd {

    private Ts3BotCore ts3BotCore = null;
    private adminbot adminbot = null;

    public chat() {
        setCommandName("chat");
        setCommandDescription("Send Chat Messages");
        setCommandSyntax("!chat <msg>");
        setCommandAuthLevel(1);
    }

    @Override
    public void runCommand(XMPPConnection conn, Chat chat, Message message) {
        Logger.debug("Begin Call");

        if (adminbot == null) {
            adminbot = (adminbot) SingletonHelper.getInstance("adminbot");
        }

        if (ts3BotCore == null) {
            ts3BotCore = (Ts3BotCore) SingletonHelper.getInstance("Ts3BotCore");
        }

        String source = chat.getXmppAddressOfChatPartner().toString();

        StringBuilder buf = new StringBuilder();
        if (source.length() > 0) {
            buf.append(source);
            buf.append(": ");
        }
        buf.append(message.getBody());

        Logger.debug("Sending message (%d Chars) to TS3", buf.length());
        ts3BotCore.sendMessageToChannel(buf.toString().trim());

        ChatManager chatManager = ChatManager.getInstanceFor(conn);
        for (AdminUser user : adminbot.adminManager.getAdmins()) {
            Logger.debug("Processing user: %s", user.jid);

            if (user.jid.equals(message.getFrom().asBareJid().toString())) {
                Logger.debug("Not sending message back to sender (%s)", user.jid);
                continue;
            }

            if (user.jid.equals("")) {
                Logger.debug("Not send chat to %s .. no jid set", user.name);
                continue;
            }

            if (user.level < 1) {
                Logger.debug("%s: User level not high enough (%d)", user.name, user.level);
                continue;
            }

            if (user.config != null && user.config.getOrDefault("xmpp_chat_disable", false)) {
                Logger.debug("%s: User does not want xmpp chat", user.name);
                continue;
            }

            try {
                Logger.debug("%s: Sending chat message (%d Chars)", user.name, buf.length());
                Chat userChat = chatManager.chatWith(user.getBareJid());
                userChat.send(buf);
            } catch (SmackException.NotConnectedException | InterruptedException e) {
                Logger.error("Could not send Message to User: %s", user.name);
            }
        }

        Logger.debug("End Call");
    }
}
