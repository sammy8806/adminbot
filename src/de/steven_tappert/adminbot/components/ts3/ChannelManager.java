package de.steven_tappert.adminbot.components.ts3;

import com.github.theholywaffle.teamspeak3.api.ChannelProperty;
import com.github.theholywaffle.teamspeak3.api.Property;
import com.github.theholywaffle.teamspeak3.api.wrapper.Channel;

import java.util.HashMap;
import java.util.Map;

public class ChannelManager {

    private final Map<Integer, Channel> channels;

    public ChannelManager() {
        this.channels = new HashMap<>();
    }
/*
    public Channel getChannel(Integer cid) {
        return channels.get(cid);
    }

    public Channel insertChannel(Channel ch) {
        return channels.put(Integer.valueOf(properties.get(ChannelProperty.CID)), ch);
    }

    public Channel updateChannel(Channel ch, ChannelProperty prop, String value) {
        Integer cid = Integer.valueOf(ch.properties.get(ChannelProperty.CID));
        Channel tmp = channels.get(cid);
        tmp.properties.replace(prop, value);
        return tmp;
    }*/
}
