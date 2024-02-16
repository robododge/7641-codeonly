package a4.pivi;

import a4.svpino.artifacts.Algorithm;
import a4.svpino.artifacts.Hazard;
import a4.svpino.artifacts.Problem;
import burlap.behavior.policy.Policy;
import burlap.behavior.policy.PolicyUtils;
import burlap.behavior.singleagent.Episode;
import burlap.behavior.singleagent.auxiliary.StateReachability;
import burlap.behavior.singleagent.auxiliary.valuefunctionvis.ValueFunctionVisualizerGUI;
import burlap.behavior.singleagent.planning.Planner;
import burlap.behavior.singleagent.planning.stochastic.policyiteration.PolicyIteration;
import burlap.behavior.valuefunction.ValueFunction;
import burlap.domain.singleagent.gridworld.GridWorldDomain;
import burlap.domain.singleagent.gridworld.GridWorldRewardFunction;
import burlap.domain.singleagent.gridworld.state.GridAgent;
import burlap.domain.singleagent.gridworld.state.GridLocation;
import burlap.domain.singleagent.gridworld.state.GridWorldState;
import burlap.mdp.auxiliary.common.SinglePFTF;
import burlap.mdp.core.TerminalFunction;
import burlap.mdp.core.oo.propositional.PropositionalFunction;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.oo.OOSADomain;
import burlap.statehashing.simple.SimpleHashableStateFactory;

import java.util.HashMap;
import java.util.List;

public class PolicyIterationExperiment {

    public static void main( String [] args) {

//        Problem problem = createProblem1();
        Problem problem = createProblem2();
        GridWorldDomain gridWorldDomain = new GridWorldDomain(problem.getWidth(), problem.getWidth());
        gridWorldDomain.setMap(problem.getMatrix());
        gridWorldDomain.setProbSucceedTransitionDynamics(0.8);


        TerminalFunction terminalFunction = new SinglePFTF(PropositionalFunction.findPF(gridWorldDomain.generatePfs(), GridWorldDomain.PF_AT_LOCATION));
        GridWorldRewardFunction rewardFunction = new GridWorldRewardFunction(problem.getWidth(), problem.getWidth(), problem.getDefaultReward());

        rewardFunction.setReward(problem.getGoal().x, problem.getGoal().y, problem.getGoalReward());

        for (Hazard hazard : problem.getHazards()) {
            rewardFunction.setReward(hazard.getLocation().x, hazard.getLocation().y, hazard.getReward());
        }

        gridWorldDomain.setTf(terminalFunction);
        gridWorldDomain.setRf(rewardFunction);

        OOSADomain domain = gridWorldDomain.generateDomain();
        GridWorldState initialState = new GridWorldState(new GridAgent(problem.getStart().x, problem.getStart().y), new GridLocation(problem.getGoal().x, problem.getGoal().y, "loc0"));
        SimpleHashableStateFactory hashingFactory = new SimpleHashableStateFactory();


        Planner policyPlanner =  new DeltaVariantPolicyIteration(domain, 0.99, hashingFactory, 0.001, 1000, 100);
        policyPlanner.planFromState(initialState);
        Policy policy = policyPlanner.planFromState(initialState);

        System.out.println("About to run Rollout: ");
        int maxNumberOfSteps = problem.getWidth() * problem.getWidth();
        Episode episode = PolicyUtils.rollout(policy, initialState, domain.getModel(), maxNumberOfSteps);

        System.out.println("Episode num time steps: "+episode.numTimeSteps());
        System.out.println("Final Rewards: "+episode.rewardSequence);

        List<State> states = StateReachability.getReachableStates(initialState, domain, hashingFactory);

        //visualize(problem, (ValueFunction) planner, policy, initialState, domain, hashingFactory, algorithm.getTitle());
        ValueFunctionVisualizerGUI gui = GridWorldDomain.getGridWorldValueFunctionVisualization(states, problem.getWidth(), problem.getWidth(), (ValueFunction)policyPlanner, policy);

        gui.setTitle("Policy Iteration");
        gui.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        gui.initGUI();
    }


