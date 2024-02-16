package org.omscs.ml.a4burlap.experiments;

import org.omscs.ml.a4burlap.mdp.MDPGridWorld;
import org.omscs.ml.a4burlap.mdp.ProblemSize;
import org.omscs.ml.a4burlap.mdp.grid.A4MainGridSelector;
import org.omscs.ml.a4burlap.qlearn.QSettings;
import org.omscs.ml.a4burlap.utils.CSVWriterGeneric;
import org.omscs.ml.a4burlap.utils.RunResultsCsvWriterCallback;

import java.util.Set;

import static org.omscs.ml.a4burlap.experiments.Runner.NAME_BLOCKDUDE;
import static org.omscs.ml.a4burlap.experiments.Runner.NAME_GRIDWORLD;
import static org.omscs.ml.a4burlap.utils.Utils.makeNewAlphaCSVCallback;
import static org.omscs.ml.a4burlap.utils.Utils.makeNewEpsilonCSVCallback;
import static org.omscs.ml.a4burlap.utils.Utils.printExerimentStartBlurb;
import static org.omscs.ml.a4burlap.utils.Utils.printRunUniqeidBlurb;

public class QonlyGWAlpha {

  public static final int GW_SMALL_CONVERGE_REWARDS = -14;
  public static final int GW_LARGE_CONVERGE_REWARDS = -84;

  public static void main(String[] args) {

    Set<String> expDirs = Set.of(NAME_BLOCKDUDE, NAME_GRIDWORLD);

    String shortname = "myq-gw";
    CSVWriterGeneric csvWriter =
        new CSVWriterGeneric("output", expDirs, "My Grid World Q Learning Experiments", shortname);

    // All MDPs
    double stochastic = 0.8;
    MDPGridWorld mdpGridWorldSM =
        new MDPGridWorld(stochastic, ProblemSize.SMALL, new A4MainGridSelector());
    MDPGridWorld mdpGridWorldLRG =
        new MDPGridWorld(stochastic, ProblemSize.LARGE, new A4MainGridSelector());

    String qSmallCsvResultName = "q_sm_gw_alpha_result";
    String qLargeCsvResultName = "q_lrg_gw_alpha_result";
    String qSmNamePattern = "q_sm_gw_alpha%.2f";
    String qLrgNamePattern = "q_lrg_gw_alpha%.2f";

    String qSmallCsvResultEpName = "q_sm_gw_ep_result";
    String qLargeCsvResultEpName = "q_lrg_gw_ep_result";
    String qSmNameEpPattern = "q_sm_gw_ep%.2f-%.2f";
    String qLrgNameEpPattern = "q_lrg_gw_ep%.5f-%.2f";

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

      printExerimentStartBlurb("Q-Learner - Small Grid World Alpha:" + alpha);
      name = String.format(qSmNamePattern, alpha);
      alphaQSmall = makeNewAlphaCSVCallback(alpha, NAME_GRIDWORLD, qSmallCsvResultName);

      QSettings qSettingsGW01 = new QSettings(name, 0.85, 0.3, alpha, -1, 0.80, 0.98);
      qSettingsGW01.setTargeConvergeReward(GW_SMALL_CONVERGE_REWARDS);

      csvWriter.appendToExperimentCatalog(qSettingsGW01);
      GridWorldQLearnerExperiment gwQExp01 =
          new GridWorldQLearnerExperiment(mdpGridWorldSM, qSettingsGW01, csvWriter);
      gwQExp01.setRunResultsCSVCallback(alphaQSmall);
      gwQExp01.tooggleVisual(false, 0);
      gwQExp01.runWithEpisodesAndSave(1, 2000);
      mdpGridWorldSM.reset();
    }

    for (float alpha : alphasLrg) {
      // Large BlockDude with Q-Learner
      printExerimentStartBlurb("Q-Learner - Large Gridworld Alpha:" + alpha);
      name = String.format(qLrgNamePattern, alpha);
      alphaQLarge = makeNewAlphaCSVCallback(alpha, NAME_GRIDWORLD, qLargeCsvResultName);
      QSettings qSettingsGW01alphaLrg = new QSettings(name, 0.99, 0.3, alpha, -1, 0.90, 0.99);
      qSettingsGW01alphaLrg.setTargeConvergeReward(GW_LARGE_CONVERGE_REWARDS);
      csvWriter.appendToExperimentCatalog(qSettingsGW01alphaLrg);
      GridWorldQLearnerExperiment bdqlExp01Alrg =
          new GridWorldQLearnerExperiment(mdpGridWorldLRG, qSettingsGW01alphaLrg, csvWriter);
      bdqlExp01Alrg.setRunResultsCSVCallback(alphaQLarge);
      bdqlExp01Alrg.runWithEpisodesAndSave(1, 2000);
      mdpGridWorldLRG.reset();
    }

    for (float eps : epsilons) {
      for (float decay : decays) {
        printExerimentStartBlurb("Q-Learner - Small GridWorld Epsilon:" + eps + " decay:" + decay);
        name = String.format(qSmNameEpPattern, eps, decay);
        alphaQSmall = makeNewEpsilonCSVCallback(eps, decay, NAME_GRIDWORLD, qSmallCsvResultEpName);
        QSettings qSettingsGWsm = new QSettings(name, 0.85, 0.3, 0.2, -1, eps, decay);
        qSettingsGWsm.setTargeConvergeReward(GW_SMALL_CONVERGE_REWARDS);
        csvWriter.appendToExperimentCatalog(qSettingsGWsm);
        GridWorldQLearnerExperiment gwqlExp01sm =
            new GridWorldQLearnerExperiment(mdpGridWorldSM, qSettingsGWsm, csvWriter);
        gwqlExp01sm.setRunResultsCSVCallback(alphaQSmall);
        gwqlExp01sm.runWithEpisodesAndSave(1, 2000);
        mdpGridWorldSM.reset();
      }
    }

    for (float[] rec : epLrgPairs) {

      float eps = rec[0], decay = rec[1];
      printExerimentStartBlurb("Q-Learner - Large GridWorld Epsilon:" + eps + " decay:" + decay);
      name = String.format(qLrgNameEpPattern, eps, decay);
      alphaQLarge = makeNewEpsilonCSVCallback(eps, decay, NAME_GRIDWORLD, qLargeCsvResultEpName);
      QSettings qSettingsGWlrg = new QSettings(name, 0.99, 0.3, 0.3, -1, eps, decay);
      qSettingsGWlrg.setTargeConvergeReward(GW_LARGE_CONVERGE_REWARDS);
      csvWriter.appendToExperimentCatalog(qSettingsGWlrg);
      GridWorldQLearnerExperiment gwqlExp01Lrg =
          new GridWorldQLearnerExperiment(mdpGridWorldLRG, qSettingsGWlrg, csvWriter);
      gwqlExp01Lrg.setRunResultsCSVCallback(alphaQLarge);
      gwqlExp01Lrg.runWithEpisodesAndSave(1, 2000);
      mdpGridWorldLRG.reset();
    }

    printRunUniqeidBlurb(shortname, csvWriter);
  }
}
