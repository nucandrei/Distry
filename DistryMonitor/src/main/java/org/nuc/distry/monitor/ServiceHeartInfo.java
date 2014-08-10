package org.nuc.distry.monitor;

import org.nuc.distry.service.hb.Heartbeat;

public class ServiceHeartInfo implements Comparable<ServiceHeartInfo> {
    private final Heartbeat heartbeat;
    private final ServiceStatus serviceStatus;
    private final boolean configuredService;
    private final String serviceName;

    public ServiceHeartInfo(Heartbeat lastHeartbeat, final String serviceName, ServiceStatus serviceStatus, boolean configuredService) {
        this.heartbeat = lastHeartbeat;
        this.serviceName = serviceName;
        this.serviceStatus = serviceStatus;
        this.configuredService = configuredService;
    }

    public Heartbeat getHeartbeat() {
        return heartbeat;
    }

    public ServiceStatus getServiceStatus() {
        return serviceStatus;
    }

    public String getServiceName() {
        return serviceName;
    }

    public boolean isConfiguredService() {
        return configuredService;
    }

    @Override
    public int compareTo(ServiceHeartInfo that) {
        return getServiceName().compareTo(that.getServiceName());
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof ServiceHeartInfo)) {
            return false;
        }
        final ServiceHeartInfo that = (ServiceHeartInfo) object;
        return this.getServiceName().equals(that.getServiceName());
    }

    @Override
    public int hashCode() {
        return getServiceName().hashCode();
    }
}
