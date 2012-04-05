package rlpark.alltests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import rlpark.plugin.rltoys.testing.RLToysTests;
import rlpark.plugin.robot.RobotTests;
import critterbot.CritterbotTests;

@RunWith(Suite.class)
@Suite.SuiteClasses({ PackageCycleTest.class, RLToysTests.class, RobotTests.class, CritterbotTests.class })
public class RLParkAllTests {
}