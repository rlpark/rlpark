package rlpark.example.learning;

import java.util.Random;

import rltoys.algorithms.learning.control.acting.EpsilonGreedy;
import rltoys.algorithms.learning.control.sarsa.Sarsa;
import rltoys.algorithms.learning.control.sarsa.SarsaControl;
import rltoys.algorithms.representations.ValueFunction2D;
import rltoys.algorithms.representations.acting.Policy;
import rltoys.algorithms.representations.actions.Action;
import rltoys.algorithms.representations.actions.TabularAction;
import rltoys.algorithms.representations.tilescoding.TileCodersNoHashing;
import rltoys.algorithms.representations.traces.AMaxTraces;
import rltoys.environments.envio.observations.TRStep;
import rltoys.environments.mountaincar.MountainCar;
import rltoys.math.vector.BinaryVector;
import rltoys.math.vector.RealVector;
import zephyr.plugin.core.api.Zephyr;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;
import zephyr.plugin.core.api.synchronization.Clock;

@Monitor
public class SarsaMountainCar implements Runnable {
  final ValueFunction2D valueFunctionDisplay;
  private final MountainCar problem;
  private final SarsaControl control;
  private final TileCodersNoHashing projector;
  private final Clock clock = new Clock("SarsaMountainCar");

  public SarsaMountainCar() {
    problem = new MountainCar(null);
    projector = new TileCodersNoHashing(problem.getObservationRanges());
    projector.addFullTilings(10, 10);
    projector.includeActiveFeature();
    TabularAction toStateAction = new TabularAction(problem.actions(), projector.vectorNorm(), projector.vectorSize());
    double alpha = .2 / projector.vectorNorm();
    double gamma = 0.99;
    double lambda = .3;
    Sarsa sarsa = new Sarsa(alpha, gamma, lambda, toStateAction.vectorSize(), new AMaxTraces());
    double epsilon = 0.01;
    Policy acting = new EpsilonGreedy(new Random(0), problem.actions(), toStateAction, sarsa, epsilon);
    control = new SarsaControl(acting, toStateAction, sarsa);
    valueFunctionDisplay = new ValueFunction2D(projector, problem, sarsa);
    Zephyr.advertise(clock, this);
  }

  @Override
  public void run() {
    TRStep step = problem.initialize();
    int nbEpisode = 0;
    RealVector x_t = null;
    while (clock.tick()) {
      BinaryVector x_tp1 = projector.project(step.o_tp1);
      Action action = control.step(x_t, step.a_t, x_tp1, step.r_tp1);
      x_t = x_tp1;
      if (step.isEpisodeEnding()) {
        System.out.println(String.format("Episode %d: %d steps", nbEpisode, step.time));
        step = problem.initialize();
        x_t = null;
        nbEpisode++;
      } else
        step = problem.step(action);
    }
  }

  public static void main(String[] args) {
    new SarsaMountainCar().run();
  }
}
