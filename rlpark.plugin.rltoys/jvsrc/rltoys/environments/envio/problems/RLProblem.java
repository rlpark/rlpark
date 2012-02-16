package rltoys.environments.envio.problems;

import rltoys.algorithms.representations.actions.Action;
import rltoys.environments.envio.observations.Legend;
import rltoys.environments.envio.observations.TRStep;

public interface RLProblem {
  TRStep initialize();

  TRStep step(Action action);

  TRStep endEpisode();

  TRStep lastStep();

  Legend legend();
}
