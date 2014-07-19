package org.nuc.distry.service.cmd;

import java.io.Serializable;

public class Command implements Serializable {
    private static final long serialVersionUID = 5591634122055612117L;
    private final String serviceName;

    public Command(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getServiceName() {
        return this.serviceName;
    }
}
