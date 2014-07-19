package org.nuc.distry.service;

import java.io.Serializable;

import javax.jms.JMSException;

import org.nuc.distry.service.messaging.MessagingAdapter;

public class Service {
    private final MessagingAdapter messagingAdapter;
    private final String serviceName;

    public Service(String serviceName, MessagingAdapter messagingAdapter) throws Exception {
        this.serviceName = serviceName;
        this.messagingAdapter = messagingAdapter;
        this.messagingAdapter.start();
    }

    public void addMessageListener(String topic, DistryListener listener) throws JMSException {
        this.messagingAdapter.addMessageListener(topic, listener);
    }

    public void removeMessageListener(String topic, DistryListener listener) throws JMSException {
        this.messagingAdapter.removeMessageListener(topic, listener);
    }

    public void sendMessage(String topic, Serializable message) throws JMSException {
        this.messagingAdapter.sendMessage(topic, message);
    }

    public String getServiceName() {
        return serviceName;
    }
}
