package rlpark.plugin.irobot.examples;

import rlpark.plugin.irobot.data.CreateAction;
import rlpark.plugin.irobot.robots.CreateRobot;
import rlpark.plugin.rltoys.agents.Agent;
import zephyr.plugin.core.api.synchronization.Clock;

public class ConstantAgent implements Agent {
  private final CreateAction action;

  public ConstantAgent() {
    this(null);
  }

  public ConstantAgent(CreateAction action) {
    this.action = action;
  }

  @Override
  public CreateAction getAtp1(double[] obs) {
    if (action != null)
      return action;
    return new CreateAction(20, 20);
  }

  @Override
  public String toString() {
    return action.toString();
  }

  public static void main(String[] args) {
    CreateRobot environment = new CreateRobot();
    ConstantAgent agent = new ConstantAgent();
    Clock clock = new Clock();
    while (clock.tick() && environment.isClosed())
      environment.sendAction(agent.getAtp1(environment.waitNewObs()));
  }
}
