package rlpark.alltests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import rlpark.alltests.rlparkview.RLParkViewsTesting;
import zephyr.plugin.junittesting.ZephyrUnitTesting;

@RunWith(Suite.class)
@Suite.SuiteClasses({ ZephyrUnitTesting.class, RLParkAllTests.class, RLParkViewsTesting.class })
public class RLParkPluginUnitTesting {
}
