package rlpark.plugin.rltoys;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({ rlpark.plugin.rltoys.math.Tests.class, rlpark.plugin.rltoys.problems.Tests.class,
    rlpark.plugin.rltoys.algorithms.Tests.class, rlpark.plugin.rltoys.experiments.tests.Tests.class, rlpark.plugin.rltoys.demons.Tests.class })
public class RLToysTests {
}