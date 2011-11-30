package rlpark.alltests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import zephyr.plugin.junittesting.ZephyrUnitTesting;

@RunWith(Suite.class)
@Suite.SuiteClasses({ ZephyrUnitTesting.class, RLParkAllTests.class })
public class RLParkPluginUnitTesting {
}
