package org.omscs.ml.a4burlap.experiments;

import org.omscs.ml.a4burlap.mdp.MDPBlockDude;
import org.omscs.ml.a4burlap.mdp.ProblemSize;
import org.omscs.ml.a4burlap.utils.CSVWriterGeneric;
import org.omscs.ml.a4burlap.utils.RunResultsCsvWriterCallback;
import org.omscs.ml.a4burlap.vipi.PISettings;
import org.omscs.ml.a4burlap.vipi.VISettings;

import java.util.Set;

import static org.omscs.ml.a4burlap.experiments.RunnerVIPI.NAME_BLOCKDUDE;
import static org.omscs.ml.a4burlap.experiments.RunnerVIPI.NAME_GRIDWORLD;
import static org.omscs.ml.a4burlap.utils.Utils.makeNewGammaCSVCallback;
import static org.omscs.ml.a4burlap.utils.Utils.printExerimentStartBlurb;
import static org.omscs.ml.a4burlap.utils.Utils.printRunUniqeidBlurb;

public class MySmBDGammaVIPI {

  public static void main(String[] args) {

    Set<String> expDirs = Set.of(NAME_BLOCKDUDE, NAME_GRIDWORLD);
    String shortname = "mybdsm";
    CSVWriterGeneric csvWriter =
        new CSVWriterGeneric("output", expDirs, "My Small Problem Experiments", shortname);

    // All MDPs
    MDPBlockDude mdpBlockDudeSM = new MDPBlockDude(ProblemSize.SMALL);
    //        MDPBlockDude mdpBlockDudeLRG = new MDPBlockDude(ProblemSize.LARGE);

    float[] gammas = {0.99f, 0.95f, 0.90f, 0.85f, 0.8f, 0.7f};

    String viCsvResultName = "vi_sm_bd_result";
    String piCsvResultName = "pi_sm_bd_result";
    String viNamePattern = "vi_sm_bd_%.2f";
    String piNamePattern = "pi_sm_bd_%.2f";
    String name = "";

    RunResultsCsvWriterCallback gammaCBVi = null;
    RunResultsCsvWriterCallback gammaCBPi = null;

    for (int i = 0; i < gammas.length; i++) {

      //       Value Iteration with small BlockDude
      printExerimentStartBlurb("VI - Small BlockDude gamma:" + gammas[i]);
      name = String.format(viNamePattern, gammas[i]);
      VISettings viSettings01 = new VISettings(gammas[i], 0.001f, 1000, name);
      gammaCBVi = makeNewGammaCSVCallback(gammas[i], NAME_BLOCKDUDE, viCsvResultName);
      csvWriter.appendToExperimentCatalog(viSettings01);
      BlockDudeVIExperiment viBlockDudeExperiment =
          new BlockDudeVIExperiment(mdpBlockDudeSM, viSettings01, csvWriter);
      viBlockDudeExperiment.setRunResultsCSVCallback(gammaCBVi);
      viBlockDudeExperiment.runAndSave(false);
      mdpBlockDudeSM.reset();

      // Policy Iterateion with Small Blockdude
      name = String.format(piNamePattern, gammas[i]);

      printExerimentStartBlurb("PI - Small BlockDude gamma:" + gammas[i]);
      PISettings piSettings01 = new PISettings(gammas[i], 0.001f, 0.001f, 1000, 100, name);
      gammaCBPi = makeNewGammaCSVCallback(gammas[i], NAME_BLOCKDUDE, piCsvResultName);
      csvWriter.appendToExperimentCatalog(piSettings01);
      BlockDudePIExperiment piBlockDudeExperiment =
          new BlockDudePIExperiment(mdpBlockDudeSM, piSettings01, csvWriter);
      piBlockDudeExperiment.setRunResultsCSVCallback(gammaCBPi);
      piBlockDudeExperiment.runAndSave(false);
      mdpBlockDudeSM.reset();
    }

    printRunUniqeidBlurb(shortname, csvWriter);
  }
}
