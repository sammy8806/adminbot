package de.steven_tappert.adminbot.components;

import de.steven_tappert.adminbot.components.ts3.Ts3BotCore;
import de.steven_tappert.tools.SingletonHelper;
import org.jxmpp.jid.Jid;

import java.util.ArrayList;
import java.util.List;

public class AdminManager {

    public List<AdminUser> users;

    public AdminManager() {
        this.users = new ArrayList<>();
    }

    public void addAdmin(AdminUser admin) {
        users.add(admin);
    }

    public void removeAdmin(AdminUser admin) {
        if(users.contains(admin))
            users.remove(admin);
    }

    public AdminUser getAdmin(Jid jid) {
        for(AdminUser admin : users) {
            if(admin.jid.toString().equalsIgnoreCase(jid.toString()))
                return admin;
        }
        return null;
    }

    public AdminUser getAdmin(String name) {
        for(AdminUser admin : users) {
            if(admin.name.equalsIgnoreCase(name))
                return admin;
        }
        return null;
    }

    public AdminUser getAdminByTs3uid(String uid) {
        for(AdminUser admin : users) {
            if(admin.ts3uid.equalsIgnoreCase(uid))
                return admin;
        }
        return null;
    }

    public List<AdminUser> getAdmins() {
        return users;
    }
}
