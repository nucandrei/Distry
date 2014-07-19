package org.nuc.distry.service.messaging;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.Topic;

import org.apache.log4j.Logger;
import org.nuc.distry.service.DistryListener;

public abstract class JMSAdapter implements MessagingAdapter {
    private final static Logger LOGGER = Logger.getLogger(JMSAdapter.class);
    private ConnectionFactory connectionFactory;
    private Connection connection;

    private final Map<String, MessageConsumer> consumersMap;
    private final Map<String, MessageProducer> producersMap;
    private final Map<String, List<DistryListener>> listenersGroupedByTopic;
    private final String address;
    private Session session;

    public JMSAdapter(String serverAddress) throws Exception {
        consumersMap = new HashMap<>();
        producersMap = new HashMap<>();
        listenersGroupedByTopic = new HashMap<>();
        this.address = serverAddress;
        connectToServer();
    }

    private void connectToServer() throws JMSException {
        LOGGER.info(String.format("Trying to connect to : %s", address));
        connectionFactory = getConnectionFactory(address);
        connection = connectionFactory.createConnection();
    }

    public void start() throws JMSException {
        connection.start();
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        LOGGER.info(String.format("Connected to : %s", address));
    }

    public void addMessageListener(String topicName, DistryListener listener) throws JMSException {
        LOGGER.info("Linking message listener to topic: " + topicName);
        MessageConsumer consumer = consumersMap.get(topicName);
        if (consumer == null) {
            final Topic topic = session.createTopic(topicName);
            consumer = session.createConsumer(topic);
            consumersMap.put(topicName, consumer);
            final List<DistryListener> distryListeners = new ArrayList<>();
            listenersGroupedByTopic.put(topicName, distryListeners);
            consumer.setMessageListener(new MessageListener() {
                @Override
                public void onMessage(Message message) {
                    try {
                        final ObjectMessage objectMessage = (ObjectMessage) message;
                        final Serializable serializable = objectMessage.getObject();
                        for (DistryListener listener : distryListeners) {
                            listener.onMessage(serializable);
                        }

                    } catch (Exception exception) {
                        LOGGER.error("Failed to parse received message", exception);
                    }
                }
            });
        }
        listenersGroupedByTopic.get(topicName).add(listener);
    }

    public void removeMessageListener(String topic, DistryListener listener) throws JMSException {
        final List<DistryListener> distryListeners = listenersGroupedByTopic.get(topic);
        if (distryListeners != null) {
            distryListeners.remove(listener);
        }
    }

    public void sendMessage(String topicName, Serializable message) throws JMSException {
        final ObjectMessage objectMessage = session.createObjectMessage();
        objectMessage.setObject(message);
        MessageProducer intendedProducer = producersMap.get(topicName);
        if (intendedProducer == null) {
            final Topic topic = session.createTopic(topicName);
            intendedProducer = session.createProducer(topic);
            producersMap.put(topicName, intendedProducer);
        }
        intendedProducer.send(objectMessage);
        LOGGER.debug("Sent message: " + message + " on topic: " + topicName);
    }

    public abstract ConnectionFactory getConnectionFactory(String address);
}
