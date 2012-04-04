package rlpark.plugin.rltoys.envio.problems;

import rlpark.plugin.rltoys.envio.actions.Action;
import rlpark.plugin.rltoys.envio.observations.Legend;
import rlpark.plugin.rltoys.envio.observations.TRStep;

public interface RLProblem {
  TRStep initialize();

  TRStep step(Action action);

  TRStep forceEndEpisode();

  TRStep lastStep();

  Legend legend();
}
