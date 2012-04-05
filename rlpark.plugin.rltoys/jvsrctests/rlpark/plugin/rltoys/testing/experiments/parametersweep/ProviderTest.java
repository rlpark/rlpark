package rlpark.plugin.rltoys.testing.experiments.parametersweep;


import java.util.List;

import rlpark.plugin.rltoys.experiments.helpers.ExperimentCounter;
import rlpark.plugin.rltoys.experiments.parametersweep.interfaces.Context;
import rlpark.plugin.rltoys.experiments.parametersweep.interfaces.JobWithParameters;
import rlpark.plugin.rltoys.experiments.parametersweep.interfaces.SweepDescriptor;
import rlpark.plugin.rltoys.experiments.parametersweep.parameters.FrozenParameters;
import rlpark.plugin.rltoys.experiments.parametersweep.parameters.Parameters;
import rlpark.plugin.rltoys.experiments.parametersweep.parameters.RunInfo;
import rlpark.plugin.rltoys.utils.Utils;

public class ProviderTest implements SweepDescriptor, Context {
  private static final String SweepDone = "sweepDone";
  public static final String ParameterName = "Param";
  public static final String ContextPath = "providertest";

  static public class SweepJob implements JobWithParameters {
    private static final long serialVersionUID = 4238136913870025414L;
    private final Parameters parameters;
    private final int counter;

    SweepJob(Parameters parameters, ExperimentCounter counter) {
      this.parameters = parameters;
      this.counter = counter.currentIndex();
    }

    @Override
    public void run() {
      parameters.putResult(SweepDone, 1);
      parameters.putResult("counter", counter);
    }

    @Override
    public Parameters parameters() {
      return parameters;
    }
  }

  private static final long serialVersionUID = 7141220137708536488L;
  private static final String FlagLabel = "FlagLabel";
  private static final String InfoLabel = "InfoLabel";
  private static final double InfoValue = 3.456;
  private final int nbParameters;
  private final int nbValues;

  public ProviderTest(int nbValues, int nbParameters) {
    this.nbParameters = nbParameters;
    this.nbValues = nbValues;
  }

  @Override
  public List<? extends Context> provideContexts() {
    return Utils.asList((Context) this);
  }

  @Override
  public List<Parameters> provideParameters(Context context) {
    return createParameters(nbValues, nbParameters);
  }

  @Override
  public String folderPath() {
    return ContextPath;
  }

  @Override
  public String fileName() {
    return ExperimentCounter.DefaultFileName;
  }

  @Override
  public Runnable createJob(Parameters parameters, ExperimentCounter counter) {
    return new SweepJob(parameters, counter);
  }

  public static boolean parametersHasBeenDone(FrozenParameters parameters) {
    return parameters.get(SweepDone) == 1;
  }

  public static List<Parameters> createParameters(int nbValues, int nbParameters) {
    double[] parameterValues = new double[nbValues];
    for (int i = 0; i < parameterValues.length; i++)
      parameterValues[i] = i;
    RunInfo infos = createRunInfo();
    List<Parameters> result = Utils.asList(new Parameters(infos));
    for (int i = 0; i < nbParameters; i++)
      result = Parameters.combine(result, ParameterName + i, parameterValues);
    return result;
  }

  protected static RunInfo createRunInfo() {
    RunInfo infos = new RunInfo();
    infos.enableFlag(FlagLabel);
    infos.put(InfoLabel, InfoValue);
    return infos;
  }
}