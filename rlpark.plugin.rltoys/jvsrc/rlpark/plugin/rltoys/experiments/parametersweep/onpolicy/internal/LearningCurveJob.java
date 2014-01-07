package rlpark.plugin.rltoys.experiments.parametersweep.onpolicy.internal;

import java.io.IOException;
import java.io.Serializable;

import rlpark.plugin.rltoys.experiments.helpers.ExperimentCounter;
import rlpark.plugin.rltoys.experiments.parametersweep.parameters.Parameters;
import rlpark.plugin.rltoys.experiments.parametersweep.reinforcementlearning.RLParameters;
import rlpark.plugin.rltoys.experiments.parametersweep.reinforcementlearning.ReinforcementLearningContext;
import rlpark.plugin.rltoys.experiments.runners.AbstractRunner;
import zephyr.plugin.core.api.internal.monitoring.fileloggers.LoggerRow;
import zephyr.plugin.core.api.signals.Listener;

@SuppressWarnings("restriction")
public class LearningCurveJob implements Runnable, Serializable {
  private static final long serialVersionUID = -5212166519929349880L;
  private final Parameters parameters;
  private final ReinforcementLearningContext context;
  private final ExperimentCounter counter;

  public LearningCurveJob(ReinforcementLearningContext context, Parameters parameters, ExperimentCounter counter) {
    this.context = context;
    this.parameters = parameters;
    this.counter = counter.clone();
  }

  protected Listener<AbstractRunner.RunnerEvent> createRewardListener(final LoggerRow loggerRow) {
    return new Listener<AbstractRunner.RunnerEvent>() {
      @Override
      public void listen(AbstractRunner.RunnerEvent eventInfo) {
        loggerRow.writeRow(eventInfo.step.time, eventInfo.step.r_tp1);
      }
    };
  }

  protected Listener<AbstractRunner.RunnerEvent> createEpisodeListener(final LoggerRow loggerRow) {
    return new Listener<AbstractRunner.RunnerEvent>() {
      @Override
      public void listen(AbstractRunner.RunnerEvent eventInfo) {
        loggerRow.writeRow(eventInfo.nbEpisodeDone, eventInfo.step.time);
      }
    };
  }

  protected void setupEpisodeListener(AbstractRunner runner, LoggerRow loggerRow) {
    loggerRow.writeLegend("Episode", "Steps");
    runner.onEpisodeEnd.connect(createEpisodeListener(loggerRow));
  }

  protected void setupRewardListener(AbstractRunner runner, LoggerRow loggerRow) {
    loggerRow.writeLegend("Time", "Reward");
    runner.onTimeStep.connect(createRewardListener(loggerRow));
  }

  @Override
  public void run() {
    AbstractRunner runner = context.createRunner(counter.currentIndex(), parameters);
    String fileName = counter.folderFilename(context.folderPath(), context.fileName());
    System.out.println(fileName);
    LoggerRow loggerRow = null;
    try {
      loggerRow = new LoggerRow(fileName, false);
    } catch (IOException e) {
      e.printStackTrace();
      return;
    }
    if (RLParameters.nbEpisode(parameters) == 1)
      setupRewardListener(runner, loggerRow);
    else
      setupEpisodeListener(runner, loggerRow);
    runner.run();
    loggerRow.close();
  }
}
