package org.nuc.distry.ms;

import org.apache.activemq.broker.BrokerService;
import org.apache.log4j.Logger;

public class ActiveMQWrapper implements MessagingServer {
    private static final Logger LOGGER = Logger.getLogger(ActiveMQWrapper.class);
    private final BrokerService brokerService = new BrokerService();

    public ActiveMQWrapper(String bindAddress) throws Exception {
        brokerService.addConnector(bindAddress);
    }

    public void start() throws Exception {
        brokerService.start();
        LOGGER.info("Started ActiveMQ server");
    }

}
