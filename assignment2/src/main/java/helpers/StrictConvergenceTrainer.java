package helpers;

import shared.ConvergenceTrainer;
import shared.Trainer;

import java.util.Arrays;
import java.util.List;

public class StrictConvergenceTrainer extends ConvergenceTrainer {

    public static final int MIN_ITERATIONS = 50;

    protected int minIteratoins = MIN_ITERATIONS;
    private CSVWriterGeneric csvWriter;

    public StrictConvergenceTrainer(Trainer trainer, double threshold, int maxIterations) {
        super(trainer, threshold, maxIterations);
    }

    public StrictConvergenceTrainer(Trainer trainer) {
        super(trainer);
    }

    public void setMinIteratoins(int minIteratoins) {
        this.minIteratoins = minIteratoins;
    }

    public void setCsvWriter(CSVWriterGeneric csvWriter) {
        this.csvWriter = csvWriter;
    }

    @Override
    public double train() {
        double lastError;
        double error = Double.MAX_VALUE;
        double absError = Double.MAX_VALUE;
        boolean notStop = true;
        int lastConvertIter = 0;
        int convergeCount = 0;

        for (int i = 0; i < maxIterations; i++) {
            iterations = i;
            lastError = error;
            error = trainer.train();
            absError = Math.abs(error - lastError);

            if (iterations % 10 == 0) {
                System.out.printf("[%d]  se: %.4f\n", i, error);
            }
            if(csvWriter != null) {
                List<String> csvRow = Arrays.asList(new String[]{Integer.toString(i), Double.toString(error)});
                csvWriter.writeRow(csvRow);
            }
            if (i > minIteratoins && absError > 0.0d && absError < 0.02d && absError < threshold) {

                int iterDiff = i - lastConvertIter;
                convergeCount = (iterDiff < 3) ? ++convergeCount : 0;
                if (convergeCount > 4) {
                    System.out.printf("CONVERGED!! iteration %d, diff:%.15f\n", i, absError);
                    return error;
                }
                lastConvertIter = i;
            }

        }
//        do {
//            iterations++;
//            lastError = error;
//            error = trainer.train();
//            absError = Math.abs(error - lastError);
//            notStop = absError > threshold || iterations < minIteratoins;
//            currentcount =
//
//
//            if (iterations % 10 == 0) {
//                System.out.printf("[%d]  se: %.4f\n", iterations, error);
//            }
//        } while (  notStop && iterations < maxIterations);
        return error;
    }


}

