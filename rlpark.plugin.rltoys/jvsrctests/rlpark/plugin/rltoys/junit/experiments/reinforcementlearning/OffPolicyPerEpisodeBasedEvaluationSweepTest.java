package rlpark.plugin.rltoys.junit.experiments.reinforcementlearning;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import rlpark.plugin.rltoys.experiments.parametersweep.offpolicy.EpisodeTriggeredEpisodeEvaluationOffPolicy;
import rlpark.plugin.rltoys.experiments.parametersweep.parameters.FrozenParameters;
import rlpark.plugin.rltoys.experiments.parametersweep.parameters.RunInfo;
import rlpark.plugin.rltoys.experiments.parametersweep.reinforcementlearning.OffPolicyProblemFactory;
import rlpark.plugin.rltoys.experiments.parametersweep.reinforcementlearning.RLParameters;
import rlpark.plugin.rltoys.junit.experiments.reinforcementlearning.OffPolicyComponentTest.OffPolicySweepDescriptor;
import rlpark.plugin.rltoys.junit.experiments.reinforcementlearning.problemtest.OffPolicyRLProblemFactoryTest;

public class OffPolicyPerEpisodeBasedEvaluationSweepTest extends AbstractOffPolicyRLSweepTest {
  final static private int NbEpisode = 100;
  final static private int NbTimeSteps = 100;
  final static private int NbBehaviourRewardCheckpoint = 10;
  final static private int NbEpisodePerEvaluation = 5;

  @Test
  public void testSweepEvaluationPerEpisode() {
    // RLParameters.NbTimeStepsPerEvaluation , NbTimeStepsPerEvaluation,
    OffPolicyProblemFactory problemFactory = new OffPolicyRLProblemFactoryTest(NbEpisode, NbTimeSteps);
    RunInfo infos = new RunInfo(RLParameters.NbRewardCheckpoint, (double) NbBehaviourRewardCheckpoint,
                                RLParameters.NbEpisodePerEvaluation, (double) NbEpisodePerEvaluation);
    OffPolicySweepDescriptor provider = new OffPolicySweepDescriptor(problemFactory,
                                                                     new EpisodeTriggeredEpisodeEvaluationOffPolicy(),
                                                                     infos);
    testSweep(provider);
    List<RunInfo> infosList = checkFile(provider, Integer.MAX_VALUE);
    checkInfos("Problem/Action01", Integer.MAX_VALUE, infosList.get(0));
    checkInfos("Problem/Action02", Integer.MAX_VALUE, infosList.get(1));
    Assert.assertTrue(isBehaviourPerformanceChecked());
  }

  private void checkInfos(String testFolder, int divergedOnSlice, RunInfo infos) {
    for (String label : infos.infoLabels()) {
      if (!label.contains("Reward") || label.equals(RLParameters.NbRewardCheckpoint))
        continue;
      checkRewardEntry(testFolder, null, infos.get(label), label);
    }
  }

  @Override
  protected void checkParameters(String testFolder, String filename, int divergedOnSlice, FrozenParameters parameters) {
    for (String label : parameters.labels()) {
      if (label.contains("NbTimeStepSliceMeasured"))
        Assert.assertEquals(NbTimeSteps, (int) parameters.get(label));
      if (!label.contains("Reward"))
        continue;
      checkRewardEntry(testFolder, filename, parameters.get(label), label);
    }
  }

  private void checkRewardEntry(String testFolder, String filename, double value, String label) {
    if (label.startsWith("Behaviour") && label.endsWith("SliceSize")) {
      Assert.assertEquals(NbEpisode / NbBehaviourRewardCheckpoint, (int) value);
      return;
    }
    if (label.endsWith("CheckPoint")) {
      Assert.assertEquals(NbBehaviourRewardCheckpoint, (int) value);
      return;
    }
    if (label.startsWith("Target") && label.endsWith("SliceSize")) {
      Assert.assertEquals(NbBehaviourRewardCheckpoint, (int) value);
      return;
    }
    int checkPoint = Integer.parseInt(label.substring(label.length() - 2, label.length()));
    if (label.contains("Behaviour"))
      checkBehaviourParameter(filename, checkPoint, label, (int) value);
    if (label.contains("Target"))
      checkTargetParameter(testFolder, checkPoint, label, (int) value);
  }

  private void checkBehaviourParameter(String filename, int checkPoint, String label, double value) {
    int sliceSize = NbEpisode / NbRewardCheckPoint;
    if (label.contains("Start")) {
      Assert.assertEquals(checkPoint * sliceSize, (int) value);
      return;
    }
    checkBehaviourPerformanceValue(filename, label, value / NbTimeSteps);
  }

  private void checkTargetParameter(String testFolder, int checkPoint, String label, double value) {
    int multiplier = Integer.parseInt(testFolder.substring(testFolder.length() - 2));
    Assert.assertTrue(checkPoint < NbBehaviourRewardCheckpoint);
    if (label.contains("Start")) {
      double binSize = NbEpisode / (NbBehaviourRewardCheckpoint - 1);
      Assert.assertEquals(checkPoint * binSize, value, 1.0);
    }
    if (label.contains("Slice"))
      Assert.assertEquals(NbTimeSteps * multiplier, value, multiplier);
    if (label.contains("Cumulated"))
      Assert.assertEquals(NbTimeSteps * multiplier, value, multiplier);
  }
}
