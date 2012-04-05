package rlpark.plugin.rltoys.testing;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({ rlpark.plugin.rltoys.testing.math.Tests.class, rlpark.plugin.rltoys.testing.problems.Tests.class,
    rlpark.plugin.rltoys.testing.algorithms.Tests.class, rlpark.plugin.rltoys.testing.experiments.tests.Tests.class, rlpark.plugin.rltoys.testing.demons.Tests.class })
public class RLToysTests {
}