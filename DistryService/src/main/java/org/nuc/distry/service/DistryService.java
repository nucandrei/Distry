package org.nuc.distry.service;

import java.io.IOException;
import java.io.Serializable;
import java.util.Timer;
import java.util.TimerTask;

import javax.jms.JMSException;

import org.apache.log4j.Logger;
import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.nuc.distry.service.cmd.Command;
import org.nuc.distry.service.cmd.CommandAction;
import org.nuc.distry.service.cmd.CommandManager;
import org.nuc.distry.service.hb.Heartbeat;
import org.nuc.distry.service.hb.HeartbeatGenerator;
import org.nuc.distry.service.hb.ServiceState;
import org.nuc.distry.service.io.FileManager;

public class DistryService extends Service {
    private final static Logger LOGGER = Logger.getLogger(DistryService.class);
    private final HeartbeatGenerator heartbeatGenerator;
    private final FileManager fileManager;
    private final ServiceConfiguration serviceConfiguration;
    private final CommandManager commandManager;

    public DistryService(String serviceName, ServiceConfiguration configuration) throws Exception {
        super(serviceName, configuration.getMessagingAdapter());
        this.serviceConfiguration = configuration;
        this.heartbeatGenerator = new HeartbeatGenerator(serviceName);
        this.fileManager = new FileManager();
        this.commandManager = new CommandManager();
    }

    public void start() throws JMSException {
        if (serviceConfiguration.sendHeartbeats()) {
            startHeartbeatGenerator();
        }
        
        if (serviceConfiguration.obeyCommands()) {
            startListeningForCommands();
        }
    }

    public ServiceConfiguration getServiceConfiguration() {
        return serviceConfiguration;
    }

    public void setServiceState(ServiceState serviceState, boolean forceHeartbeat) {
        this.heartbeatGenerator.setServiceState(serviceState);
        if (forceHeartbeat) {
            sendHeartbeat();
        }
    }

    public void setServiceComment(String comment, boolean forceHeartbeat) {
        this.heartbeatGenerator.setComment(comment);
        if (forceHeartbeat) {
            sendHeartbeat();
        }
    }

    public Document loadXMLDocument(String documentName) throws JDOMException, IOException {
        return fileManager.loadXMLDocument(documentName);
    }

    public String loadTextFile(String filepath) throws IOException {
        return fileManager.loadTextFile(filepath);
    }

    public void addCommand(Class<? extends Command> commandClass, CommandAction action) {
        this.commandManager.addSupportedCommand(commandClass, action);
    }

    private void startHeartbeatGenerator() {
        Timer timer = new Timer();
        final TimerTask heartbeatTask = new TimerTask() {
            @Override
            public void run() {
                sendHeartbeat();
            }
        };
        timer.scheduleAtFixedRate(heartbeatTask, 0, serviceConfiguration.getHeartbeatInterval());
    }

    private void sendHeartbeat() {
        final Heartbeat heartbeat = heartbeatGenerator.generateHeartbeat();
        try {
            DistryService.this.sendMessage(serviceConfiguration.getHeartbeatTopic(), heartbeat);
            LOGGER.debug("Sent heartbeat");
        } catch (Exception e) {
            LOGGER.error("Could not send heartbeat.", e);
        }
    }

    private void startListeningForCommands() throws JMSException {
        addMessageListener(serviceConfiguration.getCommandTopic(), new DistryListener() {
            @Override
            public void onMessage(Serializable message) {
                if (message instanceof Command) {
                    final Command command = (Command) message;
                    if (command.getServiceName().equals(getServiceName())) {
                        commandManager.onCommand(command);
                    }
                    
                } else {
                    LOGGER.warn("Received unwanted message on command topic : " + message.getClass().toString());
                }
            }
        });
    }
}
