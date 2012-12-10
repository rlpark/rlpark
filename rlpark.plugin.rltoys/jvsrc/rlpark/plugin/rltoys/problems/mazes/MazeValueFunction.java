package rlpark.plugin.rltoys.problems.mazes;

import rlpark.plugin.rltoys.algorithms.functions.Predictor;
import rlpark.plugin.rltoys.algorithms.functions.stateactions.StateToStateAction;
import rlpark.plugin.rltoys.algorithms.functions.states.Projector;
import rlpark.plugin.rltoys.envio.actions.Action;
import rlpark.plugin.rltoys.envio.policy.Policy;
import rlpark.plugin.rltoys.math.vector.RealVector;

public class MazeValueFunction implements MazeFunction {
  private final Maze maze;
  private final Predictor predictor;
  private final StateToStateAction toStateAction;
  private final Policy policy;
  private final Projector projector;

  public MazeValueFunction(Maze maze, Predictor predictor) {
    this(maze, predictor, null, null);
  }

  public MazeValueFunction(Maze maze, Predictor predictor, StateToStateAction toStateAction, Policy policy) {
    this.maze = maze;
    this.predictor = predictor;
    this.toStateAction = toStateAction;
    this.policy = policy;
    projector = maze.getMarkovProjector();
  }

  @Override
  public float value(int x, int y) {
    float sum = 0.0f;
    RealVector v_x = projector.project(new double[] { x, y });
    if (toStateAction == null)
      return (float) predictor.predict(v_x);
    policy.update(v_x);
    for (Action a : maze.actions()) {
      double prob = policy.pi(a);
      if (prob == 0)
        continue;
      RealVector v_xa = toStateAction.stateAction(v_x, a);
      sum += predictor.predict(v_xa) * prob;
    }
    return sum;
  }
}
