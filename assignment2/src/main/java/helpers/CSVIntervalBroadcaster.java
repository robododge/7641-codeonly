package helpers;

import de.siegmar.fastcsv.writer.CsvWriter;
import opt.EvaluationFunction;
import opt.OptimizationAlgorithm;
import opt.example.FlipFlopEvaluationFunction;
import opt.example.FourPeaksEvaluationFunction;
import shared.Instance;
import testruns.FourPeaksProblem;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;

public class CSVIntervalBroadcaster implements IntervalBroadcaster {

    private OptimizationAlgorithm oAlg;
    private EvaluationFunction ef;
    private String csvFilePath;
    private CsvWriter csvWriter;
    private Path csvPath;

    public CSVIntervalBroadcaster(OptimizationAlgorithm oAlg, EvaluationFunction ef, String csvFilePath) {
        this.oAlg = oAlg;
        this.ef = ef;
        this.csvFilePath = csvFilePath;

        this.csvPath = Path.of(this.csvFilePath);

        writeRow(Arrays.asList("interval","iteration", "trainingtime", "feval","fitness"));

    }

    @Override
    public void broadcast(int interval, int cumulative, double trainingtime ) {

        Instance optimalInstance = this.oAlg.getOptimal();
        double fitVal = this.ef.value(optimalInstance);
//        decrementIfPossible();
        writeRow(Arrays.asList(interval+"",cumulative+"", trainingtime+"", getEvalCount()+"", fitVal+""));

//        System.out.printf("Interval %d out of %d total: fitness: %5.4f\n", interval, cumulative, fitVal);
    }

    @Override
    public void stopBroadcast() {
        try {
            this.csvWriter.close();
        } catch (IOException e) {
            //nothing
        }
    }

    private void writeRow(final Iterable<String> values) {
        try (CsvWriter csv = CsvWriter.builder().build(csvPath, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            this.csvWriter = csv;
            this.csvWriter.writeRow(values);
        } catch (IOException e) {
            System.out.println("Cannot write to csv file at path:"+csvPath);
            e.printStackTrace();
        }

    }

    private void decrementIfPossible(){
        if (ef instanceof FlipFlopEvaluationFunction) {
            ((FlipFlopEvaluationFunction)ef).decrementEvalCount();
        }
        if (ef instanceof FourPeaksEvaluationFunction) {
            ((FourPeaksEvaluationFunction)ef).decrementEvalCount();
        }
    }

    private int getEvalCount(){
        if (ef instanceof FlipFlopEvaluationFunction) {
            return ((FlipFlopEvaluationFunction)ef).getFunctionEvaluations();
        }else if (ef instanceof  FourPeaksEvaluationFunction) {
            return ((FourPeaksEvaluationFunction)ef).getFunctionEvaluations();
        }
        return 0;
    }
}
