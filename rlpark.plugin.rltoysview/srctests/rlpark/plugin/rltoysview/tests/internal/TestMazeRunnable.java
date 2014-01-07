package rlpark.plugin.rltoysview.tests.internal;

import java.util.Random;

import rlpark.plugin.rltoys.agents.rl.RandomAgent;
import rlpark.plugin.rltoys.experiments.runners.Runner;
import rlpark.plugin.rltoys.problems.mazes.Maze;
import rlpark.plugin.rltoys.problems.mazes.Mazes;
import zephyr.plugin.core.api.Zephyr;
import zephyr.plugin.core.api.monitoring.annotations.Monitor;
import zephyr.plugin.core.api.monitoring.annotations.Popup;
import zephyr.plugin.core.api.synchronization.Clock;

@Monitor
public class TestMazeRunnable implements Runnable {
  private final Random random = new Random(0);
  @Popup
  private final Maze maze = Mazes.createBookMaze();
  private final Clock clock = new Clock("Maze");

  public TestMazeRunnable() {
    Zephyr.advertise(clock, this);
  }

  @Override
  public void run() {
    maze.initialize();
    Runner runner = new Runner(maze, new RandomAgent(random, maze.actions()), 6000, -1);
    while (clock.tick())
      runner.step();
  }
}
