package be.kuleuven.cs.flexsim.view;

import java.awt.Dimension;

public class ViewConfig {
    private volatile Dimension dim;
    private volatile int layX;
    private volatile int layY;

    public ViewConfig(Dimension d, int x, int y) {
        this.dim = d;
        this.layX = x;
        this.layY = y;
    }

    /**
     * @return the dim
     */
    public final Dimension getDimension() {
        return dim;
    }

    /**
     * @param dim
     *            the dim to set
     */
    public final void setDimension(Dimension dim) {
        this.dim = dim;
    }

    /**
     * @return the layX
     */
    public final int getLayX() {
        return layX;
    }

    /**
     * @param layX
     *            the layX to set
     */
    public final void setLayX(int layX) {
        this.layX = layX;
    }

    /**
     * @return the layY
     */
    public final int getLayY() {
        return layY;
    }

    /**
     * @param layY
     *            the layY to set
     */
    public final void setLayY(int layY) {
        this.layY = layY;
    }
}
