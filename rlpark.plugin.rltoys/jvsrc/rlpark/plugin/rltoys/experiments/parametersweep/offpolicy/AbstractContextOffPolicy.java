package rlpark.plugin.rltoys.experiments.parametersweep.offpolicy;

import rlpark.plugin.rltoys.agents.representations.RepresentationFactory;
import rlpark.plugin.rltoys.envio.policy.Policy;
import rlpark.plugin.rltoys.envio.rl.RLAgent;
import rlpark.plugin.rltoys.experiments.helpers.ExperimentCounter;
import rlpark.plugin.rltoys.experiments.helpers.Runner;
import rlpark.plugin.rltoys.experiments.parametersweep.offpolicy.evaluation.OffPolicyEvaluation;
import rlpark.plugin.rltoys.experiments.parametersweep.parameters.Parameters;
import rlpark.plugin.rltoys.experiments.parametersweep.parameters.RunInfo;
import rlpark.plugin.rltoys.experiments.parametersweep.reinforcementlearning.OffPolicyAgentFactory;
import rlpark.plugin.rltoys.experiments.parametersweep.reinforcementlearning.OffPolicyProblemFactory;
import rlpark.plugin.rltoys.experiments.parametersweep.reinforcementlearning.ProblemFactory;
import rlpark.plugin.rltoys.experiments.parametersweep.reinforcementlearning.RLParameters;
import rlpark.plugin.rltoys.experiments.parametersweep.reinforcementlearning.ReinforcementLearningContext;
import rlpark.plugin.rltoys.problems.RLProblem;

public abstract class AbstractContextOffPolicy implements ReinforcementLearningContext {
  private static final long serialVersionUID = -6212106048889219995L;
  private final OffPolicyAgentFactory agentFactory;
  protected final OffPolicyProblemFactory environmentFactory;
  protected final OffPolicyEvaluation evaluation;
  protected final RepresentationFactory projectorFactory;

  public AbstractContextOffPolicy(OffPolicyProblemFactory environmentFactory, RepresentationFactory projectorFactory,
      OffPolicyAgentFactory agentFactory, OffPolicyEvaluation evaluation) {
    this.evaluation = evaluation;
    this.projectorFactory = projectorFactory;
    this.environmentFactory = environmentFactory;
    this.agentFactory = agentFactory;
  }

  @Override
  public Runner createRunner(int seed, Parameters parameters) {
    RLProblem problem = environmentFactory.createEnvironment(ExperimentCounter.newRandom(seed));
    Policy behaviourPolicy = agentFactory.createBehaviourPolicy(seed, problem);
    RLAgent agent = agentFactory.createAgent(problem, projectorFactory, parameters, behaviourPolicy, seed);
    int nbEpisode = RLParameters.nbEpisode(parameters);
    int maxEpisodeTimeSteps = RLParameters.maxEpisodeTimeSteps(parameters);
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

  public OffPolicyAgentFactory agentFactory() {
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
