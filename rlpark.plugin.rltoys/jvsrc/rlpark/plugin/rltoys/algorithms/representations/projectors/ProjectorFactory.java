package rlpark.plugin.rltoys.algorithms.representations.projectors;

import java.io.Serializable;

import rlpark.plugin.rltoys.envio.problems.RLProblem;
import rlpark.plugin.rltoys.envio.states.Projector;

public interface ProjectorFactory extends Serializable {
  Projector createProjector(long seed, RLProblem problem);
}
