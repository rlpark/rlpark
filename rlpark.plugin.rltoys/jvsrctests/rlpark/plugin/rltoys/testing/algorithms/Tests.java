package rlpark.plugin.rltoys.testing.algorithms;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import rlpark.plugin.rltoys.testing.algorithms.control.acting.SoftMaxTest;
import rlpark.plugin.rltoys.testing.algorithms.control.actorcritic.ActorCriticMountainCarTest;
import rlpark.plugin.rltoys.testing.algorithms.control.actorcritic.ActorCriticOnPolicyOnPendulumTest;
import rlpark.plugin.rltoys.testing.algorithms.control.actorcritic.ActorCriticOnPolicyOnStateTest;
import rlpark.plugin.rltoys.testing.algorithms.control.actorcritic.policystructure.ScaledPolicyDistributionTest;
import rlpark.plugin.rltoys.testing.algorithms.control.actorcritic.policystructure.TestJointDistribution;
import rlpark.plugin.rltoys.testing.algorithms.control.gq.GQOnPolicyTest;
import rlpark.plugin.rltoys.testing.algorithms.control.gq.GQQLambdaTest;
import rlpark.plugin.rltoys.testing.algorithms.control.gq.GQTest;
import rlpark.plugin.rltoys.testing.algorithms.control.qlearning.QLearningTest;
import rlpark.plugin.rltoys.testing.algorithms.control.sarsa.SarsaTest;
import rlpark.plugin.rltoys.testing.algorithms.control.sarsa.TracesTest;
import rlpark.plugin.rltoys.testing.algorithms.predictions.supervised.AdalineTest;
import rlpark.plugin.rltoys.testing.algorithms.predictions.supervised.IDBDTest;
import rlpark.plugin.rltoys.testing.algorithms.predictions.supervised.K1Test;
import rlpark.plugin.rltoys.testing.algorithms.predictions.td.GTDLambdaTest;
import rlpark.plugin.rltoys.testing.algorithms.predictions.td.TDTest;
import rlpark.plugin.rltoys.testing.algorithms.representations.ObsHistoryTest;
import rlpark.plugin.rltoys.testing.algorithms.representations.actions.TabularActionTest;
import rlpark.plugin.rltoys.testing.algorithms.representations.discretizer.avebins.AveBinsTest;
import rlpark.plugin.rltoys.testing.algorithms.representations.discretizer.avebins.AveBinsTreeTest;
import rlpark.plugin.rltoys.testing.algorithms.representations.ltu.RandomNetworkTest;
import rlpark.plugin.rltoys.testing.algorithms.representations.policy.ConstantPolicyTest;
import rlpark.plugin.rltoys.testing.algorithms.representations.rbf.TestRBFs;
import rlpark.plugin.rltoys.testing.algorithms.representations.tilescoding.TileCodersHashingTest;
import rlpark.plugin.rltoys.testing.algorithms.representations.tilescoding.TileCodersNoHashingTest;
import rlpark.plugin.rltoys.testing.algorithms.representations.tilescoding.hashing.MurmurHash2Test;
import rlpark.plugin.rltoys.testing.algorithms.representations.traces.ATracesTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({ AveBinsTest.class, AveBinsTreeTest.class, ConstantPolicyTest.class, ObsHistoryTest.class,
    TabularActionTest.class, ScaledPolicyDistributionTest.class, SoftMaxTest.class, TestJointDistribution.class,
    ScaledPolicyDistributionTest.class, TileCodersNoHashingTest.class, TestRBFs.class, ATracesTest.class,
    TileCodersHashingTest.class, MurmurHash2Test.class, AdalineTest.class, IDBDTest.class, K1Test.class, TDTest.class,
    GTDLambdaTest.class, SarsaTest.class, QLearningTest.class, GQTest.class, GQOnPolicyTest.class, TracesTest.class,
    GQQLambdaTest.class, ActorCriticOnPolicyOnStateTest.class, ActorCriticOnPolicyOnPendulumTest.class,
    ActorCriticMountainCarTest.class, RandomNetworkTest.class })
public class Tests {
}