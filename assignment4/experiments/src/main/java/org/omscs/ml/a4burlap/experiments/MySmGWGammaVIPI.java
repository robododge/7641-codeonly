package org.omscs.ml.a4burlap.experiments;

import org.omscs.ml.a4burlap.mdp.MDPBlockDude;
import org.omscs.ml.a4burlap.mdp.MDPGridWorld;
import org.omscs.ml.a4burlap.mdp.ProblemSize;
import org.omscs.ml.a4burlap.mdp.grid.A4MainGridSelector;
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

public class MySmGWGammaVIPI {

  public static void main(String[] args) {

    Set<String> expDirs = Set.of(NAME_BLOCKDUDE, NAME_GRIDWORLD);
    String shortname = "mygwsm";
    CSVWriterGeneric csvWriter =
        new CSVWriterGeneric("output", expDirs, "My Small Problem Experiments", shortname);

    double stochastic = 0.8;
    // All MDPs
    MDPGridWorld mdpGridWorldSM = new MDPGridWorld(stochastic, ProblemSize.SMALL, new A4MainGridSelector());

    float[] gammas = {0.99f, 0.95f, 0.90f, 0.85f, 0.8f, 0.7f};

    String viCsvResultName = "vi_sm_gw_gamma_result";
    String piCsvResultName = "pi_sm_gw_gamma_result";
    String viNamePattern = "vi_sm_gw_%.2f";
    String piNamePattern = "pi_sm_gw_%.2f";
    String name = "";

    RunResultsCsvWriterCallback gammaCBVi = null;
    RunResultsCsvWriterCallback gammaCBPi = null;

    for (int i = 0; i < gammas.length; i++) {


      printExerimentStartBlurb("VI GridWorld Small");
      name = String.format(viNamePattern, gammas[i]);
      VISettings viSettingsGW02 = new VISettings(gammas[i], 0.000001f,
              1000,name);
      gammaCBVi = makeNewGammaCSVCallback(gammas[i], NAME_GRIDWORLD, viCsvResultName);
      csvWriter.appendToExperimentCatalog(viSettingsGW02);
      GridWorldVIExperiment gwSmVi02 = new GridWorldVIExperiment(mdpGridWorldSM,viSettingsGW02, csvWriter);
      gwSmVi02.setRunResultsCSVCallback(gammaCBVi);
      gwSmVi02.runAndSaveMulti(5);
      mdpGridWorldSM.reset();

      printExerimentStartBlurb("PI GridWorld Small");
      name = String.format(piNamePattern, gammas[i]);
      PISettings piSettingsGW02 = new PISettings(gammas[i], 0.0001f,
              0.01f, 10,
              100, name);
      gammaCBPi = makeNewGammaCSVCallback(gammas[i], NAME_GRIDWORLD, piCsvResultName);
      csvWriter.appendToExperimentCatalog(piSettingsGW02);
      GridWorldPIExperiment gwSmPi02 = new GridWorldPIExperiment(mdpGridWorldSM,piSettingsGW02, csvWriter);
      gwSmPi02.setRunResultsCSVCallback(gammaCBPi);
      gwSmPi02.runAndSaveMulti(5);
      mdpGridWorldSM.reset();


    }

    printRunUniqeidBlurb(shortname, csvWriter);
  }
}
