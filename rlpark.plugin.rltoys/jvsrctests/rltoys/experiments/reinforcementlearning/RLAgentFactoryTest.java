package rltoys.experiments.reinforcementlearning;


import rltoys.algorithms.representations.actions.Action;
import rltoys.environments.envio.RLAgent;
import rltoys.environments.envio.observations.TRStep;
import rltoys.environments.envio.problems.RLProblem;
import rltoys.experiments.parametersweep.parameters.Parameters;
import rltoys.experiments.parametersweep.reinforcementlearning.AgentFactory;

@SuppressWarnings("serial")
class RLAgentFactoryTest implements AgentFactory {
  final Action agentAction;
  final int divergeAfter;
  private static final long serialVersionUID = 1L;

  RLAgentFactoryTest(int divergeAfter, Action agentAction) {
    this.agentAction = agentAction;
    this.divergeAfter = divergeAfter;
  }

  @Override
  public String label() {
    return "Agent";
  }

  @Override
  public RLAgent createAgent(RLProblem problem, Parameters parameters, long seed) {
    return new RLAgent() {
      @Override
      public Action getAtp1(TRStep step) {
        return step.time > divergeAfter ? null : agentAction;
      }
    };
  }
}