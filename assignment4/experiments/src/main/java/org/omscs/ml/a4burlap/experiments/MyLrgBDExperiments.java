package org.omscs.ml.a4burlap.experiments;

import org.omscs.ml.a4burlap.mdp.MDPBlockDude;
import org.omscs.ml.a4burlap.mdp.ProblemSize;
import org.omscs.ml.a4burlap.qlearn.QSettings;
import org.omscs.ml.a4burlap.utils.CSVWriterGeneric;
import org.omscs.ml.a4burlap.vipi.PISettings;
import org.omscs.ml.a4burlap.vipi.VISettings;

import java.util.Set;

import static org.omscs.ml.a4burlap.experiments.RunnerVIPI.NAME_BLOCKDUDE;
import static org.omscs.ml.a4burlap.experiments.RunnerVIPI.NAME_GRIDWORLD;
import static org.omscs.ml.a4burlap.utils.Utils.printExerimentStartBlurb;

public class MyLrgBDExperiments {

  public static void main(String[] args) {

    Set<String> expDirs = Set.of(NAME_BLOCKDUDE, NAME_GRIDWORLD);
    CSVWriterGeneric csvWriter =
        new CSVWriterGeneric(
            "output", expDirs, "My Large Block Dude Problem Experiments", "mybdlrg");

    // All MDPs
    //        MDPBlockDude mdpBlockDudeSM = new MDPBlockDude(ProblemSize.SMALL);
    MDPBlockDude mdpBlockDudeLRG = new MDPBlockDude(ProblemSize.LARGE);

    printExerimentStartBlurb("VI - Large BlockDude");
    // Value Iteration with small BlockDude
    VISettings viSettings01 = new VISettings(0.40f, 0.001f, 1000, "vi_lrg_bd_01");
    csvWriter.appendToExperimentCatalog(viSettings01);
    BlockDudeVIExperiment viBlockDudeExperiment =
        new BlockDudeVIExperiment(mdpBlockDudeLRG, viSettings01, csvWriter);
    viBlockDudeExperiment.runAndSave(false);
    mdpBlockDudeLRG.reset();

    //         Policy Iterateion with Small Blockdude
//    printExerimentStartBlurb("PI - Large BlockDude");
//    PISettings piSettings01 = new PISettings(0.90f, 0.001f, 0.001f, 50, 100, "pi_lrg_bd_01");
//    csvWriter.appendToExperimentCatalog(piSettings01);
//    BlockDudePIExperiment piBlockDudeExperiment =
//        new BlockDudePIExperiment(mdpBlockDudeLRG, piSettings01, csvWriter);
//    piBlockDudeExperiment.runAndSave(false);
//    mdpBlockDudeLRG.reset();
//
//    // Small BlockDude with Q-Learner
//    printExerimentStartBlurb("Q-Learner - Large BlockDude");
//    QSettings qSettingsBD01 = new QSettings("q_lrg_bd_01", 0.99, 0.3, 0.2, 20000, 0.95, 0.5);
//    csvWriter.appendToExperimentCatalog(qSettingsBD01);
//    BlockDudeQLearnerExperiment bdqlExp01 =
//        new BlockDudeQLearnerExperiment(mdpBlockDudeLRG, qSettingsBD01, csvWriter);
//    bdqlExp01.runWithEpisodesAndSave(1, 1300);
//    mdpBlockDudeLRG.reset();
  }
}
