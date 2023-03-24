package utils.observer;

import utils.events.Event;

public interface Observable{
    void addObserver(Observer e);
    void removeObserver(Observer e);
    void notifyObservers();
}
