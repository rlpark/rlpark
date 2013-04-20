package rlpark.plugin.rltoys.junit.algorithms.control.sarsa;


import java.util.Random;

import org.junit.Test;

import rlpark.plugin.rltoys.agents.representations.IdentityProjector;
import rlpark.plugin.rltoys.agents.representations.ProjectorFactory;
import rlpark.plugin.rltoys.agents.rl.LearnerAgent;
import rlpark.plugin.rltoys.algorithms.control.ControlLearner;
import rlpark.plugin.rltoys.algorithms.control.sarsa.ExpectedSarsaControl;
import rlpark.plugin.rltoys.algorithms.control.sarsa.Sarsa;
import rlpark.plugin.rltoys.algorithms.control.sarsa.SarsaControl;
import rlpark.plugin.rltoys.algorithms.functions.Predictor;
import rlpark.plugin.rltoys.algorithms.functions.stateactions.StateToStateAction;
import rlpark.plugin.rltoys.algorithms.functions.states.Projector;
import rlpark.plugin.rltoys.algorithms.representations.discretizer.TabularActionDiscretizer;
import rlpark.plugin.rltoys.algorithms.representations.discretizer.partitions.BoundedBigPartitionFactory;
import rlpark.plugin.rltoys.algorithms.representations.discretizer.partitions.BoundedSmallPartitionFactory;
import rlpark.plugin.rltoys.algorithms.representations.tilescoding.StateActionCoders;
import rlpark.plugin.rltoys.algorithms.representations.tilescoding.TileCodersNoHashing;
import rlpark.plugin.rltoys.algorithms.representations.tilescoding.hashing.Hashing;
import rlpark.plugin.rltoys.algorithms.representations.tilescoding.hashing.MurmurHashing;
import rlpark.plugin.rltoys.algorithms.traces.ATraces;
import rlpark.plugin.rltoys.algorithms.traces.Traces;
import rlpark.plugin.rltoys.envio.actions.Action;
import rlpark.plugin.rltoys.envio.policy.Policy;
import rlpark.plugin.rltoys.envio.rl.RLAgent;
import rlpark.plugin.rltoys.junit.algorithms.control.mountaincar.ActionValueMountainCarAgentFactory;
import rlpark.plugin.rltoys.junit.algorithms.control.mountaincar.MountainCarOnPolicyTest;
import rlpark.plugin.rltoys.math.ranges.Range;
import rlpark.plugin.rltoys.problems.ProblemBounded;
import rlpark.plugin.rltoys.problems.RLProblem;
import rlpark.plugin.rltoys.problems.mountaincar.MountainCar;

@SuppressWarnings("serial")
public class SarsaTest extends MountainCarOnPolicyTest {
  static class SarsaControlFactory extends ActionValueMountainCarAgentFactory {
    private final Traces traces;

    public SarsaControlFactory() {
      this(new ATraces());
    }

    public SarsaControlFactory(Traces traces) {
      this.traces = traces;
    }

    @Override
    protected Predictor createPredictor(Action[] actions, StateToStateAction toStateAction, double vectorNorm,
        int vectorSize) {
      return new Sarsa(0.2 / vectorNorm, 0.99, 0.3, vectorSize, traces);
    }

    @Override
    protected ControlLearner createControl(MountainCar mountainCar, Predictor predictor, Projector projector,
        StateToStateAction toStateAction, Policy acting) {
      return new SarsaControl(acting, toStateAction, (Sarsa) predictor);
    }
  }

  @Test
  public void testSarsaOnMountainCar() {
    runTestOnOnMountainCar(new SarsaControlFactory());
  }

  @Test
  public void testExpectedSarsaOnMountainCar() {
    runTestOnOnMountainCar(new SarsaControlFactory() {
      @Override
      protected ControlLearner createControl(MountainCar mountainCar, Predictor predictor, Projector projector,
          StateToStateAction toStateAction, Policy acting) {
        return new ExpectedSarsaControl(mountainCar.actions(), acting, toStateAction, (Sarsa) predictor);
      }
    });
  }

  @Test
  public void testSarsaOnMountainCarHashingTileCodingWithRandom() {
    runTestOnOnMountainCar(MountainCarOnPolicyTest.hashingTileCodersFactory, new SarsaControlFactory());
  }

  @Test
  public void testSarsaOnMountainCarTileCodingWithBigPartition() {
    runTestOnOnMountainCar(new ProjectorFactory() {
      @Override
      public Projector createProjector(long seed, RLProblem problem) {
        Range[] ranges = ((ProblemBounded) problem).getObservationRanges();
        TileCodersNoHashing projector = new TileCodersNoHashing(new BoundedBigPartitionFactory(ranges), ranges.length);
        projector.addFullTilings(9, 10);
        return projector;
      }
    }, new SarsaControlFactory());
  }

  StateActionCoders createStateToStateAction(MountainCar problem) {
    Range[] ranges = problem.getObservationRanges();
    TabularActionDiscretizer actionDiscretizer = new TabularActionDiscretizer(problem.actions());
    Hashing hashing = new MurmurHashing(new Random(0), 50000);
    StateActionCoders stateActionCoders = new StateActionCoders(actionDiscretizer, hashing,
                                                                new BoundedSmallPartitionFactory(ranges), ranges.length);
    stateActionCoders.tileCoders().addFullTilings(9, 10);
    return stateActionCoders;
  }

  @Test
  public void testSarsaOnMountainCarHashingTileCodingWithActionTileCodedWithRandom() {
    runTestOnOnMountainCar(new ProjectorFactory() {
      @Override
      public Projector createProjector(long seed, RLProblem problem) {
        return new IdentityProjector(((ProblemBounded) problem).getObservationRanges());
      }
    }, new SarsaControlFactory() {
      @Override
      public RLAgent createAgent(MountainCar problem, Projector projector) {
        StateToStateAction stateActionCoders = createStateToStateAction(problem);
        Predictor predictor = createPredictor(problem.actions(), stateActionCoders,
                                              (int) stateActionCoders.vectorNorm(), stateActionCoders.vectorSize());
        Policy acting = createActing(problem, stateActionCoders, predictor);
        return new LearnerAgent(createControl(problem, predictor, projector, stateActionCoders, acting));
      }
    });
  }
}
