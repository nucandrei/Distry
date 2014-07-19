package org.nuc.distry.service.messaging;

import java.io.Serializable;

import javax.jms.JMSException;

import org.nuc.distry.service.DistryListener;

public interface MessagingAdapter {
    public void start() throws Exception;

    public void addMessageListener(String topic, DistryListener listener) throws JMSException;

    public void removeMessageListener(String topic, DistryListener listener) throws JMSException;

    public void sendMessage(String topic, Serializable message) throws JMSException;
}