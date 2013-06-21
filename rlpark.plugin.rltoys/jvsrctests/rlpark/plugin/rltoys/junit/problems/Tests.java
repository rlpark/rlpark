package rlpark.plugin.rltoys.junit.problems;


import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import rlpark.plugin.rltoys.junit.envio.actions.ActionArrayTest;
import rlpark.plugin.rltoys.junit.envio.observations.ObsFilterTest;
import rlpark.plugin.rltoys.junit.problems.helicopter.HelicopterTest;
import rlpark.plugin.rltoys.junit.problems.mazes.MazeTest;
import rlpark.plugin.rltoys.junit.problems.mountaincar.MountainCarTest;
import rlpark.plugin.rltoys.junit.problems.pendulum.SwingPendulumTest;
import rlpark.plugin.rltoys.junit.problems.puddleworld.PuddleWorldTest;
import rlpark.plugin.rltoys.junit.problems.stategraph.FiniteStateGraphTest;
import rlpark.plugin.rltoys.junit.problems.stategraph02.GraphProblemTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({ FiniteStateGraphTest.class, GraphProblemTest.class, ActionArrayTest.class, ObsFilterTest.class,
    MountainCarTest.class, SwingPendulumTest.class, PuddleWorldTest.class, MazeTest.class, HelicopterTest.class })
public class Tests {
}