package de.steven_tappert.adminbot.components.xmpp.ChatCommands;

import com.github.theholywaffle.teamspeak3.api.wrapper.Channel;
import com.github.theholywaffle.teamspeak3.api.wrapper.Client;
import de.steven_tappert.adminbot.components.ts3.Ts3BotCore;
import de.steven_tappert.adminbot.components.ts3.UserManager;
import de.steven_tappert.tools.SingletonHelper;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;

import java.util.*;

public class ts3 extends XmppChatCmd {

    private Ts3BotCore ts3BotCore = null;

    public ts3() {
        setCommandName("ts3");
        setCommandDescription("Get TS3 Control");
        setCommandSyntax("!ts3 [users|chans]");
        setCommandAuthLevel(2);
    }

    @Override
    public void runCommand(XMPPConnection conn, Chat chat, Message message) {
        if (ts3BotCore == null) {
            ts3BotCore = (Ts3BotCore) SingletonHelper.getInstance("Ts3BotCore");
        }

        String[] args = message.getBody().split("\\s");

        try {
            if (args[1].equalsIgnoreCase("users")) {
                HashSet<String> filterKeys = new HashSet<>();
                if (args.length >= 3) {
                    filterKeys.addAll(Arrays.asList(args).subList(2, args.length));
                }
                ts3BotCore.getUserManager().getUsers().forEach((s, client) -> {
                    StringBuilder buf = new StringBuilder();
                    buf.append("User: ").append(client.getNickname()).append("\n");
                    client.getMap().forEach((prop, value) -> {
                        if (filterKeys.size() > 0 && !filterKeys.contains(prop))
                            return;
                        buf.append(prop).append(": ").append(value).append("\n");
                    });

                    try {
                        chat.send(buf.toString().trim());
                    } catch (SmackException.NotConnectedException | InterruptedException e) {
                        e.printStackTrace();
                    }
                });
            } else if (args[1].equalsIgnoreCase("chans")) {
                StringBuilder buf = new StringBuilder();
                ts3BotCore.getChannels().forEach(channel -> {
                    buf.append("Channel #").append(channel.getId())
                            .append("\t").append("(").append(channel.getTotalClients());

                    if (channel.getMaxClients() > 0)
                        buf.append("/").append(channel.getMaxClients());
                    buf.append(") ").append(channel.getName()).append("\n");
                });
                try {
                    chat.send(buf.toString().trim());
                } catch (SmackException.NotConnectedException | InterruptedException e) {
                    e.printStackTrace();
                }
            } else
                sendSyntax(chat);
        } catch (ArrayIndexOutOfBoundsException e) {
            sendSyntax(chat);
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
