package rltoys.algorithms.learning.control.gq;

import org.junit.Test;

import rltoys.algorithms.learning.control.mountaincar.ActionValueMountainCarAgentFactory;
import rltoys.algorithms.learning.control.mountaincar.MountainCarOnPolicyTest;
import rltoys.algorithms.learning.predictions.Predictor;
import rltoys.algorithms.representations.acting.Policy;
import rltoys.algorithms.representations.actions.Action;
import rltoys.algorithms.representations.actions.StateToStateAction;
import rltoys.environments.envio.control.ControlLearner;
import rltoys.environments.envio.states.Projector;
import rltoys.environments.mountaincar.MountainCar;

public class GQOnPolicyTest extends MountainCarOnPolicyTest {
  @Test
  public void testGQOnMountainCar() {
    runTestOnOnMountainCar(new ActionValueMountainCarAgentFactory() {
      @Override
      protected ControlLearner createControl(MountainCar mountainCar, Predictor predictor, Projector projector,
          StateToStateAction toStateAction, Policy acting) {
        return new GQOnPolicyControl(acting, toStateAction, (GQ) predictor);
      }

      @Override
      protected Predictor createPredictor(Action[] actions, StateToStateAction toStateAction, double vectorNorm,
          int vectorSize) {
        return new GQ(0.1 / vectorNorm, 0.0, 1 - 0.9, 0.1, vectorSize);
      }
    });
  }
}
