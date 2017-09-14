package de.steven_tappert.adminbot.components.xmpp.ChatCommands;

import de.steven_tappert.adminbot.components.ts3.Ts3BotCore;
import de.steven_tappert.tools.SingletonHelper;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smack.packet.Message;

import java.util.Arrays;
import java.util.HashSet;

public class chat extends XmppChatCmd {

    private Ts3BotCore ts3BotCore = null;

    public chat() {
        setCommandName("chat");
        setCommandDescription("Send Chat Messages");
        setCommandSyntax("!chat <msg>");
        setCommandAuthLevel(1);
    }

    @Override
    public void runCommand(XMPPConnection conn, Chat chat, Message message) {
        if (ts3BotCore == null) {
            ts3BotCore = (Ts3BotCore) SingletonHelper.getInstance("Ts3BotCore");
        }

        String[] args = message.getBody().split("\\s");
        String source = chat.getXmppAddressOfChatPartner().toString();

        StringBuilder buf = new StringBuilder();
        if (source.length() > 0) {
            buf.append(source);
            buf.append(": ");
        }
        for (String arg : args) {
            buf.append(" ");
            buf.append(arg);
        }
        // chat.send(buf.toString().trim());
        ts3BotCore.sendMessageToChannel(buf.toString().trim());
    }
}
