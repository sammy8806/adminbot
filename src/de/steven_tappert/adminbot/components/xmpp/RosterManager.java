package de.steven_tappert.adminbot.components.xmpp;

import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;

public class RosterManager {

    private XMPPTCPConnection connection;
    private Roster roster;

    public RosterManager(XMPPTCPConnection connectionn) {
        this.connection = connectionn;
        loadRoster();
    }

    public void loadRoster() {
        roster = Roster.getInstanceFor(connection);
        roster.getEntries();
    }

    public Roster getRoster() {
        return roster;
    }
}
