//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package testruns;

import dist.DiscreteDependencyTree;
import dist.DiscreteUniformDistribution;
import dist.Distribution;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

import opt.DiscreteChangeOneNeighbor;
import opt.GenericHillClimbingProblem;
import opt.HillClimbingProblem;
import opt.NeighborFunction;
import opt.RandomizedHillClimbing;
import opt.SimulatedAnnealing;
import opt.example.FlipFlopEvaluationFunction;
import opt.ga.CrossoverFunction;
import opt.ga.DiscreteChangeOneMutation;
import opt.ga.GenericGeneticAlgorithmProblem;
import opt.ga.GeneticAlgorithmProblem;
import opt.ga.MutationFunction;
import opt.ga.SingleCrossOver;
import opt.ga.StandardGeneticAlgorithm;
import opt.prob.GenericProbabilisticOptimizationProblem;
import opt.prob.MIMIC;
import opt.prob.ProbabilisticOptimizationProblem;
import shared.FixedIterationTrainer;

public class FlipflopFirst {
    HillClimbingProblem flipFlopHcp;
    FlipFlopEvaluationFunction ef;
    ProbabilisticOptimizationProblem pop;
    GeneticAlgorithmProblem gap;
    static final int N = 1000;
    int[] ranges = new int[N];
    static int GLOBAL_ITER_MAX = (int)5E+3;

    public static void main(String[] args) {
        FlipflopFirst ffFirst = new FlipflopFirst();
        System.out.printf("Running Iterations: %d \n", GLOBAL_ITER_MAX);
//        ffFirst.runOpt("runHillClimb");
//        ffFirst.runOpt("runSimulatedAnnealing");
//        ffFirst.runOpt("runGA");
        ffFirst.runOpt("runMIMIC");
    }

    public FlipflopFirst() {
        Arrays.fill(this.ranges, 2);
        this.ef = new FlipFlopEvaluationFunction();
        NeighborFunction nfOne = new DiscreteChangeOneNeighbor(this.ranges);
        Distribution dUniDist = new DiscreteUniformDistribution(this.ranges);
        this.flipFlopHcp = new GenericHillClimbingProblem(this.ef, dUniDist, nfOne);

        MutationFunction mf = new DiscreteChangeOneMutation(ranges);
        CrossoverFunction cf = new SingleCrossOver();
        this.gap = new GenericGeneticAlgorithmProblem(ef, dUniDist, mf, cf);

        Distribution df = new DiscreteDependencyTree(0.1D, this.ranges);
        this.pop = new GenericProbabilisticOptimizationProblem(this.ef, dUniDist, df);

    }

    public double runHillClimb() {
        RandomizedHillClimbing optAlgHillClimb = new RandomizedHillClimbing(this.flipFlopHcp);
        this.ef.resetFunctionEvaluationCount();
        FixedIterationTrainer fixItT = new FixedIterationTrainer(optAlgHillClimb, GLOBAL_ITER_MAX);
        fixItT.train();
        return this.ef.value(optAlgHillClimb.getOptimal());
    }

    public double runSimulatedAnnealing() {
        SimulatedAnnealing optAlgSA = new SimulatedAnnealing(1.0E12D, 0.95D, this.flipFlopHcp);
        FixedIterationTrainer fixItT = new FixedIterationTrainer(optAlgSA, GLOBAL_ITER_MAX);
        fixItT.train();
        return this.ef.value(optAlgSA.getOptimal());
    }

    public double runGA() {
        StandardGeneticAlgorithm ga = new StandardGeneticAlgorithm(200, 100, 20, gap);
        FixedIterationTrainer fit = new FixedIterationTrainer(ga, GLOBAL_ITER_MAX);
        fit.train();
        return this.ef.value(ga.getOptimal());
    }

    public double runMIMIC() {
        MIMIC mimic =  new MIMIC(200, 5, this.pop);
        FixedIterationTrainer fit = new FixedIterationTrainer(mimic, 100);
        fit.train();
        return this.ef.value(mimic.getOptimal());

    }

//    public double runMIMICSampleTest() {
//        int[] sampeSize = {50,100,200,300,400};
//        MIMIC mimic =  new MIMIC(200, 5, this.pop);
//        IntervalBroadcaster ib = new CSVIntervalBroadcaster(mimic,ef);
//        for (int ss:sampeSize) {
//            System.out.printf("Starting: Running eval of mimic Samples: %d\n",ss);
//            ef.resetFunctionEvaluationCount();
//
//            mimic = new MIMIC(ss, 5, this.pop);
//            BroadcastIntervalTrainer bFit = new BroadcastIntervalTrainer(mimic, 100,25, ib);
////            FixedIterationTrainer fit = new FixedIterationTrainer(mimic, 100);
//            bFit.train();
//            System.out.printf("Done: Running eval of mimic Samples: %d\n",ss);
//        }
//
//        return this.ef.value(mimic.getOptimal());
//    }

    public void runOpt(String optName) {
        System.out.printf("Running Optimization: %s \n", optName);
        long start = System.nanoTime();
        double optResult = 0.0D;

        try {
            this.ef.resetFunctionEvaluationCount();
            Method optMethod = this.getClass().getMethod(optName);
            start = System.nanoTime();
            optResult = (Double)optMethod.invoke(this);
        } catch (NoSuchMethodException var11) {
            var11.printStackTrace();
            System.exit(1);
        } catch (InvocationTargetException var12) {
            var12.printStackTrace();
            System.exit(1);
        } catch (IllegalAccessException var13) {
            var13.printStackTrace();
            System.exit(1);
        }

        long end = System.nanoTime();
        double trainingTime = (double)(end - start);
        trainingTime /= Math.pow(10.0D, 9.0D);
        System.out.printf("Finished training %s in %.4f sec\n", optName, trainingTime);
        System.out.printf("%20.4f fitness\n", optResult);
        System.out.printf("%20d function evals\n", this.ef.getFunctionEvaluations());
    }
}
