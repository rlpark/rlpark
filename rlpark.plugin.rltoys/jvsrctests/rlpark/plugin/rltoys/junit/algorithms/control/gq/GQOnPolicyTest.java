package rlpark.plugin.rltoys.junit.algorithms.control.gq;

import org.junit.Test;

import rlpark.plugin.rltoys.algorithms.control.ControlLearner;
import rlpark.plugin.rltoys.algorithms.control.gq.GQ;
import rlpark.plugin.rltoys.algorithms.control.gq.GQOnPolicyControl;
import rlpark.plugin.rltoys.algorithms.functions.Predictor;
import rlpark.plugin.rltoys.algorithms.functions.stateactions.StateToStateAction;
import rlpark.plugin.rltoys.algorithms.functions.states.Projector;
import rlpark.plugin.rltoys.envio.actions.Action;
import rlpark.plugin.rltoys.envio.policy.Policy;
import rlpark.plugin.rltoys.junit.algorithms.control.mountaincar.ActionValueMountainCarAgentFactory;
import rlpark.plugin.rltoys.junit.algorithms.control.mountaincar.MountainCarOnPolicyTest;
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
        return new GQ(0.2 / vectorNorm, 0.0, 1 - 0.9, 0.1, vectorSize);
      }
    });
  }
}
