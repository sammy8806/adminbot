package de.steven_tappert.adminbot.components.xmpp.mucActions;

import de.steven_tappert.adminbot.components.xmpp.manager.XmppMucManager;
import de.steven_tappert.tools.Logger;
import de.steven_tappert.tools.SingletonHelper;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smackx.muc.MultiUserChat;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class mucLogger extends mucAction implements PacketListener {

    protected XmppMucManager mucManager = (XmppMucManager) SingletonHelper.getInstance("xmppmucmanager");
    protected SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
    protected SimpleDateFormat dfl = new SimpleDateFormat("yyyy_MM_dd");

    protected HashMap<String, ChatLogger> logfiles = new HashMap<String, ChatLogger>();

    public void loadAction() {

    }

    public void unloadAction() {

    }

    public void registerActions(MultiUserChat muc) {
    //    muc.addMessageListener(new MucChatMessageLogger());
        muc.addMessageListener(this);
        Logger.log(this, "registerActions", "debug", "MessageListener registered");
    }

    public void processPacket(Packet packet) {
        if (packet instanceof Message) {

            String hash = packet.getFrom().replaceAll("/.*","").trim();
            String room = mucManager.getNameFromHash(hash);
            String sender = packet.getFrom().replaceAll(".*/", "").trim();
            String message = ((Message) packet).getBody().trim().replace("<![CDATA[", "").replace("]]>", "");

            // Logger.log(this, "processPacket", "debug", "Message From: \"" + packet.getFrom() + "\" Message: \"" + ((Message) packet).getBody() + "\"");
            Logger.log("MUC - " + room,  sender + " - " + message);
            logChat(room, sender, message);
        }
    }

    protected void logChat(String room, String from, String message) {
        String logLine = "[" + df.format(new Date().getTime()) + "] " + ""+from+": " + message;
        // System.out.println(logLine);

        String chatFilename = "";
        if(!logfiles.containsKey(room))
            logfiles.put(room, new ChatLogger());

        ChatLogger chatLogfile = logfiles.get(room);
        chatLogfile.output(logLine, room);

    }

    public class ChatLogger {

        protected String defaultLogType = "INFO";
        protected SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        protected SimpleDateFormat dfl = new SimpleDateFormat("yyyy_MM_dd");

        protected String filename = "xmppbot";

        protected String activeFile = "";

        private BufferedWriter logfile;

        protected void output(String str, String room) {
            output(str, room, "chatlogs");
        }

        public void output(String str, String room, String path) {
            System.out.println(str);

            try {
                String filestr = path + "/" + room + "_" + dfl.format(new Date()) + ".log";
                if (!activeFile.equals(filestr)) {
                    System.out.println("New logfile is: " + filestr);
                    if (logfile != null)
                        logfile.close();

                    activeFile = filestr;
                    logfile = new BufferedWriter(new FileWriter(activeFile, true));
                    System.out.println("Logfile opened!");
                }

                if (logfile != null) {
                    logfile.write(str);
                    logfile.newLine();
                    logfile.flush();
                }
            } catch (IOException e) {
                System.err.println("[ERROR] Error while writing logfile: " + e);
            }
        }

    }

}
