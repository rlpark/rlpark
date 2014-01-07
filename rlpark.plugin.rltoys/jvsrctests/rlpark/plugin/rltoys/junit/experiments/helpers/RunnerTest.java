package rlpark.plugin.rltoys.junit.experiments.helpers;

import org.junit.Assert;
import org.junit.Test;

import rlpark.plugin.rltoys.envio.actions.Action;
import rlpark.plugin.rltoys.envio.observations.Legend;
import rlpark.plugin.rltoys.envio.rl.RLAgent;
import rlpark.plugin.rltoys.envio.rl.TRStep;
import rlpark.plugin.rltoys.experiments.runners.AbstractRunner;
import rlpark.plugin.rltoys.experiments.runners.Runner;
import rlpark.plugin.rltoys.problems.RLProblem;
import zephyr.plugin.core.api.signals.Listener;

@SuppressWarnings("serial")
public class RunnerTest {
  static private final double Reward = 10.0;

  final class TestEnvironment implements RLProblem {
    private TRStep step;

    @Override
    public TRStep initialize() {
      step = new TRStep(obs(), 0);
      return step;
    }

    private double[] obs() {
      return new double[] { 1.0 };
    }

    @Override
    public TRStep step(Action a_t) {
      step = new TRStep(step, a_t, obs(), Reward);
      return step;
    }

    @Override
    public TRStep forceEndEpisode() {
      step = step.createEndingStep();
      return step;
    }

    @Override
    public TRStep lastStep() {
      return step;
    }

    @Override
    public Legend legend() {
      return null;
    }
  }

  private final class TestEnvironmentFinalReward implements RLProblem {
    private TRStep step;
    final int nbTimeStep;

    public TestEnvironmentFinalReward(int nbTimeStep) {
      this.nbTimeStep = nbTimeStep;
    }

    @Override
    public TRStep initialize() {
      step = new TRStep(obs(), 0);
      return step;
    }

    private double[] obs() {
      return new double[] { 1.0 };
    }

    @Override
    public TRStep step(Action a_t) {
      step = new TRStep(step, a_t, obs(), 0);
      if (nbTimeStep > 0 && step.time == nbTimeStep)
        step = forceEndEpisode();
      return step;
    }

    @Override
    public TRStep forceEndEpisode() {
      step = new TRStep(step.time, step.o_t, step.a_t, step.o_tp1, Reward, true);
      return step;
    }

    @Override
    public TRStep lastStep() {
      return step;
    }

    @Override
    public Legend legend() {
      return null;
    }
  }

  private final RLAgent agent = new RLAgent() {
    @Override
    public Action getAtp1(TRStep step) {
      return new Action() {
      };
    }
  };

  @Test
  public void testRunner() {
    TestEnvironment environment = new TestEnvironment();
    testRunner(environment, 1, 1);
    testRunner(environment, 10, 12);
  }

  @Test
  public void testEpisodeRunner() {
    final int NbTimeSteps = 10;
    TestEnvironment environment = new TestEnvironment();
    Runner runner = new Runner(environment, agent, -1, NbTimeSteps);
    runner.runEpisode();
    AbstractRunner.RunnerEvent runnerEvent = runner.runnerEvent();
    Assert.assertEquals(Reward * NbTimeSteps, runnerEvent.episodeReward, 0);
    Assert.assertEquals(NbTimeSteps, runnerEvent.step.time);
  }


  private void testRunner(TestEnvironment environment, final int nbEpisode, final int maxEpisodeTimeSteps) {
    Runner runner = new Runner(environment, agent, nbEpisode, maxEpisodeTimeSteps);
    final int[] nbTimeSteps = new int[1];
    runner.onTimeStep.connect(new Listener<AbstractRunner.RunnerEvent>() {
      @Override
      public void listen(AbstractRunner.RunnerEvent eventInfo) {
        nbTimeSteps[0]++;
      }
    });
    final int[] nbEpisodeTerminated = new int[1];
    runner.onEpisodeEnd.connect(new Listener<AbstractRunner.RunnerEvent>() {
      @Override
      public void listen(AbstractRunner.RunnerEvent eventInfo) {
        Assert.assertEquals(maxEpisodeTimeSteps + 1, nbTimeSteps[0]);
        Assert.assertEquals(Reward * maxEpisodeTimeSteps, eventInfo.episodeReward, 0.0);
        nbTimeSteps[0] = 0;
        nbEpisodeTerminated[0]++;
      }
    });
    runner.run();
    Assert.assertEquals(nbEpisode, nbEpisodeTerminated[0]);
  }

  @Test
  public void testRunnerEndEpisode() {
    testRunnerEndEpisode(new TestEnvironmentFinalReward(5), 10);
  }

  private void testRunnerEndEpisode(final TestEnvironmentFinalReward environment, final int nbEpisode) {
    Runner runner = new Runner(environment, agent, nbEpisode, -1);
    final int[] nbTimeSteps = new int[1];
    runner.onTimeStep.connect(new Listener<AbstractRunner.RunnerEvent>() {
      @Override
      public void listen(AbstractRunner.RunnerEvent eventInfo) {
        nbTimeSteps[0]++;
      }
    });
    final int[] nbEpisodeTerminated = new int[1];
    runner.onEpisodeEnd.connect(new Listener<AbstractRunner.RunnerEvent>() {
      @Override
      public void listen(AbstractRunner.RunnerEvent eventInfo) {
        Assert.assertEquals(environment.nbTimeStep + 1, nbTimeSteps[0]);
        Assert.assertEquals(Reward, eventInfo.episodeReward, 0.0);
        nbTimeSteps[0] = 0;
        nbEpisodeTerminated[0]++;
      }
    });
    runner.run();
    Assert.assertEquals(nbEpisode, nbEpisodeTerminated[0]);
  }
}
