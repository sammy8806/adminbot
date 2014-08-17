package de.steven_tappert.adminbot.components.xmpp;

import de.steven_tappert.adminbot.components.xmpp.ChatCommands.XmppChatCommand;
import de.steven_tappert.tools.Logger;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class XmppCommandCache {

    private static LinkedHashMap<String, XmppChatCommand> commands = new LinkedHashMap<String, XmppChatCommand>();
    private static HashMap<String, Integer> commandLevel = new LinkedHashMap<String, Integer>();
    private static ClassLoader XmppCommandCacheClassLoader = XmppCommandCache.class.getClassLoader();

    private static LinkedHashMap<String, String> commandAliases = new LinkedHashMap<String, String>();

    public XmppCommandCache() {

    }

    public static synchronized XmppChatCommand getCommand(String command) throws ClassNotFoundException {
        // Logger.log(XmppCommandCache.class,"getCommand", "debug", "Beforce Call getCommand");
        XmppChatCommand cmd = null;
        if (!commands.containsKey(command)) {
            String classpath = "de.steven_tappert.adminbot.components.xmpp.ChatCommands." + command;
            Logger.log(XmppCommandCache.class.getSimpleName(), "getCommand", "debug", "Commands unkown, try to load ... [" + classpath + "]");
            try {
                Class cls = XmppCommandCacheClassLoader.loadClass(classpath);
                cmd = (XmppChatCommand) cls.newInstance();
            } catch (InstantiationException e) {
                Logger.log(XmppCommandCache.class.getSimpleName(), "getCommand", "error", e.getMessage());
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                Logger.log(XmppCommandCache.class.getSimpleName(), "getCommand", "error", e.getMessage());
                e.printStackTrace();
            }

            if (cmd != null) {
                commands.put(command, cmd);
                commandLevel.put(command, cmd.getCommandAuthLevel());
            }
        } else {
            cmd = commands.get(command);
            // Logger.log(XmppCommandCache.class.getSimpleName(), "getCommand", "debug", "Command found!");
        }

        // Logger.log(XmppCommandCache.class.getSimpleName(), "getCommand", "debug", "After Call getCommand");
        if (cmd != null) {
            Logger.log(XmppCommandCache.class.getSimpleName(), "getCommand", "debug", "Command \"" + cmd.getCommandName() + "\" returned");
        } else {
            Logger.log(XmppCommandCache.class.getSimpleName(), "getCommand", "debug", "Null Command returned");
        }

        return cmd;
    }

    public static void removeCommand(String command) {
        Logger.log(XmppCommandCache.class.getSimpleName(), "removeCommand", "debug", "Command \"" + command + "\" should be removed!");
        commands.remove(command);
        commandLevel.remove(command);
        Logger.log(XmppCommandCache.class.getSimpleName(), "removeCommand", "debug", "Command \"" + command + "\" removed!");
    }

    public static void reloadAllCommands() {
        Logger.log(XmppCommandCache.class.getSimpleName(), "reloadAllCommands", "debug", "Command-Cache should be cleared!");
        commands.clear();
        commandLevel.clear();
        Logger.log(XmppCommandCache.class.getSimpleName(), "reloadAllCommands", "debug", "Command-Cache cleared!");
    }

    public static HashMap<String, Integer> getCommandsWithLevels() {
        return (HashMap<String, Integer>) commandLevel.clone();
    }

    public static Integer getCommandLevel(String command) {
        return commandLevel.get(command);
    }

    public static LinkedHashMap<String, XmppChatCommand> getCommands() {
        return (LinkedHashMap<String, XmppChatCommand>) commands.clone();
    }

    /**
     * Look up the class in the Tread Context ClassLoader and in the "current" ClassLoader.
     *
     * @param className The class name to load
     * @return the corresponding Class instance
     * @throws ClassNotFoundException if the Class was not found.
     */
    public static Class forName(final String className) throws ClassNotFoundException {
        // Load classes from different classloaders :
        // 1. Thread Context ClassLoader
        // 2. ClassUtils ClassLoader

        ClassLoader tccl = Thread.currentThread().getContextClassLoader();
        Class cls = null;

        try {
            // Try with TCCL
            cls = Class.forName(className, true, tccl);
        } catch (ClassNotFoundException cnfe) {

            // Try now with the classloader used to load ClassUtils
            ClassLoader current = XmppCommandCache.class.getClassLoader();
            try {
                cls = Class.forName(className, true, current);
            } catch (ClassNotFoundException cnfe2) {
                // If this is still unknown, throw an Exception
                throw cnfe2;
            }
        }

        return cls;
    }

    private Boolean isRegisteredAlias(String command) {
        return commandAliases.containsKey(command);
    }

    private Boolean registerAliases(XmppChatCommand command) {
        try {
            Collection<String> aliases = command.getCommandAliases();

            for (String alias : aliases) {
                commandAliases.put(alias, command.getCommandName());
            }

            Logger.log(XmppCommandCache.class.getSimpleName(), "registerAliases", "debug", "Aliases for command \"" + command.getCommandName() + "\" successfully registered");
            return true;

        } catch (NullPointerException npe) {
            Logger.log(XmppCommandCache.class.getSimpleName(), "registerAliases", "debug", "Aliases cannot be registered, command is empty!");
            return false;
        }
    }

}
