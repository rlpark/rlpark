package rlpark.plugin.rltoys.junit.experiments.reinforcementlearning;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import rlpark.plugin.rltoys.agents.offpolicy.OffPolicyAgent;
import rlpark.plugin.rltoys.agents.offpolicy.OffPolicyAgentDirect;
import rlpark.plugin.rltoys.agents.representations.RepresentationFactory;
import rlpark.plugin.rltoys.algorithms.control.OffPolicyLearner;
import rlpark.plugin.rltoys.algorithms.functions.Predictor;
import rlpark.plugin.rltoys.envio.actions.Action;
import rlpark.plugin.rltoys.envio.actions.ActionArray;
import rlpark.plugin.rltoys.envio.policy.Policy;
import rlpark.plugin.rltoys.experiments.parametersweep.interfaces.Context;
import rlpark.plugin.rltoys.experiments.parametersweep.interfaces.SweepDescriptor;
import rlpark.plugin.rltoys.experiments.parametersweep.offpolicy.AbstractContextOffPolicy;
import rlpark.plugin.rltoys.experiments.parametersweep.parameters.Parameters;
import rlpark.plugin.rltoys.experiments.parametersweep.parameters.RunInfo;
import rlpark.plugin.rltoys.experiments.parametersweep.reinforcementlearning.OffPolicyAgentFactory;
import rlpark.plugin.rltoys.experiments.parametersweep.reinforcementlearning.OffPolicyProblemFactory;
import rlpark.plugin.rltoys.junit.experiments.reinforcementlearning.problemtest.AbstractRLProblemFactoryTest;
import rlpark.plugin.rltoys.math.vector.RealVector;
import rlpark.plugin.rltoys.problems.RLProblem;
import rlpark.plugin.rltoys.utils.Utils;

@SuppressWarnings("serial")
public class OffPolicyComponentTest {
  static class OffPolicySweepDescriptor implements SweepDescriptor {
    private final AbstractContextOffPolicy evaluation;
    private final OffPolicyProblemFactory problemFactory;
    private final RunInfo infos;

    public OffPolicySweepDescriptor(OffPolicyProblemFactory problemFactory, AbstractContextOffPolicy evaluation,
        RunInfo infos) {
      this.evaluation = evaluation;
      this.problemFactory = problemFactory;
      this.infos = infos;
    }

    @Override
    public List<? extends Context> provideContexts() {
      OffPolicyAgentFactoryTest[] factories = new OffPolicyAgentFactoryTest[] {
          new OffPolicyAgentFactoryTest("Action01", AbstractRLProblemFactoryTest.Action01),
          new OffPolicyAgentFactoryTest("Action02", AbstractRLProblemFactoryTest.Action02) };
      List<AbstractContextOffPolicy> result = new ArrayList<AbstractContextOffPolicy>();
      for (OffPolicyAgentFactoryTest factory : factories)
        result.add(evaluation.newContext(problemFactory, null, factory));
      return result;
    }

    @Override
    public List<Parameters> provideParameters(Context context) {
    	Parameters contextParameters = ((AbstractContextOffPolicy) context).contextParameters(infos);
      return Utils.asList(contextParameters);
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

    private Policy createBehaviourPolicy(long seed, RLProblem problem) {
      final Random random = new Random(seed);
      return new Policy() {
        @Override
        public double pi(Action a) {
          return 1;
        }

        @Override
        public Action sampleAction() {
          return new ActionArray(random.nextDouble());
        }

        @Override
        public void update(RealVector x) {
        }
      };
    }

    @Override
    public String label() {
      return label;
    }

    @Override
    public OffPolicyAgent createAgent(final long seed, RLProblem problem, Parameters parameters,
        RepresentationFactory projectorFactory) {
      Policy behaviourPolicy = createBehaviourPolicy(seed, problem);
      OffPolicyLearner learner = new OffPolicyLearnerTest(action);
      return new OffPolicyAgentDirect(behaviourPolicy, learner);
    }
  }
}
