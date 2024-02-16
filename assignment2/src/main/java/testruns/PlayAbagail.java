package testruns;

import dist.DiscreteUniformDistribution;
import dist.Distribution;
import shared.Instance;
import util.linalg.Vector;

import java.util.Arrays;

public class PlayAbagail {

    public static void main(String[] args) {
        distributionPlay();

    }

    public static void distributionPlay(){

        int[] ranges = new int[20];
        Arrays.fill(ranges, 2);

        double[] instVec = {1.0, 0.0};
        Instance myInst = new Instance(instVec);

        Distribution dUniDist = new DiscreteUniformDistribution(ranges);
        System.out.printf("Dist %f", dUniDist.p(myInst));

        Instance sample = null;
        for (int i = 0; i < 10; i++) {
            sample = dUniDist.sample();
            //Two digits
            int s0 = sample.getDiscrete(0), s1 = sample.getDiscrete(1);
            System.out.printf("Instance %d %d\n", s0, s1);
        }

        for (int i = 0; i < 10; i++) {
            sample = dUniDist.sample();
            printInstance(sample);
        }


    }

    static void printInstance(Instance sample) {
        Vector X0 = sample.getData();
        StringBuilder sb = new StringBuilder();

        for( int i = 0; i < X0.size(); i++) {
            sb.append((int)X0.get(i)).append(' ');
        }
        System.out.printf("Full instance : %15s\n", sb.toString());
    }


}
