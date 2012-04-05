package rlpark.plugin.rltoys.testing.math;


import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import rlpark.plugin.rltoys.testing.math.normalization.MinMaxNormalizerTest;
import rlpark.plugin.rltoys.testing.math.normalization.MovingMeanVarNormalizerTest;
import rlpark.plugin.rltoys.testing.math.normalization.NormalizerTest;
import rlpark.plugin.rltoys.testing.math.ranges.RangeTest;
import rlpark.plugin.rltoys.testing.math.vector.testing.AllVectorsTest;
import rlpark.plugin.rltoys.testing.math.vector.testing.ArrayToBinaryVectorTest;
import rlpark.plugin.rltoys.testing.math.vector.testing.BVectorTest;
import rlpark.plugin.rltoys.testing.math.vector.testing.PVectorTest;
import rlpark.plugin.rltoys.testing.math.vector.testing.SVectorTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({ BVectorTest.class, PVectorTest.class, SVectorTest.class, AllVectorsTest.class, RangeTest.class,
    HistoryTest.class, NormalizerTest.class, MinMaxNormalizerTest.class, MovingMeanVarNormalizerTest.class,
    ArrayToBinaryVectorTest.class, GrayCodeTest.class })
public class Tests {
}