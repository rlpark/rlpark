package rltoys.algorithms.representations.projectors;

import rltoys.algorithms.representations.actions.StateToStateAction;
import rltoys.environments.envio.problems.ProblemDiscreteAction;

public interface RepresentationFactory extends ProjectorFactory {
  StateToStateAction createToStateAction(long seed, ProblemDiscreteAction problem);
}