    private static Problem createProblem1() {
        /*
         * The surface can be described as follows:
         *
         * X — The starting point of the agent.
         * 0 — Represents a safe cell where the agent can move.
         * 1 — Represents a wall. The agent can't move to this cell.
         * G — Represents the goal that the agent wants to achieve.
         * S — Represents a small hazard. The agent will be penalized.
         * M — Represents a medium hazard. The agent will be penalized.
         * L — Represents a large hazard. The agent will be penalized.
         */
        String[] map = new String[] {
                "X0011110",
                "01000S10",
                "010M110S",
                "0M0000M1",
                "01111010",
                "00L010S0",
                "0S001000",
                "000000SG",
        };

        /*
         * Make sure to specify the specific number of iterations for each algorithm. If you don't
         * do this, I'm still nice and use 100 as the default value, but that wouldn't make sense
         * all the time.
         */
        HashMap<Algorithm, Integer> numIterationsHashMap = new HashMap<Algorithm, Integer>();
        numIterationsHashMap.put(Algorithm.ValueIteration, 50);
        numIterationsHashMap.put(Algorithm.PolicyIteration, 10);
//		numIterationsHashMap.put(Algorithm.QLearning, 500);
        numIterationsHashMap.put(Algorithm.QLearning, 1);

        /*
         * These are the specific rewards for each one of the hazards. Here you can be creative and
         * play with different values as you see fit.
         */
        HashMap<Hazard.HazardType, Double> hazardRewardsHashMap = new HashMap<Hazard.HazardType, Double>();
        hazardRewardsHashMap.put(Hazard.HazardType.SMALL, -1.0);
        hazardRewardsHashMap.put(Hazard.HazardType.MEDIUM, -2.0);
        hazardRewardsHashMap.put(Hazard.HazardType.LARGE, -3.0);

        /*
         * Notice how I specify below the specific default reward for cells with nothing on them (we
         * want regular cells to have a small penalty that encourages our agent to find the goal),
         * and the reward for the cell representing the goal (something nice and large so the agent
         * is happy).
         */
        return new Problem(map, numIterationsHashMap, -0.1, 10, hazardRewardsHashMap);
    }

    private static Problem createProblem2() {
        String[] map = new String[] {
                "111111111111111111111",
                "X00010001000100000101",
                "101110101L1010S110101",
                "100010101000100010101",
                "11101010101111S110101",
                "100010100000100000001",
                "1011101S1010101110101",
                "100010101010001000101",
                "101010101011111010111",
                "101000001000100010001",
                "1110101M111010M110101",
                "100010100010100000101",
                "101110101010101111S01",
                "100010001010001010001",
                "111011101010111010111",
                "101010001010001000101",
                "10101011101L001011101",
                "1000001S0000101010001",
                "101011110110101010101",
                "10100000001000100010G",
                "111111111111111111111",
        };

        HashMap<Algorithm, Integer> numIterationsHashMap = new HashMap<Algorithm, Integer>();
        numIterationsHashMap.put(Algorithm.ValueIteration, 100);
        numIterationsHashMap.put(Algorithm.PolicyIteration, 20);
        numIterationsHashMap.put(Algorithm.QLearning, 1000);

        HashMap<Hazard.HazardType, Double> hazardRewardsHashMap = new HashMap<Hazard.HazardType, Double>();
        hazardRewardsHashMap.put(Hazard.HazardType.SMALL, -1.0);
        hazardRewardsHashMap.put(Hazard.HazardType.MEDIUM, -2.0);
        hazardRewardsHashMap.put(Hazard.HazardType.LARGE, -3.0);

        return new Problem(map, numIterationsHashMap, -0.1, 10, hazardRewardsHashMap);
    }

}
