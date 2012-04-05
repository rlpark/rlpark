package rlpark.plugin.rltoys.testing.experiments.reinforcementlearning;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import rlpark.plugin.rltoys.experiments.parametersweep.interfaces.Context;
import rlpark.plugin.rltoys.experiments.parametersweep.interfaces.SweepDescriptor;
import rlpark.plugin.rltoys.experiments.parametersweep.onpolicy.AbstractContextOnPolicy;
import rlpark.plugin.rltoys.experiments.parametersweep.onpolicy.ContextEvaluation;
import rlpark.plugin.rltoys.experiments.parametersweep.parameters.FrozenParameters;
import rlpark.plugin.rltoys.experiments.parametersweep.parameters.Parameters;
import rlpark.plugin.rltoys.experiments.parametersweep.parameters.RunInfo;
import rlpark.plugin.rltoys.experiments.parametersweep.reinforcementlearning.AgentFactory;
import rlpark.plugin.rltoys.experiments.parametersweep.reinforcementlearning.ProblemFactory;
import rlpark.plugin.rltoys.experiments.parametersweep.reinforcementlearning.RLParameters;
import rlpark.plugin.rltoys.testing.experiments.reinforcementlearning.problemtest.AbstractRLProblemFactoryTest;
import rlpark.plugin.rltoys.testing.experiments.reinforcementlearning.problemtest.OnPolicyRLProblemFactoryTest;
import rlpark.plugin.rltoys.utils.Utils;

public class OnPolicySweepTest extends RLSweepTest {
  private static final String ResultFolderTest = "Problem/Agent";

  class OnPolicyTestSweep implements SweepDescriptor {
    private final int nbTimeSteps;
    private final int nbEpisode;
    private final int divergeAfter;

    public OnPolicyTestSweep(int divergeAfter, int nbTimeSteps, int nbEpisode) {
      this.nbTimeSteps = nbTimeSteps;
      this.nbEpisode = nbEpisode;
      this.divergeAfter = divergeAfter;
    }

    @Override
    public List<? extends Context> provideContexts() {
      AgentFactory agentFactory = new RLAgentFactoryTest(divergeAfter, AbstractRLProblemFactoryTest.Action01);
      ProblemFactory problemFactory = new OnPolicyRLProblemFactoryTest(nbEpisode, nbTimeSteps);
      return Utils.asList(new ContextEvaluation(problemFactory, agentFactory, NbRewardCheckPoint));
    }

    @Override
    public List<Parameters> provideParameters(Context context) {
      return Utils.asList(((AbstractContextOnPolicy) context).contextParameters());
    }
  }

  @Test
  public void testSweepOneEpisode() {
    testSweep(new OnPolicyTestSweep(Integer.MAX_VALUE, NbTimeSteps, 1));
    RunInfo infos = checkFile(ResultFolderTest, Integer.MAX_VALUE);
    checkInfos(infos, Integer.MAX_VALUE, NbTimeSteps, 1);
  }

  @Test
  public void testSweepMultipleEpisode() {
    testSweep(new OnPolicyTestSweep(Integer.MAX_VALUE, NbTimeSteps, NbEpisode));
    RunInfo infos = checkFile(ResultFolderTest, Integer.MAX_VALUE);
    checkInfos(infos, Integer.MAX_VALUE, NbTimeSteps, NbEpisode);
  }

  @Test
  public void testSweepWithBadAgent() {
    testSweep(new OnPolicyTestSweep(50, NbTimeSteps, 1));
    RunInfo infos = checkFile(ResultFolderTest, 5);
    checkInfos(infos, 50, NbTimeSteps, 1);
  }

  private void checkInfos(RunInfo infos, int divergedOnSlice, int nbTimesteps, int nbEpisodes) {
    Assert.assertEquals(nbTimesteps, (int) (double) infos.get(RLParameters.MaxEpisodeTimeSteps));
    Assert.assertEquals(nbEpisodes, (int) (double) infos.get(RLParameters.NbEpisode));
    double expectedReward = expectedReward(infos);
    for (String label : infos.infoLabels())
      checkRewardParameter(divergedOnSlice, label, expectedReward, infos.get(label));
  }

  private double expectedReward(RunInfo infos) {
    if (infos.get(RLParameters.NbEpisode) == 1.0)
      return 1.0;
    return infos.get(RLParameters.MaxEpisodeTimeSteps);
  }

  @Override
  protected void checkParameters(String testFolder, String filename, int divergedOnSlice, FrozenParameters parameters) {
    double expectedReward = expectedReward(parameters.infos());
    for (String label : parameters.labels()) {
      if (label.contains("NbTimeStepSliceMeasured")) {
        int expected = NbTimeSteps;
        Assert.assertEquals(expected, (int) parameters.get(label));
        continue;
      }
      checkRewardParameter(divergedOnSlice, label, expectedReward, parameters.get(label));
    }
  }

  private void checkRewardParameter(int divergedOnSlice, String label, double expectedReward, double value) {
    int checkPoint = 0;
    if (label.contains("Reward") && label.endsWith("RewardNbCheckPoint")) {
      Assert.assertEquals(NbRewardCheckPoint, (int) value);
      return;
    }
    if (label.contains("Reward"))
      checkPoint = Integer.parseInt(label.substring(label.length() - 2, label.length()));
    int sliceSize = NbTimeSteps / NbRewardCheckPoint;
    if (label.contains("Start"))
      Assert.assertEquals(checkPoint * sliceSize, (int) value);
    if (label.contains("Slice"))
      assertValue(checkPoint >= divergedOnSlice, expectedReward, value);
    if (label.contains("Cumulated"))
      assertValue(divergedOnSlice <= NbRewardCheckPoint, expectedReward, value);
  }
}
