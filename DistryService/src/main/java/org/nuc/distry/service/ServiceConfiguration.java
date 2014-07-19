package org.nuc.distry.service;

import org.nuc.distry.service.messaging.MessagingAdapter;

public class ServiceConfiguration {
    private final MessagingAdapter adapter;
    private final boolean sendHeartbeats;
    private final int heartbeatInterval;
    private final String heartbeatTopic;
    private final boolean obeyCommands;
    private final String commandTopic;
    private final String publishTopic;

    public ServiceConfiguration(MessagingAdapter adapter, boolean sendHeartbeats, int heartbeatInterval, String heartbeatTopic, boolean obeyCommands, String commandTopic, String publishTopic) {
        this.adapter = adapter;
        this.sendHeartbeats = sendHeartbeats;
        this.heartbeatInterval = heartbeatInterval;
        this.heartbeatTopic = heartbeatTopic;
        this.obeyCommands = obeyCommands;
        this.commandTopic = commandTopic;
        this.publishTopic = publishTopic;
    }

    public MessagingAdapter getMessagingAdapter() {
        return adapter;
    }

    public boolean sendHeartbeats() {
        return sendHeartbeats;
    }

    public int getHeartbeatInterval() {
        return heartbeatInterval;
    }

    public String getHeartbeatTopic() {
        return heartbeatTopic;
    }

    public boolean obeyCommands() {
        return obeyCommands;
    }

    public String getCommandTopic() {
        return commandTopic;
    }

    public String getPublishTopic() {
        return publishTopic;
    }
}
