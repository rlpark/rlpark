package rlpark.alltests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import rlpark.plugin.rltoys.junit.RLToysTests;
import rlpark.plugin.robot.RobotTests;

@RunWith(Suite.class)
@Suite.SuiteClasses({ PackageCycleTest.class, RLToysTests.class, RobotTests.class })
public class RLParkAllTests {
}