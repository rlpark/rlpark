package rltoys.algorithms.learning.control.gq;

import org.junit.Test;

import rltoys.algorithms.learning.control.Control;
import rltoys.algorithms.learning.control.acting.EpsilonGreedy;
import rltoys.algorithms.learning.control.mountaincar.ActionValueMountainCarAgentFactory;
import rltoys.algorithms.learning.control.mountaincar.MountainCarOnPolicyTest;
import rltoys.algorithms.learning.predictions.Predictor;
import rltoys.algorithms.representations.Projector;
import rltoys.algorithms.representations.actions.Action;
import rltoys.algorithms.representations.actions.StateToStateAction;

public class GQOnPolicyTest extends MountainCarOnPolicyTest {
  @Test
  public void testGQOnMountainCar() {
    runTestOnOnMountainCar(new ActionValueMountainCarAgentFactory() {
      @Override
      protected Control createControl(Predictor predictor, Projector projector, StateToStateAction toStateAction,
          EpsilonGreedy acting) {
        return new GQOnPolicyControl(acting, toStateAction, (GQ) predictor);
      }

      @Override
      protected Predictor createPredictor(Action[] actions, StateToStateAction toStateAction, int nbActiveFatures,
          int nbFeatures) {
        return new GQ(0.1 / nbActiveFatures, 0.0, 1 - 0.9, 0.1, nbFeatures);
      }
    });
  }
}
