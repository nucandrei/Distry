package org.nuc.distry.service.cmd;

public class TargetedCommand extends Command {
    private static final long serialVersionUID = -1393861569305151561L;
    private final String serviceName;

    public TargetedCommand(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getServiceName() {
        return this.serviceName;
    }
}
