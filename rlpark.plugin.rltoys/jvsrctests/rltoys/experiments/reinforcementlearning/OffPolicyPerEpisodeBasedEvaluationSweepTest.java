package rltoys.experiments.reinforcementlearning;

import org.junit.Assert;
import org.junit.Test;

import rltoys.experiments.parametersweep.offpolicy.evaluation.EpisodeBasedOffPolicyEvaluation;
import rltoys.experiments.parametersweep.offpolicy.evaluation.OffPolicyEvaluation;
import rltoys.experiments.parametersweep.parameters.FrozenParameters;
import rltoys.experiments.parametersweep.parameters.RunInfo;
import rltoys.experiments.parametersweep.reinforcementlearning.OffPolicyProblemFactory;
import rltoys.experiments.reinforcementlearning.OffPolicyComponentTest.OffPolicySweepDescriptor;
import rltoys.experiments.reinforcementlearning.problemtest.OffPolicyRLProblemFactoryTest;

public class OffPolicyPerEpisodeBasedEvaluationSweepTest extends AbstractOffPolicyRLSweepTest {
  final static private int NbEpisode = 100;
  final static private int NbTimeSteps = 100;
  final static private int NbBehaviourRewardCheckpoint = 10;
  final static private int NbEpisodePerEvaluation = 5;
  final static private int NbTimeStepsPerEvaluation = 20;

  @Test
  public void testSweepEvaluationPerEpisode() {
    OffPolicyEvaluation evaluation = new EpisodeBasedOffPolicyEvaluation(NbBehaviourRewardCheckpoint,
                                                                         NbTimeStepsPerEvaluation,
                                                                         NbEpisodePerEvaluation);
    OffPolicyProblemFactory problemFactory = new OffPolicyRLProblemFactoryTest(NbEpisode, NbTimeSteps);
    testSweep(new OffPolicySweepDescriptor(problemFactory, evaluation));
    RunInfo infos = checkFile("Problem/Action01", Integer.MAX_VALUE);
    checkInfos("Problem/Action01", Integer.MAX_VALUE, infos);
    infos = checkFile("Problem/Action02", Integer.MAX_VALUE);
    checkInfos("Problem/Action02", Integer.MAX_VALUE, infos);
    Assert.assertTrue(isBehaviourPerformanceChecked());
  }

  private void checkInfos(String testFolder, int divergedOnSlice, RunInfo infos) {
    for (String label : infos.infoLabels()) {
      if (!label.contains("Reward"))
        continue;
      checkRewardEntry(testFolder, null, infos.get(label), label);
    }
  }

  @Override
  protected void checkParameters(String testFolder, String filename, int divergedOnSlice, FrozenParameters parameters) {
    for (String label : parameters.labels()) {
      if (label.contains("NbTimeStepSliceMeasured")) {
        int expected = label.startsWith("Behaviour") ? NbTimeSteps : NbTimeStepsPerEvaluation;
        Assert.assertEquals(expected, (int) parameters.get(label));
      }
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

  private void checkTargetParameter(String testFolder, int checkPoint, String label, int value) {
    int multiplier = Integer.parseInt(testFolder.substring(testFolder.length() - 2));
    Assert.assertTrue(checkPoint < NbBehaviourRewardCheckpoint);
    if (label.contains("Start")) {
      Assert.assertEquals(checkPoint * ((NbEpisode - 1) / NbBehaviourRewardCheckpoint), value);
    }
    if (label.contains("Slice"))
      Assert.assertEquals(NbTimeStepsPerEvaluation * multiplier, value);
    if (label.contains("Cumulated"))
      Assert.assertEquals(NbTimeStepsPerEvaluation * multiplier, value);
  }
}
