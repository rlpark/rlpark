package rltoys.environments.continuousgridworld;

public interface TerminationFunction {
  boolean isTerminated(double[] position);
}
