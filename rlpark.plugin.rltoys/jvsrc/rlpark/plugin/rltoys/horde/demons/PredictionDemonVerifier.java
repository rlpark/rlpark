package rlpark.plugin.rltoys.horde.demons;

import java.io.Serializable;

import rlpark.plugin.rltoys.algorithms.predictions.td.OnPolicyTD;
import rlpark.plugin.rltoys.algorithms.predictions.td.TD;
import rlpark.plugin.rltoys.algorithms.predictions.td.TDErrorMonitor;
import rlpark.plugin.rltoys.algorithms.predictions.td.TDLambdaAutostep;
import rlpark.plugin.rltoys.horde.functions.RewardFunction;
import rlpark.plugin.rltoys.utils.NotImplemented;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;

public class PredictionDemonVerifier implements Serializable {
  private static final long serialVersionUID = 6127406364376542150L;
  private final PredictionDemon predictionDemon;
  private final RewardFunction rewardFunction;
  @Monitor
  private final TDErrorMonitor errorMonitor;

  public PredictionDemonVerifier(PredictionDemon predictionDemon) {
    this(predictionDemon, 0.01);
  }

  public PredictionDemonVerifier(PredictionDemon predictionDemon, double precision) {
    this.predictionDemon = predictionDemon;
    rewardFunction = predictionDemon.rewardFunction();
    double gamma = extractGamma(predictionDemon.predicter());
    errorMonitor = new TDErrorMonitor(gamma, precision);
  }

  public double extractGamma(OnPolicyTD learner) {
    if (learner instanceof TD)
      return ((TD) learner).gamma();
    if (learner instanceof TDLambdaAutostep)
      return ((TDLambdaAutostep) learner).gamma();
    throw new NotImplemented();
  }

  public TDErrorMonitor errorMonitor() {
    return errorMonitor;
  }

  public double update(boolean endOfEpisode) {
    return errorMonitor.update(predictionDemon.prediction(), rewardFunction.reward(), endOfEpisode);
  }
}
