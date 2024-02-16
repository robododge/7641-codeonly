package testruns;

import de.siegmar.fastcsv.reader.NamedCsvReader;
import dist.Distribution;
import func.nn.activation.RELU;
import func.nn.backprop.BackPropagationNetwork;
import func.nn.backprop.BackPropagationNetworkFactory;
import func.nn.backprop.BatchBackPropagationTrainer;
import func.nn.backprop.RPROPUpdateRule;
import helpers.CSVWriterGeneric;
import helpers.StrictConvergenceTrainer;
import opt.OptimizationAlgorithm;
import opt.RandomizedHillClimbing;
import opt.SimulatedAnnealing;
import opt.example.NeuralNetworkOptimizationProblem;
import opt.ga.StandardGeneticAlgorithm;
import shared.DataSet;
import shared.ErrorMeasure;
import shared.Instance;
import shared.SumOfSquaresError;
import shared.Trainer;
import shared.tester.RecallTestMetric;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class NNOptimizer {
    private static Instance[] instances = initializeInstances("a2_malicious_train.csv");
    //    private static Instance[] instancesValidate = initializeInstances("a2_malicious_validate.csv");
    private static Instance[] instancesTest = initializeInstances("a2_malicious_test.csv");


    private static int inputLayer = 15, hiddenLayer = 7, outputLayer = 1, trainingIterations = 1000;
    private static BackPropagationNetworkFactory factory = new BackPropagationNetworkFactory();

    private static ErrorMeasure measure = new SumOfSquaresError();

    private static DataSet set = new DataSet(instances);

    private static BackPropagationNetwork networks[] = new BackPropagationNetwork[3];
    private static NeuralNetworkOptimizationProblem[] nnop = new NeuralNetworkOptimizationProblem[3];


    private static String[] oaNames = {"RHC", "SA", "GA"};
    private static OptimizationAlgorithm[] oa = new OptimizationAlgorithm[oaNames.length];
    private static String results = "";

    private static DecimalFormat df = new DecimalFormat("0.000");

    public static  void trainBackProp(ResultsHolder rHolder, CSVWriterGeneric csvWriter) {
        System.out.println("Data instacnce size: " + instances.length);
        BackPropagationNetwork network = factory.createClassificationNetwork(
                new int[]{inputLayer, hiddenLayer, outputLayer}, new RELU());

        csvWriter.writeHeader(Arrays.asList(new String[]{"interval","loss"}), "BP");
        StrictConvergenceTrainer trainer = new StrictConvergenceTrainer(
                backPropTraining(network), 0.0001, 1000);
        trainer.setMinIteratoins(100);
        trainer.setCsvWriter(csvWriter);

        //Time monitor - TRAIN!!
        double start = System.nanoTime(), end, trainingTime;
        trainer.train();
        end = System.nanoTime();
        trainingTime = end - start;
        trainingTime /= Math.pow(10, 9);
        rHolder.trainTimes[0] = trainingTime;

        System.out.println("Convergence in "
                + trainer.getIterations() + " iterations");

        double predicted = 0.0d, actual = 0.0d;
        int correct = 0, incorrect = 0, totalYLablel = 0;
        RecallTestMetric recalltm = new RecallTestMetric();

        Instance[] generalized = instances;
        for (int i = 0; i < generalized.length; i++) {
            network.setInputValues(generalized[i].getData());
            network.run();
            actual = generalized[i].getLabel().getDiscrete();
            predicted = Double.parseDouble(network.getOutputValues().toString());
            long roundUp = Math.round(predicted);
//            if (predicted > 0.5) {
//                boolean tpHmm = roundUp == (long) actual;
//                System.out.printf("Predicted 1!! %f rounded %d actual: %d tp: %b\n", predicted, roundUp, (int) actual, tpHmm);
//            }
            Instance recallInstance = new Instance(generalized[i].getData());
            recallInstance.setLabel(new Instance(roundUp));
            recalltm.addResult(recallInstance, generalized[i]);
            if (actual == 1) totalYLablel++;
            double trash = Math.abs(predicted - actual) < 0.5 ? correct++ : incorrect++;
        }

        double recall2 = recalltm.getPctRecall();
        recalltm.printResults();

        double recall = recallScore(network);
        String localResults = "\nResults for BP: \nCorrectly classified " + correct + " instances." +
                "\nIncorrectly classified " + incorrect + " instances.\nPercent correctly classified: "
                + df.format((correct + 0.0d) / (correct + incorrect) * 100) + "%\n" +
                "Recall " + recall + " RecallAbagail: " + recall2 + "\n" +
                "Training time: " + df.format(trainingTime) + " seconds\n" +
                "Total test instances:" + instancesTest.length + " total Y label: " + totalYLablel + "\n";
        System.out.println(localResults);
        rHolder.recallResults[0] = recall2;

    }


    public static void main(String[] args) {

        int[] seeds = new int[]{555, 2983, 8277361,999222, 10992};
        double[] recalls = new double[4];
        double[] trainTime = new double[4];
        Set<String> nnCsvPaths = new HashSet<String>(Arrays.asList(oaNames));
        nnCsvPaths.add("BP");
        CSVWriterGeneric csvWriter = new CSVWriterGeneric("output/NN",nnCsvPaths );

        ResultsHolder[] rHolders = new ResultsHolder[seeds.length];
        Arrays.fill(recalls,0.0d);
        Arrays.fill(trainTime, 0.0d);
        for (int i = 0; i < seeds.length; i++) {
            rHolders[i] = runAllWithSeed(seeds[i], csvWriter);
            for (int j = 0; j < recalls.length; j++) {
                recalls[j] += rHolders[i].recallResults[j] ;//* (1/seeds.length);
                trainTime[j] += rHolders[i].trainTimes[j] ;//* (1/seeds.length);
            }
            csvWriter.incremenetIteration();
        }

        System.out.printf("Recall AVG: BP: %.5f,  RHC: %.5f, SA: %.5f,  GA: %.5f,   \n",recalls[0]/5,recalls[1]/5,recalls[2]/5,recalls[3]/5);
        System.out.printf("Train Time AVG: BP: %.5f,  RHC: %.5f, SA: %.5f,  GA: %.5f,   \n",trainTime[0]/5,trainTime[1]/5,trainTime[2]/5,trainTime[3]/5);
    }

    private static ResultsHolder runAllWithSeed(int seed, CSVWriterGeneric csvWriter) {

        ResultsHolder rHolder = new ResultsHolder();
        rHolder.recallResults = new double[oa.length +1];
        rHolder.trainTimes = new double[oa.length +1];
        Arrays.fill(rHolder.recallResults, 0.0d); Arrays.fill(rHolder.trainTimes, 0.0d);;

        trainBackProp(rHolder, csvWriter);

        if (false) return rHolder;

        for (int i = 0; i < oa.length; i++) {
            networks[i] = factory.createClassificationNetwork(
                    new int[]{inputLayer, hiddenLayer, outputLayer}, new RELU());
            nnop[i] = new NeuralNetworkOptimizationProblem(set, networks[i], measure);
            Distribution ddist = nnop[i].getDist();
            ddist.random.setSeed(seed);
        }


        oa[0] = new RandomizedHillClimbing(nnop[0]);
        oa[1] = new SimulatedAnnealing(1E9, .40, nnop[1]);
        oa[2] = new StandardGeneticAlgorithm(200, 100, 10, nnop[2]);


        for (int i = 0; i < oa.length; i++) {
            double start = System.nanoTime(), end, trainingTime, testingTime, correct = 0, incorrect = 0;
            train(oa[i], networks[i], oaNames[i], csvWriter); //trainer.train();
            end = System.nanoTime();
            trainingTime = end - start;
            trainingTime /= Math.pow(10, 9);

            Instance optimalInstance = oa[i].getOptimal();
            networks[i].setWeights(optimalInstance.getData());

            double predicted, actual;
            long roundUp = 0L;
            RecallTestMetric recalltm = new RecallTestMetric();
            Instance recallAddInstace = null;
            Instance[] generalized = instances;
            start = System.nanoTime();
            for (int j = 0; j < generalized.length; j++) {
                networks[i].setInputValues(generalized[j].getData());
                networks[i].run();

                actual = Double.parseDouble(generalized[j].getLabel().toString());
                predicted = Double.parseDouble(networks[i].getOutputValues().toString());

                double trash = Math.abs(predicted - actual) < 0.5 ? correct++ : incorrect++;

                roundUp = Math.round(predicted);
                recallAddInstace= new Instance(generalized[i].getData());
                recallAddInstace.setLabel(new Instance(roundUp));
                recalltm.addResult(recallAddInstace,generalized[i]);
                if (predicted > 0.5) {

                    boolean tpHmm = roundUp == (long) actual;
                    System.out.printf("Predicted 1!! %f rounded %d actual: %d tp: %b\n", predicted, roundUp, (int) actual, tpHmm);

                }

            }
            end = System.nanoTime();
            testingTime = end - start;
            testingTime /= Math.pow(10, 9);

            double recall2 = recalltm.getPctRecall();
            rHolder.recallResults[i+1] = recall2;
            rHolder.trainTimes[i+1] = trainingTime;
            recalltm.printResults();

            results += "\nResults for " + oaNames[i] + ": \nCorrectly classified " + correct + " instances." +
                    "\nIncorrectly classified " + incorrect + " instances.\nPercent correctly classified: "
                    + df.format(correct / (correct + incorrect) * 100) + "%\nTraining time: " + df.format(trainingTime)
                    + " seconds\nTesting time: " + df.format(testingTime) + " seconds\n" +
                    "Recall " + recall2 + "\n" ;
        }

        System.out.println(results);
        return  rHolder;
    }

    private static void train(Trainer oa, BackPropagationNetwork network, String oaName, CSVWriterGeneric csvWriter) {
        System.out.println("\nError results for " + oaName + "\n---------------------------");
        double mse = 0.0d, pastMse = 500.0d;
        double errorOld = 1000d;
        int convergeCount = 0, lastConvertIter = 0;

        csvWriter.writeHeader(Arrays.asList(new String[]{"interval","loss"}), oaName);
        for (int i = 0; i < trainingIterations; i++) {
            oa.train();

            double error = 0;
            for (int j = 0; j < instances.length; j++) {
                network.setInputValues(instances[j].getData());
                network.run();

                Instance output = instances[j].getLabel(), example = new Instance(network.getOutputValues());
                example.setLabel(new Instance(Double.parseDouble(network.getOutputValues().toString())));
                error += measure.value(output, example);

            }
            mse = error / instances[0].size();
            if(csvWriter != null) {
                List<String> csvRow = Arrays.asList(new String[]{Integer.toString(i), Double.toString(mse)});
                csvWriter.writeRow(csvRow);
            }


            if (i % 10 == 0) {
                System.out.printf("[%d] mse: %.4f  se: %.4f\n", i, mse, error);
            }
            double absval = Math.abs(pastMse - mse);
//            double absval = Math.abs(errorOld - error);

//            if (absval == 0.0d) {
//                System.out.println("FOUND ZEROR CONDITION! pastMse: "+pastMse + " mse: "+mse);
//            }
            if (i > 50 && absval > 0.0d && mse < 50.0d && absval < 0.0001) {

                int iterDiff = i - lastConvertIter;
                convergeCount = (iterDiff < 3) ? ++convergeCount : 0;
                if (convergeCount > 4) {
                    System.out.printf("CONVERGED!! iteration %d, diff:%.15f\n", i, absval);
                    return;
                }
                lastConvertIter = i;
            }
            pastMse = mse;
            errorOld = error;


        }
    }

    public static Trainer backPropTraining(BackPropagationNetwork network) {

        Trainer bpTrainer = new BatchBackPropagationTrainer(set, network,
                new SumOfSquaresError(), new RPROPUpdateRule());
        return bpTrainer;

    }

    public static double nanSafe(String dblString) {
        double out = 0.0d;
        try {
            out = ("".equals(dblString)) ? 0.0d : Double.parseDouble(dblString);
        } catch (NumberFormatException nfe) {
        }
        return out;
    }

    public static double recallScore(BackPropagationNetwork network) {
        long actual = 0, predicted = 0;
        double pre = 0.0d;
        int tp = 0, fn = 0;
        for (int i = 0; i < instancesTest.length; i++) {
            actual = instancesTest[i].getLabel().getDiscrete();
            pre = Double.parseDouble(network.getOutputValues().toString());
            predicted = Math.round(pre);
            //Math.abs(predicted - actual) < 0.5 ? correct++ : incorrect++;
            if (actual == predicted && actual == 1) tp++;
            if (predicted == 0 && actual == 1) fn++;
        }

        return (double) (tp + 0.0d) / (tp + fn);
    }

    private static Instance[] initializeInstances(String csvFile) {
        List<Instance> instances = null;
        Instance[] instancesOut = null;

        Path csvPath = Path.of("src/main/resources/" + csvFile);
        try {
            instances = NamedCsvReader.builder()
                    .build(csvPath, StandardCharsets.UTF_8).stream()
                    .map(row -> {
                        String colval = "";
                        double[] attrs = new double[16];
                        for (int i = 0; i < 15; i++) {
                            colval = row.getField(String.format("%d", i));
                            attrs[i] = (i == 11) ? nanSafe(colval) : Double.parseDouble(colval);
                        }

                        Instance inst = new Instance(attrs);
                        inst.setLabel(new Instance(Double.parseDouble(row.getField("y"))));
                        return inst;
                    }).collect(Collectors.toList());
        } catch (IOException e) {
            System.out.println("Error reading file  - EXITING!");
            e.printStackTrace();
        }

        instancesOut = instances.toArray(new Instance[0]);
        return instancesOut;
    }

    static class  ResultsHolder{
        double[] recallResults;
        double[] trainTimes;
    }


}
