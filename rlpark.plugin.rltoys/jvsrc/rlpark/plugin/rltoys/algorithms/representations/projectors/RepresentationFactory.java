package rlpark.plugin.rltoys.algorithms.representations.projectors;

import rlpark.plugin.rltoys.algorithms.representations.actions.StateToStateAction;
import rlpark.plugin.rltoys.envio.problems.ProblemDiscreteAction;

public interface RepresentationFactory extends ProjectorFactory {
  StateToStateAction createToStateAction(long seed, ProblemDiscreteAction problem);
}
