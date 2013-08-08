package rlpark.plugin.rltoys.junit.algorithms.control.sarsa;


import org.junit.Test;

import rlpark.plugin.rltoys.algorithms.control.ControlLearner;
import rlpark.plugin.rltoys.algorithms.control.sarsa.Sarsa;
import rlpark.plugin.rltoys.algorithms.control.sarsa.SarsaAlphaBound;
import rlpark.plugin.rltoys.algorithms.control.sarsa.SarsaControl;
import rlpark.plugin.rltoys.algorithms.functions.Predictor;
import rlpark.plugin.rltoys.algorithms.functions.stateactions.StateToStateAction;
import rlpark.plugin.rltoys.algorithms.functions.states.Projector;
import rlpark.plugin.rltoys.algorithms.traces.ATraces;
import rlpark.plugin.rltoys.envio.actions.Action;
import rlpark.plugin.rltoys.envio.policy.Policy;
import rlpark.plugin.rltoys.junit.algorithms.control.mountaincar.ActionValueMountainCarAgentFactory;
import rlpark.plugin.rltoys.junit.algorithms.control.mountaincar.MountainCarOnPolicyTest;
import rlpark.plugin.rltoys.problems.mountaincar.MountainCar;

public class SarsaAlphaBoundTest extends MountainCarOnPolicyTest {
  @Test
  public void testSarsaAlphaBoundOnMountainCar() {
    runTestOnOnMountainCar(new ActionValueMountainCarAgentFactory() {
      @Override
      protected ControlLearner createControl(MountainCar mountainCar, Predictor predictor, Projector projector,
          StateToStateAction toStateAction, Policy acting) {
        return new SarsaControl(acting, toStateAction, (Sarsa) predictor);
      }

      @Override
      protected Predictor createPredictor(Action[] actions, StateToStateAction toStateAction, double nbActiveFeatures,
          int vectorSize) {
        return new SarsaAlphaBound(1.0, 0.99, 0.3, vectorSize, new ATraces());
      }
    });
  }
}
