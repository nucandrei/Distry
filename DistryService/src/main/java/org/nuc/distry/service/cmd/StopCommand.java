package org.nuc.distry.service.cmd;

public class StopCommand extends TargetedCommand {
    private static final long serialVersionUID = 5528137147962814370L;

    public StopCommand(String serviceName) {
        super(serviceName);
    }
}
