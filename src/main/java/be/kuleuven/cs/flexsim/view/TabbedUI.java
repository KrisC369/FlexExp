package be.kuleuven.cs.flexsim.view;

import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.List;

import javax.annotation.Nullable;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;

import com.google.common.collect.Lists;

public class TabbedUI extends JTabbedPane {

    private static final long serialVersionUID = 2765705584434479523L;
    private List<Tabbable> views;

    public TabbedUI(Iterable<Tabbable> panels) {
        this.views = Lists.newArrayList(panels);

    }

    public void draw() {
        final JTabbedPane tabbedPane = new JTabbedPane();
        for (Tabbable p : views) {
            tabbedPane.addTab(p.getViewTitle(), null, p.getPanel(),
                    "Shows the " + p.getViewTitle() + " pane.");
        }
        final JFrame frame = new JFrame("[=] FlexSim Graphs [=]");
        frame.setContentPane(tabbedPane);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationByPlatform(true);
        frame.pack();
        frame.setVisible(true);
        frame.addComponentListener(new ComponentListener() {

            @Override
            public void componentShown(@Nullable ComponentEvent e) {
            }

            @Override
            public void componentResized(@Nullable ComponentEvent e) {
                if (e != null) {
                    for (Tabbable p : views) {
                        for (ComponentListener cl : p.getPanel()
                                .getComponentListeners()) {
                            cl.componentResized(e);
                        }
                    }
                }
            }

            @Override
            public void componentMoved(@Nullable ComponentEvent e) {
            }

            @Override
            public void componentHidden(@Nullable ComponentEvent e) {
            }
        });
    }
}
