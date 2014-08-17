package de.steven_tappert.adminbot.components.xmpp.listener;

import org.jivesoftware.smack.RosterListener;
import org.jivesoftware.smack.packet.Presence;

import java.util.Collection;

public class XmppRosterListener implements RosterListener {

    @Override
    public void entriesAdded(Collection<String> addresses) {
    }

    @Override
    public void entriesUpdated(Collection<String> addresses) {
    }

    @Override
    public void entriesDeleted(Collection<String> addresses) {
    }

    @Override
    public void presenceChanged(Presence presence) {
    }

}
