package org.omscs.ml.a4burlap.experiments;

import org.omscs.ml.a4burlap.mdp.MDPGridWorld;
import org.omscs.ml.a4burlap.mdp.ProblemSize;
import org.omscs.ml.a4burlap.mdp.grid.A4MainGridSelector;
import org.omscs.ml.a4burlap.qlearn.QSettings;
import org.omscs.ml.a4burlap.utils.CSVWriterGeneric;
import org.omscs.ml.a4burlap.vipi.PISettings;
import org.omscs.ml.a4burlap.vipi.VISettings;

import java.util.Set;

import static org.omscs.ml.a4burlap.experiments.Runner.NAME_BLOCKDUDE;
import static org.omscs.ml.a4burlap.experiments.Runner.NAME_GRIDWORLD;
import static org.omscs.ml.a4burlap.utils.Utils.printExerimentStartBlurb;

public class MyLrgGWExperiments {


    public static void main( String[] args ){

        Set<String> expDirs = Set.of(NAME_BLOCKDUDE, NAME_GRIDWORLD);
        System.out.println("** Initializing CSV ouput for this experiment");
        double stochastic = 1.0;
        String metaDesc = String.format("Large Gridworlds, stochastic: %.2f\n",stochastic);
        CSVWriterGeneric csvWriter = new CSVWriterGeneric("output", expDirs, "Gridworlds Large", "mygwlrg");
//        MDPBlockDude mdpBlockDudeSM = new MDPBlockDude(ProblemSize.SMALL);
//        MDPBlockDude mdpBlockDudeLRG = new MDPBlockDude(ProblemSize.LARGE);

//        MDPGridWorld mdpGridWorldSM = new MDPGridWorld(1.0, ProblemSize.SMALL);
        MDPGridWorld mdpGridWorldLRG = new MDPGridWorld(stochastic, ProblemSize.LARGE, new A4MainGridSelector());

//        VISettings viSettings01 = new VISettings(0.99f, 0.001f, 1000, "vi_sm_high_gamma" );
//        csvWriter.appendToExperimentCatalog(viSettings01);
//
//        BlockDudeVIExperiment viBlockDudeExperiment = new BlockDudeVIExperiment(mdpBlockDudeSM, viSettings01, csvWriter);
//        viBlockDudeExperiment.runAndSave(false);
//        mdpBlockDudeSM.reset();
//
//        PISettings piSettings01 = new PISettings(0.99f, 0.001f,
//                0.001f, 1000,
//                100, "pi_sm_high_gamma");
//        csvWriter.appendToExperimentCatalog(piSettings01);
//        BlockDudePIExperiment piBlockDudeExperiment = new BlockDudePIExperiment(mdpBlockDudeSM,piSettings01, csvWriter);
//        piBlockDudeExperiment.runAndSave(false);
//        mdpBlockDudeSM.reset();

        printExerimentStartBlurb("VI GridWorld Large");
        VISettings viSettingsGW01 = new VISettings(0.99f, 0.000001f,
                1000,"vi_lrg_gw_01");
        csvWriter.appendToExperimentCatalog(viSettingsGW01);
        GridWorldVIExperiment gwSmVi02 = new GridWorldVIExperiment(mdpGridWorldLRG,viSettingsGW01, csvWriter);
        gwSmVi02.runAndSave(true);
        mdpGridWorldLRG.reset();

        printExerimentStartBlurb("PI GridWorld Large");
        PISettings piSettingsLrgGw01 = new PISettings(0.79f, .0001f,
                0.001f, 100,
                100, "pi_lrg_gw_01");
        csvWriter.appendToExperimentCatalog(piSettingsLrgGw01);
        GridWorldPIExperiment piGwSm01 = new GridWorldPIExperiment(mdpGridWorldLRG, piSettingsLrgGw01, csvWriter);
        piGwSm01.runAndSave(true);
        mdpGridWorldLRG.reset();




        printExerimentStartBlurb("Q-learning GridWorld Large");

        QSettings qSettingsGW02 = new QSettings("q_lg_gw_01", 0.99, 0.3, 0.4, 1000, 0.98, 0.99 );
        csvWriter.appendToExperimentCatalog(qSettingsGW02);
        GridWorldQLearnerExperiment gwQExp02 = new GridWorldQLearnerExperiment(mdpGridWorldLRG,qSettingsGW02, csvWriter);
        gwQExp02.runWithEpisodesAndSave(3,1800);
        mdpGridWorldLRG.reset();

    }

}
