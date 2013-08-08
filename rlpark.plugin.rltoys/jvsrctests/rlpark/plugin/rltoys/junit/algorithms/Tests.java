package rlpark.plugin.rltoys.junit.algorithms;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import rlpark.plugin.rltoys.junit.algorithms.control.acting.SoftMaxTest;
import rlpark.plugin.rltoys.junit.algorithms.control.actorcritic.ActorCriticMountainCarTest;
import rlpark.plugin.rltoys.junit.algorithms.control.actorcritic.ActorCriticOnPolicyOnPendulumTest;
import rlpark.plugin.rltoys.junit.algorithms.control.actorcritic.ActorCriticOnPolicyOnStateTest;
import rlpark.plugin.rltoys.junit.algorithms.control.actorcritic.policystructure.ScaledPolicyDistributionTest;
import rlpark.plugin.rltoys.junit.algorithms.control.actorcritic.policystructure.TestJointDistribution;
import rlpark.plugin.rltoys.junit.algorithms.control.gq.GQOnPolicyTest;
import rlpark.plugin.rltoys.junit.algorithms.control.gq.GQQLambdaTest;
import rlpark.plugin.rltoys.junit.algorithms.control.gq.GQTest;
import rlpark.plugin.rltoys.junit.algorithms.control.qlearning.QLearningTest;
import rlpark.plugin.rltoys.junit.algorithms.control.sarsa.SarsaAlphaBoundTest;
import rlpark.plugin.rltoys.junit.algorithms.control.sarsa.SarsaTest;
import rlpark.plugin.rltoys.junit.algorithms.control.sarsa.TracesTest;
import rlpark.plugin.rltoys.junit.algorithms.predictions.supervised.AdalineTest;
import rlpark.plugin.rltoys.junit.algorithms.predictions.supervised.IDBDTest;
import rlpark.plugin.rltoys.junit.algorithms.predictions.supervised.K1Test;
import rlpark.plugin.rltoys.junit.algorithms.representations.ObsHistoryTest;
import rlpark.plugin.rltoys.junit.algorithms.representations.actions.TabularActionTest;
import rlpark.plugin.rltoys.junit.algorithms.representations.discretizer.avebins.AveBinsTest;
import rlpark.plugin.rltoys.junit.algorithms.representations.discretizer.avebins.AveBinsTreeTest;
import rlpark.plugin.rltoys.junit.algorithms.representations.ltu.RandomNetworkTest;
import rlpark.plugin.rltoys.junit.algorithms.representations.rbf.TestRBFs;
import rlpark.plugin.rltoys.junit.algorithms.representations.tilescoding.TileCodersHashingTest;
import rlpark.plugin.rltoys.junit.algorithms.representations.tilescoding.TileCodersNoHashingTest;
import rlpark.plugin.rltoys.junit.algorithms.representations.tilescoding.TileCodersUniformityTest;
import rlpark.plugin.rltoys.junit.algorithms.representations.tilescoding.hashing.MurmurHash2Test;
import rlpark.plugin.rltoys.junit.algorithms.traces.ATracesTest;
import rlpark.plugin.rltoys.junit.envio.policy.ConstantPolicyTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({ AveBinsTest.class, AveBinsTreeTest.class, ConstantPolicyTest.class, ObsHistoryTest.class,
    TabularActionTest.class, ScaledPolicyDistributionTest.class, SoftMaxTest.class, TestJointDistribution.class,
    ScaledPolicyDistributionTest.class, TileCodersNoHashingTest.class, TileCodersUniformityTest.class, TestRBFs.class,
    ATracesTest.class, TileCodersHashingTest.class, MurmurHash2Test.class, AdalineTest.class, IDBDTest.class,
    K1Test.class, rlpark.plugin.rltoys.junit.algorithms.predictions.td.Tests.class, SarsaTest.class,
    SarsaAlphaBoundTest.class, QLearningTest.class, GQTest.class, GQOnPolicyTest.class, TracesTest.class,
    GQQLambdaTest.class, ActorCriticOnPolicyOnStateTest.class, ActorCriticOnPolicyOnPendulumTest.class,
    ActorCriticMountainCarTest.class, RandomNetworkTest.class })
public class Tests {
}