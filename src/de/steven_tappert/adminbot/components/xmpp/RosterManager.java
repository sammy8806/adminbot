package de.steven_tappert.adminbot.components.xmpp;

import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.XMPPConnection;

public class RosterManager {

    XMPPConnection connectionn;

    private Roster roster;

    public RosterManager(XMPPConnection connectionn) {
        this.connectionn = connectionn;
        loadRoster();
    }

    public void loadRoster() {
        roster =  connectionn.getRoster();
        roster.getEntries();
    }

    public Roster getRoster() {
        return roster;
    }
}
