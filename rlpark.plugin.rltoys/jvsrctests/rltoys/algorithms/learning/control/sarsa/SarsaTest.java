package rltoys.algorithms.learning.control.sarsa;


import java.util.Random;

import org.junit.Test;

import rltoys.algorithms.learning.control.mountaincar.ActionValueMountainCarAgentFactory;
import rltoys.algorithms.learning.control.mountaincar.MountainCarOnPolicyTest;
import rltoys.algorithms.learning.predictions.Predictor;
import rltoys.algorithms.representations.acting.Policy;
import rltoys.algorithms.representations.actions.Action;
import rltoys.algorithms.representations.actions.StateToStateAction;
import rltoys.algorithms.representations.actions.StateToStateActionBuffer;
import rltoys.algorithms.representations.discretizer.TabularActionDiscretizer;
import rltoys.algorithms.representations.discretizer.partitions.PartitionFactory;
import rltoys.algorithms.representations.projectors.IdentityProjector;
import rltoys.algorithms.representations.projectors.ProjectorFactory;
import rltoys.algorithms.representations.tilescoding.StateActionCoders;
import rltoys.algorithms.representations.tilescoding.hashing.Hashing;
import rltoys.algorithms.representations.tilescoding.hashing.MurmurHashing;
import rltoys.algorithms.representations.traces.ATraces;
import rltoys.algorithms.representations.traces.Traces;
import rltoys.environments.envio.RLAgent;
import rltoys.environments.envio.agents.LearnerAgent;
import rltoys.environments.envio.control.ControlLearner;
import rltoys.environments.envio.problems.ProblemBounded;
import rltoys.environments.envio.problems.RLProblem;
import rltoys.environments.envio.states.Projector;
import rltoys.environments.mountaincar.MountainCar;
import rltoys.math.ranges.Range;

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

  StateActionCoders createStateToStateAction(MountainCar problem) {
    Range[] ranges = problem.getObservationRanges();
    TabularActionDiscretizer actionDiscretizer = new TabularActionDiscretizer(problem.actions());
    Hashing hashing = new MurmurHashing(new Random(0), 50000);
    StateActionCoders stateActionCoders = new StateActionCoders(actionDiscretizer, hashing,
                                                                new PartitionFactory(false, ranges), ranges.length);
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
        StateToStateAction stateActionCoders = new StateToStateActionBuffer(createStateToStateAction(problem));
        Predictor predictor = createPredictor(problem.actions(), stateActionCoders,
                                              (int) stateActionCoders.vectorNorm(), stateActionCoders.vectorSize());
        Policy acting = createActing(problem, stateActionCoders, predictor);
        return new LearnerAgent(createControl(problem, predictor, projector, stateActionCoders, acting));
      }
    });
  }
}
