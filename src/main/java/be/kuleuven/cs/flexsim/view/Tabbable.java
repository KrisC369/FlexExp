package be.kuleuven.cs.flexsim.view;

import javax.swing.JPanel;

public interface Tabbable {

    public abstract JPanel getPanel();

    public abstract String getViewTitle();

}