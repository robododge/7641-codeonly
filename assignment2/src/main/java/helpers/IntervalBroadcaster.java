package helpers;

import opt.OptimizationAlgorithm;

public interface IntervalBroadcaster {

    void broadcast(int interval, int cumCount, double trainingTime);

    void stopBroadcast();
}
