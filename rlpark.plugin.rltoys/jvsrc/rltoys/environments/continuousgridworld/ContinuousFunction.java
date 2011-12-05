package rltoys.environments.continuousgridworld;

public interface ContinuousFunction {
  double fun(double[] position);

  int nbDimension();
}
