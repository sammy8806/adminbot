package de.steven_tappert.adminbot.components.xmpp.mucActions;

public abstract class mucAction implements mucActionInterface {

    public String getActionName() {
        return this.getClass().getSimpleName();
    }
}
