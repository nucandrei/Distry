package org.nuc.distry.monitor;

import org.nuc.distry.service.hb.Heartbeat;

public class ServiceHeartInfo {
    private final Heartbeat heartbeat;
    private final ServiceStatus serviceStatus;

    public ServiceHeartInfo(Heartbeat lastHeartbeat, ServiceStatus serviceStatus) {
        this.heartbeat = lastHeartbeat;
        this.serviceStatus = serviceStatus;
    }

    public Heartbeat getHeartbeat() {
        return heartbeat;
    }

    public ServiceStatus getServiceStatus() {
        return serviceStatus;
    }

    public String getServiceName() {
        return heartbeat.getServiceName();
    }
}
