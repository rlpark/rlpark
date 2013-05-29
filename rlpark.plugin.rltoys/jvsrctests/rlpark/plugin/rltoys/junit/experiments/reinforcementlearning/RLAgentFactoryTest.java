package rlpark.plugin.rltoys.junit.experiments.reinforcementlearning;


import rlpark.plugin.rltoys.agents.representations.RepresentationFactory;
import rlpark.plugin.rltoys.envio.actions.Action;
import rlpark.plugin.rltoys.envio.rl.RLAgent;
import rlpark.plugin.rltoys.envio.rl.TRStep;
import rlpark.plugin.rltoys.experiments.parametersweep.parameters.Parameters;
import rlpark.plugin.rltoys.experiments.parametersweep.reinforcementlearning.AgentFactory;
import rlpark.plugin.rltoys.problems.RLProblem;

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
  public RLAgent createAgent(long seed, RLProblem problem, Parameters parameters, RepresentationFactory representationFactory) {
    return new RLAgent() {
      @Override
      public Action getAtp1(TRStep step) {
        return step.time > divergeAfter ? null : agentAction;
      }
    };
  }
}