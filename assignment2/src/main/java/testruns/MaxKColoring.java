package testruns;

import custom.MaxKColorFitnessMinmizeFunction;
import dist.DiscreteDependencyTree;
import dist.DiscreteUniformDistribution;
import dist.Distribution;
import opt.GenericHillClimbingProblem;
import opt.HillClimbingProblem;
import opt.NeighborFunction;
import opt.RandomizedHillClimbing;
import opt.SimulatedAnnealing;
import opt.SwapNeighbor;
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
import shared.DataSet;
import shared.FixedIterationTrainer;
import shared.Instance;
import shared.reader.CSVDataSetReader;
import shared.reader.DataSetReader;
import util.graph.Edge;
import util.graph.Graph;
import util.graph.Node;
import util.linalg.Vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author robododge
 * @version 1.0
 */
public class MaxKColoring {
    /**
     * The K-color value to achieve
     */
    private static final int K = 7; // K possible colors


    /**
     * Java's main() method
     * @param args ignored
     */
    public static void main(String[] args) {

        Graph graph = null;
        Set<Edge> edges = null;
//        graph = makeMlRoseExample();
        graph = makeZachery();
//        edges = makeGNP10();
//         edges = makeGNP10_90();
//         edges = makeGNP100_10();
//        graph = makeWikiEdges();
//        edges = makeWikiEdgesS1();
//        edges = makeSEdges();
//        graph = makePetersen();
//        graph = makeDivetm();
        edges = graph.getEdges();


        MaxKColorFitnessMinmizeFunction ef = new MaxKColorFitnessMinmizeFunction(edges.toArray(new Edge[edges.size()]));
        int N = graph.getNodeCount();
        int[] ranges = new int[N];
        Arrays.fill(ranges, K);
        Distribution odd = new DiscreteUniformDistribution(ranges);

//        Instance inst = odd.sample();
//        System.out.println(inst.getData());
        Set colorUnique = new HashSet(K);

        for (int i = 0; i < 200; i++) {
            colorUnique.add(odd.sample().getDiscrete());
        }
        System.out.printf("Unique colors  for node %s \n", colorUnique);


        NeighborFunction nf = new SwapNeighbor();
        MutationFunction mf = new SwapMutation();
        CrossoverFunction cf = new SingleCrossOver();
        HillClimbingProblem hcp = new GenericHillClimbingProblem(ef, odd, nf);
        GeneticAlgorithmProblem gap = new GenericGeneticAlgorithmProblem(ef, odd, mf, cf);

//        Distribution df = new DiscreteDependencyTree(.1);
        Distribution df = new DiscreteDependencyTree(0.1D, ranges);
        ProbabilisticOptimizationProblem pop = new GenericProbabilisticOptimizationProblem(ef, odd, df);

        long starttime = System.currentTimeMillis();
        RandomizedHillClimbing rhc = new RandomizedHillClimbing(hcp);
        FixedIterationTrainer fit = new FixedIterationTrainer(rhc, 200000);//20000
        fit.train();
        System.out.println("\n\n");
        System.out.println("RHC: " + ef.value(rhc.getOptimal()) + ".. Colors: " + discreteColors(rhc.getOptimal()));
        System.out.println(ef.foundConflict());
        System.out.println("Time : " + (System.currentTimeMillis() - starttime));
        System.out.println("fEvals : " + ef.getFunctionEvaluations());

        System.out.println("============================");
        ef.resetFunctionEvaluationCount();

        starttime = System.currentTimeMillis();
        SimulatedAnnealing sa = new SimulatedAnnealing(1E12, .1, hcp);
        fit = new FixedIterationTrainer(sa, 200000);
        fit.train();
        System.out.println("SA: " + ef.value(sa.getOptimal())+ ".. Colors: " + discreteColors(sa.getOptimal()));
        System.out.println(ef.foundConflict());
        System.out.println("Time : " + (System.currentTimeMillis() - starttime));
        System.out.println("fEvals : " + ef.getFunctionEvaluations());

        System.out.println("============================");

        starttime = System.currentTimeMillis();
        StandardGeneticAlgorithm ga = new StandardGeneticAlgorithm(200, 100, 10, gap);
        fit = new FixedIterationTrainer(ga, 50);
        fit.train();
        System.out.println("GA: " + ef.value(ga.getOptimal())+ ".. Colors: " + discreteColors(ga.getOptimal()));
        System.out.println(ef.foundConflict());
        System.out.println("Time : " + (System.currentTimeMillis() - starttime));
        System.out.println("fEvals : " + ef.getFunctionEvaluations());

        System.out.println("============================");
        ef.resetFunctionEvaluationCount();


        starttime = System.currentTimeMillis();
        MIMIC mimic = new MIMIC(100, 10, pop);
        fit = new FixedIterationTrainer(mimic, 10);
        fit.train();
        System.out.println("MIMIC: " + ef.value(mimic.getOptimal()) + ".. Colors: " + discreteColors(mimic.getOptimal()));
        System.out.println(ef.foundConflict());
        System.out.println("Time : " + (System.currentTimeMillis() - starttime));
        System.out.println("fEvals : " + ef.getFunctionEvaluations());

    }

