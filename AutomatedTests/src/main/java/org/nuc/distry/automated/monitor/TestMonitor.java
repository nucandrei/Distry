package org.nuc.distry.automated.monitor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.apache.log4j.BasicConfigurator;
import org.junit.Test;
import org.nuc.distry.monitor.DistryMonitor;
import org.nuc.distry.ms.ActiveMQWrapper;
import org.nuc.distry.ms.MessagingServer;
import org.nuc.distry.service.ServiceConfiguration;
import org.nuc.distry.service.cmd.Command;
import org.nuc.distry.service.cmd.PublishSupportedCmdsCommand;
import org.nuc.distry.service.cmd.StopCommand;
import org.nuc.distry.service.messaging.ActiveMQAdapter;

public class TestMonitor {
    @Test
    public void testDistryMonitorPublishItsSupportedCommands() throws Exception {
        BasicConfigurator.configure();
        new Thread() {
            @Override
            public void run() {

                try {
                    MessagingServer messagingServer = new ActiveMQWrapper("tcp://localhost:61616");
                    messagingServer.start();
                } catch (Exception e) {

                }
            }
        }.start();

        final String serverAddress = "failover://(tcp://localhost:61616)?initialReconnectDelay=2000&maxReconnectAttempts=2";
        final DistryMonitor distryMonitor = new DistryMonitor(new ServiceConfiguration(new ActiveMQAdapter(serverAddress), true, 10000, "Heartbeat", true, "Command", "Publish"));
        Thread.sleep(500);
        Set<Class<? extends Command>> supportedCommands = distryMonitor.getSupportedCommands("DistryMonitor");
        
        assertEquals(2, supportedCommands.size());
        assertTrue(supportedCommands.contains(StopCommand.class));
        assertTrue(supportedCommands.contains(PublishSupportedCmdsCommand.class));
    }
}
