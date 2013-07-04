package rlpark.plugin.rltoys.junit.problems.mazes;

import java.awt.Point;

import org.junit.Assert;

import org.junit.Test;

import rlpark.plugin.rltoys.algorithms.functions.states.Projector;
import rlpark.plugin.rltoys.envio.actions.ActionArray;
import rlpark.plugin.rltoys.envio.rl.TRStep;
import rlpark.plugin.rltoys.math.vector.implementations.BVector;
import rlpark.plugin.rltoys.problems.mazes.Maze;
import rlpark.plugin.rltoys.problems.mazes.Mazes;

public class MazeTest {
  @Test
  public void testBookMaze() {
    Maze maze = Mazes.createBookMaze();
    TRStep step = maze.initialize();
    assertEquals(new Point(3, 1), step);
    step = maze.step(Maze.Down);
    assertEquals(new Point(3, 1), step);
    step = maze.step(Maze.Up);
    assertEquals(new Point(3, 2), step);
    step = maze.step(Maze.Up);
    assertEquals(new Point(3, 2), step);
    step = maze.step(Maze.Left);
    assertEquals(new Point(2, 2), step);
    step = maze.step(Maze.Left);
    assertEquals(new Point(1, 2), step);
    step = maze.step(Maze.Left);
    assertEquals(new Point(1, 2), step);
    step = maze.step(Maze.Right);
    assertEquals(new Point(2, 2), step);
    step = maze.step(new ActionArray(1 - 2, 9 - 2));
    Assert.assertTrue(step.isEpisodeEnding());
  }

  private void assertEquals(Point point, TRStep step) {
    Assert.assertEquals(point.x, (int) step.o_tp1[0]);
    Assert.assertEquals(point.y, (int) step.o_tp1[1]);
    Assert.assertEquals(-1, step.r_tp1, 0.0);
  }

  @Test
  public void testMazeProjection() {
    Maze maze = Mazes.createBookMaze();
    Projector projector = maze.getMarkovProjector();
    boolean[] stateList = new boolean[projector.vectorSize()];
    Point mazeSize = maze.size();
    for (int x = 0; x < mazeSize.x; x++) {
      for (int y = 0; y < mazeSize.y; y++) {
        BVector v = (BVector) projector.project(new double[] { x, y });
        Assert.assertEquals(v.nonZeroElements(), 1);
        int activeIndex = v.getActiveIndexes()[0];
        Assert.assertFalse(stateList[activeIndex]);
        stateList[activeIndex] = true;
      }
    }
  }
}
