package rltoys.algorithms.learning.control.qlearning;

import org.junit.Test;

import rltoys.algorithms.learning.control.Control;
import rltoys.algorithms.learning.control.acting.EpsilonGreedy;
import rltoys.algorithms.learning.control.mountaincar.ActionValueMountainCarAgentFactory;
import rltoys.algorithms.learning.control.mountaincar.MountainCarOnPolicyTest;
import rltoys.algorithms.learning.predictions.Predictor;
import rltoys.algorithms.representations.Projector;
import rltoys.algorithms.representations.actions.Action;
import rltoys.algorithms.representations.actions.StateToStateAction;
import rltoys.algorithms.representations.traces.ATraces;

public class QLearningTest extends MountainCarOnPolicyTest {
  @Test
  public void testQLearningOnMountainCar() {
    runTestOnOnMountainCar(new ActionValueMountainCarAgentFactory() {
      @Override
      protected Control createControl(Predictor predictor, Projector projector, StateToStateAction toStateAction,
          EpsilonGreedy acting) {
        return new QLearningControl(acting, (QLearning) predictor);
      }

      @Override
      protected Predictor createPredictor(Action[] actions, StateToStateAction toStateAction, int nbActiveFeatures,
          int nbFeatures) {
        return new QLearning(actions, 0.1 / nbActiveFeatures, 0.9, 0.0, toStateAction, nbFeatures, new ATraces());
      }
    });
  }
}
