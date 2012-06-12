package rlpark.plugin.rltoys.experiments.parametersweep;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import rlpark.plugin.rltoys.experiments.helpers.ExperimentCounter;
import rlpark.plugin.rltoys.experiments.parametersweep.interfaces.Context;
import rlpark.plugin.rltoys.experiments.parametersweep.interfaces.SweepDescriptor;
import rlpark.plugin.rltoys.experiments.parametersweep.onpolicy.AbstractContextOnPolicy;
import rlpark.plugin.rltoys.experiments.parametersweep.parameters.FrozenParameters;
import rlpark.plugin.rltoys.experiments.parametersweep.parameters.Parameters;
import rlpark.plugin.rltoys.experiments.parametersweep.parameters.RunInfo;
import rlpark.plugin.rltoys.experiments.scheduling.schedulers.LocalScheduler;
import rlpark.plugin.rltoys.experiments.scheduling.schedulers.Schedulers;

public class SweepSelected {
  private final SweepDescriptor sweepDescriptor;
  private final ExperimentCounter counter;
  private final LocalScheduler scheduler = new LocalScheduler();
  private final List<FrozenParameters> todoParameters;

  public SweepSelected(List<FrozenParameters> todoParameters, SweepDescriptor sweepDescriptor, ExperimentCounter counter) {
    this.counter = counter;
    this.sweepDescriptor = sweepDescriptor;
    this.todoParameters = todoParameters;
  }

  private List<Parameters> createJobsDescription(Context context) {
    Set<FrozenParameters> contextTodoParameters = selectConsistentParameters(context);
    ArrayList<Parameters> result = new ArrayList<Parameters>();
    if (contextTodoParameters.isEmpty())
      return result;
    List<Parameters> allParameters = sweepDescriptor.provideParameters(context);
    for (Parameters parameters : allParameters) {
      if (contextTodoParameters.contains(parameters.froze()))
        result.add(parameters);
    }
    return result;
  }

  private Set<FrozenParameters> selectConsistentParameters(Context context) {
    AbstractContextOnPolicy onPolicyContext = (AbstractContextOnPolicy) context;
    String algorithmLabel = onPolicyContext.agentFactory().label();
    String problemLabel = onPolicyContext.problemFactory().label();
    Set<FrozenParameters> selected = new LinkedHashSet<FrozenParameters>();
    for (FrozenParameters parameters : todoParameters)
      if (parameters.hasFlag(problemLabel) && parameters.hasFlag(algorithmLabel)) {
        Parameters parametersCompleted = new Parameters(parameters);
        onPolicyContext.problemFactory().setExperimentParameters(parametersCompleted);
        selected.add(parametersCompleted.froze());
      }
    return selected;
  }

  public void generateLearningCurve() {
    System.out.println("Preparing job descriptions...");
    Map<Context, List<Parameters>> descriptions = new LinkedHashMap<Context, List<Parameters>>();
    for (Context context : sweepDescriptor.provideContexts())
      descriptions.put(context, createJobsDescription(context));
    List<Runnable> jobs = new ArrayList<Runnable>();
    while (counter.hasNext()) {
      counter.nextExperiment();
      for (Map.Entry<Context, List<Parameters>> entry : descriptions.entrySet())
        for (Parameters parameters : entry.getValue())
          jobs.add(entry.getKey().createJob(parameters, counter));
    }
    Schedulers.addAll(scheduler, jobs, null);
    scheduler.start();
    scheduler.waitAll();
    scheduler.dispose();
  }

  public static List<FrozenParameters> toParametersList(String[] args) {
    List<FrozenParameters> results = new ArrayList<FrozenParameters>();
    for (String arg : args) {
      String[] components = arg.split("_");
      if (components.length == 1)
        continue;
      RunInfo infos = new RunInfo();
      infos.enableFlag(components[0]);
      infos.enableFlag(components[1]);
      Parameters parameters = new Parameters(infos);
      for (int i = 1; i < components.length / 2; i++)
        parameters.putSweepParam(components[i * 2], Double.parseDouble(components[i * 2 + 1]));
      results.add(parameters.froze());
    }
    return results;
  }
}
