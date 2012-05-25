package rlpark.alltests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import rlpark.alltests.rlparkview.RLParkViewsTesting;
import rlpark.alltests.scripts.JythonHordeTests;
import zephyr.plugin.junittesting.ZephyrUnitTesting;

@RunWith(Suite.class)
@Suite.SuiteClasses({ RLParkAllTests.class, ZephyrUnitTesting.class, RLParkViewsTesting.class, JythonHordeTests.class })
public class RLParkPluginUnitTesting {
}
