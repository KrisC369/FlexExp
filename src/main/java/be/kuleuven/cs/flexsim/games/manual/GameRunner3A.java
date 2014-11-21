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
public class GameRunner3A {
    private static final int AGGSTEPS = 1;
    private static final int agentsPoolSize = 3;

    public static void main(String[] args) {
        MersenneTwister t = new MersenneTwister();
        List<GameInstance> app30 = Lists.newArrayList();
        for (int i = 0; i < 100; i++) {
            app30.add(new Game2_0(t.nextInt(), true, agentsPoolSize));
        }

        List<GameInstance> app21 = Lists.newArrayList();
        for (int i = 0; i < 100; i++) {
            app21.add(new GameX_X(t.nextInt(), 1, 2));
        }

        t = new MersenneTwister();
        List<GameInstance> app12 = Lists.newArrayList();
        for (int i = 0; i < 100; i++) {
            app12.add(new GameX_X(t.nextInt(), 2, 1));
        }

        t = new MersenneTwister();
        List<GameInstance> app03 = Lists.newArrayList();
        for (int i = 0; i < 100; i++) {
            app03.add(new Game2_0(t.nextInt(), false, agentsPoolSize));
        }

        runSet(app30);
        System.out.println("Set 1 done.");
        runSet(app21);
        System.out.println("Set 2 done.");
        runSet(app12);
        System.out.println("Set 3 done.");
        runSet(app03);
        System.out.println("Set 4 done.");

        int a = (average(app30, 0) + average(app30, 1) + average(app30, 2)) / 3;
        int b = (average(app21, 0) + average(app21, 1)) / 2;
        int c = average(app21, 2);

        int d = average(app12, 0);
        int e = (average(app12, 1) + average(app12, 2)) / 2;
        int f = (average(app03, 0) + average(app03, 1) + average(app03, 2)) / 3;

        prettyPrint(a, b, c, d, e, f);

    }

    private static void prettyPrint(int a, int b, int c, int d, int e, int f) {
        StringBuilder bb = new StringBuilder();
        bb.append("Heuristic Payoff Table A : 2x2 game:\n")
                .append("------------------------\n").append("| ").append(a)
                .append(" | ").append(b).append(" |\n").append("| ").append(b)
                .append(" | ").append(f).append(" |\n")
                .append("------------------------\n");
        bb.append("Heuristic Payoff Table B : 2x2 game:\n")
                .append("------------------------\n").append("| ").append(c)
                .append(" | ").append(e).append(" |\n").append("| ").append(e)
                .append(" | ").append(f).append(" |\n")
                .append("------------------------\n");
        bb.append("/nParams:\n").append("a: ").append(a).append(" b: ")
                .append(b).append(" c: ").append(c).append(" d: ").append(d)
                .append(" e: ").append(e).append(" f: ").append(f);
        System.out.println(bb.toString());
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
