package rlpark.plugin.rltoys.problems.mazes;

import rlpark.plugin.rltoys.algorithms.functions.stateactions.StateToStateAction;
import rlpark.plugin.rltoys.algorithms.functions.stateactions.TabularAction;
import rlpark.plugin.rltoys.algorithms.functions.states.Projector;
import rlpark.plugin.rltoys.envio.actions.Action;
import rlpark.plugin.rltoys.math.vector.RealVector;

public class MazeProjector {
  private final Maze maze;
  private final StateToStateAction toStateAction;
  private final Projector projector;

  public MazeProjector(Maze maze) {
    this(maze, maze.getMarkovProjector());
  }

  public MazeProjector(Maze maze, Projector projector) {
    this(maze, projector, new TabularAction(maze.actions(), projector.vectorNorm(), projector.vectorSize()));
  }

  public MazeProjector(Maze maze, Projector projector, StateToStateAction toStateAction) {
    this.maze = maze;
    this.toStateAction = toStateAction;
    this.projector = projector;
  }

  public RealVector toState(int x, int y) {
    return projector.project(new double[] { x, y });
  }

  public RealVector stateAction(RealVector x, Action a) {
    return toStateAction.stateAction(x, a);
  }

  public Maze maze() {
    return maze;
  }

  public StateToStateAction toStateAction() {
    return toStateAction;
  }

  public Projector projector() {
    return projector;
  }
}
