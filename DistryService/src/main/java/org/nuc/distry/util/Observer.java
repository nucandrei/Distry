package org.nuc.distry.util;

public interface Observer<T> {

    public void update(Observable<T> observable);

    public void update(Observable<T> observable, T update);
}
