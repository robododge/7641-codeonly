package org.omscs.ml.a4burlap.experiments;

import org.omscs.ml.a4burlap.mdp.MDPBlockDude;
import org.omscs.ml.a4burlap.mdp.MDPGridWorld;
import org.omscs.ml.a4burlap.mdp.ProblemSize;
import org.omscs.ml.a4burlap.mdp.grid.A4MainGridSelector;
import org.omscs.ml.a4burlap.qlearn.QSettings;
import org.omscs.ml.a4burlap.utils.CSVWriterGeneric;
import org.omscs.ml.a4burlap.vipi.PISettings;
import org.omscs.ml.a4burlap.vipi.VISettings;

import java.util.Set;

import static org.omscs.ml.a4burlap.experiments.RunnerVIPI.NAME_BLOCKDUDE;
import static org.omscs.ml.a4burlap.experiments.RunnerVIPI.NAME_GRIDWORLD;
import static org.omscs.ml.a4burlap.utils.Utils.printExerimentStartBlurb;

public class MySmGWExperiments {

  public static void main(String[] args) {

    Set<String> expDirs = Set.of(NAME_BLOCKDUDE, NAME_GRIDWORLD);
    System.out.println("** Initializing CSV ouput for this experiment");
    double stochastic = 1.0;
    String metaDesc = String.format("Small Gridworlds, stochastic: %.2f\n", stochastic);
    CSVWriterGeneric csvWriter = new CSVWriterGeneric("output", expDirs, metaDesc, "mygwsm");
    MDPBlockDude mdpBlockDudeSM = new MDPBlockDude(ProblemSize.SMALL);
    MDPBlockDude mdpBlockDudeLRG = new MDPBlockDude(ProblemSize.LARGE);

    MDPGridWorld mdpGridWorldSM = new MDPGridWorld(stochastic, ProblemSize.SMALL, new A4MainGridSelector());
    MDPGridWorld mdpGridWorldLRG = new MDPGridWorld(ProblemSize.LARGE);

    //        VISettings viSettings01 = new VISettings(0.99f, 0.001f, 1000, "vi_sm_high_gamma" );
    //        csvWriter.appendToExperimentCatalog(viSettings01);
    //
    //        BlockDudeVIExperiment viBlockDudeExperiment = new
    // BlockDudeVIExperiment(mdpBlockDudeSM, viSettings01, csvWriter);
    //        viBlockDudeExperiment.runAndSave(false);
    //        mdpBlockDudeSM.reset();
    //
    //        PISettings piSettings01 = new PISettings(0.99f, 0.001f,
    //                0.001f, 1000,
    //                100, "pi_sm_high_gamma");
    //        csvWriter.appendToExperimentCatalog(piSettings01);
    //        BlockDudePIExperiment piBlockDudeExperiment = new
    // BlockDudePIExperiment(mdpBlockDudeSM,piSettings01, csvWriter);
    //        piBlockDudeExperiment.runAndSave(false);
    //        mdpBlockDudeSM.reset();

    //        PISettings piSettingsL01 = new PISettings(0.1f, .5f,
    //                0.1f, 10,
    //                20, "pi_lrg_high_gamma");
    //        csvWriter.appendToExperimentCatalog(piSettingsL01);
    //        piBlockDudeExperiment = new BlockDudePIExperiment(mdpBlockDudeLRG,piSettingsL01,
    // csvWriter);
    //        piBlockDudeExperiment.runAndSave(false);
    //        mdpBlockDudeLRG.reset();

    printExerimentStartBlurb("VI GridWorld Small");
    VISettings viSettingsGW02 = new VISettings(0.90f, 0.000001f, 1000, "vi_gw_small_02");
    csvWriter.appendToExperimentCatalog(viSettingsGW02);
    GridWorldVIExperiment gwSmVi02 =
        new GridWorldVIExperiment(mdpGridWorldSM, viSettingsGW02, csvWriter);
    gwSmVi02.runAndSaveMulti(5);
    mdpGridWorldSM.reset();

    printExerimentStartBlurb("PI GridWorld Small");
    PISettings piSettingsGW02 = new PISettings(0.30f, 0.001f, 0.01f, 10, 100, "pi_gw_small_02");
    csvWriter.appendToExperimentCatalog(piSettingsGW02);
    GridWorldPIExperiment gwSmPi02 =
        new GridWorldPIExperiment(mdpGridWorldSM, piSettingsGW02, csvWriter);
    gwSmPi02.runAndSaveMultiWithVisual(5, 0);
    mdpGridWorldSM.reset();
    //
    //        PISettings piSettingsGW03 = new PISettings(0.99f, 0.001f,
    //                0.001f, 1000,
    //                100, "pi_lrg_gw_high_gamma");
    //        csvWriter.appendToExperimentCatalog(piSettingsGW03);
    //        gridWorldPIExperiment = new GridWorldPIExperiment(mdpGridWorldLRG,piSettingsGW03,
    // csvWriter);
    //        gridWorldPIExperiment.runAndSave(false);
    //        mdpGridWorldLRG.reset();

    //// GOOD settings, get small BlockDude Q learner
    //        QSettings qSettingsBD01 = new QSettings("q_sm_bd_01", 0.99, 0.3, 0.2, 2000, 0.98, 0.99
    // );
    //        csvWriter.appendToExperimentCatalog(qSettingsBD01);
    //        BlockDudeQLearnerExperiment bdqlExp01 = new
    // BlockDudeQLearnerExperiment(mdpBlockDudeSM,qSettingsBD01, csvWriter);
    //        bdqlExp01.runWithEpisodesAndSave(3,300);
    //        mdpBlockDudeSM.reset();

    // NOT going to work
    //        QSettings qSettingsBD02 = new QSettings("q_lg_bd_01", 0.99, 0.3, 0.2, 2500000, 0.98,
    // 0.99 );
    //        csvWriter.appendToExperimentCatalog(qSettingsBD02);
    //        BlockDudeQLearnerExperiment bdqlExp02 = new
    // BlockDudeQLearnerExperiment(mdpBlockDudeLRG,qSettingsBD02, csvWriter);
    //        bdqlExp02.runWithEpisodesAndSave(3,1500);
    //        mdpBlockDudeSM.reset();

    /// GOOOD!
    printExerimentStartBlurb("QL GridWorld Small");
    QSettings qSettingsGW01 = new QSettings("q_gw_sm_01", 0.90, 0.3, 0.2, -1, 0.80, 0.98);
    csvWriter.appendToExperimentCatalog(qSettingsGW01);
    GridWorldQLearnerExperiment gwQExp01 =
        new GridWorldQLearnerExperiment(mdpGridWorldSM, qSettingsGW01, csvWriter);
    gwQExp01.tooggleVisual(false, 0);
    gwQExp01.runWithEpisodesAndSave(5, 400);
    mdpGridWorldSM.reset();

    // GOOD settings, get about -88 rewards
    //        QSettings qSettingsGW02 = new QSettings("q_lg_gw_01", 0.99, 0.3, 0.2, 500, 0.98, 0.99
    // );
    //        csvWriter.appendToExperimentCatalog(qSettingsGW02);
    //        GridWorldQLearnerExperiment gwQExp02 = new
    // GridWorldQLearnerExperiment(mdpGridWorldLRG,qSettingsGW02, csvWriter);
    //        gwQExp02.runWithEpisodesAndSave(3,100000);
    //        mdpGridWorldLRG.reset();

  }
}
