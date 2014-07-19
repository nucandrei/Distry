package org.nuc.distry.monitor;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.jms.JMSException;

import org.apache.log4j.Logger;
import org.nuc.distry.service.DistryListener;
import org.nuc.distry.service.DistryService;
import org.nuc.distry.service.ServiceConfiguration;
import org.nuc.distry.service.hb.Heartbeat;
import org.nuc.distry.util.Observable;

public class DistryMonitor extends DistryService {
    private static final String DISTRYMONITOR_SERVICE_NAME = "DistryMonitor";
    private static final Logger LOGGER = Logger.getLogger(DistryMonitor.class);
    private final Map<String, ServiceHeartbeatCollector> collectors = new HashMap<>();
    public final Observable<ServiceHeartInfo> serviceInfo = new Observable<>();

    public DistryMonitor(ServiceConfiguration configuration) throws Exception {
        super(DISTRYMONITOR_SERVICE_NAME, configuration);
        super.start();
        startListeningForHeartbeats();
        startTick();
    }

    private void startListeningForHeartbeats() throws JMSException {
        final DistryListener heartbeatListener = new DistryListener() {
            @Override
            public void onMessage(Serializable message) {
                if (message instanceof Heartbeat) {
                    final Heartbeat receivedHeartbeat = (Heartbeat) message;
                    interpretHeartbeat(receivedHeartbeat);
                } else {
                    LOGGER.warn("Received unwanted message on heartbeat topic : " + message.getClass().toString());
                }
            }
        };
        addMessageListener(getServiceConfiguration().getHeartbeatTopic(), heartbeatListener);
    }

    private void interpretHeartbeat(Heartbeat receivedHeartbeat) {
        final String serviceName = receivedHeartbeat.getServiceName();
        ServiceHeartbeatCollector serviceHeartbeatCollector = collectors.get(serviceName);
        if (serviceHeartbeatCollector == null) {
            serviceHeartbeatCollector = new ServiceHeartbeatCollector(serviceName);
            collectors.put(serviceName, serviceHeartbeatCollector);
        }

        LOGGER.info("Received heartbeat for service " + serviceName);
        serviceHeartbeatCollector.onHeartbeat(receivedHeartbeat);
        serviceInfo.notifyObservers(serviceHeartbeatCollector.getServiceHeartInfo());
    }

    private void startTick() {
        final TimerTask tickTask = new TimerTask() {
            @Override
            public void run() {
                for (ServiceHeartbeatCollector collector : collectors.values()) {
                    collector.tick();
                    serviceInfo.notifyObservers(collector.getServiceHeartInfo());
                }
            }
        };
        new Timer().scheduleAtFixedRate(tickTask, 0, getServiceConfiguration().getHeartbeatInterval());
    }

}
