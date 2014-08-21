package be.kuleuven.cs.flexsim.view;

import be.kuleuven.cs.flexsim.domain.util.listener.Listener;

public interface RefreshTrigger {
    void subscribeForTrigger(Listener l);
}
