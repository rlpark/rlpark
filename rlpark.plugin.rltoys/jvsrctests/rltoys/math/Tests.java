package rltoys.math;


import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import rltoys.math.normalization.MinMaxNormalizerTest;
import rltoys.math.normalization.MovingMeanVarNormalizerTest;
import rltoys.math.normalization.NormalizerTest;
import rltoys.math.ranges.RangeTest;
import rltoys.math.vector.testing.AllVectorsTest;
import rltoys.math.vector.testing.ArrayToBinaryVectorTest;
import rltoys.math.vector.testing.BVectorTest;
import rltoys.math.vector.testing.PVectorTest;
import rltoys.math.vector.testing.SVectorTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({ BVectorTest.class, PVectorTest.class, SVectorTest.class, AllVectorsTest.class, RangeTest.class,
    HistoryTest.class, NormalizerTest.class, MinMaxNormalizerTest.class, MovingMeanVarNormalizerTest.class,
    ArrayToBinaryVectorTest.class, GrayCodeTest.class })
public class Tests {
}