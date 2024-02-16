package helpers;

import shared.FixedIterationTrainer;
import shared.Trainer;

public class BroadcastIntervalTrainer implements Trainer {
    /**
     * Make a new fixed iterations trainer
     *
     * @param t    the trainer
     * @param iter the number of iterations
     */
    private int interval;
    private int iterations;
    private int intevalCount = 0;
    private Trainer trainer;
    private IntervalBroadcaster broadcaster;


    public BroadcastIntervalTrainer(Trainer t, int iter, int interval, IntervalBroadcaster ib) {

        assert iter > interval;
        this.interval = interval;
        this.iterations = iter;
        this.trainer = t;
        this.broadcaster = ib;
    }

    @Override
    public double train() {
        long start = 0;
        double trainingTime = 0d;
        double sum = 0;
        for (int i = 0; i < this.iterations; i++) {
            start = System.nanoTime();
            sum += this.trainer.train();
            long end = System.nanoTime();
            trainingTime = end - start;
            double elapsedSeconds  = trainingTime / 1_000_000_000;
            if(i>0 && i % interval == 0){
                broadcaster.broadcast(this.intevalCount,i, elapsedSeconds);
                this.intevalCount++;
            }
        }
        return sum / iterations;
    }
}
