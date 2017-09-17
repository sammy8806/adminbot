package de.steven_tappert.adminbot.components.xmpp.ChatCommands;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;

import java.util.Collection;

public interface XmppChatCommand {

    public void runCommand(XMPPConnection conn, Chat chat, Message message) throws XMPPException;

    public String getCommandName();
    public Collection<String> getCommandAliases();
    public Collection<String> getCommandRequirements();
    public String getCommandDescription();
    public String getCommandSyntax();
    public Integer getCommandAuthLevel();

}
