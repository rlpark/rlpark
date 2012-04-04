package rlpark.plugin.rltoys.experiments.reinforcementlearning;


import rlpark.plugin.rltoys.agents.RLAgent;
import rlpark.plugin.rltoys.envio.actions.Action;
import rlpark.plugin.rltoys.envio.observations.TRStep;
import rlpark.plugin.rltoys.envio.problems.RLProblem;
import rlpark.plugin.rltoys.experiments.parametersweep.parameters.Parameters;
import rlpark.plugin.rltoys.experiments.parametersweep.reinforcementlearning.AgentFactory;

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