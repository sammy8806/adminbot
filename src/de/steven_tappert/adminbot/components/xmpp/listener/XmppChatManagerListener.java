package de.steven_tappert.adminbot.components.xmpp.listener;

import de.steven_tappert.adminbot.components.xmpp.manager.MessageManager;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManagerListener;

public class XmppChatManagerListener implements ChatManagerListener {

    @Override
    public void chatCreated(Chat chat, boolean createdLocally) {
        if (!createdLocally) {
            chat.addMessageListener(new MessageManager());
        }
    }
}
