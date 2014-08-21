package be.kuleuven.cs.flexsim.view;


public class MyEdge<T> {

    private T src;
    private T dst;

    public MyEdge(T src, T dst) {
        this.src = src;
        this.dst = dst;
    }

    public T getSource() {
        return src;
    }

    public T getTarget() {
        return dst;
    }

}
