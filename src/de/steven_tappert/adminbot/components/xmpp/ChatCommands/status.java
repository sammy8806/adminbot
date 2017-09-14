package de.steven_tappert.adminbot.components.xmpp.ChatCommands;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;

public class status extends XmppChatCmd {

    protected String defaultStatus = "Chat Helper V2";

    public status() {
        setCommandName("status");
        setCommandDescription("Set the Status");
        setCommandSyntax("!status [Status]");
        setCommandAuthLevel(4);
    }

    @Override
    public void runCommand(XMPPConnection conn, Chat chat, Message message) {
        String parameters = message.getBody().replaceFirst("^!"+commandName, "");

        String s = parameters.length() > 1 ? parameters.substring(1) : defaultStatus;
        Presence status = new Presence(Presence.Type.available);
        status.setMode(Presence.Mode.chat);
        status.setStatus("<body><gameStatus>outOfGame</gameStatus><level>42</level><wins>1337</wins><statusMsg>" + s + "</statusMsg><profileIcon>28</profileIcon></body>");

        try {
            conn.sendStanza(status);
        } catch (InterruptedException | SmackException.NotConnectedException e) {
            e.printStackTrace();
        }
    }
}
