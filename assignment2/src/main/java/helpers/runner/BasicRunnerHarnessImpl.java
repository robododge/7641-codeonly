package helpers.runner;

import helpers.CSVWriterGeneric;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Set;

public class BasicRunnerHarnessImpl implements RunnerHarness{
    CSVWriterGeneric csvWriter;
    ProblemHolderWriterInject ph ;
    String rootPath;

    @Override
    public void initialize(ProblemHolder ph, String rootPath) {
        this.ph = (ProblemHolderWriterInject)ph;
        this.rootPath = rootPath;
    }

    @Override
    public void runProblemWithAllOptimizers(int epochCount, Set<String> optimizers) {
        Path pathPlus = Path.of(this.rootPath, ph.getProblemName());
        csvWriter = new CSVWriterGeneric(pathPlus.toString(), optimizers, ph.getProblemMetaData("empty"));

        for (String optimizer : optimizers) {

            csvWriter.writeHeader(Arrays.asList(new String[]{ "trainingtime", "feval", "fitness"}), optimizer);

            for (int i = 0; i < epochCount; i++) {

                ph.resetEvalFunc();
                try {
                    Method optMethod = ph.getClass().getMethod("run" + optimizer, CSVWriterGeneric.class);
                    optMethod.invoke(ph, csvWriter);
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
