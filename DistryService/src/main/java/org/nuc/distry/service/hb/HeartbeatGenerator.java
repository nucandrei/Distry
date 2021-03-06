package org.nuc.distry.service.hb;

public class HeartbeatGenerator {
    private final String serviceName;
    private ServiceState serviceState = ServiceState.NO_ERROR;
    private String commment = "";
    private long time;

    public HeartbeatGenerator(String serviceName) {
        this.serviceName = serviceName;
    }

    public HeartbeatGenerator setServiceState(ServiceState serviceState) {
        this.serviceState = serviceState;
        return this;
    }

    public HeartbeatGenerator setComment(String comment) {
        this.commment = comment;
        return this;
    }

    private HeartbeatGenerator updateTime() {
        this.time = System.currentTimeMillis();
        return this;
    }

    public Heartbeat generateHeartbeat() {
        updateTime();
        return new Heartbeat(serviceName, serviceState, commment, time);
    }

    public void reset() {
        this.serviceState = ServiceState.NO_ERROR;
        this.commment = "";
    }
}
