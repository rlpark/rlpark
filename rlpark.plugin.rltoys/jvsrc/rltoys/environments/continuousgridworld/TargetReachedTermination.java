package rltoys.environments.continuousgridworld;

public class TargetReachedTermination implements TerminationFunction {
  private final double[] target;
  private final double tolerance;

  public TargetReachedTermination(double[] target, double tolerance) {
    this.target = target;
    this.tolerance = tolerance;
  }

  @Override
  public boolean isTerminated(double[] position) {
    double distance = 0.0;
    assert position.length == target.length;
    for (int i = 0; i < position.length; i++) {
      double diff = target[i] - position[i];
      distance += diff * diff;
    }
    distance = Math.sqrt(distance);
    return distance < tolerance;
  }
}
