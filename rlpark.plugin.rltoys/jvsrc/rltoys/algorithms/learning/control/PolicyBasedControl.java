package rltoys.algorithms.learning.control;

import rltoys.algorithms.representations.acting.Policy;

public interface PolicyBasedControl extends Control {
  Policy policy();
}
