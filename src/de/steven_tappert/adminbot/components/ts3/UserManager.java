package de.steven_tappert.adminbot.components.ts3;

import com.github.theholywaffle.teamspeak3.api.wrapper.Client;

import java.util.HashMap;
import java.util.Map;

import static de.steven_tappert.tools.Logger.log;

public class UserManager {
    private final Map<Integer, Client> users;
    private final Map<Integer, String> clidToUnique;

    public UserManager() {
        users = new HashMap<>();
        clidToUnique = new HashMap<>();
    }

    public boolean registerUser(Integer clid, Client user) {
        if (users.containsKey(clid)) return false;
        log(this, "registerUser", "Debug", "Registering: " + clid + " (" + user.getNickname() + ")");
        for(Map.Entry<String, String> prop : user.getMap().entrySet()) {
            log(this, "registerUser", "Debug", "- " + prop.getKey() + " # " + prop.getValue());
        }
        users.put(clid, user);
        clidToUnique.put(user.getId(), user.getUniqueIdentifier());

        return true;
    }

    public void updateUser(Integer clid, Client user) {
        users.remove(clid);
        users.put(clid, user);
    }

    public void removeUser(Integer clid) {
        users.remove(clid);
    }

    public Client getUser(Integer clid) {
        return users.get(clid);
    }

    /*public Client getUserById(Integer clid) {
        return getUser(clidToUnique.get(clid));
    }*/

    public int getUserCount() {
        return users.size();
    }

    public Map<Integer, Client> getUsers() {
        return users;
    }
}
