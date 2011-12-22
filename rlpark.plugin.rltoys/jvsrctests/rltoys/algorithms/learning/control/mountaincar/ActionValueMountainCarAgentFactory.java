package rltoys.algorithms.learning.control.mountaincar;

import java.util.Random;

import rltoys.algorithms.learning.control.acting.EpsilonGreedy;
import rltoys.algorithms.learning.control.mountaincar.MountainCarOnPolicyTest.MountainCarAgentFactory;
import rltoys.algorithms.learning.predictions.Predictor;
import rltoys.algorithms.representations.acting.Policy;
import rltoys.algorithms.representations.actions.Action;
import rltoys.algorithms.representations.actions.StateToStateAction;
import rltoys.algorithms.representations.actions.TabularAction;
import rltoys.environments.envio.RLAgent;
import rltoys.environments.envio.agents.LearnerAgentFA;
import rltoys.environments.envio.control.ControlLearner;
import rltoys.environments.envio.states.Projector;
import rltoys.environments.mountaincar.MountainCar;

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