package rlpark.plugin.rltoys.problems;


import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import rlpark.plugin.rltoys.envio.actions.ActionArrayTest;
import rlpark.plugin.rltoys.envio.observations.ObsFilterTest;
import rlpark.plugin.rltoys.problems.continuousgridworld.TestContinuousGridworld;
import rlpark.plugin.rltoys.problems.mountaincar.MountainCarBehaviourPolicyTest;
import rlpark.plugin.rltoys.problems.mountaincar.MountainCarTest;
import rlpark.plugin.rltoys.problems.pendulum.SwingPendulumTest;
import rlpark.plugin.rltoys.problems.stategraph.FiniteStateGraphTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({ FiniteStateGraphTest.class, ActionArrayTest.class, ObsFilterTest.class, MountainCarTest.class,
    SwingPendulumTest.class, MountainCarBehaviourPolicyTest.class, TestContinuousGridworld.class })
public class Tests {
}