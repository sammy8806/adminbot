package de.steven_tappert.adminbot.components.xmpp.manager;

import de.steven_tappert.adminbot.components.xmpp.XmppUser;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class XmppUserManager {

    public static LinkedHashMap<String, XmppUser> users = new LinkedHashMap<String, XmppUser>();

    public XmppUserManager() {

    }

    public static void storeUser(XmppUser user) {
        users.put(user.getUsername(), user);
    }

    public static XmppUser getUser(String name) {
        return users.get(name);
    }

    public static Map<String, XmppUser> searchUser(HashMap<String, String> ident) {
        Map<String, XmppUser> result = new HashMap<String, XmppUser>();
        for(Map.Entry<String, String> entry : ident.entrySet()) {

        }
        return result;
    }
}
