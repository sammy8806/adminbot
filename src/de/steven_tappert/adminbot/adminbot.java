package de.steven_tappert.adminbot;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.steven_tappert.adminbot.components.AdminManager;
import de.steven_tappert.adminbot.components.AdminUser;
import de.steven_tappert.tools.Logger;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.LinkedList;

public class adminbot {

    private final String author = "Steven Tappert";
    private final String version = "v0.5";

    private boolean DEBUG = true;

    private HashMap<String, BotComponent> components = new HashMap<String, BotComponent>();

    private ClassLoader classLoader = adminbot.class.getClassLoader();

    public AdminManager adminManager;

    public adminbot() {
        // TODO: Streamumlenkungen + Verabeitung (LogStream)
        //    System.setErr(new BotErrorHandler(new LogStream(), true));
        //    System.setOut(new BotErrorHandler(new LogStream(), true));
        adminManager = new AdminManager();
        adminManager.loadUsers();
    }

    public void loadComponent(BotComponent component) {
        component.loadComponent();
        components.put(
                component.getComponentName(),
                component
        );
    }

    public BotComponent getInstance(String componentName) {
        return components.get(componentName);
    }

    public void loadComponent(String componentName, String className) throws IllegalAccessException, InstantiationException {
        String classpath = "de.steven_tappert.adminbot.components." + componentName + "." + className;
        try {
            Class cls = classLoader.loadClass(classpath);
            BotComponent instance = (BotComponent) cls.newInstance();
            instance.loadComponent();

            Constructor dummy = cls.getConstructor((Class<BotComponent>) null);
            dummy.newInstance((Object) null);

            // TODO: Move in thread?

            components.put(componentName, instance);
        } catch (ClassNotFoundException e) {
            Logger.logShort("error", "Class \"%s\" not found!", classpath);
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

    }

    public void unloadComponent() {

    }
}
