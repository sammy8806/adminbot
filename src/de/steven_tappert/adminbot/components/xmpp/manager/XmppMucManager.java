package de.steven_tappert.adminbot.components.xmpp.manager;

import de.steven_tappert.adminbot.components.xmpp.XmppBotCore;
import de.steven_tappert.adminbot.components.xmpp.mucActions.mucAction;
import de.steven_tappert.tools.Logger;
import de.steven_tappert.tools.SingletonHelper;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.muc.MultiUserChat;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static de.steven_tappert.tools.Hash.SHA1;

public class XmppMucManager {

    protected static HashMap<String, MultiUserChat> mucChats = new HashMap<String, MultiUserChat>();
    protected static HashMap<String, mucAction> mucActions = new HashMap<String, mucAction>();

    protected static HashMap<String, String> mucHashTable = new HashMap<String, String>();
    /*
    protected static ArrayList<PacketListener> messageListener = new ArrayList<PacketListener>();
    protected static ArrayList<PacketListener> participantListener = new ArrayList<PacketListener>();
    protected static ArrayList<ParticipantStatusListener> participantStatusListener = new ArrayList<ParticipantStatusListener>();
    protected static ArrayList<UserStatusListener> userStatusListener = new ArrayList<UserStatusListener>();
    protected static ArrayList<InvitationRejectionListener> invitationRejectionListener = new ArrayList<InvitationRejectionListener>();
    protected static ArrayList<PacketInterceptor> presenceInterceptor = new ArrayList<PacketInterceptor>();
    protected static ArrayList<SubjectUpdatedListener> subjectUpdatedListener = new ArrayList<SubjectUpdatedListener>();
    */

    protected XmppBotCore conn;

    public XmppMucManager() {
        // SmackConfiguration.setPacketReplyTimeout(10000);
        conn = (XmppBotCore) SingletonHelper.getInstance("XmppBotCore");
        Logger.log(this, "XmppMucManager", "debug", "I'm: " + conn.getUser());
    }

    private String formatRoom(String room) {
        return "pu~" + SHA1(room) + "@conference.pvp.net".toLowerCase();
    }

    public void joinRoom(String roomName) {
        joinRoom(roomName, formatRoom(roomName));
    }

    public void joinRoom(String roomName, String hash) {
        final String ROOM = roomName;

        if (mucChats.containsKey(roomName)) {
            Logger.log(this, "joinRoom", "debug", "Already joined \"" + roomName + "\"");
            return;
        }

        MultiUserChat muc = new MultiUserChat(conn, hash);
        Logger.log(this, "joinRoom", "debug", "Muc \"" + roomName + "\" ["+hash+"] created");

        String username = conn.getXmppUsername();

        try {
            Logger.log(this, "joinRoom", "debug", "Try to join MUC-Room \"" + roomName + "\" ["+hash+"] as \""+username+"\"!");
            muc.join(username);

            for (Map.Entry<String, mucAction> action : mucActions.entrySet()) {
                action.getValue().registerActions(muc);
                Logger.log(this, "joinRoom", "debug", "Action \"" + action.getKey() + "\" registered");
            }

            mucChats.put(roomName, muc);
            mucHashTable.put(hash, roomName);

            Logger.log(this, "joinRoom", "info", "MUC-Room \"" + roomName + "\" has joined!");
        } catch (XMPPException e) {
            if (e.toString().contains("503")) {
                Logger.log(this, "joinRoom", "error", "Could not join " + roomName + ". Retry in 15s.");
                Timer t = new Timer();
                t.schedule(new TimerTask() {

                    @Override
                    public void run() {
                        joinRoom(ROOM);
                    }
                }, 1 * 15 * 1000);
            } else {
                Logger.log(this, "joinRoom", "error", "Could not join " + ROOM + ": " + e);
                e.printStackTrace();
            }
        }
    }

    public void partRoom(String roomName) {
        Logger.log(this, "partRoom", "debug", "Room \"" + roomName + "\" should be left");
        MultiUserChat muc = mucChats.get(roomName);

        muc.leave();
        Logger.log(this, "partRoom", "debug", "Room \"" + muc.getRoom() + "\" has left");

        mucChats.remove(roomName);
        Logger.log(this, "partRoom", "debug", "Room \"" + roomName + "\" has left");
    }

    public void partAllRooms() {
        for(Map.Entry<String, MultiUserChat> entry : mucChats.entrySet()) {
            partRoom(entry.getKey());
        }
    }

    public HashMap<String, MultiUserChat> getMucChats() {
        return mucChats;
    }

    public MultiUserChat getMucChat(String name) {
        return mucChats.get(name);
    }

    public void addMucHash(String name, String hash) {
        mucHashTable.put(hash, name);
    }

    public String getNameFromHash(String hash) {
        return mucHashTable.get(hash);
    }

    public void registerMucAction(String action) {
        String classpath = "de.steven_tappert.adminbot.components.xmpp.mucActions." + action;
        try {
            Class cls = null;
            cls = this.getClass().getClassLoader().loadClass(classpath);
            mucAction cmd = (mucAction) cls.newInstance();

            Constructor dummy = cls.getConstructor(null);
            dummy.newInstance(null);

            mucActions.put(action, cmd);

            Logger.log(this, "registerMucAction", "debug", "mucAction \"" + action + "\" registered!");
        } catch (ClassNotFoundException e) {
            Logger.log(this, "registerMucAction", "error", "mucAction \"" + action + "\" not found in classpath \"" + classpath + "\"");
        } catch (NoSuchMethodException e) {
            Logger.log(this, "registerMucAction", "error", e.getMessage());
        } catch (InstantiationException e) {
            Logger.log(this, "registerMucAction", "error", e.getMessage());
        } catch (IllegalAccessException e) {
            Logger.log(this, "registerMucAction", "error", e.getMessage());
        } catch (InvocationTargetException e) {
            Logger.log(this, "registerMucAction", "error", e.getMessage());
        }
    }

    public void unregisterMucAction(String name) {
        mucActions.remove(name);
        Logger.log(this, "registerMucAction", "error", "mucAction \"" + name + "\" unregistered!");
    }

    /*
    protected void addListener(MultiUserChat muc) {
        muc.addMessageListener();
        muc.addParticipantListener();
        muc.addParticipantStatusListener();
        muc.addUserStatusListener();
        muc.addInvitationRejectionListener();
        muc.addPresenceInterceptor();
        muc.addSubjectUpdatedListener();
    }

    protected void addListener(MultiUserChat muc, ArrayList<?> list) {
        for (Object entry : list) {

        }
    }
    */

}
