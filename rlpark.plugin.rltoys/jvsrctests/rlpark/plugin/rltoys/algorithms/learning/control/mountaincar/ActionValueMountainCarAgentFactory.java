package rlpark.plugin.rltoys.algorithms.learning.control.mountaincar;

import java.util.Random;

import rlpark.plugin.rltoys.agents.LearnerAgentFA;
import rlpark.plugin.rltoys.agents.RLAgent;
import rlpark.plugin.rltoys.algorithms.learning.control.acting.EpsilonGreedy;
import rlpark.plugin.rltoys.algorithms.learning.control.mountaincar.MountainCarOnPolicyTest.MountainCarAgentFactory;
import rlpark.plugin.rltoys.algorithms.learning.predictions.Predictor;
import rlpark.plugin.rltoys.algorithms.representations.acting.Policy;
import rlpark.plugin.rltoys.algorithms.representations.actions.StateToStateAction;
import rlpark.plugin.rltoys.algorithms.representations.actions.TabularAction;
import rlpark.plugin.rltoys.envio.actions.Action;
import rlpark.plugin.rltoys.envio.control.ControlLearner;
import rlpark.plugin.rltoys.envio.states.Projector;
import rlpark.plugin.rltoys.problems.mountaincar.MountainCar;

public abstract class ActionValueMountainCarAgentFactory implements MountainCarAgentFactory {
  @Override
  public RLAgent createAgent(MountainCar mountainCar, Projector projector) {
    StateToStateAction toStateAction = new TabularAction(mountainCar.actions(), projector.vectorNorm(),
                                                         projector.vectorSize());
    Predictor predictor = createPredictor(mountainCar.actions(), toStateAction, (int) projector.vectorNorm(),
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