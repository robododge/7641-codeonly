package org.omscs.ml.a4burlap.experiments;

import org.omscs.ml.a4burlap.mdp.MDPBlockDude;
import org.omscs.ml.a4burlap.mdp.ProblemSize;
import org.omscs.ml.a4burlap.qlearn.QSettings;
import org.omscs.ml.a4burlap.utils.CSVWriterGeneric;
import org.omscs.ml.a4burlap.utils.RunResultsCsvWriterCallback;

import java.util.Set;

import static org.omscs.ml.a4burlap.experiments.RunnerVIPI.NAME_BLOCKDUDE;
import static org.omscs.ml.a4burlap.experiments.RunnerVIPI.NAME_GRIDWORLD;
import static org.omscs.ml.a4burlap.utils.Utils.makeNewAlphaCSVCallback;
import static org.omscs.ml.a4burlap.utils.Utils.makeNewEpsilonCSVCallback;
import static org.omscs.ml.a4burlap.utils.Utils.printExerimentStartBlurb;
import static org.omscs.ml.a4burlap.utils.Utils.printRunUniqeidBlurb;

public class QonlyBDAlpha {

  public static final int BD_SMALL_CONVERGE_REWARDS = -19;
  public static final int BD_LARGE_CONVERGE_REWARDS = -31;


