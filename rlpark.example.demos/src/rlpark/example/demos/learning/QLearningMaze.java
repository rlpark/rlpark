package rlpark.example.demos.learning;

import java.util.Random;

import rlpark.plugin.rltoys.agents.rl.LearnerAgentFA;
import rlpark.plugin.rltoys.algorithms.control.ControlLearner;
import rlpark.plugin.rltoys.algorithms.control.acting.EpsilonGreedy;
import rlpark.plugin.rltoys.algorithms.control.qlearning.QLearning;
import rlpark.plugin.rltoys.algorithms.control.qlearning.QLearningControl;
import rlpark.plugin.rltoys.algorithms.functions.stateactions.TabularAction;
import rlpark.plugin.rltoys.algorithms.functions.states.Projector;
import rlpark.plugin.rltoys.algorithms.traces.RTraces;
import rlpark.plugin.rltoys.envio.policy.Policy;
import rlpark.plugin.rltoys.experiments.runners.Runner;
import rlpark.plugin.rltoys.experiments.runners.AbstractRunner.RunnerEvent;
import rlpark.plugin.rltoys.math.vector.implementations.PVector;
import rlpark.plugin.rltoys.problems.mazes.Maze;
import rlpark.plugin.rltoys.problems.mazes.MazeValueFunction;
import rlpark.plugin.rltoys.problems.mazes.Mazes;
import zephyr.plugin.core.api.Zephyr;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;
import zephyr.plugin.core.api.signals.Listener;
import zephyr.plugin.core.api.synchronization.Clock;

@Monitor
public class QLearningMaze implements Runnable {
  final MazeValueFunction mazeValueFunction;
  private final Maze problem = Mazes.createBookMaze();
  private final ControlLearner control;
  private final Clock clock = new Clock("QLearningMaze");
  private final Projector projector;
  private final PVector occupancy;
  private final LearnerAgentFA agent;

  public QLearningMaze() {
    projector = problem.getMarkovProjector();
    occupancy = new PVector(projector.vectorSize());
    TabularAction toStateAction = new TabularAction(problem.actions(), projector.vectorNorm(), projector.vectorSize());
    double alpha = .15 / projector.vectorNorm();
    double gamma = 1.0;
    double lambda = 0.6;
    QLearning qlearning = new QLearning(problem.actions(), alpha, gamma, lambda, toStateAction, new RTraces());
    double epsilon = 0.3;
    Policy acting = new EpsilonGreedy(new Random(0), problem.actions(), toStateAction, qlearning, epsilon);
    control = new QLearningControl(acting, qlearning);
    agent = new LearnerAgentFA(control, projector);
    mazeValueFunction = new MazeValueFunction(problem, qlearning, toStateAction, qlearning.greedy());
    Zephyr.advertise(clock, this);
  }

  @Override
  public void run() {
    Runner runner = new Runner(problem, agent);
    runner.onEpisodeEnd.connect(new Listener<RunnerEvent>() {
      @Override
      public void listen(RunnerEvent eventInfo) {
        System.out.println(String.format("Episode %d: %d steps", eventInfo.nbEpisodeDone, eventInfo.step.time));
      }
    });
    while (clock.tick()) {
      runner.step();
      occupancy.addToSelf(agent.lastState());
    }
  }

  public static void main(String[] args) {
    new QLearningMaze().run();
  }
}
