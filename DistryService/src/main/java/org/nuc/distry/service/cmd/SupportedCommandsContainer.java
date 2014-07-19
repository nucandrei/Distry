package org.nuc.distry.service.cmd;

import java.io.Serializable;
import java.util.Set;

public class SupportedCommandsContainer implements Serializable {
    private static final long serialVersionUID = 7856361769385377446L;
    private final Set<Class<? extends Command>> supportedCommands;
    private final String serviceName;

    public SupportedCommandsContainer(Set<Class<? extends Command>> supportedCommands, String serviceName) {
        this.supportedCommands = supportedCommands;
        this.serviceName = serviceName;
    }

    public Set<Class<? extends Command>> getSupportedCommands() {
        return supportedCommands;
    }

    public String getServiceName() {
        return serviceName;
    }
}
