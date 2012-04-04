package rlpark.plugin.rltoys.algorithms;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import rlpark.plugin.rltoys.algorithms.learning.control.acting.SoftMaxTest;
import rlpark.plugin.rltoys.algorithms.learning.control.actorcritic.ActorCriticMountainCarTest;
import rlpark.plugin.rltoys.algorithms.learning.control.actorcritic.ActorCriticOnPolicyOnPendulumTest;
import rlpark.plugin.rltoys.algorithms.learning.control.actorcritic.ActorCriticOnPolicyOnStateTest;
import rlpark.plugin.rltoys.algorithms.learning.control.actorcritic.policystructure.ScaledPolicyDistributionTest;
import rlpark.plugin.rltoys.algorithms.learning.control.actorcritic.policystructure.TestJointDistribution;
import rlpark.plugin.rltoys.algorithms.learning.control.gq.GQOnPolicyTest;
import rlpark.plugin.rltoys.algorithms.learning.control.gq.GQQLambdaTest;
import rlpark.plugin.rltoys.algorithms.learning.control.gq.GQTest;
import rlpark.plugin.rltoys.algorithms.learning.control.qlearning.QLearningTest;
import rlpark.plugin.rltoys.algorithms.learning.control.sarsa.SarsaTest;
import rlpark.plugin.rltoys.algorithms.learning.control.sarsa.TracesTest;
import rlpark.plugin.rltoys.algorithms.learning.predictions.supervised.AdalineTest;
import rlpark.plugin.rltoys.algorithms.learning.predictions.supervised.IDBDTest;
import rlpark.plugin.rltoys.algorithms.learning.predictions.supervised.K1Test;
import rlpark.plugin.rltoys.algorithms.learning.predictions.td.GTDLambdaTest;
import rlpark.plugin.rltoys.algorithms.learning.predictions.td.TDTest;
import rlpark.plugin.rltoys.algorithms.representations.ObsHistoryTest;
import rlpark.plugin.rltoys.algorithms.representations.actions.TabularActionTest;
import rlpark.plugin.rltoys.algorithms.representations.discretizer.avebins.AveBinsTest;
import rlpark.plugin.rltoys.algorithms.representations.discretizer.avebins.AveBinsTreeTest;
import rlpark.plugin.rltoys.algorithms.representations.ltu.RandomNetworkTest;
import rlpark.plugin.rltoys.algorithms.representations.policy.ConstantPolicyTest;
import rlpark.plugin.rltoys.algorithms.representations.rbf.TestRBFs;
import rlpark.plugin.rltoys.algorithms.representations.tilescoding.TileCodersHashingTest;
import rlpark.plugin.rltoys.algorithms.representations.tilescoding.TileCodersNoHashingTest;
import rlpark.plugin.rltoys.algorithms.representations.tilescoding.hashing.MurmurHash2Test;
import rlpark.plugin.rltoys.algorithms.representations.traces.ATracesTest;

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