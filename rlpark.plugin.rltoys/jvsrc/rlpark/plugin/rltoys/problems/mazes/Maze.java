package rlpark.plugin.rltoys.problems.mazes;

import java.awt.Point;

import rlpark.plugin.rltoys.algorithms.functions.states.Projector;
import rlpark.plugin.rltoys.envio.actions.Action;
import rlpark.plugin.rltoys.envio.actions.ActionArray;
import rlpark.plugin.rltoys.envio.observations.Legend;
import rlpark.plugin.rltoys.envio.rl.TRStep;
import rlpark.plugin.rltoys.math.vector.RealVector;
import rlpark.plugin.rltoys.math.vector.implementations.BVector;
import rlpark.plugin.rltoys.problems.ProblemDiscreteAction;

public class Maze implements ProblemDiscreteAction {
  static private Legend legend = new Legend("x", "y");
  public static final ActionArray Left = new ActionArray(-1, 0);
  public static final ActionArray Right = new ActionArray(+1, 0);
  public static final ActionArray Up = new ActionArray(0, +1);
  public static final ActionArray Down = new ActionArray(0, -1);
  public static final ActionArray Stop = new ActionArray(0, 0);
  static final public Action[] Actions = { Left, Right, Stop, Up, Down };
  private final byte[][] layout;
  private final Point start;
  private final boolean[][] endEpisode;
  private final double[][] rewardFunction;
  private TRStep step;

  public Maze(byte[][] layout, double[][] rewardFunction, boolean[][] endEpisode, Point start) {
    this.layout = layout;
    this.rewardFunction = rewardFunction;
    this.endEpisode = endEpisode;
    this.start = start;
    initialize();
  }

  @Override
  public TRStep initialize() {
    step = new TRStep(new double[] { start.x, start.y }, rewardFunction[start.x][start.y]);
    return step;
  }

  @Override
  public TRStep step(Action action) {
    double[] actions = ((ActionArray) action).actions;
    int newX = (int) (step.o_tp1[0] + actions[0]);
    int newY = (int) (step.o_tp1[1] + actions[1]);
    if (layout[newX][newY] != 0) {
      newX = (int) step.o_tp1[0];
      newY = (int) step.o_tp1[1];
    }
    step = new TRStep(step, action, new double[] { newX, newY }, rewardFunction[newX][newY]);
    if (endEpisode[newX][newY])
      forceEndEpisode();
    return step;
  }

  @Override
  public TRStep forceEndEpisode() {
    step = step.createEndingStep();
    return step;
  }

  @Override
  public TRStep lastStep() {
    return step;
  }

  @Override
  public Legend legend() {
    return legend;
  }

  @Override
  public Action[] actions() {
    return Actions;
  }

  public byte[][] layout() {
    return layout;
  }

  public Point size() {
    return new Point(layout.length, layout[0].length);
  }

  public boolean[][] endEpisode() {
    return endEpisode;
  }

  @SuppressWarnings("serial")
  public Projector getMarkovProjector() {
    final Point size = size();
    final BVector projection = new BVector(size.x * size.y);
    return new Projector() {
      @Override
      public int vectorSize() {
        return projection.size;
      }

      @Override
      public double vectorNorm() {
        return 1;
      }

      @Override
      public RealVector project(double[] obs) {
        projection.clear();
        if (obs != null)
          projection.setOn((int) (obs[0] * size.y + obs[1]));
        return projection;
      }
    };
  }
}
