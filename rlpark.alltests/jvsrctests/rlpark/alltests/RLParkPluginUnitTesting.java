package rlpark.alltests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import rlpark.alltests.rlparkview.RLParkViewsTesting;
import zephyr.plugin.junittesting.ZephyrUnitTesting;

@RunWith(Suite.class)
@Suite.SuiteClasses({ RLParkAllTests.class, ZephyrUnitTesting.class, RLParkViewsTesting.class })
public class RLParkPluginUnitTesting {
}
