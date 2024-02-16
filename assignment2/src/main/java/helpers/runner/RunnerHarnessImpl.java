package helpers.runner;

import helpers.Utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public class RunnerHarnessImpl implements RunnerHarness{

    private String rootPath = null;
    private ProblemHolderStringInject ph = null;


    @Override
    public void initialize(ProblemHolder ph, String rootPath) {

        this.rootPath = rootPath;
        this.ph = (ProblemHolderStringInject) ph;
        File dir = new File(rootPath);
        boolean created = dir.mkdirs();
        if (created) System.out.printf("New dir [%s] needed to be created for %s problem.\n",rootPath,ph.getProblemName());

    }

    @Override
    public void runProblemWithAllOptimizers(int epochCount, Set<String> optimizers) {

        if (epochCount > 99 ) {
            System.out.println("Cannot support no more than 99 epochs");
            System.exit(1);
        }
        String uniqueD = Utils.uniqueDirName();
        File problemDir = new File(this.rootPath,ph.getProblemName()+File.separator+uniqueD );
        boolean created = problemDir.mkdirs();
        assert !created : String.format("The unique output directory for problem set already exists %s, exiting\n",problemDir);


        File metaFile = new File(this.rootPath, ph.getProblemName()+File.separator+uniqueD+".txt");
        writeMeta(metaFile, uniqueD);

        File rhcFile = null, saFile = null, gaFile = null, mimicFile = null;
        File optFile =null;


        for (String optimizer: optimizers) {
            for (int i = 0; i < epochCount; i++) {
                ph.resetEvalFunc();
                try {
                    Method optMethod = ph.getClass().getMethod("run"+optimizer, String.class);
                    optFile = new File( createOptimizerDir(problemDir, optimizer), String.format("%02d.csv", i));
                    optMethod.invoke(ph, optFile.toString());
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }

        }
//        for (int i = 0; i < epochCount; i++) {
//            rhcFile = new File( createOptimizerDir(problemDir, "RHC"), String.format("%02d.csv", i));
//            ph.runRHC(rhcFile.toString());
//        }
//        ph.resetEvalFunc();
//        for (int i = 0; i < epochCount; i++) {
//            saFile = new File( createOptimizerDir(problemDir, "SA"), String.format("%02d.csv", i));
//            ph.runSA(saFile.toString());
//        }
//        ph.resetEvalFunc();
//        for (int i = 0; i < epochCount; i++) {
//            gaFile = new File( createOptimizerDir(problemDir, "GA"), String.format("%02d.csv", i));
//            ph.runGA(gaFile.toString());
//        }
//        ph.resetEvalFunc();
//        for (int i = 0; i < epochCount; i++) {
//            mimicFile = new File( createOptimizerDir(problemDir, "MIMIC"), String.format("%02d.csv", i));
//            ph.runMIMIC(mimicFile.toString());
//        }

    }

    File createOptimizerDir(File problemRootDir, String optName) {
        File optimizerDir = new File(problemRootDir, optName);
        optimizerDir.mkdirs();
        return optimizerDir;
    }



    private void writeMeta(File outfile, String  uniqueD) {
        try {
            FileWriter myWriter = new FileWriter(outfile);
            myWriter.write(this.ph.getProblemMetaData(uniqueD));
            myWriter.close();
            System.out.printf("Successfully wrote metat file the file %s\n", outfile);
        } catch (IOException e) {
            System.out.printf("An error occurred writing meta file %s\n",outfile);
            e.printStackTrace();
        }
    }

}
