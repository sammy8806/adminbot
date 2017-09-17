package de.steven_tappert.adminbot.components.xmpp.ChatCommands;

import de.steven_tappert.adminbot.components.xmpp.XmppCommandCache;
import de.steven_tappert.adminbot.components.xmpp.XmppUser;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smack.packet.Message;

import java.util.Collection;

public class XmppChatCmd implements XmppChatCommand {

    protected String commandName = "";
    protected Collection<String> commandAliases;
    protected Collection<String> commandRequirements;
    protected String commandDescription = "";
    protected String commandSyntax = "";
    protected Integer commandAuthLevel = 0;

    protected String[] args;

    public void unloadCommand() {
        String cmd = getCommandName();
        XmppCommandCache.removeCommand(cmd);
    }

    public void runCommand(XMPPConnection conn, Chat chat, Message message) throws XMPPException {
        if (!message.getFrom().getDomain().toString().equals("dark-it.net"))
            return;
    }

    public String getCommandName() {
        return commandName;
    }

    public Collection<String> getCommandAliases() {
        return commandAliases;
    }

    public Integer getCommandAuthLevel() {
        return commandAuthLevel;
    }

    public void setCommandName(String commandName) {
        this.commandName = commandName;
    }

    public void setCommandAliases(Collection<String> commandAliases) {
        this.commandAliases = commandAliases;
    }

    public void setCommandRequirements(Collection<String> commandRequirements) {
        this.commandRequirements = commandRequirements;
    }

    public void setCommandDescription(String commandDescription) {
        this.commandDescription = commandDescription;
    }

    public void setCommandSyntax(String commandSyntax) {
        this.commandSyntax += (!this.commandSyntax.equals("") ? "\n" : "") + commandSyntax;
    }

    public void setCommandAuthLevel(Integer commandAuthLevel) {
        this.commandAuthLevel = commandAuthLevel;
    }

    public Collection<String> getCommandRequirements() {
        return commandRequirements;
    }

    public String getCommandDescription() {
        return commandDescription;
    }

    public String getCommandSyntax() {
        return commandSyntax;
    }

    protected String[] getArgs(Message message) {
        args = message.getBody().trim().split("\\s");
        return args;
    }

}
