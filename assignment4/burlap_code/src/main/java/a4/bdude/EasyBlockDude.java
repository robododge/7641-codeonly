package a4.bdude;

import a4.svpino.artifacts.Analysis;
import burlap.behavior.policy.Policy;
import burlap.behavior.policy.PolicyUtils;
import burlap.behavior.singleagent.Episode;
import burlap.behavior.singleagent.planning.Planner;
import burlap.behavior.singleagent.planning.stochastic.valueiteration.ValueIteration;
import burlap.domain.singleagent.blockdude.BlockDudeLevelConstructor;
import burlap.domain.singleagent.blockdude.BlockDudeModel;
import burlap.domain.singleagent.blockdude.BlockDudeTF;
import burlap.domain.singleagent.blockdude.state.BlockDudeAgent;
import burlap.domain.singleagent.blockdude.state.BlockDudeCell;
import burlap.domain.singleagent.blockdude.state.BlockDudeMap;
import burlap.domain.singleagent.blockdude.state.BlockDudeState;
import burlap.mdp.auxiliary.DomainGenerator;
import burlap.mdp.core.TerminalFunction;
import burlap.mdp.core.action.Action;
import burlap.mdp.core.action.UniversalActionType;
import burlap.mdp.core.oo.OODomain;
import burlap.mdp.core.oo.propositional.PropositionalFunction;
import burlap.mdp.core.oo.state.OOState;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.SADomain;
import burlap.mdp.singleagent.common.UniformCostRF;
import burlap.mdp.singleagent.model.FactoredModel;
import burlap.mdp.singleagent.model.RewardFunction;
import burlap.mdp.singleagent.oo.OOSADomain;
import burlap.statehashing.simple.SimpleHashableStateFactory;

import java.util.Arrays;
import java.util.List;

public class EasyBlockDude implements DomainGenerator {

  public static void main(String[] args) {

    EasyBlockDude ebd = new EasyBlockDude();
    SADomain edbDomain = ebd.generateDomain();

//    BlockDudeState initialBdState = (BlockDudeState)BlockDudeLevelConstructor.getLevel2(edbDomain);
    BlockDudeState initialBdState = (BlockDudeState)BlockDudeLevelConstructor.getLevel1(edbDomain);
    int mapXDim = initialBdState.map.map.length;


    SimpleHashableStateFactory hashingFactory = new SimpleHashableStateFactory();

    Analysis analysis = new Analysis();

    Planner viPlanner = new ValueIteration((SADomain) edbDomain, 0.80, hashingFactory, 0.001, 10000 );
    int maxEpisodes = 1;

    Policy policy = null;
    Episode episode = null;
    long startTime = System.nanoTime();

    List<Action> actionSeq = null;
    for (int i = 0; i < maxEpisodes; i++) {

      startTime = System.nanoTime();
      System.out.printf("Starting episode: %d\n",i);
      policy = viPlanner.planFromState(initialBdState);

      episode = PolicyUtils.rollout(policy, initialBdState, edbDomain.getModel(),mapXDim*mapXDim *2 );
      analysis.add(i, episode.rewardSequence, episode.numTimeSteps(),(System.nanoTime() - startTime) / 1000000);
      System.out.printf("Done episode: %d\n",i);
      actionSeq = episode.actionSequence;
    }
    analysis.print();
    System.out.println(actionSeq);

    //[west, pickup, west, putdown, up, up, west, pickup, west, west, up, west, west, putdown, up, up, west, west, west]

  }

  public static final String VAR_X = "x";
  public static final String VAR_Y = "y";

  public static final String VAR_DIR = "dir";
  public static final String VAR_HOLD = "holding";

  public static final String VAR_MAP = "map";
  public static final String CLASS_AGENT = "agent";
  public static final String CLASS_BLOCK = "block";
  public static final String CLASS_MAP = "map";
  public static final String CLASS_EXIT = "exit";

  public static final String ACTION_UP = "up";
  public static final String ACTION_EAST = "east";
  public static final String ACTION_WEST = "west";
  public static final String ACTION_PICKUP = "pickup";
  public static final String ACTION_PUT_DOWN = "putdown";

  public static final String PF_HOLDING_BLOCK = "holdingBlock";
  public static final String PF_AT_EXIT = "atExit";

  protected int maxx = 25;
  protected int maxy = 25;

  protected RewardFunction rf;
  protected TerminalFunction tf;

  public List<PropositionalFunction> generatePfs() {
    return Arrays.asList(new EasyBlockDude.HoldingBlockPF(), new EasyBlockDude.AtExitPF());
  }

  @Override
  public OOSADomain generateDomain() {
    OOSADomain domain = new OOSADomain();

    domain
        .addStateClass(CLASS_AGENT, BlockDudeAgent.class)
        .addStateClass(CLASS_MAP, BlockDudeMap.class)
        .addStateClass(CLASS_EXIT, BlockDudeCell.class)
        .addStateClass(CLASS_BLOCK, BlockDudeCell.class);

    domain
        .addActionType(new UniversalActionType(ACTION_EAST))
        .addActionType(new UniversalActionType(ACTION_WEST))
        .addActionType(new UniversalActionType(ACTION_UP))
        .addActionType(new UniversalActionType(ACTION_PICKUP))
        .addActionType(new UniversalActionType(ACTION_PUT_DOWN));

    OODomain.Helper.addPfsToDomain(domain, this.generatePfs());

    RewardFunction rf = this.rf;
    TerminalFunction tf = this.tf;

    if (tf == null) {
      tf = new BlockDudeTF();
    }
    if (rf == null) {
      rf = new UniformCostRF();
    }

//    BlockDudeModel smodel = new BlockDudeModel(maxx, maxy);
    BlockDudeModel smodel = new EasyBlockDudeModel(maxx, maxy);

    FactoredModel model = new FactoredModel(smodel, rf, tf);
    domain.setModel(model);

    return domain;
  }

  public class HoldingBlockPF extends PropositionalFunction {

    public HoldingBlockPF() {
      super(PF_HOLDING_BLOCK, new String[] {CLASS_AGENT, CLASS_BLOCK});
    }

    @Override
    public boolean isTrue(OOState st, String... params) {

      BlockDudeAgent a = (BlockDudeAgent) st.object(params[0]);

      if (!a.holding) {
        return false;
      }

      BlockDudeCell b = (BlockDudeCell) st.object(params[1]);

      if (a.x == b.x && a.y == b.y - 1) {
        return true;
      }

      return false;
    }
  }

  public class AtExitPF extends PropositionalFunction {

    public AtExitPF() {
      super(PF_AT_EXIT, new String[] {CLASS_AGENT, CLASS_EXIT});
    }

    @Override
    public boolean isTrue(OOState st, String... params) {

      BlockDudeAgent a = (BlockDudeAgent) st.object(params[0]);
      BlockDudeCell e = (BlockDudeCell) st.object(params[1]);

      if (a.x == e.x && a.y == e.y) {
        return true;
      }

      return false;
    }
  }
}
