package org.nuc.distry.monitor;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import javax.jms.JMSException;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.nuc.distry.service.DistryListener;
import org.nuc.distry.service.DistryService;
import org.nuc.distry.service.ServiceConfiguration;
import org.nuc.distry.service.cmd.Command;
import org.nuc.distry.service.cmd.PublishSupportedCmdsCommand;
import org.nuc.distry.service.cmd.SupportedCommandsContainer;
import org.nuc.distry.service.cmd.TargetedCommand;
import org.nuc.distry.service.hb.Heartbeat;
import org.nuc.distry.service.messaging.ActiveMQAdapter;
import org.nuc.distry.util.Observable;

public class DistryMonitor extends DistryService {
    private static final String SERVER_ADDRESS = "serverAddress";
    private static final String LOG4J = "log4j";
    private static final String DISTRYMONITOR_SERVICE_NAME = "DistryMonitor";
    private static final Logger LOGGER = Logger.getLogger(DistryMonitor.class);
    private final Map<String, ServiceHeartbeatCollector> collectors = new HashMap<>();
    private final Map<String, Set<Class<? extends Command>>> supportedCommandsByServiceName = new HashMap<>();
    public final Observable<ServiceHeartInfo> serviceInfo = new Observable<>();

    public DistryMonitor(ServiceConfiguration configuration) throws Exception {
        super(DISTRYMONITOR_SERVICE_NAME, configuration);
        super.start();
        startListeningForHeartbeats();
        requestSupportedCommands();
        startTick();
    }

    public void sendCommand(Command command) throws JMSException {
        if (command instanceof TargetedCommand) {
            final TargetedCommand targetedCommand = (TargetedCommand) command;
            final String serviceName = targetedCommand.getServiceName();
            if (!getSupportedCommands(serviceName).contains(targetedCommand.getClass())) {
                LOGGER.error("Tried to send illegal command " + command.getClass());
                return;
            }
        }
        sendMessage(getServiceConfiguration().getCommandTopic(), command);
    }

    public Set<Class<? extends Command>> getSupportedCommands(String serviceName) {
        if (supportedCommandsByServiceName.containsKey(serviceName)) {
            return supportedCommandsByServiceName.get(serviceName);

        } else {
            return Collections.emptySet();
        }
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

    private void requestSupportedCommands() throws JMSException {
        final DistryListener supportedCommandsListener = new DistryListener() {
            @Override
            public void onMessage(Serializable message) {
                if (message instanceof SupportedCommandsContainer) {
                    final SupportedCommandsContainer container = (SupportedCommandsContainer) message;
                    supportedCommandsByServiceName.put(container.getServiceName(), container.getSupportedCommands());
                }
            }
        };
        addMessageListener(getServiceConfiguration().getPublishTopic(), supportedCommandsListener);
        sendMessage(getServiceConfiguration().getCommandTopic(), new PublishSupportedCmdsCommand());
    }

    public static void main(String[] args) {
        try {
            BasicConfigurator.configure();
            final String serverAddress = parseArguments(args);
            new DistryMonitor(new ServiceConfiguration(new ActiveMQAdapter(serverAddress), true, 10000, "Heartbeat", true, "Commands", "Publish"));

        } catch (Exception e) {
            LOGGER.error("Failed to start DistryMonitor", e);
        }
    }

    private static String parseArguments(String[] args) throws Exception {
        final Options options = new Options();
        options.addOption(LOG4J, true, "Log4j configuration file");
        options.addOption(SERVER_ADDRESS, true, "Server address");

        final CommandLineParser parser = new BasicParser();
        final CommandLine commandLine = parser.parse(options, args);

        if (commandLine.hasOption(LOG4J)) {
            PropertyConfigurator.configure(commandLine.getOptionValue(LOG4J));

        } else {
            throw new IllegalArgumentException("log4j argument is missing");
        }

        if (commandLine.hasOption(SERVER_ADDRESS)) {
            return commandLine.getOptionValue(SERVER_ADDRESS);

        } else {
            throw new IllegalArgumentException("serverAddress argument is missing");
        }
    }
}
