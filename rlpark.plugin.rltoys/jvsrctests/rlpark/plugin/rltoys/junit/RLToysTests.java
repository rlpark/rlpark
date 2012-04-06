package rlpark.plugin.rltoys.junit;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({ rlpark.plugin.rltoys.junit.math.Tests.class, rlpark.plugin.rltoys.junit.problems.Tests.class,
    rlpark.plugin.rltoys.junit.algorithms.Tests.class, rlpark.plugin.rltoys.junit.experiments.tests.Tests.class, rlpark.plugin.rltoys.junit.demons.Tests.class })
public class RLToysTests {
}