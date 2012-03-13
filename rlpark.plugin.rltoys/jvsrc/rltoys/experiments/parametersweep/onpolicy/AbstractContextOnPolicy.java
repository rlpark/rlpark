package rltoys.experiments.parametersweep.onpolicy;

import rltoys.environments.envio.RLAgent;
import rltoys.environments.envio.Runner;
import rltoys.environments.envio.problems.RLProblem;
import rltoys.experiments.ExperimentCounter;
import rltoys.experiments.parametersweep.parameters.Parameters;
import rltoys.experiments.parametersweep.parameters.RunInfo;
import rltoys.experiments.parametersweep.reinforcementlearning.AgentFactory;
import rltoys.experiments.parametersweep.reinforcementlearning.ProblemFactory;
import rltoys.experiments.parametersweep.reinforcementlearning.ReinforcementLearningContext;

public abstract class AbstractContextOnPolicy implements ReinforcementLearningContext {
  private static final long serialVersionUID = -6212106048889219995L;
  private final AgentFactory agentFactory;
  private final ProblemFactory environmentFactory;

  public AbstractContextOnPolicy(ProblemFactory environmentFactory, AgentFactory agentFactory) {
    this.environmentFactory = environmentFactory;
    this.agentFactory = agentFactory;
  }

  @Override
  public Runner createRunner(int counter, Parameters parameters) {
    RLProblem problem = environmentFactory.createEnvironment(ExperimentCounter.newRandom(counter));
    RLAgent agent = agentFactory.createAgent(problem, parameters, counter);
    int nbEpisode = parameters.nbEpisode();
    int maxEpisodeTimeSteps = parameters.maxEpisodeTimeSteps();
    return new Runner(problem, agent, nbEpisode, maxEpisodeTimeSteps);
  }

  @Override
  public String fileName() {
    return ExperimentCounter.DefaultFileName;
  }

  @Override
  public String folderPath() {
    return environmentFactory.label() + "/" + agentFactory.label();
  }

  public AgentFactory agentFactory() {
    return agentFactory;
  }

  public ProblemFactory problemFactory() {
    return environmentFactory;
  }

  public Parameters contextParameters() {
    RunInfo infos = new RunInfo();
    infos.enableFlag(agentFactory.label());
    infos.enableFlag(environmentFactory.label());
    Parameters parameters = new Parameters(infos);
    environmentFactory.setExperimentParameters(parameters);
    return parameters;
  }
}
