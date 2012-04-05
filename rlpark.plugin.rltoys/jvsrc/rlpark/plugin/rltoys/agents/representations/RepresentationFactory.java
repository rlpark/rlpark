package rlpark.plugin.rltoys.agents.representations;

import rlpark.plugin.rltoys.algorithms.functions.stateactions.StateToStateAction;
import rlpark.plugin.rltoys.problems.ProblemDiscreteAction;

public interface RepresentationFactory extends ProjectorFactory {
  StateToStateAction createToStateAction(long seed, ProblemDiscreteAction problem);
}
