package rlpark.plugin.rltoys.testing.problems;


import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import rlpark.plugin.rltoys.testing.envio.actions.ActionArrayTest;
import rlpark.plugin.rltoys.testing.envio.observations.ObsFilterTest;
import rlpark.plugin.rltoys.testing.problems.continuousgridworld.TestContinuousGridworld;
import rlpark.plugin.rltoys.testing.problems.mountaincar.MountainCarBehaviourPolicyTest;
import rlpark.plugin.rltoys.testing.problems.mountaincar.MountainCarTest;
import rlpark.plugin.rltoys.testing.problems.pendulum.SwingPendulumTest;
import rlpark.plugin.rltoys.testing.problems.stategraph.FiniteStateGraphTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({ FiniteStateGraphTest.class, ActionArrayTest.class, ObsFilterTest.class, MountainCarTest.class,
    SwingPendulumTest.class, MountainCarBehaviourPolicyTest.class, TestContinuousGridworld.class })
public class Tests {
}