package rlpark.plugin.rltoysview.tests.internal;

import java.util.Random;

import rlpark.plugin.rltoys.agents.rl.RandomAgent;
import rlpark.plugin.rltoys.envio.actions.Action;
import rlpark.plugin.rltoys.envio.actions.ActionArray;
import rlpark.plugin.rltoys.envio.rl.RLAgent;
import rlpark.plugin.rltoys.envio.rl.TRStep;
import rlpark.plugin.rltoys.experiments.runners.Runner;
import rlpark.plugin.rltoys.problems.ProblemDiscreteAction;
import rlpark.plugin.rltoys.problems.RLProblem;
import zephyr.plugin.core.api.Zephyr;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;
import zephyr.plugin.core.api.monitoring.annotations.Popup;
import zephyr.plugin.core.api.synchronization.Clock;

@Monitor
public class TestProblem implements Runnable, RLAgent {
  private static final long serialVersionUID = -7618574896704964858L;
  Random random = new Random(0);
  @Popup
  private final ProblemDiscreteAction problem;
  private final Clock clock;

  public TestProblem(ProblemDiscreteAction problem) {
    this.problem = problem;
    clock = new Clock(RLProblem.class.getSimpleName());
    Zephyr.advertise(clock, this);
  }

  @Override
  public void run() {
    RandomAgent agent = new RandomAgent(random, problem.actions());
    Runner runner = new Runner(problem, agent, 6000, -1);
    while (clock.tick())
      runner.step();
  }

  @Override
  public Action getAtp1(TRStep step) {
    return new ActionArray(.0, .0, random.nextDouble() * .2, .25);
  }
}
