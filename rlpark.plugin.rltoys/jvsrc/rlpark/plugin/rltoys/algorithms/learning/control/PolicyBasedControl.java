package rlpark.plugin.rltoys.algorithms.learning.control;

import rlpark.plugin.rltoys.algorithms.representations.acting.Policy;
import rlpark.plugin.rltoys.envio.control.ControlLearner;

public interface PolicyBasedControl extends ControlLearner {
  Policy policy();
}
