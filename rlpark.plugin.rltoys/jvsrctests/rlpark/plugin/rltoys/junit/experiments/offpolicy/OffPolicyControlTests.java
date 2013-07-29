package rlpark.plugin.rltoys.junit.experiments.offpolicy;

import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

import rlpark.plugin.rltoys.agents.offpolicy.OffPolicyAgentDirect;
import rlpark.plugin.rltoys.agents.offpolicy.OffPolicyAgentEvaluable;
import rlpark.plugin.rltoys.algorithms.control.acting.Greedy;
import rlpark.plugin.rltoys.algorithms.control.acting.SoftMax;
import rlpark.plugin.rltoys.algorithms.control.actorcritic.offpolicy.ActorLambdaOffPolicy;
import rlpark.plugin.rltoys.algorithms.control.actorcritic.offpolicy.ActorOffPolicy;
import rlpark.plugin.rltoys.algorithms.control.actorcritic.offpolicy.CriticAdapterFA;
import rlpark.plugin.rltoys.algorithms.control.actorcritic.offpolicy.OffPAC;
import rlpark.plugin.rltoys.algorithms.control.gq.GQ;
import rlpark.plugin.rltoys.algorithms.control.gq.GreedyGQ;
import rlpark.plugin.rltoys.algorithms.control.qlearning.QLearning;
import rlpark.plugin.rltoys.algorithms.control.qlearning.QLearningControl;
import rlpark.plugin.rltoys.algorithms.functions.policydistributions.PolicyDistribution;
import rlpark.plugin.rltoys.algorithms.functions.policydistributions.structures.BoltzmannDistribution;
import rlpark.plugin.rltoys.algorithms.functions.stateactions.StateToStateAction;
import rlpark.plugin.rltoys.algorithms.functions.states.Projector;
import rlpark.plugin.rltoys.algorithms.predictions.td.GTDLambda;
import rlpark.plugin.rltoys.algorithms.predictions.td.OffPolicyTD;
import rlpark.plugin.rltoys.algorithms.traces.ATraces;
import rlpark.plugin.rltoys.envio.policy.Policy;
import rlpark.plugin.rltoys.experiments.testing.control.MountainCarOffPolicyLearning;
import rlpark.plugin.rltoys.experiments.testing.control.MountainCarOffPolicyLearning.MountainCarEvaluationAgentFactory;
import rlpark.plugin.rltoys.problems.mountaincar.MountainCar;

public class OffPolicyControlTests {
  @Test
  public void qlearningOnMountainCarTest() {
    final MountainCarEvaluationAgentFactory factory = new MountainCarEvaluationAgentFactory() {
      @Override
      public OffPolicyAgentEvaluable createOffPolicyAgent(Random random, MountainCar problem, Policy behaviour,
          double gamma) {
        StateToStateAction toStateAction = MountainCarOffPolicyLearning.createToStateAction(random, problem);
        double alpha_v = .1 / toStateAction.vectorNorm();
        QLearning qlearning = new QLearning(problem.actions(), alpha_v, gamma, .6, toStateAction, new ATraces());
        Policy targetPolicy = new Greedy(qlearning, problem.actions(), toStateAction);
        QLearningControl learner = new QLearningControl(targetPolicy, qlearning);
        return new OffPolicyAgentDirect(behaviour, learner);
      }
    };
    Assert.assertTrue(MountainCarOffPolicyLearning.evaluate(factory) < 165);
  }

  @Test
  public void greedyGQOnMountainCarTest() {
    final MountainCarEvaluationAgentFactory factory = new MountainCarEvaluationAgentFactory() {
      @Override
      public OffPolicyAgentEvaluable createOffPolicyAgent(Random random, MountainCar problem, Policy behaviour,
          double gamma) {
        StateToStateAction toStateAction = MountainCarOffPolicyLearning.createToStateAction(random, problem);
        double alpha_v = .1 / toStateAction.vectorNorm();
        double alpha_w = .0001 / toStateAction.vectorNorm();
        GQ gq = new GQ(alpha_v, alpha_w, 1 - gamma, .4, toStateAction.vectorSize(), new ATraces());
        Policy targetPolicy = new Greedy(gq, problem.actions(), toStateAction);
        GreedyGQ learner = new GreedyGQ(gq, problem.actions(), toStateAction, targetPolicy, behaviour);
        return new OffPolicyAgentDirect(behaviour, learner);
      }
    };
    Assert.assertTrue(MountainCarOffPolicyLearning.evaluate(factory) < 270);
  }

  @Test
  public void softmaxGQOnMountainCarTest() {
    final MountainCarEvaluationAgentFactory factory = new MountainCarEvaluationAgentFactory() {
      @Override
      public OffPolicyAgentEvaluable createOffPolicyAgent(Random random, MountainCar problem, Policy behaviour,
          double gamma) {
        StateToStateAction toStateAction = MountainCarOffPolicyLearning.createToStateAction(random, problem);
        double alpha_v = .1 / toStateAction.vectorNorm();
        double alpha_w = .0005 / toStateAction.vectorNorm();
        GQ gq = new GQ(alpha_v, alpha_w, 1 - gamma, .4, toStateAction.vectorSize(), new ATraces());
        Policy targetPolicy = new SoftMax(random, gq, problem.actions(), toStateAction, .1);
        GreedyGQ learner = new GreedyGQ(gq, problem.actions(), toStateAction, targetPolicy, behaviour);
        return new OffPolicyAgentDirect(behaviour, learner);
      }
    };
    Assert.assertTrue(MountainCarOffPolicyLearning.evaluate(factory) < 190);
  }

  @Test
  public void offpacOnMountainCarTest() {
    final MountainCarEvaluationAgentFactory factory = new MountainCarEvaluationAgentFactory() {
      @Override
      public OffPolicyAgentEvaluable createOffPolicyAgent(Random random, MountainCar problem, Policy behaviour,
          double gamma) {
        Projector criticProjector = MountainCarOffPolicyLearning.createProjector(random, problem);
        OffPolicyTD critic = createCritic(criticProjector, gamma);
        StateToStateAction toStateAction = MountainCarOffPolicyLearning.createToStateAction(random, problem);
        PolicyDistribution target = new BoltzmannDistribution(random, problem.actions(), toStateAction);
        double alpha_u = 1.0 / criticProjector.vectorNorm();
        ActorOffPolicy actor = new ActorLambdaOffPolicy(0, gamma, target, alpha_u, toStateAction.vectorSize(),
                                                        new ATraces());
        return new OffPolicyAgentDirect(behaviour, new OffPAC(behaviour, critic, actor));
      }

      private OffPolicyTD createCritic(Projector criticProjector, double gamma) {
        double alpha_v = .05 / criticProjector.vectorNorm();
        double alpha_w = .0001 / criticProjector.vectorNorm();
        GTDLambda gtd = new GTDLambda(0, gamma, alpha_v, alpha_w, criticProjector.vectorSize(), new ATraces());
        return new CriticAdapterFA(criticProjector, gtd);
      }
    };
    Assert.assertTrue(MountainCarOffPolicyLearning.evaluate(factory) < 120);
  }

  public static void main(String[] args) {
    new OffPolicyControlTests().qlearningOnMountainCarTest();
  }
}
