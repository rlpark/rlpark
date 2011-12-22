package rltoys.algorithms.representations.projectors;

import java.io.Serializable;

import rltoys.environments.envio.problems.RLProblem;
import rltoys.environments.envio.states.Projector;

public interface ProjectorFactory extends Serializable {
  Projector createProjector(long seed, RLProblem problem);
}
