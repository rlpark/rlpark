package rlpark.plugin.rltoys.experiments.parametersweep;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import rlpark.plugin.rltoys.experiments.helpers.ExperimentCounter;
import rlpark.plugin.rltoys.experiments.parametersweep.interfaces.Context;
import rlpark.plugin.rltoys.experiments.parametersweep.interfaces.JobWithParameters;
import rlpark.plugin.rltoys.experiments.parametersweep.interfaces.SweepDescriptor;
import rlpark.plugin.rltoys.experiments.parametersweep.internal.ParametersLogFileReader;
import rlpark.plugin.rltoys.experiments.parametersweep.internal.ParametersLogFileWriter;
import rlpark.plugin.rltoys.experiments.parametersweep.parameters.FrozenParameters;
import rlpark.plugin.rltoys.experiments.parametersweep.parameters.Parameters;
import rlpark.plugin.rltoys.experiments.scheduling.interfaces.JobDoneEvent;
import rlpark.plugin.rltoys.experiments.scheduling.interfaces.JobPool;
import rlpark.plugin.rltoys.experiments.scheduling.interfaces.JobPool.JobPoolListener;
import rlpark.plugin.rltoys.experiments.scheduling.interfaces.PoolResult;
import rlpark.plugin.rltoys.experiments.scheduling.interfaces.Scheduler;
import rlpark.plugin.rltoys.experiments.scheduling.pools.FileJobPool;
import rlpark.plugin.rltoys.experiments.scheduling.pools.PoolResults;
import rlpark.plugin.rltoys.experiments.scheduling.schedulers.LocalScheduler;
import zephyr.plugin.core.api.signals.Listener;

public class SweepAll {
  static private boolean verbose = true;
  private final Scheduler scheduler;
  private final PoolResults poolResults = new PoolResults();
  int nbJobs;


  public SweepAll() {
    this(new LocalScheduler());
  }

  public SweepAll(Scheduler scheduler) {
    this.scheduler = scheduler;
  }

  private void createAndSubmitRequiredJobs(SweepDescriptor sweepDescriptor, ExperimentCounter counter, Context context,
      ParametersLogFileWriter logFile) {
    List<Parameters> allParameters = sweepDescriptor.provideParameters(context);
    String[] parameterLabels = allParameters.get(0).labels();
    Set<FrozenParameters> doneParameters = new HashSet<FrozenParameters>(extractParameters(logFile.filepath,
                                                                                           parameterLabels));
    List<Runnable> todoJobList = new ArrayList<Runnable>();
    for (Parameters parameters : allParameters) {
      if (!doneParameters.contains(parameters.froze()))
        todoJobList.add(context.createJob(parameters, counter));
    }
    if (todoJobList.size() == 0) {
      logFile.reorganizeLogFile(parameterLabels);
      return;
    }
    println(String.format("Submitting %d/%d jobs for %s...", todoJobList.size(), allParameters.size(),
                          extractName(logFile)));
    submitRequiredJob(logFile, parameterLabels, todoJobList);
  }

  private List<FrozenParameters> extractParameters(String filepath, String[] parameterLabels) {
    ParametersLogFileReader logFile = new ParametersLogFileReader(filepath);
    return logFile.extractParameters(parameterLabels);
  }

  private void submitRequiredJob(ParametersLogFileWriter logFile, String[] parameterLabels, List<Runnable> todoJobList) {
    Listener<JobDoneEvent> jobListener = createJobListener(logFile);
    JobPoolListener poolListener = createPoolListener(logFile, parameterLabels);
    JobPool pool = new FileJobPool(extractName(logFile), poolListener, jobListener);
    for (Runnable job : todoJobList)
      pool.add(job);
    PoolResult poolResult = pool.submitTo(scheduler);
    poolResults.add(poolResult);
  }

  private String extractName(ParametersLogFileWriter logFile) {
    File file = new File(logFile.filepath);
    File algoNameParentFile = file.getParentFile();
    File problemNameParentFile = algoNameParentFile.getParentFile();
    return String.format("%s/%s/%s", problemNameParentFile.getName(), algoNameParentFile.getName(), file.getName());
  }

  private JobPoolListener createPoolListener(final ParametersLogFileWriter logFile, final String[] parameterLabels) {
    return new JobPoolListener() {
      @Override
      public void listen(JobPool eventInfo) {
        logFile.reorganizeLogFile(parameterLabels);
      }
    };
  }

  private Listener<JobDoneEvent> createJobListener(final ParametersLogFileWriter logFile) {
    return new Listener<JobDoneEvent>() {
      @Override
      public void listen(JobDoneEvent eventInfo) {
        Parameters doneParameters = ((JobWithParameters) eventInfo.done).parameters();
        logFile.appendParameters(doneParameters);
        nbJobs++;
      }
    };
  }

  private void println(String message) {
    if (!verbose)
      return;
    System.out.println(message);
  }

  public void runSweep(SweepDescriptor sweepDescriptor, ExperimentCounter counter) {
    submitSweep(sweepDescriptor, counter);
    runAll();
  }

  public void runAll() {
    startScheduler();
    waitAll();
  }

  public void dispose() {
    scheduler.dispose();
  }

  public void waitAll() {
    poolResults.waitPools();
    scheduler.waitAll();
  }

  public void startScheduler() {
    scheduler.start();
  }

  public void submitSweep(SweepDescriptor sweepDescriptor, ExperimentCounter counter) {
    println(String.format("Submitting sweep in %s...", counter.folder.getPath()));
    counter.reset();
    while (counter.hasNext()) {
      counter.nextExperiment();
      submitOneSweep(sweepDescriptor, counter);
    }
  }

  private void submitOneSweep(SweepDescriptor sweepDescriptor, ExperimentCounter counter) {
    List<? extends Context> contexts = sweepDescriptor.provideContexts();
    for (Context context : contexts) {
      String filename = counter.folderFilename(context.folderPath(), context.fileName());
      ParametersLogFileWriter logFile = new ParametersLogFileWriter(filename);
      createAndSubmitRequiredJobs(sweepDescriptor, counter, context, logFile);
    }
  }

  public int nbJobs() {
    return nbJobs;
  }

  static public void disableVerbose() {
    verbose = false;
  }

  public Scheduler scheduler() {
    return scheduler;
  }
}
