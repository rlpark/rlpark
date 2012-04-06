package rlpark.plugin.rltoys.junit.experiments.reinforcementlearning.problemtest;

import rlpark.plugin.rltoys.envio.actions.Action;
import rlpark.plugin.rltoys.envio.actions.ActionArray;
import rlpark.plugin.rltoys.experiments.parametersweep.parameters.Parameters;
import rlpark.plugin.rltoys.experiments.parametersweep.parameters.RunInfo;
import rlpark.plugin.rltoys.experiments.parametersweep.reinforcementlearning.ProblemFactory;
import rlpark.plugin.rltoys.experiments.parametersweep.reinforcementlearning.RLParameters;

@SuppressWarnings("serial")
public abstract class AbstractRLProblemFactoryTest implements ProblemFactory {
  private final int nbEpisode;
  private final int nbTimeSteps;
  static final public Action Action01 = new ActionArray(1.0);
  static final public Action Action02 = new ActionArray(2.0);

  public AbstractRLProblemFactoryTest(int nbEpisode, int nbTimeSteps) {
    this.nbEpisode = nbEpisode;
    this.nbTimeSteps = nbTimeSteps;
  }

  @Override
  public String label() {
    return "Problem";
  }

  @Override
  public void setExperimentParameters(Parameters parameters) {
    RunInfo infos = parameters.infos();
    infos.put(RLParameters.MaxEpisodeTimeSteps, nbTimeSteps);
    infos.put(RLParameters.NbEpisode, nbEpisode);
  }
}