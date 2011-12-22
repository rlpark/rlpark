package rltoys.experiments.reinforcementlearning;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import rltoys.algorithms.learning.predictions.Predictor;
import rltoys.algorithms.representations.acting.Policy;
import rltoys.algorithms.representations.actions.Action;
import rltoys.algorithms.representations.projectors.RepresentationFactory;
import rltoys.environments.envio.actions.ActionArray;
import rltoys.environments.envio.offpolicy.OffPolicyAgent;
import rltoys.environments.envio.offpolicy.OffPolicyAgentDirect;
import rltoys.environments.envio.offpolicy.OffPolicyLearner;
import rltoys.environments.envio.problems.RLProblem;
import rltoys.experiments.parametersweep.interfaces.Context;
import rltoys.experiments.parametersweep.interfaces.SweepDescriptor;
import rltoys.experiments.parametersweep.offpolicy.AbstractContextOffPolicy;
import rltoys.experiments.parametersweep.offpolicy.ContextEvaluation;
import rltoys.experiments.parametersweep.offpolicy.evaluation.OffPolicyEvaluation;
import rltoys.experiments.parametersweep.parameters.Parameters;
import rltoys.experiments.parametersweep.reinforcementlearning.OffPolicyAgentFactory;
import rltoys.experiments.parametersweep.reinforcementlearning.OffPolicyProblemFactory;
import rltoys.experiments.reinforcementlearning.problemtest.AbstractRLProblemFactoryTest;
import rltoys.math.vector.RealVector;
import rltoys.utils.Utils;

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
    public Policy createBehaviourPolicy(RLProblem problem, long seed) {
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
