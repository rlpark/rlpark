package rlpark.plugin.rltoys.junit.problems.pendulum;

import java.util.Random;

import org.junit.Assert;

import org.junit.Test;

import rlpark.plugin.rltoys.envio.actions.Action;
import rlpark.plugin.rltoys.envio.actions.ActionArray;
import rlpark.plugin.rltoys.envio.rl.RLAgent;
import rlpark.plugin.rltoys.envio.rl.TRStep;
import rlpark.plugin.rltoys.problems.pendulum.SwingPendulum;

@SuppressWarnings("serial")
public class SwingPendulumTest {
  private TRStep runProblem(SwingPendulum problem, RLAgent agent) {
    TRStep step = problem.initialize();
    while (!step.isEpisodeEnding()) {
      step = problem.step(agent.getAtp1(step));
      if (step.time == 2000)
        step = problem.forceEndEpisode();
      if (!step.isEpisodeEnding()) {
        Assert.assertTrue(SwingPendulum.ThetaRange.in(step.o_tp1[0]));
        Assert.assertTrue(SwingPendulum.VelocityRange.in(step.o_tp1[1]));
      }
    }
    return step;
  }

  @Test
  public void testZeroTorque() {
    SwingPendulum problem = new SwingPendulum(new Random(0));
    for (int i = 0; i < 10; i++) {
      TRStep finalStep = runProblem(problem, new RLAgent() {
        @Override
        public Action getAtp1(TRStep step) {
          return new ActionArray(0.0);
        }
      });
      Assert.assertEquals(Math.PI, Math.abs(finalStep.o_t[0]), 0.001);
      Assert.assertEquals(2000, finalStep.time);
    }
  }

  @Test
  public void testMaximumTorque() {
    SwingPendulum problem = new SwingPendulum(new Random(0));
    for (int i = 0; i < 10; i++)
      runProblem(problem, new RLAgent() {
        @Override
        public Action getAtp1(TRStep step) {
          return new ActionArray(Math.signum(step.o_tp1[1]) * SwingPendulum.uMax);
        }
      });
  }
}
