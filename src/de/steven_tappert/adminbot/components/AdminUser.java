package de.steven_tappert.adminbot.components;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.steven_tappert.tools.Logger;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import static de.steven_tappert.tools.Logger.log;

public class AdminUser {
    public String jid;
    public List<String> ts3uid;
    public String name;
    public Integer level;

    public AdminUser() {
        ts3uid = new LinkedList<>();
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
