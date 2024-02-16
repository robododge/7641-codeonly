package a4.pivi;

import burlap.behavior.policy.GreedyQPolicy;
import burlap.behavior.singleagent.planning.stochastic.policyiteration.PolicyIteration;
import burlap.debugtools.DPrint;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.SADomain;
import burlap.statehashing.HashableStateFactory;

import java.util.ArrayList;
import java.util.List;

public class DeltaVariantPolicyIteration extends PolicyIteration {

    List<PIVIDeltaMetric> deltaMetrics;


    public DeltaVariantPolicyIteration(SADomain domain, double gamma, HashableStateFactory hashingFactory, double maxDelta, int maxEvaluationIterations, int maxPolicyIterations) {
        super(domain, gamma, hashingFactory, maxDelta, maxEvaluationIterations, maxPolicyIterations);

        deltaMetrics = new ArrayList<PIVIDeltaMetric>();
    }

    @Override
    public GreedyQPolicy planFromState(State initialState) {

        int iterations = 0;
        if(this.performReachabilityFrom(initialState) || !this.hasRunPlanning){

            double delta;
            long startTime, wallClockMilli;
            do{
                startTime = markStartTimeNano();
                delta = this.evaluatePolicy();
                wallClockMilli =nanoToMilli(diffTimesNano(startTime));
                deltaMetrics.add(new PIVIDeltaMetric(delta,wallClockMilli));
                System.out.printf("Policy Iteration %d -- delta: %f, wallClock: %d \n", iterations,delta,wallClockMilli);
                iterations++;
                this.evaluativePolicy = new GreedyQPolicy(this.getCopyOfValueFunction());

            }while(delta > this.maxPIDelta && iterations < maxPolicyIterations);



            this.hasRunPlanning = true;

        }

        DPrint.cl(this.debugCode, "Total policy iterations: " + iterations);
        this.totalPolicyIterations += iterations;

        return (GreedyQPolicy)this.evaluativePolicy;

    }

    public List<PIVIDeltaMetric> getDeltaMetrics() {
        return deltaMetrics;
    }

    public static long markStartTimeNano() {
        return System.nanoTime();
    }

    public static long diffTimesNano(long start) {
        return System.nanoTime() - start;
    }

    public static long nanoToMilli (long nanoTime) {
        return (long) nanoTime / 1000000;
    }

}
