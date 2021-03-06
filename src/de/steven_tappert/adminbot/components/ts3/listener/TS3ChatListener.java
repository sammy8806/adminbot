package de.steven_tappert.adminbot.components.ts3.listener;

import com.github.theholywaffle.teamspeak3.api.TextMessageTargetMode;
import com.github.theholywaffle.teamspeak3.api.event.TS3EventAdapter;
import com.github.theholywaffle.teamspeak3.api.event.TextMessageEvent;
import com.sun.org.apache.xalan.internal.xsltc.DOM;
import de.steven_tappert.adminbot.components.AdminManager;
import de.steven_tappert.adminbot.components.AdminUser;
import de.steven_tappert.adminbot.components.ts3.Ts3BotCore;
import de.steven_tappert.adminbot.components.ts3.UserManager;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.chat2.Chat;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.jid.parts.Domainpart;
import org.jxmpp.jid.parts.Localpart;
import org.jxmpp.stringprep.XmppStringprepException;

import java.util.LinkedList;
import java.util.List;

import static de.steven_tappert.tools.Logger.log;

public class TS3ChatListener extends TS3EventAdapter {
    private Ts3BotCore core;

    public TS3ChatListener(Ts3BotCore core) {
        this.core = core;
    }

    @Override
    public void onTextMessage(TextMessageEvent e) {
        super.onTextMessage(e);

        log(this, "onTextMessage", "Info", "Got chat message from #" + e.getInvokerId() + " " +
                core.getUserManager().getUser(e.getInvokerId()).getNickname()
        );
        if (e.getTargetMode() == TextMessageTargetMode.CHANNEL && e.getInvokerId() != this.core.getClientId()) {
            log(this, "onTextMessage", "Debug", "Message: " + e.getMessage());
            for (AdminUser adminUser : core.adminManager.getAdmins()) {
                try {
                    if (adminUser.jid == null) {
                        log(this, "onTextMessage", "Debug", "Skipping Admin without JID");
                        continue;
                    }
                    if (adminUser.ts3uid.contains(e.getInvokerUniqueId())) {
                        log(this, "onTextMessage", "Debug", "Not sending the message back to sender");
                        if (adminUser.config.getOrDefault("ts3_own_message_enable", false)) {
                            log(this, "onTextMessage", "Debug", "User has set 'ts3_own_message_enable' ... sending");
                        } else {
                            continue;
                        }
                    }
                    if (adminUser.config != null) {
                        if (adminUser.config.getOrDefault("ts3_redirect_disable", false)) {
                            log(this, "onTextMessage", "Info", "User has set 'ts3_redirect_disable'");
                            continue;
                        }
                    }

                    List<String> bbTags = new LinkedList<>();
                    bbTags.add("URL");
                    bbTags.add("b");
                    bbTags.add("i");
                    bbTags.add("u");

                    String msg = e.getMessage();
                    for (String tag : bbTags) {
                        msg = msg.replaceAll(String.format("\\[%s\\]", tag), "");
                        msg = msg.replaceAll(String.format("\\[/%s\\]", tag), "");
                    }

                    Chat chat = core.chatManager.chatWith(adminUser.getBareJid());
                    chat.send(core.getUserManager().getUser(e.getInvokerId()).getNickname() + ": " + msg);
                    log(this, "onTextMessage", "Debug", "Sending message to " + adminUser.jid);
                } catch (SmackException.NotConnectedException | InterruptedException e1) {
                    e1.printStackTrace();
                }
            }

        }
    }
}
