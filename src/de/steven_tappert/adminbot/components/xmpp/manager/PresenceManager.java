package de.steven_tappert.adminbot.components.xmpp.manager;

import de.steven_tappert.adminbot.components.xmpp.XmppUser;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;

public class PresenceManager implements PacketListener {


    @Override
    public void processPacket(Packet packet) {
        String pvp = packet.getFrom().replaceAll("/.*", "");
        String sender = XmppUser.getNameFromJID(packet.getFrom());

        Presence presence = (Presence) packet;

        XmppUser user = XmppUserManager.getUser(sender);

        if (user == null) {
            user = new XmppUser();
            user.setOnlineStatus(presence.getType());
            user.setStatus(presence.getStatus());
            user.setUsername(sender);
        }
        /*
        if (presence.getType() == Presence.Type.available) {
            if (presence.getStatus() != null) {
                Pattern p = Pattern.compile("<statusMsg>([\\w\\W]*)</statusMsg>");
                Matcher m = p.matcher(presence.getStatus());
                String status = m.find() ? m.group(1) : "Online";

                user.setStatus(status);

                if (s != null) {
                    if (pvp.equals(s.getUser())) {
                        // Change status message
                        onPresence(s, presence.getType(), presence.getMode(), status);
                    } else if (presence.toXML().contains("<show>chat</show>")) {
                        // User joined chatroom
                        // onJoin(s, getMUC(pvp));
                        debug("PRESENCE", s.getName() + " joined");
                    }
                }
            }
        } else if (presence.getType() == Presence.Type.subscribe) {
            debug("SUBSCRIBE", sender);
        } else if (presence.getType() == Presence.Type.unavailable) {
            Summoner s = Summoner.get(sender);
            if (s != null)
                s.setStatus("Offline");
            debug("PRESENCE", sender + "(" + pvp + ")" + ": " + presence.getType());
        }         */
    }
}
