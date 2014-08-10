package org.nuc.distry.service.cmd;

public class ResetHeartbeatCommand extends TargetedCommand {
    private static final long serialVersionUID = -6008659026411135935L;

    public ResetHeartbeatCommand(String serviceName) {
        super(serviceName);
    }
}
