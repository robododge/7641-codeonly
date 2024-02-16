package a4.bdude;

import burlap.domain.singleagent.blockdude.BlockDude;
import burlap.domain.singleagent.blockdude.BlockDudeLevelConstructor;
import burlap.domain.singleagent.blockdude.BlockDudeVisualizer;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.SADomain;
import burlap.shell.visual.VisualExplorer;
import burlap.visualizer.Visualizer;

public class EasyBlockDudeViewer {

    public static void main(String[] args){

        EasyBlockDude ebd = new EasyBlockDude();
        SADomain domain = ebd.generateDomain();


        State s = BlockDudeLevelConstructor.getLevel1(domain);

        Visualizer v = BlockDudeVisualizer.getVisualizer(ebd.maxx, ebd.maxy);


        VisualExplorer exp = new VisualExplorer(domain, v, s);

        exp.addKeyAction("w", EasyBlockDude.ACTION_UP, "");
        exp.addKeyAction("d", EasyBlockDude.ACTION_EAST, "");
        exp.addKeyAction("a", EasyBlockDude.ACTION_WEST, "");
        exp.addKeyAction("s", EasyBlockDude.ACTION_PICKUP, "");
        exp.addKeyAction("x", EasyBlockDude.ACTION_PUT_DOWN, "");

        exp.initGUI();

    }

}
