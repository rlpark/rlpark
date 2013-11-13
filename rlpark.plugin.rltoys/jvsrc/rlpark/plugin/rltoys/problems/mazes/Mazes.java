package rlpark.plugin.rltoys.problems.mazes;

import java.awt.Point;
import java.util.Arrays;

public class Mazes {
  static public final byte[][] BookMaze = { { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 }, { 1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1 },
      { 1, 0, 0, 1, 0, 0, 0, 0, 1, 0, 1 }, { 1, 0, 0, 1, 0, 0, 0, 0, 1, 0, 1 }, { 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1 },
      { 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1 }, { 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 }, { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 } };

  static public Maze createBookMaze() {
    byte[][] layout = BookMaze;
    double[][] rewardFunction = createDefaultRewardFunction(layout, -1);
    boolean[][] endEpisodeFunction = createDefaultEndEpisode(layout);
    endEpisodeFunction[1][9] = true;
    Point start = new Point(3, 1);
    return new Maze(layout, rewardFunction, endEpisodeFunction, start);
  }

  static public Maze createBookMazePositiveReward() {
    byte[][] layout = BookMaze;
    double[][] rewardFunction = createDefaultRewardFunction(layout, 0);
    rewardFunction[1][9] = 1;
    boolean[][] endEpisodeFunction = createDefaultEndEpisode(layout);
    endEpisodeFunction[1][9] = true;
    Point start = new Point(3, 1);
    return new Maze(layout, rewardFunction, endEpisodeFunction, start);
  }

  public static double[][] createDefaultRewardFunction(byte[][] layout, double reward) {
    double[][] rewardFunction = new double[layout.length][];
    for (int i = 0; i < rewardFunction.length; i++) {
      rewardFunction[i] = new double[layout[i].length];
      Arrays.fill(rewardFunction[i], reward);
    }
    return rewardFunction;
  }

  public static boolean[][] createDefaultEndEpisode(byte[][] layout) {
    boolean[][] endEpisodeFunction = new boolean[layout.length][];
    for (int i = 0; i < endEpisodeFunction.length; i++)
      endEpisodeFunction[i] = new boolean[layout[i].length];
    return endEpisodeFunction;
  }
}
