package rlpark.plugin.rltoys.problems.puddleworld;

public class TargetReachedL2NormTermination implements TerminationFunction {
  private final double[] target;
  private final double tolerance;

  public TargetReachedL2NormTermination(double[] target, double tolerance) {
    assert tolerance > 0;
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
