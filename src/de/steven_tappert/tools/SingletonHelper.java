package de.steven_tappert.tools;

import java.util.HashMap;

public class SingletonHelper {

    private static HashMap<String, Object> instances = new HashMap<String, Object>();

    public static Object getInstance(String name) {
        if(!instances.containsKey(name.toLowerCase())) {
            Logger.log(SingletonHelper.class.getSimpleName(), "getInstance", "debug", "Instance with name \""+name.toLowerCase()+"\" not registered");
            return new Object();
        }
        Object instance = instances.get(name.toLowerCase());
        Logger.log(SingletonHelper.class.getSimpleName(), "getInstance", "debug", "Instance with name \""+name.toLowerCase()+"\" successfully loaded");
        return instance;
    }

    public static void registerInstanceOnce(Object obj) {
        if(!instances.containsValue(obj)) {
            registerInstance(obj);
        }
    }

    public static void registerInstance(Object obj) {
        String instanceName = obj.getClass().getSimpleName().toLowerCase();
        SingletonHelper.registerInstance(obj, instanceName);
    }

    public static void registerInstance(Object obj, String instanceName) {
        if(!instances.containsKey(instanceName)) {
            instances.put(instanceName, obj);
            Logger.log(SingletonHelper.class.getSimpleName(), "registerInstance", "debug", "Instance with name \""+instanceName+"\" successfully registered");
        } else {
            Logger.log(SingletonHelper.class.getSimpleName(), "registerInstance", "error", "Instance with name \""+instanceName+"\" are already registered");
        }
    }

    public static void unregisterInstance(String name) {
        name = name.toLowerCase();
        if(instances.containsKey(name)) {
            instances.remove(name);
            Logger.log(SingletonHelper.class.getSimpleName(), "registerInstance", "debug", "Instance with name \""+name+"\" is unregisted");
        } else {
            Logger.log(SingletonHelper.class.getSimpleName(), "registerInstance", "debug", "Instance with name \""+name+"\" is not registered! Cannot unregister!");
        }
    }

}