  public static void main(String[] args) {

    Set<String> expDirs = Set.of(NAME_BLOCKDUDE, NAME_GRIDWORLD);

    String shortname = "myq-bd";
    CSVWriterGeneric csvWriter =
        new CSVWriterGeneric("output", expDirs, "My Dude Q Learning Experiments", shortname);

    // All MDPs
    MDPBlockDude mdpBlockDudeSM = new MDPBlockDude(ProblemSize.SMALL);
    MDPBlockDude mdpBlockDudeLRG = new MDPBlockDude(ProblemSize.LARGE);

    String qSmallCsvResultName = "q_sm_bd_alpha_result";
    String qLargeCsvResultName = "q_lrg_bd_alpha_result";
    String qSmNamePattern = "q_sm_bd_alpha%.2f";
    String qLrgNamePattern = "q_lrg_bd_alpha%.2f";

    String qSmallCsvResultEpName = "q_sm_bd_ep_result";
    String qLargeCsvResultEpName = "q_lrg_bd_ep_result";
    String qSmNameEpPattern = "q_sm_bd_ep%.2f-%.2f";
    String qLrgNameEpPattern = "q_lrg_bd_ep%.5f-%.2f";

    //    String qSmallCsvResultDecayName = "q_sm_bd_decay_result";
    //    String qLargeCsvResultDecayName = "q_lrg_bd_decay_result";
    //    String qSmNameDecayPattern = "q_sm_bd_decay%.2f";
    //    String qLrgNameDecayPattern = "q_lrg_bd_decay%.2f";

    String name = "";

    RunResultsCsvWriterCallback alphaQSmall = null;
    RunResultsCsvWriterCallback alphaQLarge = null;

    float[] alphas = {
      0.05f, 0.1f, 0.15f, 0.20f, 0.25f, 0.30f, 0.35f, 0.40f, 0.50f, 0.60f, 0.70f, 0.80f, 0.90f
    };
    float[] epsilons = {0.05f, 0.1f, 0.3f, 0.40f, 0.50f, 0.60f, 0.70f, 0.8f, 0.90f, 0.95f, 0.99f};
    float[] decays = {0.0f, 0.10f, 0.20f, 0.30f, 0.40f, 0.50f, 0.6f};

    float[] alphasLrg = {0.90f, 0.50f, 0.20f};
    //    float[] decays = {0};

    float[][] epLrgPairs = {{0.00001f, 0.0f}, {0.0001f, 0.0f}, {0.99f, 0.1f}};


    for (float alpha : alphas) {

      printExerimentStartBlurb("Q-Learner - Small BlockDude Alpha:" + alpha);
      name = String.format(qSmNamePattern, alpha);
      alphaQSmall = makeNewAlphaCSVCallback(alpha, NAME_BLOCKDUDE, qSmallCsvResultName);
      QSettings qSettingsBDsm = new QSettings(name, 0.99, 0.3, alpha, 2000, 0.90, 0.99);
      qSettingsBDsm.setTargeConvergeReward(BD_SMALL_CONVERGE_REWARDS);
      csvWriter.appendToExperimentCatalog(qSettingsBDsm);
      BlockDudeQLearnerExperiment bdqlExp01sm =
          new BlockDudeQLearnerExperiment(mdpBlockDudeSM, qSettingsBDsm, csvWriter);
      bdqlExp01sm.setRunResultsCSVCallback(alphaQSmall);
      bdqlExp01sm.runWithEpisodesAndSave(1, 300);
      mdpBlockDudeSM.reset();
    }

    for (float alpha : alphasLrg) {
      // Large BlockDude with Q-Learner
      printExerimentStartBlurb("Q-Learner - Large BlockDude Alpha:" + alpha);
      name = String.format(qLrgNamePattern, alpha);
      alphaQLarge = makeNewAlphaCSVCallback(alpha, NAME_BLOCKDUDE, qLargeCsvResultName);
      QSettings qSettingsBD01alphaLrg = new QSettings(name, 0.99, 0.3, alpha, 20000, 0.90, 0.99);
      qSettingsBD01alphaLrg.setTargeConvergeReward(BD_LARGE_CONVERGE_REWARDS);
      csvWriter.appendToExperimentCatalog(qSettingsBD01alphaLrg);
      BlockDudeQLearnerExperiment bdqlExp01Alrg =
          new BlockDudeQLearnerExperiment(mdpBlockDudeLRG, qSettingsBD01alphaLrg, csvWriter);
      bdqlExp01Alrg.setRunResultsCSVCallback(alphaQLarge);
      bdqlExp01Alrg.runWithEpisodesAndSave(1, 2000);
      mdpBlockDudeLRG.reset();
    }

    for (float eps : epsilons) {
      for (float decay : decays) {
        printExerimentStartBlurb("Q-Learner - Small BlockDude Epsilon:" + eps + " decay:" + decay);
        name = String.format(qSmNameEpPattern, eps, decay);
        alphaQSmall = makeNewEpsilonCSVCallback(eps, decay, NAME_BLOCKDUDE, qSmallCsvResultEpName);
        QSettings qSettingsBDsm = new QSettings(name, 0.99, 0.3, 0.2, 2000, eps, decay);
        qSettingsBDsm.setTargeConvergeReward(BD_SMALL_CONVERGE_REWARDS);
        csvWriter.appendToExperimentCatalog(qSettingsBDsm);
        BlockDudeQLearnerExperiment bdqlExp01sm =
            new BlockDudeQLearnerExperiment(mdpBlockDudeSM, qSettingsBDsm, csvWriter);
        bdqlExp01sm.setRunResultsCSVCallback(alphaQSmall);
        bdqlExp01sm.runWithEpisodesAndSave(1, 2000);
        mdpBlockDudeSM.reset();
      }
    }

    for (float[] rec : epLrgPairs) {

      float eps = rec[0], decay = rec[1];
      printExerimentStartBlurb("Q-Learner - Large BlockDude Epsilon:" + eps + " decay:" + decay);
      name = String.format(qLrgNameEpPattern, eps, decay);
      alphaQLarge = makeNewEpsilonCSVCallback(eps, decay, NAME_BLOCKDUDE, qLargeCsvResultEpName);
      QSettings qSettingsBDlrg = new QSettings(name, 0.99, 0.3, 0.3, 20000, eps, decay);
      qSettingsBDlrg.setTargeConvergeReward(BD_LARGE_CONVERGE_REWARDS);
      csvWriter.appendToExperimentCatalog(qSettingsBDlrg);
      BlockDudeQLearnerExperiment bdqlExp01sm =
          new BlockDudeQLearnerExperiment(mdpBlockDudeLRG, qSettingsBDlrg, csvWriter);
      bdqlExp01sm.setRunResultsCSVCallback(alphaQLarge);
      bdqlExp01sm.runWithEpisodesAndSave(1, 2000);
      mdpBlockDudeLRG.reset();
    }

    printRunUniqeidBlurb(shortname, csvWriter);
  }
}
