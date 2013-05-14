package rlpark.plugin.rltoys.junit.experiments.tests;


import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import rlpark.plugin.rltoys.junit.experiments.helpers.RunnerTest;
import rlpark.plugin.rltoys.junit.experiments.offpolicy.OffPolicyControlTests;
import rlpark.plugin.rltoys.junit.experiments.parametersweep.ParametersTest;
import rlpark.plugin.rltoys.junit.experiments.parametersweep.SweepTest;
import rlpark.plugin.rltoys.junit.experiments.predictions.onpolicytd.OnPolicyTDSweepTest;
import rlpark.plugin.rltoys.junit.experiments.predictions.supervised.SupervisedSweepTest;
import rlpark.plugin.rltoys.junit.experiments.reinforcementlearning.OffPolicyContinuousEvaluationSweepTest;
import rlpark.plugin.rltoys.junit.experiments.reinforcementlearning.OffPolicyPerEpisodeBasedEvaluationSweepTest;
import rlpark.plugin.rltoys.junit.experiments.reinforcementlearning.OnPolicySweepTest;
import rlpark.plugin.rltoys.junit.experiments.scheduling.JobPoolTest;
import rlpark.plugin.rltoys.junit.experiments.scheduling.SchedulerTest;
import rlpark.plugin.rltoys.junit.experiments.scheduling.UnreliableNetworkClientTest;
import rlpark.plugin.rltoys.junit.experiments.scheduling.UnreliableNetworkClientWithPoolTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({ RunnerTest.class, ParametersTest.class, SchedulerTest.class, JobPoolTest.class,
    UnreliableNetworkClientTest.class, UnreliableNetworkClientWithPoolTest.class, SweepTest.class,
    SupervisedSweepTest.class, OnPolicyTDSweepTest.class, OnPolicySweepTest.class,
    OffPolicyContinuousEvaluationSweepTest.class, OffPolicyPerEpisodeBasedEvaluationSweepTest.class,
    OffPolicyControlTests.class })
public class Tests {
}