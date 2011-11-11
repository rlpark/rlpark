package rltoys.algorithms.learning.control.mountaincar;

import java.util.Random;

import rltoys.algorithms.learning.control.Control;
import rltoys.algorithms.learning.control.acting.EpsilonGreedy;
import rltoys.algorithms.learning.control.mountaincar.MountainCarOnPolicyTest.MountainCarControlFactory;
import rltoys.algorithms.learning.predictions.Predictor;
import rltoys.algorithms.representations.Projector;
import rltoys.algorithms.representations.actions.Action;
import rltoys.algorithms.representations.actions.StateToStateAction;
import rltoys.algorithms.representations.actions.TabularAction;
import rltoys.environments.mountaincar.MountainCar;

public abstract class ActionValueMountainCarAgentFactory implements MountainCarControlFactory {
  @Override
  public Control createControl(MountainCar mountainCar, Projector projector) {
    StateToStateAction toStateAction = new TabularAction(mountainCar.actions(), projector.vectorSize());
    Predictor predictor = createPredictor(mountainCar.actions(), toStateAction, (int) projector.vectorNorm(),
                                          toStateAction.vectorSize());
    EpsilonGreedy acting = new EpsilonGreedy(new Random(0), mountainCar.actions(), toStateAction, predictor, 0.01);
    return createControl(predictor, projector, toStateAction, acting);
  }

  protected abstract Predictor createPredictor(Action[] actions, StateToStateAction toStateAction, int nbActiveFatures,
      int nbFeatures);

  protected abstract Control createControl(Predictor predictor, Projector projector,
      StateToStateAction toStateAction, EpsilonGreedy acting);
}