package de.steven_tappert.adminbot;

import de.steven_tappert.adminbot.components.xmpp.XmppComponent;
import de.steven_tappert.tools.SingletonHelper;

public class startup {

    public static void main(String args[]) {

        SingletonHelper.registerInstance(new adminbot());
        adminbot bot = (adminbot) SingletonHelper.getInstance("adminbot");

        bot.loadComponent(new XmppComponent());
    }
}
