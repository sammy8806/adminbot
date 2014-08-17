package de.steven_tappert.adminbot;

public interface BotComponent {

    public String getComponentName();

    public boolean loadComponent();

    public boolean unloadComponent();

}
