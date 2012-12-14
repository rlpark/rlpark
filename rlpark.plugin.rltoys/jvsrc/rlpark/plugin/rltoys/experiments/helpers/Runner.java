package rlpark.plugin.rltoys.experiments.helpers;

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

public class Runner implements Serializable, MonitorContainer {
  private static final long serialVersionUID = 465593140388569561L;

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

  public final Signal<RunnerEvent> onEpisodeEnd = new Signal<RunnerEvent>();
  public final Signal<RunnerEvent> onTimeStep = new Signal<RunnerEvent>();
  protected final RunnerEvent runnerEvent = new RunnerEvent();
  @Monitor
  private final RLAgent agent;
  @Monitor
  private final RLProblem problem;
  private Action agentAction = null;
  private final int maxEpisodeTimeSteps;
  private final int nbEpisode;

  public Runner(RLProblem problem, RLAgent agent) {
    this(problem, agent, 1, -1);
  }

  public Runner(RLProblem environment, RLAgent agent, int nbEpisode, int maxEpisodeTimeSteps) {
    this.problem = environment;
    this.agent = agent;
    this.nbEpisode = nbEpisode;
    this.maxEpisodeTimeSteps = maxEpisodeTimeSteps;
  }

  public void run() {
    for (int i = 0; i < nbEpisode; i++)
      runEpisode();
  }

  public void runEpisode() {
    assert runnerEvent.step == null || runnerEvent.step.isEpisodeEnding();
    int currentEpisode = runnerEvent.nbEpisodeDone;
    do {
      step();
    } while (currentEpisode == runnerEvent.nbEpisodeDone);
    assert runnerEvent.step.isEpisodeEnding();
  }

  public void step() {
    assert nbEpisode < 0 || runnerEvent.nbEpisodeDone < nbEpisode;
    if (runnerEvent.step == null || runnerEvent.step.isEpisodeEnding()) {
      runnerEvent.step = problem.initialize();
      runnerEvent.episodeReward = 0;
      agentAction = null;
      assert runnerEvent.step.isEpisodeStarting();
    } else {
      runnerEvent.step = problem.step(agentAction);
      if (runnerEvent.step.time == maxEpisodeTimeSteps)
        runnerEvent.step = problem.forceEndEpisode();
    }
    agentAction = agent.getAtp1(runnerEvent.step);
    runnerEvent.episodeReward += runnerEvent.step.r_tp1;
    runnerEvent.nbTotalTimeSteps++;
    onTimeStep.fire(runnerEvent);
    if (runnerEvent.step.isEpisodeEnding()) {
      runnerEvent.nbEpisodeDone += 1;
      onEpisodeEnd.fire(runnerEvent);
    }
  }

  public RunnerEvent runnerEvent() {
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
}
