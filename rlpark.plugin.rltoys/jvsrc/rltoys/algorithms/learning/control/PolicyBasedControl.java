package rltoys.algorithms.learning.control;

import rltoys.algorithms.representations.acting.Policy;
import rltoys.environments.envio.control.ControlLearner;

public interface PolicyBasedControl extends ControlLearner {
  Policy policy();
}
