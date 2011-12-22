package rltoys.algorithms.learning.control.qlearning;

import org.junit.Test;

import rltoys.algorithms.learning.control.mountaincar.ActionValueMountainCarAgentFactory;
import rltoys.algorithms.learning.control.mountaincar.MountainCarOnPolicyTest;
import rltoys.algorithms.learning.predictions.Predictor;
import rltoys.algorithms.representations.acting.Policy;
import rltoys.algorithms.representations.actions.Action;
import rltoys.algorithms.representations.actions.StateToStateAction;
import rltoys.algorithms.representations.traces.ATraces;
import rltoys.environments.envio.control.ControlLearner;
import rltoys.environments.envio.states.Projector;
import rltoys.environments.mountaincar.MountainCar;

public class QLearningTest extends MountainCarOnPolicyTest {
  @Test
  public void testQLearningOnMountainCar() {
    runTestOnOnMountainCar(new ActionValueMountainCarAgentFactory() {
      @Override
      protected ControlLearner createControl(MountainCar mountainCar, Predictor predictor, Projector projector,
          StateToStateAction toStateAction, Policy acting) {
        return new QLearningControl(acting, (QLearning) predictor);
      }

      @Override
      protected Predictor createPredictor(Action[] actions, StateToStateAction toStateAction, double nbActiveFeatures,
          int vectorSize) {
        return new QLearning(actions, 0.1 / nbActiveFeatures, 0.9, 0.0, toStateAction, vectorSize, new ATraces());
      }
    });
  }
}
