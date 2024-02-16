package testruns;

import custom.MaxKColorFitnessMinmizeFunction;
import dist.DiscreteDependencyTree;
import dist.DiscreteUniformDistribution;
import dist.Distribution;
import helpers.CSVWriterGeneric;
import helpers.Utils;
import helpers.runner.BasicRunnerHarnessImpl;
import helpers.runner.ProblemHolder;
import helpers.runner.ProblemHolderWriterInject;
import helpers.runner.RunnerHarness;
import helpers.runner.RunnerHarnessImpl;
import opt.GenericHillClimbingProblem;
import opt.HillClimbingProblem;
import opt.NeighborFunction;
import opt.RandomizedHillClimbing;
import opt.SimulatedAnnealing;
import opt.SwapNeighbor;
import opt.example.FourPeaksEvaluationFunction;
import opt.ga.CrossoverFunction;
import opt.ga.GenericGeneticAlgorithmProblem;
import opt.ga.GeneticAlgorithmProblem;
import opt.ga.MutationFunction;
import opt.ga.SingleCrossOver;
import opt.ga.StandardGeneticAlgorithm;
import opt.ga.SwapMutation;
import opt.prob.GenericProbabilisticOptimizationProblem;
import opt.prob.MIMIC;
import opt.prob.ProbabilisticOptimizationProblem;
import shared.FixedIterationTrainer;
import shared.Instance;
import util.graph.Edge;
import util.graph.Graph;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class MaxKColoringProblem implements ProblemHolderWriterInject {

    HillClimbingProblem hcp;
    MaxKColorFitnessMinmizeFunction ef;
    ProbabilisticOptimizationProblem pop;
    GeneticAlgorithmProblem gap;



    String crossOverStrategy = "";
    String mutationStrategy = "";
    String nodeConfig ="";

    static int GLOBAL_ITER_MAX = (int) 2E+5;

    static final double SA_T = 1.0E12D, SA_COOL = 0.95D;
    static final int RHC_ITERATIONS = GLOBAL_ITER_MAX;
    static final int SA_ITERATIONS = GLOBAL_ITER_MAX;
    static final int GA_POP_SZ = 200, GA_TO_MATE = 100, GA_TO_MUTATE = 10,  GA_ITERATIONS=50;
    static final int MIMIC_SAMP_SZ = 200, MIMIC_TO_KEEP = 5, MIMIC_ITERATIONS = 10;


    static final int SAMPLE_COUNT = 200;
    static final int K =3; //Kcolor value

    @Override
    public String getProblemName() {
        return "kcolor";
    }

    @Override
    public String getProblemMetaData(String runId) {
//        Timestamp ts = new Timestamp(System.currentTimeMillis());
//        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");
        String header = String.format("KColoring  K=%d   GraphType: %s  SAMPLES: %d\n", K, this.nodeConfig, SAMPLE_COUNT);
        String settingsSA = String.format("SA temp:%,.3f cooling:%.3f training iterations: %d\n", SA_T, SA_COOL, SA_ITERATIONS);
        String settingsGA = String.format("GA pop:%d, mate:%d, mutate:%d  iterations: %d\n", GA_POP_SZ, GA_TO_MATE, GA_TO_MUTATE, GA_ITERATIONS);
        String settingsGAc = String.format("GA crossover strategy: %s, mutation strategy %s\n", this.crossOverStrategy, this.mutationStrategy);
        String settingsMimic = String.format("MIMIC samp_size:%d, to_keep:%d  iterations: %d\n", MIMIC_SAMP_SZ, MIMIC_TO_KEEP, MIMIC_ITERATIONS);
        StringBuilder builder = new StringBuilder();
        return builder.append(header).append(settingsSA).append(settingsGAc).append(settingsGA).append(settingsMimic).toString();
    }

    @Override
    public void runRHC(CSVWriterGeneric csvWriter) {

        long starttime = System.nanoTime();
        RandomizedHillClimbing rhc = new RandomizedHillClimbing(hcp);
        FixedIterationTrainer fit = new FixedIterationTrainer(rhc, RHC_ITERATIONS);//20000
        fit.train();
        double trainingTime = System.nanoTime() - starttime;

        Instance optimal = rhc.getOptimal();
        String fitVal = Double.toString(ef.value(optimal));
        System.out.println("RHC: " + fitVal + ".. Colors: " + MaxKColoring.discreteColors(optimal));
        System.out.println(ef.foundConflict());

        double elapsedSeconds  = trainingTime / 1_000_000_000;
        String trainTime = Double.toString(elapsedSeconds);
        System.out.println("RHC Time : " + trainTime);
        String fEvals = Integer.toString(ef.getFunctionEvaluations());
        System.out.println("RHC fEvals : " + fEvals);
        csvWriter.writeRow( Arrays.asList(new String[]{trainTime,fEvals,fitVal}) );

    }

    @Override
    public void runSA(CSVWriterGeneric csvWriter) {

        long starttime = System.nanoTime();
        SimulatedAnnealing sa = new SimulatedAnnealing(SA_T, SA_COOL, hcp);
        FixedIterationTrainer fit = new FixedIterationTrainer(sa, SA_ITERATIONS);
        fit.train();

        double trainingTime = System.nanoTime() - starttime;

        Instance optimal = sa.getOptimal();
        String fitVal = Double.toString(ef.value(optimal));
        System.out.println("SA: " + fitVal+ ".. Colors: " + MaxKColoring.discreteColors(optimal));
        System.out.println(ef.foundConflict());

        double elapsedSeconds  = trainingTime / 1_000_000_000;
        String trainTime = Double.toString(elapsedSeconds);
        System.out.printf("SA Time : %.10f\n", elapsedSeconds);
        String fEvals = Integer.toString(ef.getFunctionEvaluations());
        System.out.println("SA fEvals : " +fEvals );

        csvWriter.writeRow( Arrays.asList(new String[]{trainTime,fEvals,fitVal}) );

    }

    @Override
    public void runGA(CSVWriterGeneric csvWriter) {

        StandardGeneticAlgorithm ga = new StandardGeneticAlgorithm(GA_POP_SZ, GA_TO_MATE, GA_TO_MUTATE, gap);
        long starttime = System.nanoTime();
        FixedIterationTrainer fit = new FixedIterationTrainer(ga, GA_ITERATIONS);
        fit.train();
        double trainingTime = System.nanoTime() - starttime;

        Instance optimal = ga.getOptimal();
        String fitVal = Double.toString(ef.value(optimal));
        System.out.println("GA: " + fitVal+ ".. Colors: " + MaxKColoring.discreteColors(optimal));
        System.out.println(ef.foundConflict());

        double elapsedSeconds  = trainingTime / 1_000_000_000;
        String trainTime = Double.toString(elapsedSeconds);
        System.out.println("GA Time : " + trainTime);

        String fEvals = Integer.toString(ef.getFunctionEvaluations());
        System.out.println("GA fEvals : " + fEvals);

        csvWriter.writeRow( Arrays.asList(new String[]{trainTime,fEvals,fitVal}) );
    }

    @Override
    public void runMIMIC(CSVWriterGeneric csvWriter) {

        MIMIC mimic = new MIMIC(MIMIC_SAMP_SZ, MIMIC_TO_KEEP, pop);
        long starttime = System.nanoTime();
        FixedIterationTrainer fit = new FixedIterationTrainer(mimic, MIMIC_ITERATIONS);
        fit.train();
        double trainingTime = System.nanoTime() - starttime;

        Instance optimal = mimic.getOptimal();
        String fitVal = Double.toString(ef.value(optimal));
        System.out.println("MIMIC: " + fitVal + ".. Colors: " + MaxKColoring.discreteColors(optimal));
        System.out.println(ef.foundConflict());

        double elapsedSeconds  = trainingTime / 1_000_000_000;
        String trainTime = Double.toString(elapsedSeconds);
        System.out.println("MIMIC Time : " + trainTime);

        String fEvals = Integer.toString(ef.getFunctionEvaluations());
        System.out.println("MIMIC fEvals : " + fEvals);

        csvWriter.writeRow( Arrays.asList(new String[]{trainTime,fEvals,fitVal}) );
    }

    @Override
    public void resetEvalFunc() {
        this.ef.resetFunctionEvaluationCount();
    }

    public static void main(String[] args) {

        ProblemHolder problem = new MaxKColoringProblem();

        RunnerHarness harness = new BasicRunnerHarnessImpl();
        harness.initialize(problem, Constants.ROOT_OUTPUT_DIR);

        harness.runProblemWithAllOptimizers(SAMPLE_COUNT, Utils.newHashSet("RHC", "SA", "GA", "MIMIC"));

    }

    public MaxKColoringProblem() {

        Graph graph = null;
        Set<Edge> edges = null;
//        graph = makeMlRoseExample(); this.nodeConfig = "MLRoseGraph";
//        graph = MaxKColoring.makeZachery(); this.nodeConfig = "Zachary";
        graph = MaxKColoring.makeWikiEdges(); this.nodeConfig = "WikiEdges";
//        graph = MaxKColoring.makeSEdges(); this.nodeConfig = "SEdges";
//        graph = MaxKColoring.makePetersen(); this.nodeConfig = "Petersen";
//        graph = makeDivetm(); this.nodeConfig = "Divetm";


        edges = graph.getEdges();


        ef = new MaxKColorFitnessMinmizeFunction(edges.toArray(new Edge[edges.size()]));
        int N = graph.getNodeCount();
        int[] ranges = new int[N];
        Arrays.fill(ranges, K);
        Distribution odd = new DiscreteUniformDistribution(ranges);

        NeighborFunction nf = new SwapNeighbor();
        MutationFunction mf = new SwapMutation();
        CrossoverFunction cf = new SingleCrossOver();
        hcp = new GenericHillClimbingProblem(ef, odd, nf);
        gap = new GenericGeneticAlgorithmProblem(ef, odd, mf, cf);

        Distribution df = new DiscreteDependencyTree(0.1D, ranges);
        pop = new GenericProbabilisticOptimizationProblem(ef, odd, df);

    }
}
