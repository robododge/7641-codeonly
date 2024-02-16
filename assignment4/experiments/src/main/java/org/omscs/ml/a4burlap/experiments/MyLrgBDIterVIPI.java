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

public class MyLrgBDIterVIPI {

  public static void main(String[] args) {

    Set<String> expDirs = Set.of(NAME_BLOCKDUDE, NAME_GRIDWORLD);
    String shortname = "mybdlrg";
    CSVWriterGeneric csvWriter =
        new CSVWriterGeneric(
            "output", expDirs, "My Large Block Dude Problem Experiments", shortname);

    // All MDPs
    //        MDPBlockDude mdpBlockDudeSM = new MDPBlockDude(ProblemSize.SMALL);
    MDPBlockDude mdpBlockDudeLRG = new MDPBlockDude(ProblemSize.LARGE);

    String viCsvResultName = "vi_lrg_bd_result";
    String piCsvResultName = "pi_lrg_bd_result";
    String viNamePattern = "vi_lrg_bd_%.2f";
    String piNamePattern = "pi_lrg_bd_%.2f";
    String name = "";
    RunResultsCsvWriterCallback gammaCBVi = null;
    RunResultsCsvWriterCallback gammaCBPi = null;


    float[] gammas = {0.4f, 0.5f, 0.6f, 0.7f, 0.8f, 0.85f, 0.9f, 0.95f};
//             { 0.95f, 0.90f, 0.85f, 0.8f, 0.7f, 0.6f, 0.5f, 0.4f};
    for (int i = 0; i < gammas.length; i++) {

      printExerimentStartBlurb("VI - Large BlockDude");
      // Value Iteration with small BlockDude
      name = String.format(viNamePattern, gammas[i]);
      gammaCBVi = makeNewGammaCSVCallback(gammas[i], NAME_BLOCKDUDE, viCsvResultName);
      VISettings viSettings01 = new VISettings(gammas[i], 0.001f, 1000, name);
      csvWriter.appendToExperimentCatalog(viSettings01);
      BlockDudeVIExperiment viBlockDudeExperiment =
          new BlockDudeVIExperiment(mdpBlockDudeLRG, viSettings01, csvWriter);
      viBlockDudeExperiment.setRunResultsCSVCallback(gammaCBVi);
      viBlockDudeExperiment.runAndSave(false);
      mdpBlockDudeLRG.reset();

//               Policy Iterateion with Small Blockdude
      printExerimentStartBlurb("PI - Large BlockDude");
      name = String.format(piNamePattern, gammas[i]);
      gammaCBPi = makeNewGammaCSVCallback(gammas[i], NAME_BLOCKDUDE, piCsvResultName);
      PISettings piSettings01 = new PISettings(gammas[i], 0.001f, 0.001f, 50, 100, name);
      csvWriter.appendToExperimentCatalog(piSettings01);
      BlockDudePIExperiment piBlockDudeExperiment =
          new BlockDudePIExperiment(mdpBlockDudeLRG, piSettings01, csvWriter);
      piBlockDudeExperiment.setRunResultsCSVCallback(gammaCBPi);
      piBlockDudeExperiment.runAndSave(false);
      mdpBlockDudeLRG.reset();

    }

    printRunUniqeidBlurb(shortname, csvWriter);
  }
}
