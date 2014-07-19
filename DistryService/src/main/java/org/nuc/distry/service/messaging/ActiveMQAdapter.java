package org.nuc.distry.service.messaging;

import javax.jms.ConnectionFactory;

import org.apache.activemq.ActiveMQConnectionFactory;

public class ActiveMQAdapter extends JMSAdapter {

    public ActiveMQAdapter(String serverAddress) throws Exception {
        super(serverAddress);
    }

    @Override
    public ConnectionFactory getConnectionFactory(String address) {
        return new ActiveMQConnectionFactory(address);
    }

}
