package rltoys.environments.pendulum;

import java.util.Random;

import junit.framework.Assert;

import org.junit.Test;

import rltoys.algorithms.representations.actions.Action;
import rltoys.environments.envio.RLAgent;
import rltoys.environments.envio.actions.ActionArray;
import rltoys.environments.envio.observations.TRStep;

@SuppressWarnings("serial")
public class SwingPendulumTest {
  private TRStep runProblem(SwingPendulum problem, RLAgent agent) {
    TRStep step = problem.initialize();
    while (!step.isEpisodeEnding()) {
      step = problem.step(agent.getAtp1(step));
      if (step.time == 2000)
        step = problem.endEpisode();
      if (!step.isEpisodeEnding()) {
        Assert.assertTrue(SwingPendulum.thetaRange.in(step.o_tp1[0]));
        Assert.assertTrue(SwingPendulum.velocityRange.in(step.o_tp1[1]));
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
