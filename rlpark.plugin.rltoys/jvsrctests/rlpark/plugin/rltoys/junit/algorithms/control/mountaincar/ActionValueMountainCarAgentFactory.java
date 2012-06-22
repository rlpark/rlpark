package rlpark.plugin.rltoys.junit.algorithms.control.mountaincar;

import java.util.Random;

import rlpark.plugin.rltoys.agents.rl.LearnerAgentFA;
import rlpark.plugin.rltoys.algorithms.control.ControlLearner;
import rlpark.plugin.rltoys.algorithms.control.acting.EpsilonGreedy;
import rlpark.plugin.rltoys.algorithms.functions.Predictor;
import rlpark.plugin.rltoys.algorithms.functions.stateactions.StateToStateAction;
import rlpark.plugin.rltoys.algorithms.functions.stateactions.TabularAction;
import rlpark.plugin.rltoys.algorithms.functions.states.Projector;
import rlpark.plugin.rltoys.envio.actions.Action;
import rlpark.plugin.rltoys.envio.policy.Policy;
import rlpark.plugin.rltoys.envio.rl.RLAgent;
import rlpark.plugin.rltoys.junit.algorithms.control.mountaincar.MountainCarOnPolicyTest.MountainCarAgentFactory;
import rlpark.plugin.rltoys.problems.mountaincar.MountainCar;

public abstract class ActionValueMountainCarAgentFactory implements MountainCarAgentFactory {
  @Override
  public RLAgent createAgent(MountainCar mountainCar, Projector projector) {
    StateToStateAction toStateAction = new TabularAction(mountainCar.actions(), projector.vectorNorm(),
                                                         projector.vectorSize());
    Predictor predictor = createPredictor(mountainCar.actions(), toStateAction, toStateAction.vectorNorm(),
                                          toStateAction.vectorSize());
    Policy acting = createActing(mountainCar, toStateAction, predictor);
    return new LearnerAgentFA(createControl(mountainCar, predictor, projector, toStateAction, acting), projector);
  }

  protected Policy createActing(MountainCar mountainCar, StateToStateAction toStateAction, Predictor predictor) {
    return new EpsilonGreedy(new Random(0), mountainCar.actions(), toStateAction, predictor, 0.01);
  }

  protected abstract Predictor createPredictor(Action[] actions, StateToStateAction toStateAction, double vectorNorm,
      int vectorSize);

  protected abstract ControlLearner createControl(MountainCar mountainCar, Predictor predictor, Projector projector,
      StateToStateAction toStateAction, Policy acting);
}