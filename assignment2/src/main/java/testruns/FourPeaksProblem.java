package testruns;

import dist.DiscreteDependencyTree;
import dist.DiscreteUniformDistribution;
import dist.Distribution;
import helpers.BroadcastIntervalTrainer;
import helpers.CSVIntervalBroadcaster;
import helpers.IntervalBroadcaster;
import helpers.Utils;
import helpers.runner.ProblemHolder;
import helpers.runner.ProblemHolderStringInject;
import helpers.runner.RunnerHarness;
import helpers.runner.RunnerHarnessImpl;
import opt.DiscreteChangeOneNeighbor;
import opt.GenericHillClimbingProblem;
import opt.HillClimbingProblem;
import opt.NeighborFunction;
import opt.RandomizedHillClimbing;
import opt.SimulatedAnnealing;
import opt.example.FlipFlopEvaluationFunction;
import opt.example.FourPeaksEvaluationFunction;
import opt.ga.CrossoverFunction;
import opt.ga.DiscreteChangeOneMutation;
import opt.ga.GenericGeneticAlgorithmProblem;
import opt.ga.GeneticAlgorithmProblem;
import opt.ga.MutationFunction;
import opt.ga.SingleCrossOver;
import opt.ga.StandardGeneticAlgorithm;
import opt.ga.TwoPointCrossOver;
import opt.prob.GenericProbabilisticOptimizationProblem;
import opt.prob.MIMIC;
import opt.prob.ProbabilisticOptimizationProblem;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Arrays;

public class FourPeaksProblem implements ProblemHolderStringInject {

    HillClimbingProblem hcp;
    FourPeaksEvaluationFunction  ef;
    ProbabilisticOptimizationProblem pop;
    GeneticAlgorithmProblem gap;
    static final int N = 100;
    static final int T = 10;

    String crossOverStrategy = "";
    String mutationStrategy = "";

    int[] ranges = new int[N];
    static int GLOBAL_ITER_MAX = (int) 12000;

    static final double SA_T = 1.0E12D, SA_COOL = 0.99D;
    static final int GA_POP_SZ = 200, GA_TO_MATE = 100, GA_TO_MUTATE = 10;
    static final int MIMIC_SAMP_SZ = 200, MIMIC_TO_KEEP = 5;

    @Override
    public String getProblemName() {
        return "fourpeaks";
    }

    @Override
    public String getProblemMetaData(String runId) {
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");
        String header = String.format("       RunId: %s   Run time: %s \n ===================================\n", runId, sdf1.format(ts));
        String bitSize = String.format("FourPeaks T: %d,  bit size: %d\n", T, N);
        String settingsSA = String.format("SA temp:%,.3f cooling:%.3f\n", SA_T, SA_COOL);
        String settingsGA = String.format("GA pop:%d, mate:%d, mutate:%d\n", GA_POP_SZ, GA_TO_MATE, GA_TO_MUTATE);
        String settingsGAc = String.format("GA crossover strategy: %s, mutation strategy %s\n", this.crossOverStrategy, this.mutationStrategy);
        String settingsMimic = String.format("MIMIC samp_size:%d, to_keep:%d \n", MIMIC_SAMP_SZ, MIMIC_TO_KEEP);
        StringBuilder builder = new StringBuilder();
        return builder.append(header).append(bitSize).append(settingsSA).append(settingsGAc).append(settingsGA).append(settingsMimic).toString();
    }

    @Override
    public void runRHC(String outputFilename) {

        System.out.println("RUNNING RHC: " + outputFilename);
        RandomizedHillClimbing optAlgHillClimb = new RandomizedHillClimbing(this.hcp);
        this.ef.resetFunctionEvaluationCount();
        IntervalBroadcaster ib = new CSVIntervalBroadcaster(optAlgHillClimb, ef, outputFilename);
        BroadcastIntervalTrainer bFit = new BroadcastIntervalTrainer(optAlgHillClimb, GLOBAL_ITER_MAX, 25, ib);
        bFit.train();

        ib.stopBroadcast();

    }

    @Override
    public void runSA(String outputFilename) {
        System.out.println("RUNNING SA: " + outputFilename);
        SimulatedAnnealing optAlgSA = new SimulatedAnnealing(SA_T, SA_COOL, this.hcp);

        IntervalBroadcaster ib = new CSVIntervalBroadcaster(optAlgSA, ef, outputFilename);
        BroadcastIntervalTrainer bFit = new BroadcastIntervalTrainer(optAlgSA, GLOBAL_ITER_MAX, 25, ib);
        bFit.train();

        ib.stopBroadcast();

    }

    @Override
    public void runGA(String outputFilename) {

        System.out.println("RUNNING GA: " + outputFilename);
        StandardGeneticAlgorithm ga = new StandardGeneticAlgorithm(GA_POP_SZ, GA_TO_MATE, GA_TO_MUTATE, gap);
        IntervalBroadcaster ib = new CSVIntervalBroadcaster(ga, ef, outputFilename);
        BroadcastIntervalTrainer bFit = new BroadcastIntervalTrainer(ga, GLOBAL_ITER_MAX, 25, ib);
        bFit.train();

        ib.stopBroadcast();

    }

    @Override
    public void runMIMIC(String outputFilename) {

        System.out.println("RUNNING MIMIC: " + outputFilename);

        MIMIC mimic = new MIMIC(MIMIC_SAMP_SZ, MIMIC_TO_KEEP, this.pop);
        IntervalBroadcaster ib = new CSVIntervalBroadcaster(mimic, ef, outputFilename);
        BroadcastIntervalTrainer bFit = new BroadcastIntervalTrainer(mimic, 3000, 20, ib);
        bFit.train();

        ib.stopBroadcast();

    }

    @Override
    public void resetEvalFunc() {
        this.ef.resetFunctionEvaluationCount();
    }

    public FourPeaksProblem() {
        Arrays.fill(this.ranges, 2);
        this.ef  = new FourPeaksEvaluationFunction(T);
        NeighborFunction nfOne = new DiscreteChangeOneNeighbor(this.ranges);
        Distribution dUniDist = new DiscreteUniformDistribution(this.ranges);
        this.hcp = new GenericHillClimbingProblem(this.ef, dUniDist, nfOne);

        //Setup Genetic Algorithm
        MutationFunction mf = new DiscreteChangeOneMutation(ranges);
        this.mutationStrategy = "DISCRETE_CHANGE_ONE";
        CrossoverFunction cf = new SingleCrossOver();
        this.crossOverStrategy = "SINGLE_CROSS_OVER";

//        CrossoverFunction cf2 = new TwoPointCrossOver();
//        this.crossOverStrategy = "TWOPOINT_CROSS_OVER";

        this.gap = new GenericGeneticAlgorithmProblem(ef, dUniDist, mf, cf);

        Distribution df = new DiscreteDependencyTree(0.1D, this.ranges);
        this.pop = new GenericProbabilisticOptimizationProblem(this.ef, dUniDist, df);
    }

    public static void main(String[] args) {
        ProblemHolder problem = new FourPeaksProblem();

        RunnerHarness harness = new RunnerHarnessImpl();
        harness.initialize(problem, Constants.ROOT_OUTPUT_DIR);

        harness.runProblemWithAllOptimizers(5, Utils.newHashSet("RHC", "SA", "GA", "MIMIC"));

    }
}
