package rltoys.environments.nostate;

import static rltoys.utils.Utils.square;
import rltoys.algorithms.representations.actions.Action;
import rltoys.environments.envio.actions.ActionArray;
import rltoys.environments.envio.observations.Legend;
import rltoys.environments.envio.observations.TRStep;
import rltoys.environments.envio.problems.RLProblem;
import rltoys.math.ranges.Range;

public class NoStateProblem implements RLProblem {
  public interface NoStateRewardFunction {
    double reward(double action);
  }

  public static class NormalReward implements NoStateRewardFunction {
    public final double mu;
    private final double sigma;

    public NormalReward(double mu, double sigma) {
      this.mu = mu;
      this.sigma = sigma;
    }

    @Override
    public double reward(double x) {
      return 1.0 / Math.sqrt(2 * Math.PI * square(sigma)) * Math.exp(-square(x - mu) / (2 * square(sigma)));
    }
  }

  private TRStep current = null;
  private final NoStateRewardFunction reward;
  public final Range range;
  private static final Legend legend = new Legend("State");

  public NoStateProblem(NoStateRewardFunction reward) {
    this(null, reward);
  }


  public NoStateProblem(Range range, NoStateRewardFunction reward) {
    this.reward = reward;
    this.range = range;
  }

  @Override
  public TRStep initialize() {
    current = new TRStep(state(), Double.NaN);
    return current;
  }

  private double[] state() {
    return new double[] { 1.0 };
  }


  @Override
  public TRStep step(Action a_t) {
    assert current != null;
    if (a_t == null)
      return new TRStep(current, null, null, -Double.MAX_VALUE);
    double a = ActionArray.toDouble(a_t);
    if (range != null)
      a = range.bound(a);
    double r = reward.reward(a);
    current = new TRStep(current, a_t, state(), r);
    return current;
  }

  @Override
  public Legend legend() {
    return legend;
  }


  @Override
  public TRStep lastStep() {
    return current;
  }
}
