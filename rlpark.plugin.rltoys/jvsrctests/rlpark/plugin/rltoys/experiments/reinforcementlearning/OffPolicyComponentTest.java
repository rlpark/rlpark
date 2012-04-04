package rlpark.plugin.rltoys.experiments.reinforcementlearning;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import rlpark.plugin.rltoys.agents.offpolicy.OffPolicyAgent;
import rlpark.plugin.rltoys.agents.offpolicy.OffPolicyAgentDirect;
import rlpark.plugin.rltoys.algorithms.learning.control.offpolicy.OffPolicyLearner;
import rlpark.plugin.rltoys.algorithms.learning.predictions.Predictor;
import rlpark.plugin.rltoys.algorithms.representations.acting.Policy;
import rlpark.plugin.rltoys.algorithms.representations.projectors.RepresentationFactory;
import rlpark.plugin.rltoys.envio.actions.Action;
import rlpark.plugin.rltoys.envio.actions.ActionArray;
import rlpark.plugin.rltoys.envio.problems.RLProblem;
import rlpark.plugin.rltoys.experiments.parametersweep.interfaces.Context;
import rlpark.plugin.rltoys.experiments.parametersweep.interfaces.SweepDescriptor;
import rlpark.plugin.rltoys.experiments.parametersweep.offpolicy.AbstractContextOffPolicy;
import rlpark.plugin.rltoys.experiments.parametersweep.offpolicy.ContextEvaluation;
import rlpark.plugin.rltoys.experiments.parametersweep.offpolicy.evaluation.OffPolicyEvaluation;
import rlpark.plugin.rltoys.experiments.parametersweep.parameters.Parameters;
import rlpark.plugin.rltoys.experiments.parametersweep.reinforcementlearning.OffPolicyAgentFactory;
import rlpark.plugin.rltoys.experiments.parametersweep.reinforcementlearning.OffPolicyProblemFactory;
import rlpark.plugin.rltoys.experiments.reinforcementlearning.problemtest.AbstractRLProblemFactoryTest;
import rlpark.plugin.rltoys.math.vector.RealVector;
import rlpark.plugin.rltoys.utils.Utils;

@SuppressWarnings("serial")
public class OffPolicyComponentTest {
  static class OffPolicySweepDescriptor implements SweepDescriptor {
    private final OffPolicyEvaluation evaluation;
    private final OffPolicyProblemFactory problemFactory;

    public OffPolicySweepDescriptor(OffPolicyProblemFactory problemFactory, OffPolicyEvaluation evaluation) {
      this.evaluation = evaluation;
      this.problemFactory = problemFactory;
    }

    @Override
    public List<? extends Context> provideContexts() {
      OffPolicyAgentFactoryTest[] factories = new OffPolicyAgentFactoryTest[] {
          new OffPolicyAgentFactoryTest("Action01", AbstractRLProblemFactoryTest.Action01),
          new OffPolicyAgentFactoryTest("Action02", AbstractRLProblemFactoryTest.Action02) };
      List<ContextEvaluation> result = new ArrayList<ContextEvaluation>();
      for (OffPolicyAgentFactoryTest factory : factories)
        result.add(new ContextEvaluation(problemFactory, null, factory, evaluation));
      return result;
    }

    @Override
    public List<Parameters> provideParameters(Context context) {
      return Utils.asList(((AbstractContextOffPolicy) context).contextParameters());
    }
  }

  static class OffPolicyLearnerTest implements OffPolicyLearner {
    private final Action action;

    public OffPolicyLearnerTest(Action action) {
      this.action = action;
    }

    @Override
    public void learn(RealVector x_t, Action a_t, RealVector x_tp1, Action a_tp1, double reward) {
    }

    @Override
    public Action proposeAction(RealVector x_t) {
      return action;
    }

    @Override
    public Policy targetPolicy() {
      return null;
    }

    @Override
    public Predictor predictor() {
      return null;
    }
  }

  static class OffPolicyAgentFactoryTest implements OffPolicyAgentFactory {
    private final Action action;
    private final String label;

    public OffPolicyAgentFactoryTest(String label, Action action) {
      this.action = action;
      this.label = label;
    }

    @Override
    public Policy createBehaviourPolicy(long seed, RLProblem problem) {
      final Random random = new Random(seed);
      return new Policy() {
        @Override
        public double pi(RealVector s, Action a) {
          return 1;
        }

        @Override
        public Action decide(RealVector s) {
          return new ActionArray(random.nextDouble());
        }
      };
    }

    @Override
    public String label() {
      return label;
    }

    @Override
    public OffPolicyAgent createAgent(RLProblem problem, RepresentationFactory projectorFactory, Parameters parameters,
        Policy behaviourPolicy, final long seed) {
      OffPolicyLearner learner = new OffPolicyLearnerTest(action);
      return new OffPolicyAgentDirect(behaviourPolicy, learner);
    }
  }
}
