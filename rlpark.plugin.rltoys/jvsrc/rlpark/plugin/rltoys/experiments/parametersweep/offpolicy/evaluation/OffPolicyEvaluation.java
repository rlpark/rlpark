package rlpark.plugin.rltoys.experiments.parametersweep.offpolicy.evaluation;

import java.io.Serializable;

import rlpark.plugin.rltoys.agents.offpolicy.OffPolicyAgentEvaluable;
import rlpark.plugin.rltoys.agents.representations.RepresentationFactory;
import rlpark.plugin.rltoys.experiments.parametersweep.interfaces.PerformanceEvaluator;
import rlpark.plugin.rltoys.experiments.parametersweep.parameters.Parameters;
import rlpark.plugin.rltoys.experiments.parametersweep.reinforcementlearning.OffPolicyProblemFactory;
import rlpark.plugin.rltoys.experiments.runners.AbstractRunner;
import rlpark.plugin.rltoys.experiments.runners.AbstractRunner.RunnerEvent;
import zephyr.plugin.core.api.signals.Signal;

public interface OffPolicyEvaluation extends Serializable {
  PerformanceEvaluator connectEvaluator(int counter, AbstractRunner behaviourRunner, Signal<RunnerEvent> signal,
      OffPolicyProblemFactory environmentFactory, RepresentationFactory projectorFactory, OffPolicyAgentEvaluable learningAgent, Parameters parameters);

  int nbRewardCheckpoint();
}
