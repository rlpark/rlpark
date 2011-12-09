package rltoys.experiments.reinforcementlearning.problemtest;

import rltoys.algorithms.representations.actions.Action;
import rltoys.environments.envio.actions.ActionArray;
import rltoys.experiments.parametersweep.parameters.Parameters;
import rltoys.experiments.parametersweep.parameters.RunInfo;
import rltoys.experiments.parametersweep.reinforcementlearning.ProblemFactory;
import rltoys.experiments.parametersweep.reinforcementlearning.RLParameters;

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