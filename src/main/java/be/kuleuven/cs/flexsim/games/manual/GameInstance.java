/**
 * 
 */
package be.kuleuven.cs.flexsim.games.manual;


/**
 * @author Kristof Coninx (kristof.coninx AT cs.kuleuven.be)
 *
 */
public interface GameInstance {

    public abstract long[] getPayOffs();

    public abstract void start();

    public abstract void init();

}
