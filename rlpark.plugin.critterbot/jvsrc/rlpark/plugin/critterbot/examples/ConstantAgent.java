package rlpark.plugin.critterbot.examples;

import rlpark.plugin.critterbot.actions.CritterbotAction;
import rlpark.plugin.critterbot.environment.CritterbotEnvironment;
import rlpark.plugin.rltoys.agents.Agent;
import rlpark.plugin.rltoys.envio.actions.Action;
import zephyr.plugin.core.api.synchronization.Clock;

public class ConstantAgent implements Runnable, Agent {
  private final Clock clock = new Clock("ConstantAgent");
  private final CritterbotAction action;
  private final CritterbotEnvironment environment;

  public ConstantAgent(CritterbotEnvironment environment) {
    this(environment, CritterbotAction.DoNothing);
  }

  public ConstantAgent(CritterbotEnvironment environment, CritterbotAction action) {
    this.environment = environment;
    this.action = action;
  }

  @Override
  public Action getAtp1(double[] obs) {
    return action;
  }

  @Override
  public void run() {
    while (clock.tick() && !environment.isClosed())
      environment.sendAction(action);
  }
}
