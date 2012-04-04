package rlpark.plugin.rltoys.problems.continuousgridworld;

public interface TerminationFunction {
  boolean isTerminated(double[] position);
}
