package org.nuc.distry.service;

public interface Publisher {
    public void publishSupportedCommands();

    public void shutdownGracefully();
}
