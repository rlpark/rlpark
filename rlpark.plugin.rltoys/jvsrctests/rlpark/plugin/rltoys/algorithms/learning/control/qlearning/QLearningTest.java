package rlpark.plugin.rltoys.algorithms.learning.control.qlearning;

import org.junit.Test;

import rlpark.plugin.rltoys.algorithms.learning.control.mountaincar.ActionValueMountainCarAgentFactory;
import rlpark.plugin.rltoys.algorithms.learning.control.mountaincar.MountainCarOnPolicyTest;
import rlpark.plugin.rltoys.algorithms.learning.control.qlearning.QLearning;
import rlpark.plugin.rltoys.algorithms.learning.control.qlearning.QLearningControl;
import rlpark.plugin.rltoys.algorithms.learning.predictions.Predictor;
import rlpark.plugin.rltoys.algorithms.representations.acting.Policy;
import rlpark.plugin.rltoys.algorithms.representations.actions.StateToStateAction;
import rlpark.plugin.rltoys.algorithms.representations.traces.ATraces;
import rlpark.plugin.rltoys.envio.actions.Action;
import rlpark.plugin.rltoys.envio.control.ControlLearner;
import rlpark.plugin.rltoys.envio.states.Projector;
import rlpark.plugin.rltoys.problems.mountaincar.MountainCar;

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
