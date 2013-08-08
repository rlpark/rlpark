package rlpark.plugin.rltoys.junit.algorithms.predictions.td;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({ TDTest.class, TDLambdaTest.class, TDLambdaAlphaBoundTest.class, TDCTest.class,
    TDLambdaAutostepTest.class, GTDLambdaTest.class, HTDTest.class, TDErrorMonitorTest.class })
public class Tests {
}