package rltoys.experiments.continuousaction;

import rltoys.algorithms.learning.control.actorcritic.onpolicy.ActorCritic;
import rltoys.algorithms.representations.actions.Action;
import rltoys.algorithms.representations.tilescoding.TileCoders;
import rltoys.environments.envio.control.ControlLearner;
import rltoys.environments.envio.observations.TRStep;
import rltoys.environments.envio.problems.RLProblem;
import rltoys.math.vector.RealVector;
import zephyr.plugin.core.api.labels.Labeled;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;

public class SwingPendulumExperiment implements Labeled {
  public final TileCoders tileCoders;
  @Monitor
  public final ControlLearner control;
  private TRStep step;
  private RealVector x_t = null;
  @Monitor
  protected double reward;
  @Monitor
  public final RLProblem environment;
  @Monitor
  private int nbEpisode = 0;

  public SwingPendulumExperiment(RLProblem environnment, ControlLearner actorCritic, TileCoders tileCoders) {
    this.control = actorCritic;
    this.tileCoders = tileCoders;
    this.environment = environnment;
    step = environnment.initialize();
  }

  public TRStep step() {
    if (step.time == 2000)
      step = environment.endEpisode();
    if (step.isEpisodeEnding()) {
      nbEpisode++;
      x_t = null;
      step = environment.initialize();
    }
    RealVector x_tp1 = tileCoders.project(step.o_tp1);
    Action a_tp1 = control.step(x_t, step.a_t, x_tp1, step.r_tp1);
    step = environment.step(a_tp1);
    reward = step.r_tp1;
    x_t = x_tp1;
    return step;
  }

  static public boolean checkActorCritic(RLProblem environnment, ControlLearner actorCritic, TileCoders tileCoders,
      int maxNbEpisode) {
    SwingPendulumExperiment experiment = new SwingPendulumExperiment(environnment, actorCritic, tileCoders);
    double cumulativeReward = 0.0;
    while (experiment.nbEpisode < maxNbEpisode) {
      if (experiment.step.isEpisodeStarting())
        cumulativeReward = -1;
      experiment.step();
      cumulativeReward += experiment.reward;
      if (cumulativeReward > 0.0)
        return true;
    }
    return false;
  }

  public ControlLearner control() {
    return control;
  }

  @Override
  public String label() {
    return ((ActorCritic) control()).actor().policy().getClass().getSimpleName();
  }
}
