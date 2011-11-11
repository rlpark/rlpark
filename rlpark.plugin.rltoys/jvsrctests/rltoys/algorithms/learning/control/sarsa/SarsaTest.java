package rltoys.algorithms.learning.control.sarsa;


import java.util.Random;

import org.junit.Test;

import rltoys.algorithms.learning.control.Control;
import rltoys.algorithms.learning.control.acting.EpsilonGreedy;
import rltoys.algorithms.learning.control.mountaincar.ActionValueMountainCarAgentFactory;
import rltoys.algorithms.learning.control.mountaincar.MountainCarOnPolicyTest;
import rltoys.algorithms.learning.predictions.Predictor;
import rltoys.algorithms.representations.Projector;
import rltoys.algorithms.representations.actions.Action;
import rltoys.algorithms.representations.actions.StateToStateAction;
import rltoys.algorithms.representations.discretizer.partitions.PartitionFactory;
import rltoys.algorithms.representations.tilescoding.TileCoders;
import rltoys.algorithms.representations.tilescoding.TileCodersHashing;
import rltoys.algorithms.representations.tilescoding.hashing.UNH;
import rltoys.algorithms.representations.traces.AMaxTraces;
import rltoys.algorithms.representations.traces.ATraces;
import rltoys.algorithms.representations.traces.RTraces;
import rltoys.algorithms.representations.traces.Traces;
import rltoys.math.ranges.Range;


public class SarsaTest extends MountainCarOnPolicyTest {
  abstract static class SarsaControlFactory extends ActionValueMountainCarAgentFactory {
    private final Traces traces;

    public SarsaControlFactory() {
      this(new ATraces());
    }

    public SarsaControlFactory(Traces traces) {
      this.traces = traces;
    }

    @Override
    protected Predictor createPredictor(Action[] actions, StateToStateAction toStateAction, int nbActiveFatures,
        int nbFeatures) {
      return new Sarsa(0.2 / nbActiveFatures, 0.99, 0.3, nbFeatures, traces);
    }

    @Override
    protected Control createControl(Predictor predictor, Projector projector, StateToStateAction toStateAction,
        EpsilonGreedy acting) {
      return createControl(acting, toStateAction, (Sarsa) predictor);
    }

    abstract protected Control createControl(EpsilonGreedy acting, StateToStateAction toStateAction, Sarsa predictor);
  }

  @Test
  public void testSarsaOnMountainCar() {
    runTestOnOnMountainCar(new SarsaControlFactory() {
      @Override
      protected Control createControl(EpsilonGreedy acting, StateToStateAction toStateAction, Sarsa predictor) {
        return new SarsaControl(acting, toStateAction, predictor);
      }
    });
  }

  @Test
  public void testExpectedSarsaOnMountainCar() {
    runTestOnOnMountainCar(new SarsaControlFactory() {
      @Override
      protected Control createControl(EpsilonGreedy acting, StateToStateAction toStateAction, Sarsa predictor) {
        return new ExpectedSarsaControl(acting.actions(), acting, toStateAction, predictor);
      }
    });
  }

  @Test
  public void testSarsaOnMountainCarAccumulatingTraces() {
    runTestOnOnMountainCar(new SarsaControlFactory(new ATraces()) {
      @Override
      protected Control createControl(EpsilonGreedy acting, StateToStateAction toStateAction, Sarsa predictor) {
        return new SarsaControl(acting, toStateAction, predictor);
      }
    });
  }

  @Test
  public void testSarsaOnMountainCarAMaxTraces() {
    runTestOnOnMountainCar(new SarsaControlFactory(new AMaxTraces()) {
      @Override
      protected Control createControl(EpsilonGreedy acting, StateToStateAction toStateAction, Sarsa predictor) {
        return new SarsaControl(acting, toStateAction, predictor);
      }
    });
  }

  @Test
  public void testSarsaOnMountainCarReplacingTraces() {
    runTestOnOnMountainCar(new SarsaControlFactory(new RTraces()) {
      @Override
      protected Control createControl(EpsilonGreedy acting, StateToStateAction toStateAction, Sarsa predictor) {
        return new SarsaControl(acting, toStateAction, predictor);
      }
    });
  }

  @Test
  public void testSarsaOnMountainCarHashingTileCodingWithRandom() {
    runTestOnOnMountainCar(new TileCodersFactory() {
      @Override
      public TileCoders create(Range[] ranges) {
        Random random = new Random(0);
        PartitionFactory discretizerFactory = new PartitionFactory(ranges);
        discretizerFactory.setRandom(random, 0.1);
        TileCoders tileCoders = new TileCodersHashing(new UNH(random, 10000), discretizerFactory, ranges.length);
        return tileCoders;
      }
    }, new SarsaControlFactory(new AMaxTraces()) {
      @Override
      protected Control createControl(EpsilonGreedy acting, StateToStateAction toStateAction, Sarsa predictor) {
        return new SarsaControl(acting, toStateAction, predictor);
      }
    });
  }
}
