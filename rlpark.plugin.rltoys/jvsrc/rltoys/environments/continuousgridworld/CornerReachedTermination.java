package rltoys.environments.continuousgridworld;

public class CornerReachedTermination implements TerminationFunction {
  private final double target;

  public CornerReachedTermination(double target) {
    this.target = target;
  }

  @Override
  public boolean isTerminated(double[] position) {
    for (double x : position)
      if (x < target)
        return false;
    return true;
  }
}
