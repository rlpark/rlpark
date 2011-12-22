package rltoys.algorithms.learning.control.actorcritic;


import java.util.Random;

import org.junit.Test;

import rltoys.algorithms.learning.control.actorcritic.onpolicy.ActorCritic;
import rltoys.algorithms.learning.control.actorcritic.onpolicy.ActorLambda;
import rltoys.algorithms.learning.control.actorcritic.policystructure.BoltzmannDistribution;
import rltoys.algorithms.learning.control.mountaincar.MountainCarOnPolicyTest;
import rltoys.algorithms.learning.predictions.td.OnPolicyTD;
import rltoys.algorithms.learning.predictions.td.TDLambda;
import rltoys.algorithms.learning.predictions.td.TDLambdaAutostep;
import rltoys.algorithms.representations.acting.PolicyDistribution;
import rltoys.algorithms.representations.actions.StateToStateAction;
import rltoys.algorithms.representations.actions.TabularAction;
import rltoys.algorithms.representations.traces.ATraces;
import rltoys.environments.envio.RLAgent;
import rltoys.environments.envio.agents.LearnerAgentFA;
import rltoys.environments.envio.states.Projector;
import rltoys.environments.mountaincar.MountainCar;


public class ActorCriticMountainCarTest extends MountainCarOnPolicyTest {
  static class MountainCarActorCriticControlFactory implements MountainCarAgentFactory {
    @Override
    public RLAgent createAgent(MountainCar mountainCar, Projector projector) {
      final double lambda = .3;
      final double gamma = .99;
      OnPolicyTD critic = createCritic(projector, lambda, gamma);
      StateToStateAction toStateAction = new TabularAction(mountainCar.actions(), projector.vectorNorm(),
                                                           projector.vectorSize());
      PolicyDistribution distribution = new BoltzmannDistribution(new Random(0), mountainCar.actions(), toStateAction);
      ActorLambda actor = new ActorLambda(lambda, distribution, .01 / projector.vectorNorm(), projector.vectorSize());
      return new LearnerAgentFA(new ActorCritic(critic, actor), projector);
    }

    protected OnPolicyTD createCritic(Projector projector, final double lambda, final double gamma) {
      return new TDLambda(lambda, gamma, .1 / projector.vectorNorm(), projector.vectorSize(),
                          new ATraces((int) (projector.vectorNorm() * 100), 0.05));
    }
  }

  @Test
  public void testDiscreteActorCriticOnMountainCar() {
    runTestOnOnMountainCar(new MountainCarActorCriticControlFactory());
  }

  @Test
  public void testDiscreteAutostepActorCriticOnMountainCar() {
    runTestOnOnMountainCar(new MountainCarActorCriticControlFactory() {
      @Override
      protected OnPolicyTD createCritic(Projector projector, final double lambda, final double gamma) {
        return new TDLambdaAutostep(lambda, gamma, projector.vectorSize());
      }
    });
  }
}
