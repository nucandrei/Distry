package org.nuc.distry.monitor;

import org.nuc.distry.service.hb.Heartbeat;

public class ServiceHeartbeatCollector {
    private final String serviceName;
    private Heartbeat lastHeartbeat;
    private ServiceStatus serviceStatus;

    public ServiceHeartbeatCollector(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void onHeartbeat(Heartbeat receivedHeartbeat) {
        this.lastHeartbeat = receivedHeartbeat;
        this.serviceStatus = ServiceStatus.OK;
    }

    public void tick() {
        switch (serviceStatus) {
        case OK:
            this.serviceStatus = ServiceStatus.OKLATE;
            break;
        case OKLATE:
            this.serviceStatus = ServiceStatus.LATE;
            break;
        case LATE:
            this.serviceStatus = ServiceStatus.LOST;
            break;
        case UNKNOWN:
            break;
        case LOST:
            break;
        }
    }

    public ServiceHeartInfo getServiceHeartInfo() {
        return new ServiceHeartInfo(lastHeartbeat, getServiceStatus());
    }

    public ServiceStatus getServiceStatus() {
        if (serviceStatus.equals(ServiceStatus.OKLATE)) {
            return ServiceStatus.OK;
        }
        return serviceStatus;
    }
}
