package de.steven_tappert.adminbot.components;

import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static de.steven_tappert.tools.Logger.log;

public class AdminUser {
    public String jid;
    public List<String> ts3uid;
    public String name;
    public Integer level;
    public Map<String, Boolean> config;

    public AdminUser() {
        ts3uid = new LinkedList<>();
        config = new HashMap<>();
    }

    public EntityBareJid getBareJid() {
        try {
            return JidCreate.bareFrom(jid).asEntityBareJidIfPossible();
        } catch (XmppStringprepException e) {
            log(this, "getBareJid", "Error", e.getCausingString());
            e.printStackTrace();
        }

        return null;
    }
}
