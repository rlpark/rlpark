package rlpark.plugin.rltoys.algorithms.learning.control.gq;

import org.junit.Test;

import rlpark.plugin.rltoys.algorithms.learning.control.gq.GQ;
import rlpark.plugin.rltoys.algorithms.learning.control.gq.GQOnPolicyControl;
import rlpark.plugin.rltoys.algorithms.learning.control.mountaincar.ActionValueMountainCarAgentFactory;
import rlpark.plugin.rltoys.algorithms.learning.control.mountaincar.MountainCarOnPolicyTest;
import rlpark.plugin.rltoys.algorithms.learning.predictions.Predictor;
import rlpark.plugin.rltoys.algorithms.representations.acting.Policy;
import rlpark.plugin.rltoys.algorithms.representations.actions.StateToStateAction;
import rlpark.plugin.rltoys.envio.actions.Action;
import rlpark.plugin.rltoys.envio.control.ControlLearner;
import rlpark.plugin.rltoys.envio.states.Projector;
import rlpark.plugin.rltoys.problems.mountaincar.MountainCar;

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
