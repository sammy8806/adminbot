package de.steven_tappert.adminbot.components.xmpp.ChatCommands;

import java.util.Random;

public class troll extends XmppChatCmd {

    private String trollName;
    private String trollMessage;

    public troll() {
        Random random = new Random();
        Integer zahl = random.nextInt();








        if(zahl.equals(5)) {}
    }

    public String getTrollName() {
        return trollName;
    }

    public void setTrollName(String trollName) {
        this.trollName = trollName;
    }

    public String getTrollMessage() {
        return trollMessage;
    }

    public void setTrollMessage(String trollMessage) {
        this.trollMessage = trollMessage;
    }


}
