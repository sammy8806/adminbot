package de.steven_tappert.adminbot.components.xmpp.mucActions;

import org.jivesoftware.smackx.muc.MultiUserChat;

public interface mucActionInterface {

    public void loadAction();
    public void unloadAction();

    public void registerActions(MultiUserChat muc);

    public String getActionName();
}
