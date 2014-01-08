package rlpark.plugin.rltoys.experiments.runners;

import java.io.Serializable;

import rlpark.plugin.rltoys.envio.actions.Action;
import rlpark.plugin.rltoys.envio.rl.RLAgent;
import rlpark.plugin.rltoys.envio.rl.TRStep;
import rlpark.plugin.rltoys.problems.RLProblem;
import zephyr.plugin.core.api.monitoring.abstracts.DataMonitor;
import zephyr.plugin.core.api.monitoring.abstracts.MonitorContainer;
import zephyr.plugin.core.api.monitoring.abstracts.Monitored;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;
import zephyr.plugin.core.api.signals.Signal;

public abstract class AbstractRunner implements Serializable, MonitorContainer {
  private static final long serialVersionUID = 511454210699491736L;

  @SuppressWarnings("serial")
  static public class RunnerEvent implements Serializable {
    public int nbTotalTimeSteps = 0;
    public int nbEpisodeDone = 0;
    public TRStep step = null;
    public double episodeReward = Double.NaN;

    @Override
    public String toString() {
      return String.format("Ep(%d): %s on %d", nbEpisodeDone, step, nbTotalTimeSteps);
    }
  }


  public final Signal<AbstractRunner.RunnerEvent> onEpisodeEnd = new Signal<AbstractRunner.RunnerEvent>();
  public final Signal<AbstractRunner.RunnerEvent> onTimeStep = new Signal<AbstractRunner.RunnerEvent>();
  protected final AbstractRunner.RunnerEvent runnerEvent = new AbstractRunner.RunnerEvent();
  @Monitor
  private final RLAgent agent;
  @Monitor
  private final RLProblem problem;
  private Action agentAction = null;
  private final int maxEpisodeTimeSteps;

  public AbstractRunner(RLProblem environment, RLAgent agent, int maxEpisodeTimeSteps) {
    this.problem = environment;
    this.agent = agent;
    this.maxEpisodeTimeSteps = maxEpisodeTimeSteps;
  }

  abstract public void run();

  public void runEpisode() {
    assert runnerEvent.step == null || runnerEvent.step.isEpisodeEnding();
    int currentEpisode = runnerEvent.nbEpisodeDone;
    do {
      step();
    } while (currentEpisode == runnerEvent.nbEpisodeDone);
    assert runnerEvent.step.isEpisodeEnding();
  }

  public void step() {
    if (runnerEvent.step == null || runnerEvent.step.isEpisodeEnding()) {
      runnerEvent.step = problem.initialize();
      runnerEvent.episodeReward = 0;
      agentAction = null;
      assert runnerEvent.step.isEpisodeStarting();
    } else {
      runnerEvent.step = problem.step(agentAction);
    }
    agentAction = agent.getAtp1(runnerEvent.step);
    if (runnerEvent.step.time == maxEpisodeTimeSteps)
      runnerEvent.step = problem.forceEndEpisode();
    runnerEvent.episodeReward += runnerEvent.step.r_tp1;
    runnerEvent.nbTotalTimeSteps++;
    onTimeStep.fire(runnerEvent);
    if (runnerEvent.step.isEpisodeEnding()) {
      runnerEvent.nbEpisodeDone += 1;
      onEpisodeEnd.fire(runnerEvent);
    }
  }

  public AbstractRunner.RunnerEvent runnerEvent() {
    return runnerEvent;
  }

  public RLAgent agent() {
    return agent;
  }

  @Override
  public void addToMonitor(DataMonitor monitor) {
    monitor.add("Reward", new Monitored() {
      @Override
      public double monitoredValue() {
        if (runnerEvent.step == null)
          return 0;
        return runnerEvent.step.r_tp1;
      }
    });
  }

  public int nbEpisodeDone() {
    return runnerEvent.nbEpisodeDone;
  }
}
