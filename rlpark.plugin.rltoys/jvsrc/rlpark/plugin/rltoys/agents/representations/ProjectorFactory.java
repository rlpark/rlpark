package rlpark.plugin.rltoys.agents.representations;

import java.io.Serializable;

import rlpark.plugin.rltoys.algorithms.functions.states.Projector;
import rlpark.plugin.rltoys.problems.RLProblem;

public interface ProjectorFactory extends Serializable {
  Projector createProjector(long seed, RLProblem problem);
}
