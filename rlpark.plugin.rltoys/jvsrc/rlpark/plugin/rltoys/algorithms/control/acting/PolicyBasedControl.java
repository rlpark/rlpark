package rlpark.plugin.rltoys.algorithms.control.acting;

import rlpark.plugin.rltoys.algorithms.control.ControlLearner;
import rlpark.plugin.rltoys.envio.policy.Policy;

public interface PolicyBasedControl extends ControlLearner {
  Policy policy();
}
