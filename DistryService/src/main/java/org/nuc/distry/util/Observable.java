package org.nuc.distry.util;

import java.util.HashSet;
import java.util.Set;

public class Observable<T> {
    private Set<Observer<T>> observers = new HashSet<>();

    public void addObserver(Observer<T> observer) {
        observers.add(observer);
    }

    public boolean removeObserver(Observer<T> observer) {
        return observers.remove(observer);
    }

    public void removeAllObservers() {
        observers.clear();
    }

    public void notifyObservers() {
        for (Observer<T> observer : observers) {
            observer.update(this);
        }
    }

    public void notifyObservers(T update) {
        for (Observer<T> observer : observers) {
            observer.update(this, update);
        }
    }
}
