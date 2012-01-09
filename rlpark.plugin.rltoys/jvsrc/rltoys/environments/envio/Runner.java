package rltoys.environments.envio;

import java.io.Serializable;

import rltoys.algorithms.representations.actions.Action;
import rltoys.environments.envio.observations.TRStep;
import rltoys.environments.envio.problems.RLProblem;
import zephyr.plugin.core.api.monitoring.abstracts.DataMonitor;
import zephyr.plugin.core.api.monitoring.abstracts.MonitorContainer;
import zephyr.plugin.core.api.monitoring.abstracts.Monitored;
import zephyr.plugin.core.api.signals.Signal;

public class Runner implements Serializable, MonitorContainer {
  private static final long serialVersionUID = 465593140388569561L;

  static public class RunnerEvent {
    public int nbTotalTimeSteps = 0;
    public int episode = -1;
    public TRStep step = null;
    public double episodeReward = Double.NaN;

    @Override
    public String toString() {
      return String.format("Ep(%d): %s on %d", episode, step, nbTotalTimeSteps);
    }
  }

  public final Signal<RunnerEvent> onEpisodeEnd = new Signal<RunnerEvent>();
  public final Signal<RunnerEvent> onTimeStep = new Signal<RunnerEvent>();
  protected final RunnerEvent runnerEvent = new RunnerEvent();
  private final RLAgent agent;
  private final RLProblem environment;
  private final int maxEpisodeTimeSteps;
  private final int nbEpisode;

  public Runner(RLProblem environment, RLAgent agent) {
    this(environment, agent, 1, -1);
  }

  public Runner(RLProblem environment, RLAgent agent, int nbEpisode, int maxEpisodeTimeSteps) {
    this.environment = environment;
    this.agent = agent;
    this.nbEpisode = nbEpisode;
    this.maxEpisodeTimeSteps = maxEpisodeTimeSteps;
  }

  public RunnerEvent run() {
    assert runnerEvent.nbTotalTimeSteps == 0;
    assert runnerEvent.episode == -1;
    while (runnerEvent.episode < nbEpisode - 1)
      runEpisode();
    return runnerEvent;
  }

  public void runEpisode() {
    assert runnerEvent.step == null || runnerEvent.step.isEpisodeEnding();
    do {
      step();
    } while (!runnerEvent.step.isEpisodeEnding());
  }

  public void step() {
    // This code guarantee that o_tp1 and x_tp1 are non null
    assert runnerEvent.episode < nbEpisode;
    if (runnerEvent.step == null || runnerEvent.step.isEpisodeEnding()) {
      runnerEvent.step = environment.initialize();
      assert runnerEvent.step.isEpisodeStarting();
      runnerEvent.episode += 1;
      runnerEvent.episodeReward = 0;
    }
    onTimeStep.fire(runnerEvent);
    Action action = agent.getAtp1(runnerEvent.step);
    runnerEvent.step = environment.step(action);
    if (runnerEvent.step.time == maxEpisodeTimeSteps)
      runnerEvent.step = runnerEvent.step.createEndingStep();
    if (!runnerEvent.step.isEpisodeEnding()) {
      runnerEvent.episodeReward += runnerEvent.step.r_tp1;
      runnerEvent.nbTotalTimeSteps++;
    } else
      onEpisodeEnd.fire(runnerEvent);
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