    static Graph makeMlRoseExample() {
        // 5-Node Graph from as shown in mlrose documentation https://mlrose.readthedocs.io/en/stable/source/fitness.html#mlrose.fitness.MaxKColor
        List<Edge> edges = Arrays.asList(new Edge[6]);
        List<Node> nodes = Arrays.asList(new Node[5]);

        for (int i = 0; i < nodes.size(); i++) {
            Node node = new Node(i);
            nodes.set(i, node);
        }

        for (int i = 0; i < edges.size(); i++) {
            Edge edge = new Edge();
            edges.set(i, edge);
        }

        nodes.get(0).connect(nodes.get(1), edges.get(0));
        nodes.get(0).connect(nodes.get(2), edges.get(1));
        nodes.get(0).connect(nodes.get(4), edges.get(2));
        nodes.get(1).connect(nodes.get(3), edges.get(3));
        nodes.get(2).connect(nodes.get(3), edges.get(4));
        nodes.get(3).connect(nodes.get(4), edges.get(5));


        Graph graph = new Graph();
        graph.setNodes(nodes);
        System.out.println(graph);
        return graph;

    }

    static Graph makeSEdges() {
        //Simple 4 node with 5 edges, 3-color is deterministic
        //https://www.geeksforgeeks.org/m-coloring-problem-backtracking-5/
        List<Edge> edges = Arrays.asList(new Edge[5]);
        List<Node> nodes = Arrays.asList(new Node[4]);

        for (int i = 0; i < nodes.size(); i++) {
            Node node = new Node(i);
            nodes.set(i, node);
        }

        for (int i = 0; i < edges.size(); i++) {
            Edge edge = new Edge();
            edges.set(i, edge);
        }

        nodes.get(0).connect(nodes.get(1), edges.get(0));
        nodes.get(0).connect(nodes.get(2), edges.get(1));
        nodes.get(0).connect(nodes.get(3), edges.get(2));
        nodes.get(3).connect(nodes.get(2), edges.get(3));
        nodes.get(2).connect(nodes.get(1), edges.get(4));

        Graph graph = new Graph();
        graph.setNodes(nodes);
        System.out.println(graph);

        return graph;

    }



    static Graph makeZachery() {
        //From the well known Zachary Karate Club https://commons.wikimedia.org/wiki/File:Zachary%27s_karate_club.png
        return  makeNetwork(34,"src/main/resources/zach_edgelist.csv");
    }

//    static List<Edge> makeGNP15() {
//        List<Edge> edges = makeNetwork(15,"src/main/resources/gnp_graph_15.csv");
//        return edges;
//    }
//    static List<Edge> makeGNP10() {
//        List<Edge> edges = makeNetwork(10,"src/main/resources/gnp_graph_10.csv");
//        return edges;
//    }
//    static List<Edge> makeGNP10_90() {
//        List<Edge> edges = makeNetwork(10,"src/main/resources/gnp_graph_10_90.csv");
//        return edges;
//    }
//    static List<Edge> makeGNP100_10() {
//        List<Edge> edges = makeNetwork(100,"src/main/resources/gnp_graph_100_0.1.csv");
//        return edges;
//    }
    static Graph makeWikiEdges() {
        //6-Node Graph from wikipedia https://en.wikipedia.org/wiki/Graph_coloring#Polynomial_time
       return  makeNetwork(6,"src/main/resources/wiki_graph.csv");
    }

//    static List<Edge> makeWikiEdgesS1() {
//        List<Edge> edges = makeNetwork(10,"src/main/resources/wiki_graph_structure1.csv");
//        return edges;
//    }
    static Graph makePetersen() {
        //Petersen graph from wikipedia https://en.wikipedia.org/wiki/Petersen_graph
        return makeNetwork(10,"src/main/resources/petersen_graph_structure1.csv");
    }

    static Graph makeDivetm() {
        //from the divetm repo https://github.com/divetm/Randomized-optimization/blob/master/k_color_problem.py
        return makeNetwork(5, "src/main/resources/divetm.csv");
    }

    static Graph makeNetwork(int count, String filename) {
        int zachNodeCount = count;
        int a = 0, b = 0;

        List<Node> nodes = Arrays.asList(new Node[zachNodeCount]);
        List<Edge> edges = null;

        for (int i = 0; i < nodes.size(); i++) {
            Node node = new Node(i);
            nodes.set(i, node);
        }

        Instance[] instances = edgeTuples(filename);
        edges = new ArrayList(instances.length);
        Edge edg = null;
        for (Instance inst : instances
        ) {
            a = inst.getDiscrete(0);
            b = inst.getDiscrete(1);
            edg = new Edge();
            edges.add(edg);
            nodes.get(a).connect(nodes.get(b), edg);
        }

        Graph graph = new Graph();
        graph.setNodes(nodes);
        System.out.println(graph);

        assert graph.getEdges().size() == edges.size();
        return graph;


    }



    static Instance[] edgeTuples(String filename) {

//        Tuple<Integer, Integer> edgeTup = null;
//
//        List<Tuple<Integer, Integer>> tupList = new ArrayList<Tuple<Integer, Integer>>();
        DataSetReader reader = new CSVDataSetReader(filename);
        DataSet ds = null;
        try {
            ds = reader.read();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Instance[] instances = ds.getInstances();
//        for (Instance inst : instances
//        ) {
//            System.out.println(inst.getDiscrete(1));
//        }

        return instances;
    }

    static String discreteColors(Instance inst){

        int[] dColors = new int[inst.size()];
        Vector data = inst.getData();
        for (int i = 0;  i < dColors.length ; i++) {
            dColors[i] = (int)data.get(i);
        }
        return Arrays.toString(dColors);

    }

}
