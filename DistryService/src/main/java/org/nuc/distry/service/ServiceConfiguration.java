package org.nuc.distry.service;

import org.nuc.distry.service.messaging.MessagingAdapter;

public abstract class ServiceConfiguration {
    private final MessagingAdapter adapter;
    private final boolean sendHeartbeats;
    private final int heartbeatInterval;
    private final String heartbeatTopic;

    public ServiceConfiguration(MessagingAdapter adapter, boolean sendHeartbeats, int heartbeatInterval, String heartbeatTopic) {
        this.adapter = adapter;
        this.sendHeartbeats = sendHeartbeats;
        this.heartbeatInterval = heartbeatInterval;
        this.heartbeatTopic = heartbeatTopic;
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
}
