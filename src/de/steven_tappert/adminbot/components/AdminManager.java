package de.steven_tappert.adminbot.components;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import de.steven_tappert.tools.Logger;
import org.jxmpp.jid.Jid;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class AdminManager {

    private static final String usersFilename = "users.json";

    public List<AdminUser> users;

    public AdminManager() {
        this.users = new ArrayList<>();
    }

    public void addAdmin(AdminUser admin) {
        users.add(admin);
        writeUsers();
    }

    public void removeAdmin(AdminUser admin) {
        if (users.contains(admin)) {
            users.remove(admin);
            writeUsers();
        }
    }

    public AdminUser getAdmin(Jid jid) {
        for (AdminUser admin : users) {
            if (admin.jid.equalsIgnoreCase(jid.toString()))
                return admin;
        }
        return null;
    }

    public AdminUser getAdmin(String name) {
        for (AdminUser admin : users) {
            if (admin.name.equalsIgnoreCase(name))
                return admin;
        }
        return null;
    }

    public AdminUser getAdminByTs3uid(String uid) {
        for (AdminUser admin : users) {
            if (admin.ts3uid.contains(uid))
                return admin;
        }
        return null;
    }

    public List<AdminUser> getAdmins() {
        return users;
    }

    private void generateUsers() {
        LinkedList<AdminUser> users = new LinkedList<>();

        AdminUser user = new AdminUser();
        user.name = "Admin User";
        user.ts3uid.add("bla==");
        user.jid = "test@test.de";
        user.level = 4;
        users.add(user);

        this.users = users;

        writeUsers();
    }

    public void writeUsers() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.disable(MapperFeature.AUTO_DETECT_GETTERS);
        mapper.enable(SerializationFeature.INDENT_OUTPUT);

        try {
            mapper.writeValue(new File(usersFilename), users);
            Logger.log(this, "writeUsers", "debug", "Written " + usersFilename + " failed!");
        } catch (IOException e) {
            Logger.log(this, "writeUsers", "error", "Loading " + usersFilename + "failed!");
            e.printStackTrace();
        }
    }

    public void loadUsers() {
        ObjectMapper mapper = new ObjectMapper();
        AdminUser[] users = null;

        try {
            users = mapper.readValue(new File(usersFilename), AdminUser[].class);
            Logger.log(this, "loadUsers", "debug", "Loaded " + users.length + " users");
        } catch (FileNotFoundException e) {
            Logger.log(this, "loadUsers", "debug", "Users file not found ... generating a default one");
            generateUsers();
            loadUsers();
            return;
        } catch (IOException e) {
            e.printStackTrace();
        }

        assert users != null;

        this.users = new LinkedList<>();

        for (AdminUser user : users) {
            Logger.log(this, "loadUsers", "info", "Imported admin-user: " + user.name);
            addAdmin(user);
        }
    }
}
