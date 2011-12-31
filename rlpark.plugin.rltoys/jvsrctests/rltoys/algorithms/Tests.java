package rltoys.algorithms;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import rltoys.algorithms.learning.control.acting.SoftMaxTest;
import rltoys.algorithms.learning.control.actorcritic.ActorCriticMountainCarTest;
import rltoys.algorithms.learning.control.actorcritic.ActorCriticOnPolicyOnPendulumTest;
import rltoys.algorithms.learning.control.actorcritic.ActorCriticOnPolicyOnStateTest;
import rltoys.algorithms.learning.control.actorcritic.policystructure.ScaledPolicyDistributionTest;
import rltoys.algorithms.learning.control.actorcritic.policystructure.TestJointDistribution;
import rltoys.algorithms.learning.control.gq.GQOnPolicyTest;
import rltoys.algorithms.learning.control.gq.GQQLambdaTest;
import rltoys.algorithms.learning.control.gq.GQTest;
import rltoys.algorithms.learning.control.qlearning.QLearningTest;
import rltoys.algorithms.learning.control.sarsa.SarsaTest;
import rltoys.algorithms.learning.control.sarsa.TracesTest;
import rltoys.algorithms.learning.predictions.supervised.AdalineTest;
import rltoys.algorithms.learning.predictions.supervised.IDBDTest;
import rltoys.algorithms.learning.predictions.supervised.K1Test;
import rltoys.algorithms.learning.predictions.td.GTDLambdaTest;
import rltoys.algorithms.learning.predictions.td.TDTest;
import rltoys.algorithms.representations.ObsHistoryTest;
import rltoys.algorithms.representations.actions.TabularActionTest;
import rltoys.algorithms.representations.discretizer.avebins.AveBinsTest;
import rltoys.algorithms.representations.discretizer.avebins.AveBinsTreeTest;
import rltoys.algorithms.representations.ltu.RandomNetworkTest;
import rltoys.algorithms.representations.policy.ConstantPolicyTest;
import rltoys.algorithms.representations.rbf.TestRBFs;
import rltoys.algorithms.representations.tilescoding.TileCodersHashingTest;
import rltoys.algorithms.representations.tilescoding.TileCodersNoHashingTest;
import rltoys.algorithms.representations.tilescoding.hashing.MurmurHash2Test;
import rltoys.algorithms.representations.traces.ATracesTest;

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