package org.nuc.distry.service;

import java.io.Serializable;

public interface DistryListener {
    public void onMessage(Serializable message);
}
