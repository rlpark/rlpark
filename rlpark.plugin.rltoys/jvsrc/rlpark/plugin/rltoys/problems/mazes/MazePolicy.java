package rlpark.plugin.rltoys.problems.mazes;

import rlpark.plugin.rltoys.algorithms.functions.states.Projector;
import rlpark.plugin.rltoys.envio.actions.Action;
import rlpark.plugin.rltoys.envio.policy.Policy;
import rlpark.plugin.rltoys.math.vector.RealVector;
import rlpark.plugin.rltoys.math.vector.implementations.PVector;
import rlpark.plugin.rltoys.utils.Utils;

public class MazePolicy {
  private final Policy policy;
  private final Projector projector;
  private final double[] probs;
  private final Action[] actions;

  public MazePolicy(Maze maze, Projector projector, Policy policy) {
    this.projector = projector;
    this.policy = Utils.clone(policy);
    actions = maze.actions();
    probs = new double[actions.length];
  }

  public double[] policy(int x, int y) {
    RealVector v_x = projector.project(new double[] { x, y });
    policy.update(v_x);
    for (int i = 0; i < actions.length; i++) {
      probs[i] = policy.pi(actions[i]);
    }
    assert Math.abs(new PVector(probs).sum() - 1.0) < .01;
    return probs;
  }
}
