package be.kuleuven.cs.flexsim.games.manual;

import java.math.BigInteger;
import java.util.List;

import org.apache.commons.math3.random.MersenneTwister;

import com.google.common.collect.Lists;

/**
 * Example template of how to use the FlexSim project to create simulations.
 * 
 * @author Kristof Coninx (kristof.coninx AT cs.kuleuven.be)
 *
 */
public class GameRunner {
    private static final int AGGSTEPS = 1;

    public static void main(String[] args) {
        MersenneTwister t = new MersenneTwister();
        List<GameInstance> app22_1 = Lists.newArrayList();
        for (int i = 0; i < 100; i++) {
            app22_1.add(new Game2_0(t.nextInt(), true));
        }

        t = new MersenneTwister();
        List<GameInstance> app11 = Lists.newArrayList();
        for (int i = 0; i < 100; i++) {
            app11.add(new Game2_0(t.nextInt(), true));
        }

        t = new MersenneTwister();
        List<GameInstance> app22_2 = Lists.newArrayList();
        for (int i = 0; i < 100; i++) {
            app22_2.add(new Game2_0(t.nextInt(), false));
        }

        runSet(app22_1);
        System.out.println("Set 1 done.");
        runSet(app11);
        System.out.println("Set 2 done.");
        runSet(app22_2);
        System.out.println("Set 3 done.");

        int avg00 = (average(app22_1, 0) + average(app22_1, 1)) / 2;
        int avg01 = average(app11, 0);
        int avg10 = average(app11, 1);
        int avg11 = (average(app22_2, 0) + average(app22_2, 1)) / 2;

        prettyPrint(avg00, avg01, avg10, avg11);

    }

    private static void prettyPrint(int avg00, int avg01, int avg10, int avg11) {
        StringBuilder b = new StringBuilder();
        b.append("Heuristic Payoff Table: 2x2 game:\n")
                .append("------------------------\n").append("| ")
                .append(avg00).append(" | ").append(avg01).append(" |\n")
                .append("| ").append(avg10).append(" | ").append(avg11)
                .append(" |\n").append("------------------------\n");
        System.out.println(b.toString());

    }

    private static int average(List<GameInstance> app11, int i) {
        BigInteger sum = BigInteger.valueOf(0);
        for (GameInstance g : app11) {
            sum = sum.add(BigInteger.valueOf(g.getPayOffs()[i]));
        }
        int result = sum.divide(BigInteger.valueOf(app11.size())).intValue();
        return result;
    }

    private static void runSet(List<GameInstance> app22) {
        for (GameInstance g : app22) {
            g.init();
            g.start();
        }
    }

}
