package rlpark.plugin.rltoys.junit.problems;


import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import rlpark.plugin.rltoys.junit.envio.actions.ActionArrayTest;
import rlpark.plugin.rltoys.junit.envio.observations.ObsFilterTest;
import rlpark.plugin.rltoys.junit.problems.continuousgridworld.TestContinuousGridworld;
import rlpark.plugin.rltoys.junit.problems.mountaincar.MountainCarBehaviourPolicyTest;
import rlpark.plugin.rltoys.junit.problems.mountaincar.MountainCarTest;
import rlpark.plugin.rltoys.junit.problems.pendulum.SwingPendulumTest;
import rlpark.plugin.rltoys.junit.problems.stategraph.FiniteStateGraphTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({ FiniteStateGraphTest.class, ActionArrayTest.class, ObsFilterTest.class, MountainCarTest.class,
    SwingPendulumTest.class, MountainCarBehaviourPolicyTest.class, TestContinuousGridworld.class })
public class Tests {
}