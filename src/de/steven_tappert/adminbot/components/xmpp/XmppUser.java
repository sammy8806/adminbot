package de.steven_tappert.adminbot.components.xmpp;

import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;

import java.util.Date;
import java.util.HashMap;

public class XmppUser {

    private String username;
    private String domain;
    private String jid; // bare jid: user@domain
    private String resource;

    private String status; // Der Statustext
    private Presence.Type onlineStatus; // Der Status (online, afk, offline)

    private Integer rights; // 0 = User, 1 = Mod, 2 = Admin

    private HashMap<String, mucRight> muc; // Channel => (0 = User, 1 = Voice, 2 = Op, 3 = Mod, 4 = Admin)

    private Message lastMessage;
    private Date lastMessageTime;

    private Integer blockedTime = 0; // in seconds
    private Date blockedSince;

    private Boolean subscribed;

    public Boolean hasSubscribed() {
        return subscribed;
    }

    public void setSubscribe(Boolean subscribed) {
        this.subscribed = subscribed;
    }

    static enum userLevel {
        User,
        Mod,
        Admin
    }

    static enum mucRight {
        User,
        Voice,
        Op,
        Mod,
        Admin
    }

    public XmppUser() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getJid() {
        return jid;
    }

    public void setJid(String jid) {
        this.jid = jid;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Presence.Type getOnlineStatus() {
        return onlineStatus;
    }

    public void setOnlineStatus(Presence.Type onlineStatus) {
        this.onlineStatus = onlineStatus;
    }

    public Integer getRights() {
        return rights;
    }

    public void setRights(Integer rights) {
        this.rights = rights;
    }

    public HashMap<String, mucRight> getMuc() {
        return muc;
    }

    public mucRight getMuc(String chat) {
        return muc.get(chat);
    }

    public void addMuc(String chat, mucRight right) {
        this.muc.put(chat, right);
    }

    public Message getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(Message lastMessage) {
        this.lastMessage = lastMessage;
    }

    public Date getLastMessageTime() {
        return lastMessageTime;
    }

    public void setLastMessageTime(Date lastMessageTime) {
        this.lastMessageTime = lastMessageTime;
    }

    public Integer getBlockedTime() {
        return blockedTime;
    }

    public void setBlockedTime(Integer blockedTime) {
        this.blockedTime = blockedTime;
    }

    public Date getBlockedSince() {
        return blockedSince;
    }

    public void setBlockedSince(Date blockedSince) {
        this.blockedSince = blockedSince;
    }

    public static String getNameFromJID(String jid) {
        String name = jid;

        if(jid.matches("(.*)@conference\\.pvp\\.net(.*)"))
            name = jid.replaceAll(".*/", "").trim();
        else if (jid.matches("(.*)@pvp\\.net(.*)"))
            name = jid.replaceAll("@pvp\\.net/.*", "");

        return name;
    }
}
